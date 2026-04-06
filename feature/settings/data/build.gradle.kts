plugins {
	id("convention.data")
	alias(libs.plugins.kotlin.serialization)
}

kotlin {
	android {
		namespace = "com.purecipes.feature.settings.data"
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":feature:settings:domain"))
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
