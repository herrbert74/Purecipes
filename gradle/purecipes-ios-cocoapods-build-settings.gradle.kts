fun purecipesCocoapodsSdkRoot(platform: String): String =
	when (platform.lowercase()) {
		"iphoneos", "ios" -> "iphoneos"
		else -> "iphonesimulator"
	}

fun purecipesCocoapodsFallbackSettings(
	platform: String,
	projectDir: java.io.File,
	buildDir: String,
): String {
	val sdkRoot = purecipesCocoapodsSdkRoot(platform)
	val effectivePlatformName = "-$sdkRoot"
	return """
		BUILD_DIR=$buildDir
		CONFIGURATION_BUILD_DIR=$buildDir
		TARGET_BUILD_DIR=$buildDir
		CONFIGURATION=Debug
		PLATFORM_NAME=$sdkRoot
		EFFECTIVE_PLATFORM_NAME=$effectivePlatformName
		PODS_TARGET_SRCROOT=${projectDir.absolutePath}
		SDKROOT=$sdkRoot
	""".trimIndent() + "\n"
}

fun purecipesIsExpensiveCocoapodsPodTask(taskName: String): Boolean =
	taskName == "xcodeVersion" ||
		taskName.startsWith("podBuild") ||
		taskName.startsWith("podInstall") ||
		taskName.startsWith("podSetup") ||
		taskName.startsWith("podGen")

val cocoapodsBuildSettingsPlatforms =
	extra["purecipesCocoapodsBuildSettingsPlatforms"] as? List<String>
val cocoapodsBuildSettingsModules =
	extra["purecipesCocoapodsBuildSettingsModules"] as? List<String>

if (cocoapodsBuildSettingsPlatforms != null && cocoapodsBuildSettingsModules != null) {
	val buildSettingsDir = layout.buildDirectory.dir("cocoapods/buildSettings").get().asFile
	buildSettingsDir.mkdirs()
	val fallbackBuildDir = layout.buildDirectory.get().asFile.absolutePath
	cocoapodsBuildSettingsPlatforms.forEach { platform ->
		cocoapodsBuildSettingsModules.forEach { moduleName ->
			val buildSettingsFile = buildSettingsDir.resolve("build-settings-$platform-$moduleName.properties")
			val expectedSdkRoot = purecipesCocoapodsSdkRoot(platform)
			val settings = if (buildSettingsFile.exists()) buildSettingsFile.readText() else ""
			if (
				!settings.contains("BUILD_DIR=") ||
				!settings.contains("CONFIGURATION=") ||
				!settings.contains("SDKROOT=$expectedSdkRoot")
			) {
				buildSettingsFile.writeText(
					purecipesCocoapodsFallbackSettings(
						platform = platform,
						projectDir = projectDir,
						buildDir = fallbackBuildDir,
					),
				)
			}
		}
	}
}

val shouldRunExpensivePodBuildTasks =
	extra.properties["purecipesShouldRunPodBuildTasks"] as? Boolean ?: false

tasks.configureEach {
	if (purecipesIsExpensiveCocoapodsPodTask(name)) {
		enabled = shouldRunExpensivePodBuildTasks
	}
}
