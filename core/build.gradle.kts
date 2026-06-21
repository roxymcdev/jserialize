plugins {
    id("jserialize.common-conventions")
}

dependencies {
    api(project(":jserialize-annotations"))
    api(libs.geantyref)
}

sourceSets {
    main {
        multirelease {
            alternateVersions(16)
        }
    }
}
