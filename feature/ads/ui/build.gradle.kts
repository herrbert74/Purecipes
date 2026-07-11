plugins {
	id("convention.ui")
	id("convention.common-test")
}

kotlin {
	android {
		namespace = "app.purecipes.feature.ads.ui"
		withDeviceTestBuilder {
			sourceSetTreeName = "androidDeviceTest"
		}.configure {
			instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		}
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":feature:ads:domain"))
				implementation(project(":shared:data"))
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
				implementation(libs.kotlinx.coroutinesTest)
				implementation(libs.kotlinx.datetime)
			}
		}
		named("androidDeviceTest") {
			dependencies {
				implementation(libs.dejavu)
				implementation(project(":shared:ui"))
			}
		}
	}

	compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")
}
