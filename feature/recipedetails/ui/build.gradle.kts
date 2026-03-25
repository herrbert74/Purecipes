plugins {
	id("convention.ui")
}

kotlin {
	android {
		namespace = "com.purecipes.feature.recipedetails.ui"
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
				implementation(libs.coil.compose)
				implementation(libs.coil.networkKtor3)
			}
		}
		commonTest {
		}
		androidMain {
		}
		named("androidDeviceTest") {
		}
	}
}
