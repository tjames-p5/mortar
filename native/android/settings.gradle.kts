// Mortar for Jetpack Compose. Build from this directory with Gradle 8.11
// (gradle -p native/android …); see docs/native.md.
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "mortar-android"
include(":mortar")
