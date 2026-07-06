plugins {
    id("jserialize.common-conventions")
    id("com.gradleup.shadow")
}

val shade = configurations.create("shade")

configurations {
    compileClasspath {
        extendsFrom(shade)
    }

    runtimeClasspath {
        extendsFrom(shade)
    }

    testImplementation {
        extendsFrom(shade)
    }

    afterEvaluate {
        fun Configuration.replaceArtifact(notation: Any) {
            outgoing.artifacts.clear()
            outgoing.variants.clear()

            outgoing.artifact(notation)
        }

        apiElements {
            replaceArtifact(tasks.shadowJar)
        }

        runtimeElements {
            replaceArtifact(tasks.shadowJar)
        }
    }
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }

    jar {
        archiveClassifier = "thin" // what's the opposite of fat?
    }

    shadowJar {
        archiveClassifier = null

        // not sure how to handle multi-release source sets, but this seems to work
        sourceSets.configureEach {
            if (name.startsWith("java")) {
                val javaVer = name.removePrefix("java")

                from(output) {
                    into("META-INF/versions/$javaVer")
                }
            }
        }

        configurations = listOf(shade)
    }
}
