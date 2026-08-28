plugins {
	id("convention.data")
	id("convention.common-test")
}

kotlin {
	android {
		namespace = "app.purecipes.feature.onboarding.data"
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":feature:onboarding:domain"))
				implementation(libs.multiplatformSettings.noargs)
			}
		}
	}
}
