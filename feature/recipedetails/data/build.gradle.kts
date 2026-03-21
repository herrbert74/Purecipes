plugins {
	id("convention.kmp")
	alias(libs.plugins.androidKotlinMultiPlatformLibrary)
	alias(libs.plugins.metro)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
kotlin {
	android {
        namespace = "com.purecipes.feature.recipedetails.data"
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
				api(project(":feature:recipedetails:domain"))
				implementation(project(":shared:data"))
				implementation(libs.kotlinResult.result)
			}
		}
		commonTest {
			dependencies {
				implementation(libs.kotlinx.coroutinesTest)
			}
		}
	}
}
