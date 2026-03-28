plugins {
	id("convention.ui")
	id("org.jetbrains.kotlin.native.cocoapods")
}

kotlin {
	cocoapods {
		version = "1.0"
		summary = "Purecipes authentication UI"
		homepage = "https://github.com/zsoltbertalan/Purecipes"
		ios.deploymentTarget = "26.0"
		podfile = project.file("../../../iosApp/PurecipesIOSApp/Podfile")
		pod("AppAuth")
		pod("GTMAppAuth")
		pod("GoogleSignIn")
	}

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
			iosMain {
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
