plugins {
	id("convention.kmp")
	alias(libs.plugins.androidLibrary)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
kotlin {
	androidTarget()

	wasmJs {
		browser()
		binaries.executable()
	}

	iosArm64()
	iosSimulatorArm64()

	sourceSets {
		commonMain {
			dependencies {
				api(project(":base:kotlin"))
				api(project(":shared:domain"))
			}
		}
	}
}

android {
	namespace = "com.purecipes.feature.recipedetails.domain"
	compileSdk = 36
	defaultConfig {
		minSdk = 24
	}
}
