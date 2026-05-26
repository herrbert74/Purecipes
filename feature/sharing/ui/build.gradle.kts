plugins {
	id("convention.ui")
}

kotlin {
	android {
		namespace = "app.purecipes.feature.sharing.ui"
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":feature:sharing:domain"))
			}
		}
	}
}
