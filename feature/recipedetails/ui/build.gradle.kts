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
				api(project(":feature:library:domain"))
				api(project(":feature:recipedetails:domain"))
				api(project(":feature:measurement:domain"))
				api(project(":feature:sharing:domain"))
				implementation(project(":feature:ads:ui"))
				implementation(project(":feature:sharing:ui"))
				api(project(":shared:domain"))
				implementation(libs.coil.compose)
				implementation(libs.coil.networkKtor3)
				implementation(libs.jetbrains.androidXNavigation3Ui)
				implementation(libs.jetbrains.composeMaterial3AdaptiveNavigation3)
				implementation(libs.kotlinx.collectionsImmutable)
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
