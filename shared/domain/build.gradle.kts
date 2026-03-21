plugins {
	id("convention.kmp")
	alias(libs.plugins.androidKotlinMultiPlatformLibrary)
	alias(libs.plugins.kotlin.serialization)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
kotlin {
	android {
        namespace = "com.purecipes.shared.domain"
        compileSdk = 36
        minSdk = 24
    }
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
