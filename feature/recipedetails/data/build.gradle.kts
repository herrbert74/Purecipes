plugins {
	id("convention.data")
}

kotlin {
	android {
		namespace = "com.purecipes.feature.recipedetails.data"
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
