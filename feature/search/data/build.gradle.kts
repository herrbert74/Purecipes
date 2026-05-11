plugins {
	id("convention.data")
}

kotlin {
	android {
		namespace = "app.purecipes.feature.search.data"
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":feature:search:domain"))
			}
		}
		commonTest {
			dependencies {
				implementation(project(":shared:dataTestFixtures"))
			}
		}
	}
}
