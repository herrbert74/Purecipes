plugins {
	id("convention.domain")
	id("convention.common-test")
}

kotlin {
	android {
		namespace = "app.purecipes.feature.measurement.domain"
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":shared:domain"))
			}
		}
	}
}
