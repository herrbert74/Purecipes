pluginManagement {
	repositories {
		google()
		mavenCentral()
		gradlePluginPortal()
		maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
	}
}

dependencyResolutionManagement {
	@Suppress("UnstableApiUsage")
	repositories {
		mavenCentral()
		google()
		maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
	}
}

plugins {
	id("dev.panuszewski.typesafe-conventions") version "0.11.1"
}

rootProject.name = "build-logic"
