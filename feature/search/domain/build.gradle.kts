plugins {
	id("convention.kmp")
	alias(libs.plugins.androidKotlinMultiPlatformLibrary)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
kotlin {
	android {
        namespace = "com.purecipes.feature.search.domain"
        compileSdk = 36
        minSdk = 24
    }

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
