#!/usr/bin/env kotlin

import java.io.File

val MAX_PLAY_RELEASE_NOTES_LENGTH = 500

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

fun trimLine(line: String): String = line.trim()

fun extractPlainLines(sectionLines: List<String>): List<String> {
	val plain = mutableListOf<String>()
	for (line in sectionLines) {
		val trimmed = trimLine(line)
		when {
			trimmed.isEmpty() -> plain.add("")
			trimmed.startsWith("### ") -> {
				plain.add("")
				plain.add(trimmed.removePrefix("### "))
				plain.add("")
			}
			else -> plain.add(trimmed)
		}
	}
	while (plain.isNotEmpty() && plain.first().isBlank()) {
		plain.removeAt(0)
	}
	while (plain.isNotEmpty() && plain.last().isBlank()) {
		plain.removeAt(plain.lastIndex)
	}
	return plain
}

fun main(args: Array<String>) {
	if (args.isEmpty()) {
		System.err.println(
			"Usage: kotlin scripts/release/extract_release_notes.main.kts <version> [changelog-file] [output-file]",
		)
		System.err.println("  version: semver without v prefix, e.g. 0.2.0")
		kotlin.system.exitProcess(1)
	}
	val version = args[0]
	val root = repoRoot()
	val changelogFile = args.getOrNull(1)?.let { File(it) } ?: File(root, "CHANGELOG.md")
	val outputFile = args.getOrNull(2)?.let { File(it) } ?: File(root, "build/release-notes.txt")

	if (!changelogFile.isFile) {
		System.err.println("Changelog not found: ${changelogFile.absolutePath}")
		kotlin.system.exitProcess(1)
	}

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
		System.err.println("No changelog section found for version $version (expected header: $sectionHeader)")
		kotlin.system.exitProcess(1)
	}

	val plainLines = extractPlainLines(notes)
	if (plainLines.isEmpty()) {
		System.err.println("Changelog section for $version is empty")
		kotlin.system.exitProcess(1)
	}

	val plainText = plainLines.joinToString("\n") + "\n"
	if (plainText.trim().length > MAX_PLAY_RELEASE_NOTES_LENGTH) {
		System.err.println(
			"Extracted release notes are ${plainText.trim().length} characters; " +
				"Google Play allows at most $MAX_PLAY_RELEASE_NOTES_LENGTH. Rephrase the " +
				"CHANGELOG.md section for $version (do not truncate).",
		)
		kotlin.system.exitProcess(1)
	}

	outputFile.parentFile?.mkdirs()
	outputFile.writeText(plainText)
	println("Wrote release notes to ${outputFile.absolutePath} (${plainText.trim().length} chars)")
}

main(args)
