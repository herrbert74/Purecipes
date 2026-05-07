val osName = System.getProperty("os.name").orEmpty()
val isMacOsHost = osName.contains("Mac", ignoreCase = true) || osName.contains("Darwin", ignoreCase = true)

val requestedTaskNames = gradle.startParameter.taskNames
val isIdeSync =
	System.getProperty("idea.sync.active") == "true" ||
		System.getProperty("idea.active") == "true" ||
		System.getProperty("android.injected.invoked.from.ide") == "true"

val disableIosPodsExplicit =
	findProperty("purecipes.disableIosPods") == "true" ||
		findProperty("enableIosPods") == "false"

fun taskNamesSuggestIosOrFullTreeWork(): Boolean {
	if (requestedTaskNames.isEmpty()) {
		return false
	}
	return requestedTaskNames.any { taskName ->
		taskName.contains("ios", ignoreCase = true) ||
			taskName.contains("pod", ignoreCase = true) ||
			taskName.contains("xcode", ignoreCase = true) ||
			taskName.contains("embedAndSign", ignoreCase = true) ||
			taskName.equals("build", ignoreCase = true) ||
			taskName.endsWith(":build", ignoreCase = true)
	}
}

val shouldRunPodBuildTasks =
	isMacOsHost && !isIdeSync && !disableIosPodsExplicit && taskNamesSuggestIosOrFullTreeWork()

val shouldApplyCocoapodsKotlin = isMacOsHost

extra["purecipesShouldApplyCocoapodsKotlin"] = shouldApplyCocoapodsKotlin
extra["purecipesShouldRunPodBuildTasks"] = shouldRunPodBuildTasks
