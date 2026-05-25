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
	id("com.autonomousapps.build-health") version ("3.13.0")
	id("org.jetbrains.kotlin.jvm") version ("2.3.21") apply false
	id("com.android.application") version ("9.2.1") apply false
	id("org.jetbrains.kotlin.android") version ("2.3.21") apply false
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
include(":enrichment")
include(":umbrella")
include(":base:kotlin")
include(":feature:auth:data")
include(":feature:auth:domain")
include(":feature:auth:ui")
include(":feature:analytics:data")
include(":feature:analytics:domain")
include(":feature:analytics:ui")
include(":feature:main")
include(":feature:newrecipe:data")
include(":feature:newrecipe:domain")
include(":feature:newrecipe:ui")
include(":feature:cooking:ui")
include(":feature:favorites:data")
include(":feature:favorites:domain")
include(":feature:favorites:ui")
include(":feature:recipedetails:data")
include(":feature:recipedetails:domain")
include(":feature:recipedetails:ui")
include(":feature:search:data")
include(":feature:search:domain")
include(":feature:search:ui")
include(":feature:settings:data")
include(":feature:settings:domain")
include(":feature:settings:ui")
include(":shared:data")
include(":rules")
include(":shared:dataTestFixtures")
include(":shared:domain")
include(":shared:testfixtures")
include(":shared:ui")
