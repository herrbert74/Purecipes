plugins {
	id("convention.data")
	id("convention.common-test")
	alias(libs.plugins.kotlin.serialization)
}

kotlin {
	android {
		namespace = "app.purecipes.feature.library.data"
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":feature:library:domain"))
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
