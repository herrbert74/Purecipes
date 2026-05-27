plugins {
	id("convention.ui")
	id("convention.common-test")
	alias(libs.plugins.kotlin.serialization)
}

kotlin {
	android {
		namespace = "app.purecipes.feature.recipedetails.ui"
		androidResources.enable = true
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
				api(project(":feature:favorites:domain"))
				api(project(":feature:recipedetails:domain"))
				api(project(":feature:settings:domain"))
				api(project(":feature:sharing:domain"))
				implementation(project(":feature:sharing:ui"))
				api(project(":shared:domain"))
				implementation(libs.coil.compose)
				implementation(libs.coil.networkKtor3)
				implementation(libs.jetbrains.androidXNavigation3Ui)
				implementation(libs.kotlinx.serializationJson)
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
