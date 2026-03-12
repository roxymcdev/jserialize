plugins {
    `java-platform`
    id("jserialize.base-conventions")
}

indra.configurePublications {
    from(components["javaPlatform"])
}

dependencies.constraints {
    listOf(
        "annotations",
        "core",
        "format-bson",
        "format-configurate",
        "format-gson"
    ).forEach { module ->
        api(project(":jserialize-$module"))
    }
}
