plugins {
	id("convention.domain")
	id("convention.common-test")
}

kotlin {
	android {
		namespace = "app.purecipes.feature.settings.domain"
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":shared:domain"))
			}
		}
	}
}
