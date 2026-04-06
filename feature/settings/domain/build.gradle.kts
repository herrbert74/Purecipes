plugins {
	id("convention.domain")
}

kotlin {
	android {
		namespace = "com.purecipes.feature.settings.domain"
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":shared:domain"))
			}
		}
	}
}
