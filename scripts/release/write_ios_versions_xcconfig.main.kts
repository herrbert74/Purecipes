#!/usr/bin/env kotlin

import java.io.File

fun repoRoot(): File {
	var dir = File(System.getProperty("user.dir"))
	while (dir.parentFile != null) {
		if (File(dir, "settings.gradle.kts").exists()) {
			return dir
		}
		dir = dir.parentFile
	}
	error("Could not find repository root (settings.gradle.kts)")
}

fun writeIosVersionsXcconfig(repoRoot: File, versionName: String, versionCode: String) {
	val xcconfig = File(repoRoot, "iosApp/PurecipesIOSApp/Config/Versions.xcconfig")
	xcconfig.parentFile.mkdirs()
	xcconfig.writeText(
		"MARKETING_VERSION = $versionName\nCURRENT_PROJECT_VERSION = $versionCode\n",
	)
	println(
		"Wrote ${xcconfig.relativeTo(repoRoot)} " +
			"(MARKETING_VERSION=$versionName CURRENT_PROJECT_VERSION=$versionCode)",
	)
}

fun main() {
	val root = repoRoot()
	val versionsFile = File(root, "gradle/libs.versions.toml")
	if (!versionsFile.isFile) {
		System.err.println("Missing ${versionsFile.absolutePath}")
		kotlin.system.exitProcess(1)
	}
	var versionName = ""
	var versionCode = ""
	val versionCodePattern = Regex("""^versionCode = "(\d+)"""")
	val versionNamePattern = Regex("""^versionName = "(.*)"""")
	versionsFile.readLines().forEach { line ->
		versionCodePattern.matchEntire(line)?.let { versionCode = it.groupValues[1] }
		versionNamePattern.matchEntire(line)?.let { versionName = it.groupValues[1] }
	}
	if (versionName.isBlank() || versionCode.isBlank()) {
		System.err.println("Could not read versionName/versionCode from ${versionsFile.absolutePath}")
		kotlin.system.exitProcess(1)
	}
	writeIosVersionsXcconfig(root, versionName, versionCode)
}

main()
