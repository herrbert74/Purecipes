plugins {
	id("convention.data")
}

kotlin {
	android {
		namespace = "com.purecipes.feature.recipedetails.data"
		compileSdk = 36
		minSdk = 24
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":feature:recipedetails:domain"))
			}
		}
		commonTest {
		}
	}
}
