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
				implementation(libs.kotlinx.datetime)
				implementation(libs.kotlinx.serializationJson)
				implementation(libs.multiplatformSettings.noargs)
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
