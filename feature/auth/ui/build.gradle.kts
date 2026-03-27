plugins {
	id("convention.ui")
}

kotlin {
	android {
		namespace = "com.purecipes.feature.auth.ui"
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":feature:auth:domain"))
				implementation(libs.coil.compose)
				implementation(libs.coil.networkKtor3)
				implementation(libs.kmpauth.uihelper)
			}
		}
			androidMain {
				dependencies {
					implementation(libs.kmpauth.google)
				}
			}
			wasmJsMain {
				dependencies {
					implementation(libs.kmpauth.google)
				}
			}
	}
}
