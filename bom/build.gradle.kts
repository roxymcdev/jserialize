plugins {
    `java-platform`
    id("jserialize.base-conventions")
}

dependencies.constraints {
    listOf(
        "annotations",
        "core",
        "adapter-bson",
        "adapter-configurate",
        "adapter-gson"
    ).forEach { module ->
        api(project(":jserialize-$module"))
    }
}
