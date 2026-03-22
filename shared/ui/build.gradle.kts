import org.gradle.kotlin.dsl.named
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
	id("convention.kmp")
	alias(libs.plugins.androidKotlinMultiPlatformLibrary)
	alias(libs.plugins.jetBrainsCompose)
	alias(libs.plugins.kotlin.composeCompiler)
	alias(libs.plugins.android.lint)
}

kotlin {

	// Target declarations - add or remove as needed below. These define
	// which platforms this KMP module supports.
	// See: https://kotlinlang.org/docs/multiplatform-discover-project.html#targets
	android {
		namespace = "com.purecipes.shared.ui"
		compileSdk = 36
		minSdk = 24

		withHostTestBuilder {
		}

		withDeviceTestBuilder {
			sourceSetTreeName = "test"
		}.configure {
			instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		}

		androidResources.enable = true
	}

	// For iOS targets, this is also where you should
	// configure native binary output. For more information, see:
	// https://kotlinlang.org/docs/multiplatform-build-native-binaries.html#build-xcframeworks

	// A step-by-step guide on how to include this library in an XCode
	// project can be found here:
	// https://developer.android.com/kotlin/multiplatform/migrate
	val xcfName = "uiKit"

	targets.named<KotlinNativeTarget>("iosArm64") {
		binaries.framework {
			baseName = xcfName
		}
	}

	targets.named<KotlinNativeTarget>("iosSimulatorArm64") {
		binaries.framework {
			baseName = xcfName
		}
	}

	// Source set declarations.
	// Declaring a target automatically creates a source set with the same name. By default, the
	// Kotlin Gradle Plugin creates additional source sets that depend on each other, since it is
	// common to share sources between related targets.
	// See: https://kotlinlang.org/docs/multiplatform-hierarchy.html
	sourceSets {
		commonMain {
			dependencies {
				implementation(libs.kotlin.stdlib)
				implementation(libs.jetbrains.composeUi)
				implementation(libs.jetbrains.composeMaterial3)
				implementation(libs.jetbrains.composeResources)
			}
		}

		androidMain {
			dependencies {
				implementation(libs.androidx.activityCompose)
				implementation(libs.jetbrains.composeMaterialIconsExtended)
			}
		}

		getByName("androidDeviceTest") {
			dependencies {
				implementation(libs.androidx.testRunner)
				implementation(libs.core)
				implementation(libs.androidx.testExtJUnit)
			}
		}

		iosMain {
			dependencies {
				// Add iOS-specific dependencies here. This a source set created by Kotlin Gradle
				// Plugin (KGP) that each specific iOS target depends on as
				// part of KMP’s default source set hierarchy. Note that this source set depends
				// on common by default and will correctly pull the iOS artifacts of any
				// KMP dependencies declared in commonMain.
			}
		}
	}

}
