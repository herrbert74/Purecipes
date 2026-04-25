plugins {
	id("convention.ui")
	id("org.jetbrains.kotlin.native.cocoapods")
}

val requestedTaskNames = gradle.startParameter.taskNames
val isIdeSync =
	System.getProperty("idea.sync.active") == "true" ||
		System.getProperty("idea.active") == "true" ||
		System.getProperty("android.injected.invoked.from.ide") == "true"
val podInteropEnabled = providers.gradleProperty("enableIosPods").orNull == "true"
val hasIosOrPodTaskRequest = requestedTaskNames.any { taskName ->
	taskName.contains("ios", ignoreCase = true) ||
		taskName.contains("pod", ignoreCase = true)
}
val shouldRunPodBuildTasks = podInteropEnabled && hasIosOrPodTaskRequest && !isIdeSync

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
		"AppAuth",
		"FBSDKCoreKit",
		"FBSDKLoginKit",
		"FirebaseAuth",
		"FirebaseCore",
		"GoogleSignIn",
		"GTMAppAuth",
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
	cocoapods {
		version = "1.0"
		summary = "Purecipes authentication UI"
		homepage = "https://github.com/zsoltbertalan/Purecipes"
		ios.deploymentTarget = "26.0"
		podfile = project.file("../../../iosApp/PurecipesIOSApp/Podfile")
		pod("AppAuth")
		pod("FBSDKCoreKit", "18.0.0")
		pod("FBSDKLoginKit", "18.0.0")
		pod("FirebaseAuth")
		pod("FirebaseCore")
		pod("GTMAppAuth")
		pod("GoogleSignIn")
	}

	android {
		namespace = "com.purecipes.feature.auth.ui"
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
				api(project(":feature:auth:domain"))
				implementation(libs.coil.compose)
				implementation(libs.coil.networkKtor3)
				implementation(libs.kmpauth.uihelper)
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
		androidMain {
			dependencies {
				implementation(project.dependencies.platform(libs.firebaseBom))
				implementation(libs.firebaseAuth)
				implementation(libs.kmpauth.firebase)
				implementation(libs.kmpauth.firebaseFacebook)
				implementation(libs.kmpauth.google)
			}
		}
		iosMain {
			dependencies {
				implementation(libs.kmpauth.firebase)
				implementation(libs.kmpauth.firebaseFacebook)
				implementation(libs.kmpauth.google)
			}
		}
		wasmJsMain {
			dependencies {
				implementation(libs.kmpauth.google)
			}
		}
	}
}
