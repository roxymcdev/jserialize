plugins {
    id("jserialize.common-conventions")
}

dependencies {
    api(project(":jserialize-core"))
    api(libs.gson)
}
