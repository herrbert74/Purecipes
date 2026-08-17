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

fun runCommand(repoRoot: File, vararg command: String) {
	val process = ProcessBuilder(*command)
		.directory(repoRoot)
		.inheritIO()
		.start()
	val exitCode = process.waitFor()
	if (exitCode != 0) {
		error("Command failed ($exitCode): ${command.joinToString(" ")}")
	}
}

fun runCommandCapture(repoRoot: File, vararg command: String): String {
	val process = ProcessBuilder(*command)
		.directory(repoRoot)
		.redirectErrorStream(true)
		.start()
	val output = process.inputStream.bufferedReader().readText()
	val exitCode = process.waitFor()
	if (exitCode != 0) {
		error("Command failed ($exitCode): ${command.joinToString(" ")}\n$output")
	}
	return output.trim()
}

fun trimLine(line: String): String = line.trim()

fun changelogSectionLines(changelogFile: File, version: String): List<String> {
	val sectionHeader = "## [$version]"
	var inSection = false
	val notes = mutableListOf<String>()
	for (line in changelogFile.readLines()) {
		when {
			line.startsWith(sectionHeader) -> inSection = true
			inSection && line.startsWith("## [") -> break
			inSection -> notes.add(line)
		}
	}
	if (!inSection) {
		error("No CHANGELOG.md section for version $version (expected header: $sectionHeader)")
	}
	return notes
}

fun changelogSectionHasContent(notes: List<String>): Boolean {
	val plain = notes.map { trimLine(it) }.filter { it.isNotEmpty() }
	return plain.any { line ->
		line.startsWith("- ") || line.startsWith("### ") || line.isNotBlank()
	}
}

val licenseDefinitionFiles = listOf(
	"app/src/main/res/raw/aboutlibraries.json",
	"feature/settings/ui/src/commonMain/composeResources/files/aboutlibraries.json",
)

fun exportLicenseDefinitions(repoRoot: File) {
	println("Exporting open source license definitions")
	runCommand(
		repoRoot,
		"./gradlew",
		":app:exportLibraryDefinitions",
		":feature:settings:ui:exportLibraryDefinitions",
	)
}

fun bumpVersion(versionsFile: File, version: String, bumpCode: Boolean) {
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

fun main(args: Array<String>) {
	if (args.size < 2) {
		System.err.println(
			"Usage: kotlin scripts/release/open_android_release_pr.main.kts",
		)
		System.err.println("  <version> <previous_tag> [bump_version_code]")
		System.err.println("  Requires CHANGELOG.md section ## [version] drafted locally first.")
		System.err.println("  Requires git authenticated for this repository.")
		System.err.println("  Open the PR with GitHub MCP create_pull_request (see build/release-pr-body.md).")
		kotlin.system.exitProcess(1)
	}
	val version = args[0]
	val previousTag = args[1]
	val bumpCode = args.getOrNull(2)?.lowercase() != "false"
	if (!Regex("""^\d+\.\d+\.\d+(-[a-zA-Z0-9.]+)?$""").matches(version)) {
		error("version must look like semver (e.g. 0.2.0)")
	}

	val root = repoRoot()
	val changelogFile = File(root, "CHANGELOG.md")
	val versionsFile = File(root, "gradle/libs.versions.toml")
	val notes = changelogSectionLines(changelogFile, version)
	if (!changelogSectionHasContent(notes)) {
		error("CHANGELOG.md section for $version is empty; draft release notes before opening a PR")
	}
	runCommand(root, "kotlin", "scripts/release/extract_release_notes.main.kts", version)

	val branch = "release/v$version-changelog"
	bumpVersion(versionsFile, version, bumpCode)
	exportLicenseDefinitions(root)
	runCommand(root, "git", "checkout", "-b", branch)
	runCommand(
		root,
		"git",
		"add",
		"CHANGELOG.md",
		"gradle/libs.versions.toml",
		*licenseDefinitionFiles.toTypedArray(),
	)
	if (runCommandCapture(root, "git", "diff", "--cached", "--name-only").isBlank()) {
		error("Nothing to commit; CHANGELOG.md and gradle/libs.versions.toml are unchanged")
	}
	runCommand(root, "git", "commit", "-m", "Prepare Android release $version")
	runCommand(root, "git", "push", "-u", "origin", branch)

	val prBody = """
		## Summary

		Android release **$version** (since `$previousTag`).

		## Review before merge

		- [ ] Edit CHANGELOG.md for accuracy and tester-friendly wording
		- [ ] Confirm extracted release notes are ≤500 characters (Play limit; rephrase if needed)
		- [ ] Confirm versionCode and versionName in gradle/libs.versions.toml
		- [ ] Merge, then tag v$version (or v$version-rc.N) on main to trigger distribution

		Do not tag until this PR is merged.
	""".trimIndent()
	val bodyFile = File(root, "build/release-pr-body.md")
	bodyFile.parentFile?.mkdirs()
	bodyFile.writeText(prBody)
	println("Pushed branch $branch")
	println("Open a PR with GitHub MCP create_pull_request:")
	println("  title: Release $version changelog")
	println("  base: main")
	println("  head: $branch")
	println("  body: ${bodyFile.absolutePath}")
}

main(args)
