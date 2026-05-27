package convention

import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
	id("convention.kmp")
	id("dev.zacsweers.metro")
}

extensions.configure<KotlinMultiplatformExtension> {
	sourceSets {
		commonMain.dependencies {
			api(project(":base:kotlin"))
			api(project(":shared:domain"))
		}
	}
}
