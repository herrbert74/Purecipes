plugins {
	id("convention.data")
	id("convention.common-test")
}

kotlin {
	android {
		namespace = "app.purecipes.feature.ads.data"
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":feature:ads:domain"))
				implementation(libs.kotlinx.coroutinesCore)
			}
		}
		androidMain {
			dependencies {
				implementation(libs.googlePlay.servicesAds)
			}
		}
		commonTest {
			dependencies {
				implementation(project(":shared:testfixtures"))
			}
		}
	}
}
