plugins {
	id("convention.data")
	id("convention.common-test")
	alias(libs.plugins.kotlin.serialization)
}

kotlin {
	android {
		namespace = "app.purecipes.feature.home.data"
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":feature:home:domain"))
				implementation(libs.kotlinx.datetime)
			}
		}
		commonTest {
			dependencies {
				implementation(project(":shared:dataTestFixtures"))
			}
		}
	}
}
