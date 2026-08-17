plugins {
	id("convention.data")
	id("convention.common-test")
	alias(libs.plugins.kotlin.serialization)
}

kotlin {
	android {
		namespace = "app.purecipes.feature.search.data"
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":feature:search:domain"))
				implementation(libs.multiplatformSettings.noargs)
				implementation(libs.kotlinx.serializationJson)
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
