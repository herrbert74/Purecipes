plugins {
	id("convention.domain")
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
