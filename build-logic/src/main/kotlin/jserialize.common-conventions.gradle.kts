import org.gradle.kotlin.dsl.the
import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("net.kyori.indra")
    id("jserialize.base-conventions")
}

repositories {
    mavenCentral()
}

dependencies {
    val libs = project.the<LibrariesForLibs>()

    compileOnlyApi(libs.jetbrains.annotations)
    api(libs.jspecify)
}

indra {
    javaVersions {
        target(11)
    }
}
