pluginManagement {
	repositories {
		google()
		mavenCentral()
		gradlePluginPortal()
	}
}

dependencyResolutionManagement {
	@Suppress("UnstableApiUsage")
	repositories {
		mavenCentral()
		google()
	}
}

plugins {
	id("dev.panuszewski.typesafe-conventions") version "0.12.2"
}

rootProject.name = "build-logic"
