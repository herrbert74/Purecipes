plugins {
	id("convention.ui")
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
			}
		}
		named("androidDeviceTest") {
		}
	}
}
