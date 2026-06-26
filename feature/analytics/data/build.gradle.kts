import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
	id("convention.data")
	id("convention.common-test")
}

kotlin {
	swiftPMDependencies {
		iosMinimumDeploymentTarget.set("26.0")
		swiftPackage(
			url = url("https://github.com/firebase/firebase-ios-sdk.git"),
			version = exact("12.14.0"),
			products = listOf(
				product("FirebaseAnalytics"),
				product("FirebaseCrashlytics"),
			),
		)
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
