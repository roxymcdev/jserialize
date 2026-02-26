plugins {
    id("jserialize.common-conventions")
}

dependencies {
    api(project(":jserialize-annotations"))
    api(libs.geantyref)
}
