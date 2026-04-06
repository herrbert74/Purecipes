plugins {
	id("convention.ui")
}

kotlin {
	android {
		namespace = "com.purecipes.feature.settings.ui"
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":feature:settings:domain"))
			}
		}
	}
}
