plugins {
    id("jserialize.common-conventions")
}

dependencies {
    api(project(":jserialize-core"))
    api(libs.configurate.core) {
        exclude("io.leangen.geantyref")
    }
}
