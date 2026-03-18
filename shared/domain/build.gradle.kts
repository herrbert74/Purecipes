plugins {
	id("convention.kmp")
	alias(libs.plugins.androidLibrary)
	alias(libs.plugins.kotlin.serialization)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
kotlin {
	androidTarget()
	jvm()

	wasmJs {
		browser()
		binaries.executable()
	}

	iosArm64()
	iosSimulatorArm64()

	sourceSets {
		commonMain {
			dependencies {
				implementation(libs.kotlinx.serializationJson)
			}
		}
	}
}

android {
	namespace = "com.purecipes.shared.domain"
	compileSdk = 36
	defaultConfig {
		minSdk = 24
	}
}
