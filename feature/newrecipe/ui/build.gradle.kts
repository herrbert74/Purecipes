plugins {
	id("convention.ui")
}

kotlin {
	android {
		namespace = "com.purecipes.feature.newrecipe.ui"
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
				api(project(":feature:newrecipe:domain"))
				implementation(libs.coil.compose)
				implementation(libs.coil.networkKtor3)
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
	}
}
