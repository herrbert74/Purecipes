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

fun main(args: Array<String>) {
	if (args.isEmpty()) {
		System.err.println("Usage: kotlin scripts/release/bump_android_version.main.kts <version> [bump_version_code]")
		kotlin.system.exitProcess(1)
	}
	val version = args[0]
	val bumpCode = args.getOrNull(1)?.lowercase() != "false"
	val versionsFile = File(repoRoot(), "gradle/libs.versions.toml")

	if (!versionsFile.isFile) {
		System.err.println("Missing ${versionsFile.absolutePath}")
		kotlin.system.exitProcess(1)
	}

	val lines = versionsFile.readLines().toMutableList()
	val versionCodePattern = Regex("""^versionCode = "(\d+)"""")
	val versionNamePattern = Regex("""^versionName = ".*"""")

	var currentCode = 0
	lines.forEach { line ->
		versionCodePattern.matchEntire(line)?.let { currentCode = it.groupValues[1].toInt() }
	}

	val newCode = currentCode + 1
	for (index in lines.indices) {
		when {
			bumpCode && versionCodePattern.matches(lines[index]) ->
				lines[index] = """versionCode = "$newCode""""
			versionNamePattern.matches(lines[index]) ->
				lines[index] = """versionName = "$version""""
		}
	}

	versionsFile.writeText(lines.joinToString("\n") + "\n")
	println("Set versionName=$version versionCode=$newCode (bump_code=$bumpCode)")
}

main(args)
