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

rootProject.name = "CalendarApp"
include(":app")
include(":feature:calendar")
include(":feature:event")
include(":core:common")
include(":core:domain")
include(":core:data")
