plugins {
	id("convention.ui")
	id("convention.common-test")
}

kotlin {
	android {
		namespace = "app.purecipes.feature.onboarding.ui"
		withDeviceTestBuilder {
			sourceSetTreeName = "androidDeviceTest"
		}.configure {
			instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		}
	}

	sourceSets {
		commonMain {
			dependencies {
				implementation(libs.kotlinx.collectionsImmutable)
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
