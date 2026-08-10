plugins {
	id("convention.ui")
	id("convention.common-test")
	alias(libs.plugins.kotlin.serialization)
}

kotlin {
	android {
		namespace = "app.purecipes.feature.auth.ui"
		withDeviceTestBuilder {
			sourceSetTreeName = "androidDeviceTest"
		}.configure {
			instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		}
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":feature:analytics:domain"))
				api(project(":feature:auth:domain"))
				implementation(libs.coil.compose)
				implementation(libs.coil.networkKtor3)
				implementation(libs.jetbrains.androidXNavigation3Ui)
				implementation(libs.kmpauth.uihelper)
				implementation(libs.kotlinx.serializationJson)
			}
		}
		commonTest {
			dependencies {
				implementation(project(":shared:testfixtures"))
			}
		}
		named("androidDeviceTest") {
			dependencies {
				implementation(libs.dejavu)
				implementation(project(":shared:testfixtures"))
			}
		}
		androidMain {
			dependencies {
				implementation(project.dependencies.platform(libs.firebaseBom))
				implementation(libs.firebaseAuth)
				implementation(libs.gitlive.firebaseAuth)
				implementation(libs.kmpauth.firebase)
				implementation(libs.kmpauth.firebaseFacebook)
				implementation(libs.kmpauth.google)
			}
		}
		iosMain {
			dependencies {
				implementation(libs.gitlive.firebaseAuth)
				implementation(libs.kmpauth.firebase)
				implementation(libs.kmpauth.firebaseFacebook)
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
