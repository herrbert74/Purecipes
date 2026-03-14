@file:Suppress("UnstableApiUsage")

includeBuild("build-logic")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version ("1.0.0")
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

rootProject.name = "Purecipes"
include(":app")
include(":backend")
include(":umbrella")
include(":base:kotlin")
include(":feature:main")
include(":feature:search:data")
include(":feature:search:domain")
include(":feature:search:ui")
include(":shared:data")
include(":shared:ui")
