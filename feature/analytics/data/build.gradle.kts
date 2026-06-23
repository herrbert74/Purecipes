import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
	id("convention.data")
	id("convention.common-test")
	id("org.jetbrains.kotlin.native.cocoapods")
}

extra["purecipesCocoapodsBuildSettingsPlatforms"] =
	listOf("ios", "iosSimulator", "iossimulator", "iphoneos", "iphonesimulator")
extra["purecipesCocoapodsBuildSettingsModules"] =
	listOf(
		"FirebaseAnalytics",
		"Usercentrics",
		"UsercentricsUI",
	)
apply(from = rootProject.file("gradle/purecipes-ios-cocoapods.gradle.kts"))
apply(from = rootProject.file("gradle/purecipes-ios-cocoapods-build-settings.gradle.kts"))
val shouldApplyCocoapodsKotlin = extra["purecipesShouldApplyCocoapodsKotlin"] as Boolean
val shouldRunPodBuildTasks = extra["purecipesShouldRunPodBuildTasks"] as Boolean

kotlin {
	if (shouldApplyCocoapodsKotlin) {
		cocoapods {
			version = "1.0"
			summary = "Purecipes analytics data"
			homepage = "https://github.com/zsoltbertalan/Purecipes"
			ios.deploymentTarget = "26.0"
			podfile = project.file("../../../iosApp/PurecipesIOSApp/Podfile")
			pod("FirebaseAnalytics")
			pod("Usercentrics")
			pod("UsercentricsUI")
		}
	}

	android {
		namespace = "app.purecipes.feature.analytics.data"
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":feature:analytics:domain"))
				implementation(libs.kotlinx.coroutinesCore)
			}
		}
		commonTest {
			dependencies {
				implementation(project(":shared:testfixtures"))
			}
		}
		androidMain {
			dependencies {
				implementation(project.dependencies.platform(libs.firebaseBom))
				implementation(libs.crashkios.crashlytics)
				implementation(libs.firebaseAnalytics)
				implementation(libs.firebaseCrashlytics)
				implementation(libs.mixpanelAndroid)
				implementation(libs.usercentricsUi)
			}
		}
		iosMain {
			dependencies {
				implementation(libs.crashkios.crashlytics)
			}
		}
	}

	compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")
	targets.withType<KotlinNativeTarget>().configureEach {
		compilerOptions.freeCompilerArgs.add("-opt-in=kotlinx.cinterop.ExperimentalForeignApi")
	}
	if (name.contains("WasmJs")) {
		compilerOptions.freeCompilerArgs.add("-opt-in=kotlin.js.ExperimentalWasmJsInterop")
	}
}
