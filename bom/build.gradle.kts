plugins {
    `java-platform`
    id("jserialize.base-conventions")
}

indra.configurePublications {
    from(components["javaPlatform"])
}

dependencies.constraints {
    rootProject.subprojects.filter { it != project }.forEach(::api)
}
