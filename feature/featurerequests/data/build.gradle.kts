plugins {
	id("convention.data")
	id("convention.common-test")
	alias(libs.plugins.kotlin.serialization)
}

kotlin {
	android {
		namespace = "app.purecipes.feature.featurerequests.data"
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":feature:featurerequests:domain"))
				implementation(libs.kotlinx.serializationJson)
			}
		}
		commonTest {
			dependencies {
				implementation(project(":shared:dataTestFixtures"))
			}
		}
	}
}
