plugins {
	id("convention.ui")
}

kotlin {
	android {
		namespace = "com.purecipes.feature.search.ui"
		compileSdk = 36
		minSdk = 24
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":feature:search:domain"))
				implementation(libs.coil.compose)
				implementation(libs.coil.networkKtor3)
			}
		}
	}
}
