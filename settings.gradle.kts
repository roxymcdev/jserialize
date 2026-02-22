pluginManagement {
    includeBuild("build-logic")
}

rootProject.name = "jserialize-parent"

fun include(path: String, action: ProjectDescriptor.() -> Unit) {
    include(path)
    project(path).action()
}

listOf(
    "annotations",
    "bom",
    "core"
).forEach { module ->
    include(":jserialize-$module") {
        projectDir = file(module)
    }
}

listOf(
    "bson",
    "configurate",
    "gson"
).forEach { module ->
    include(":jserialize-adapter-$module") {
        projectDir = file("adapter/$module")
    }
}
