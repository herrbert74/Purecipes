plugins {
	id("convention.data")
}

kotlin {
	android {
		namespace = "app.purecipes.feature.auth.data"
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":feature:auth:domain"))
				implementation(project(":shared:data"))
			}
		}
		commonTest {
			dependencies {
				implementation(project(":shared:dataTestFixtures"))
			}
		}
	}
}
