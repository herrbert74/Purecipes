plugins {
	id("convention.data")
}

kotlin {
	android {
		namespace = "com.purecipes.feature.favorites.data"
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":feature:favorites:domain"))
			}
		}
	}
}
