plugins {
	id("convention.domain")
	id("convention.common-test")
}

kotlin {
	android {
		namespace = "app.purecipes.feature.subscription.domain"
	}

	sourceSets {
		commonMain {
			dependencies {
				implementation(libs.kotlinx.coroutinesCore)
				implementation(libs.kotlinx.datetime)
			}
		}
		commonTest {
			dependencies {
				implementation(project(":shared:testfixtures"))
				implementation(libs.kotlinx.coroutinesTest)
			}
		}
	}
}
