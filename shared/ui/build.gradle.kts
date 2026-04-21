import org.gradle.kotlin.dsl.named
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
	id("convention.kmp")
	id("convention.compose")
	alias(libs.plugins.android.lint)
}

kotlin {
	android {
		namespace = "com.purecipes.shared.ui"

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
				implementation(libs.jetbrains.composeUiToolingPreview)
				implementation(libs.kotlin.stdlib)
			}
		}

		androidMain {
			dependencies {
			}
		}

		getByName("androidDeviceTest") {
			dependencies {
				implementation(libs.androidx.testRunner)
				implementation(libs.androidx.testCore)
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

dependencies {
	androidRuntimeClasspath(libs.jetbrains.composeUiTooling)
}
