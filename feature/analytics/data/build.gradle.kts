plugins {
	id("convention.data")
	id("convention.common-test")
	id("org.jetbrains.kotlin.native.cocoapods")
}

apply(from = rootProject.file("gradle/purecipes-ios-cocoapods.gradle.kts"))
val shouldApplyCocoapodsKotlin = extra["purecipesShouldApplyCocoapodsKotlin"] as Boolean
val shouldRunPodBuildTasks = extra["purecipesShouldRunPodBuildTasks"] as Boolean

val cocoapodsBuildSettingsDir = layout.buildDirectory.dir("cocoapods/buildSettings").get().asFile
cocoapodsBuildSettingsDir.mkdirs()
val cocoapodsFallbackBuildDir = layout.buildDirectory.get().asFile.absolutePath
val cocoapodsFallbackSettings =
	"""
		BUILD_DIR=$cocoapodsFallbackBuildDir
		CONFIGURATION_BUILD_DIR=$cocoapodsFallbackBuildDir
		TARGET_BUILD_DIR=$cocoapodsFallbackBuildDir
		CONFIGURATION=Debug
		PLATFORM_NAME=iphonesimulator
		EFFECTIVE_PLATFORM_NAME=-iphonesimulator
		PODS_TARGET_SRCROOT=${project.projectDir.absolutePath}
		SDKROOT=iphonesimulator
	""".trimIndent() + "\n"
val cocoapodsBuildSettingsPlatforms = listOf("ios", "iosSimulator", "iossimulator", "iphoneos", "iphonesimulator")
val cocoapodsBuildSettingsModules =
	listOf(
		"FirebaseAnalytics",
		"FirebaseCrashlytics",
		"Mixpanel-swift",
		"Usercentrics",
		"UsercentricsUI",
	)
cocoapodsBuildSettingsPlatforms.forEach { platform ->
	cocoapodsBuildSettingsModules.forEach { moduleName ->
		val buildSettingsFile = cocoapodsBuildSettingsDir.resolve("build-settings-$platform-$moduleName.properties")
		if (!buildSettingsFile.exists()) {
			buildSettingsFile.writeText(cocoapodsFallbackSettings)
		} else {
			val settings = buildSettingsFile.readText()
			if (!settings.contains("BUILD_DIR=") || !settings.contains("CONFIGURATION=")) {
				buildSettingsFile.writeText(cocoapodsFallbackSettings)
			}
		}
	}
}

val cocoapodsDefsDir = layout.buildDirectory.dir("cocoapods/defs").get().asFile
cocoapodsDefsDir.mkdirs()
val cocoapodsDefsModules =
	listOf(
		"FirebaseAnalytics",
		"FirebaseCrashlytics",
		"Mixpanel",
		"Usercentrics",
		"UsercentricsUI",
	)
cocoapodsDefsModules.forEach { moduleName ->
	val defFile = cocoapodsDefsDir.resolve("$moduleName.def")
	if (!defFile.exists()) {
		defFile.createNewFile()
	}
}

tasks.configureEach {
	val isPodOrInteropTask =
		name.contains("pod", ignoreCase = true) ||
			name.contains("cinterop", ignoreCase = true) ||
			name.contains("xcode", ignoreCase = true) ||
			name.startsWith("generateDef")
	if (isPodOrInteropTask) {
		enabled = shouldRunPodBuildTasks
	}
}

kotlin {
	if (shouldApplyCocoapodsKotlin) {
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
	if (name.contains("Ios")) {
		compilerOptions.freeCompilerArgs.add("-opt-in=kotlinx.cinterop.ExperimentalForeignApi")
	}
	if (name.contains("WasmJs")) {
		compilerOptions.freeCompilerArgs.add("-opt-in=kotlin.js.ExperimentalWasmJsInterop")
	}
}
