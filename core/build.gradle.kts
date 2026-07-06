plugins {
    id("jserialize.shadow-conventions")
}

dependencies {
    api(project(":jserialize-annotations"))
    shade("io.leangen.geantyref:geantyref")
}

sourceSets {
    main {
        multirelease {
            alternateVersions(16)
        }
    }
}

tasks.shadowJar {
    relocate("io.leangen.geantyref", "net.roxymc.jserialize.internal.geantyref")
}
