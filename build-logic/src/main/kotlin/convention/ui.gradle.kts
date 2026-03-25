package convention

import libs
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
	id("convention.kmp")
	id("convention.compose")
}

extensions.configure<KotlinMultiplatformExtension> {
	sourceSets {
		commonMain.dependencies {
			implementation(project(":shared:ui"))
			implementation(libs.jetbrains.androidXLifecycleViewmodel)
			implementation(libs.kotlinResult.result)
			implementation(libs.kotlinx.coroutinesCore)
		}
		commonTest.dependencies {
			implementation(libs.kotlinx.coroutinesTest)
		}
	}
}
