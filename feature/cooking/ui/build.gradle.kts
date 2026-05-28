plugins {
	id("convention.ui")
	id("convention.common-test")
	alias(libs.plugins.kotlin.serialization)
}

kotlin {
	android {
		namespace = "app.purecipes.feature.cooking.ui"
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
				api(project(":feature:recipedetails:domain"))
				api(project(":feature:measurement:domain"))
				api(project(":shared:domain"))
				implementation(libs.jetbrains.androidXNavigation3Ui)
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
	}
}
