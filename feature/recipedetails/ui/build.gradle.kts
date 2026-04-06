plugins {
	id("convention.ui")
}

kotlin {
	android {
		namespace = "com.purecipes.feature.recipedetails.ui"
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
				api(project(":feature:favorites:domain"))
				api(project(":feature:recipedetails:domain"))
				api(project(":feature:settings:domain"))
				api(project(":shared:domain"))
				implementation(libs.coil.compose)
				implementation(libs.coil.networkKtor3)
			}
		}
		commonTest {
				dependencies {
					implementation(project(":shared:testfixtures"))
				}
		}
		androidMain {
		}
		named("androidDeviceTest") {
				dependencies {
					implementation(project(":shared:testfixtures"))
				}
		}
	}
}
