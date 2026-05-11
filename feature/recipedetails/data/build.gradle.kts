plugins {
	id("convention.data")
	id("convention.common-test")
}

kotlin {
	android {
		namespace = "app.purecipes.feature.recipedetails.data"
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":feature:recipedetails:domain"))
			}
		}
		commonTest {
			dependencies {
				implementation(project(":shared:dataTestFixtures"))
				implementation(project(":shared:testfixtures"))
			}
		}
	}
}
