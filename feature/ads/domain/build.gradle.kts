plugins {
	id("convention.domain")
	id("convention.common-test")
}

kotlin {
	android {
		namespace = "app.purecipes.feature.ads.domain"
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":feature:subscription:domain"))
				implementation(libs.kotlinx.coroutinesCore)
				implementation(libs.kotlinx.datetime)
			}
		}
		commonTest {
			dependencies {
				implementation(project(":shared:testfixtures"))
				implementation(libs.kotlinx.coroutinesTest)
				implementation(libs.kotlinx.datetime)
			}
		}
	}
}
