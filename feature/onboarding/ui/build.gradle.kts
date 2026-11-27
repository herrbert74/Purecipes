plugins {
	id("convention.ui")
	id("convention.common-test")
}

kotlin {
	android {
		namespace = "app.purecipes.feature.onboarding.ui"
	}

	sourceSets {
		commonMain {
			dependencies {
				implementation(libs.kotlinx.collectionsImmutable)
			}
		}
		named("androidHostTest") {
			dependencies {
				implementation(libs.dejavu)
				implementation(project(":shared:testfixtures"))
			}
		}
	}
}
