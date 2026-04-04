plugins {
	id("convention.ui")
}

kotlin {
	android {
		namespace = "com.purecipes.feature.cooking.ui"
		withDeviceTestBuilder {
			sourceSetTreeName = "test"
		}.configure {
			instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		}
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":feature:analytics:domain"))
				api(project(":feature:recipedetails:domain"))
			}
		}
		named("androidDeviceTest") {
		}
	}
}
