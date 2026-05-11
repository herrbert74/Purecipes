plugins {
	id("convention.ui")
}

kotlin {
	android {
		namespace = "app.purecipes.feature.analytics.ui"
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":feature:analytics:domain"))
			}
		}
	}
}
