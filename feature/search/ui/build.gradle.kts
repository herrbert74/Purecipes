plugins {
	id("convention.ui")
}

kotlin {
	android {
		namespace = "com.purecipes.feature.search.ui"
		withDeviceTestBuilder {
			sourceSetTreeName = "androidDeviceTest"
		}.configure {
			instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		}
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":feature:analytics:domain"))
				api(project(":feature:search:domain"))
				api(project(":feature:settings:domain"))
				api(project(":shared:domain"))
				implementation(libs.coil.compose)
				implementation(libs.coil.networkKtor3)
				implementation(libs.jetbrains.composeUiToolingPreview)
				implementation(libs.kotlinx.collectionsImmutable)
			}
		}
		commonTest {
			dependencies {
				implementation(project(":shared:testfixtures"))
			}
		}
		named("androidDeviceTest") {
			dependencies {
				implementation(libs.dejavu)
				implementation(project(":shared:testfixtures"))
			}
		}
	}
}

dependencies {
	androidRuntimeClasspath(libs.jetbrains.composeUiTooling)
}
