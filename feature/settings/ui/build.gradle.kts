plugins {
	id("convention.ui")
	id("convention.common-test")
	alias(libs.plugins.kotlin.serialization)
	alias(libs.plugins.aboutLibraries)
}

aboutLibraries {
	export {
		outputFile = file("src/commonMain/composeResources/files/aboutlibraries.json")
		prettyPrint = true
	}
}

kotlin {
	android {
		namespace = "app.purecipes.feature.settings.ui"
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
				api(project(":feature:measurement:domain"))
				api(project(":feature:search:domain"))
				api(project(":feature:settings:domain"))
				implementation(project(":feature:subscription:ui"))
				implementation(project(":shared:data"))
				implementation(libs.aboutLibraries.core)
				implementation(libs.aboutLibraries.composeM3)
				implementation(libs.jetbrains.androidXNavigation3Ui)
				implementation(libs.kotlinx.serializationJson)
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
