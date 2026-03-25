plugins {
	id("convention.data")
}

kotlin {
	android {
		namespace = "com.purecipes.feature.search.data"
		compileSdk = 36
		minSdk = 24
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":feature:search:domain"))
			}
		}
		commonTest {
		}
	}
}
