plugins {
	id("convention.ui")
	id("convention.common-test")
	alias(libs.plugins.kotlin.serialization)
}

kotlin {
	android {
		namespace = "app.purecipes.feature.home.ui"
		withDeviceTestBuilder {
			sourceSetTreeName = "androidDeviceTest"
		}.configure {
			instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		}
	}

	compilerOptions.freeCompilerArgs.add(
		"-opt-in=androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi",
	)

	sourceSets {
		commonMain {
			dependencies {
				api(project(":feature:home:domain"))
				api(project(":shared:domain"))
				implementation(project(":feature:ads:ui"))
				implementation(project(":feature:library:domain"))
				implementation(project(":shared:data"))
				implementation(libs.jetbrains.androidXNavigation3Ui)
				implementation(libs.jetbrains.composeMaterial3AdaptiveNavigation3)
				implementation(libs.kotlinx.collectionsImmutable)
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
