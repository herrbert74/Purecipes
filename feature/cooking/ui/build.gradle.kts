plugins {
	id("convention.kmp")
	id("convention.compose")
}

kotlin {
	android {
		namespace = "com.purecipes.feature.cooking.ui"
		compileSdk = 36
		minSdk = 24
		withDeviceTestBuilder {
			sourceSetTreeName = "test"
		}.configure {
			instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		}
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":feature:recipedetails:domain"))
				implementation(project(":shared:ui"))
				implementation(libs.jetbrains.androidXLifecycleViewmodel)
				implementation(libs.kotlinResult.result)
				implementation(libs.kotlinx.coroutinesCore)
			}
		}
		commonTest {
			dependencies {
				implementation(libs.kotlinx.coroutinesTest)
			}
		}
		named("androidDeviceTest") {
		}
	}
}
