package convention

import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
	id("convention.kmp")
	id("dev.zacsweers.metro")
}

extensions.configure<KotlinMultiplatformExtension> {
	sourceSets {
		commonMain.dependencies {
			implementation(project(":shared:data"))
			implementation(libs.kotlinResult.result)
		}
		commonTest.dependencies {
			implementation(libs.kotlinx.coroutinesTest)
		}
	}
}
