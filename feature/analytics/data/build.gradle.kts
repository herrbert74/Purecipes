plugins {
	id("convention.data")
	id("org.jetbrains.kotlin.native.cocoapods")
}

val requestedTaskNames = gradle.startParameter.taskNames
val shouldRunPodBuildTasks = requestedTaskNames.any { taskName ->
	taskName.contains("ios", ignoreCase = true) ||
		taskName.contains("pod", ignoreCase = true)
}

tasks.matching {
	it.name.startsWith("podSetupBuild") || it.name.startsWith("podBuild")
}.configureEach {
	onlyIf {
		shouldRunPodBuildTasks
	}
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>().configureEach {
	compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")
	if (name.contains("Ios")) {
		compilerOptions.freeCompilerArgs.add("-opt-in=kotlinx.cinterop.ExperimentalForeignApi")
	}
	if (name.contains("WasmJs")) {
		compilerOptions.freeCompilerArgs.add("-opt-in=kotlin.js.ExperimentalWasmJsInterop")
	}
}

kotlin {
	cocoapods {
		version = "1.0"
		summary = "Purecipes analytics data"
		homepage = "https://github.com/zsoltbertalan/Purecipes"
		ios.deploymentTarget = "26.0"
		podfile = project.file("../../../iosApp/PurecipesIOSApp/Podfile")
		pod("FirebaseAnalytics")
		pod("FirebaseCrashlytics")
		pod("Mixpanel-swift") {
			moduleName = "Mixpanel"
		}
		pod("Usercentrics")
		pod("UsercentricsUI")
	}

	android {
		namespace = "com.purecipes.feature.analytics.data"
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
}
