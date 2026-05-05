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
				implementation(libs.kotlinx.serializationJson)
				implementation(libs.multiplatformSettings.noargs)
			}
		}
		commonTest {
			dependencies {
				implementation(project(":shared:dataTestFixtures"))
			}
		}
	}
}
