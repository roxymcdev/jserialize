rootProject.name = "jserialize-vendor"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

fun include(path: String, action: ProjectDescriptor.() -> Unit) {
    include(path)
    project(path).action()
}

listOf(
    "geantyref"
).forEach { module ->
    include(":$module") {
        projectDir = file("$module/work")
    }
}
