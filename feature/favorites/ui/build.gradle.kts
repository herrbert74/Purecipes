plugins {
	id("convention.ui")
	alias(libs.plugins.kotlin.serialization)
}

kotlin {
	android {
		namespace = "com.purecipes.feature.favorites.ui"
		withDeviceTestBuilder {
			sourceSetTreeName = "androidDeviceTest"
		}.configure {
			instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		}
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":feature:favorites:domain"))
				implementation(libs.coil.compose)
				implementation(libs.coil.networkKtor3)
				implementation(libs.kotlinx.collectionsImmutable)
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
