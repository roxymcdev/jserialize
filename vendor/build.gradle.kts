import ca.stellardrift.gitpatcher.GitPatcherExtension
import ca.stellardrift.gitpatcher.internal.Utils
import groovy.xml.XmlSlurper
import groovy.xml.slurpersupport.GPathResult

plugins {
    alias(libs.plugins.gitpatcher)
}

val vendor = project

@Suppress("AvoidApplyPluginMethod")
subprojects {
    apply<JavaLibraryPlugin>()

    version = "patched"

    repositories {
        mavenCentral()
    }

    val repoDetails = vendor.the<GitPatcherExtension>().patchedRepos.create(name) {
        val path = projectDir.parentFile.toRelativeString(vendor.projectDir)

        submodule = "$path/upstream"
        target = projectDir
        patches = vendor.file("$path/patches")
    }

    the<JavaPluginExtension>().toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }

    tasks {
        val applyPatches = vendor.tasks.named("apply${Utils.capitalize(repoDetails.name)}Patches")

        withType<AbstractArchiveTask>().configureEach {
            isPreserveFileTimestamps = false
            isReproducibleFileOrder = true
        }

        withType<JavaCompile>().configureEach {
            dependsOn(applyPatches)

            options.encoding = Charsets.UTF_8.name()
            options.release = 11

            options.compilerArgs.add("-Xlint:none")
        }

        named<JavaCompile>("compileTestJava") {
            options.release = 17
        }

        withType<ProcessResources>().configureEach {
            dependsOn(applyPatches)
        }
    }
}

fun GPathResult.resolve(vararg path: String): GPathResult {
    var result = this

    for (property in path) {
        result = result.getProperty(property) as GPathResult
    }

    return result
}

val mavenScopes = mapOf(
    "provided" to "compileOnly",
    "runtime" to "runtimeOnly",
    "compile" to "implementation",
    "test" to "testImplementation"
)

fun Project.initPom() {
    val pom = XmlSlurper().parse(projectDir.parentFile.resolve("upstream/pom.xml"))

    group = pom.resolve("groupId").text()

    dependencies {
        pom.resolve("dependencies", "dependency").filterIsInstance<GPathResult>().forEach { dependency ->
            val group = dependency.resolve("groupId").text()
            val artifact = dependency.resolve("artifactId").text()
            val version = dependency.resolve("version").text()

            val configuration = mavenScopes[dependency.resolve("scope").text()]!!

            add(configuration, "$group:$artifact:$version")
        }
    }
}

project(":geantyref") {
    initPom()
}
