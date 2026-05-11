plugins {
	id("convention.data")
	alias(libs.plugins.kotlin.serialization)
}

kotlin {
	android {
		namespace = "app.purecipes.feature.favorites.data"
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
