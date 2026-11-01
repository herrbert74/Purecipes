package app.purecipes.backend.tools

import app.purecipes.backend.db.Db
import app.purecipes.backend.feature.nutrition.IngredientLineOverridesLoader
import java.io.File
import kotlin.system.exitProcess

private const val ARG_DIR_PREFIX = "--dir="
private const val ARG_MODE_PREFIX = "--mode="
private const val ARG_FAIL_ON_DIFF = "--fail-on-diff"
private const val ARG_SEED_PINS = "--seed-pins"
private const val MODE_DIFF = "diff"
private const val MODE_WRITE = "write"
private const val NAMES_FILE_NAME = "names.tsv"
private const val LINES_FILE_NAME = "lines.tsv"

fun main(args: Array<String>) {
	val dirPath = args.firstOrNull { it.startsWith(ARG_DIR_PREFIX) }
		?.removePrefix(ARG_DIR_PREFIX)
		?.takeIf(String::isNotBlank)
	val mode = args.firstOrNull { it.startsWith(ARG_MODE_PREFIX) }
		?.removePrefix(ARG_MODE_PREFIX)
		?.lowercase()
		?: MODE_WRITE
	val failOnDiff = args.contains(ARG_FAIL_ON_DIFF)
	val seedPins = args.contains(ARG_SEED_PINS)

	val snapshotDir = resolveSnapshotDir(dirPath)
	val namesFile = File(snapshotDir, NAMES_FILE_NAME)
	val linesFile = File(snapshotDir, LINES_FILE_NAME)

	val existingNames = if (namesFile.isFile) {
		IngredientSnapshotExporter.parseNamesTsv(namesFile.readText())
	} else {
		emptyList()
	}
	val existingLines = if (linesFile.isFile) {
		IngredientSnapshotExporter.parseLinesTsv(linesFile.readText())
	} else {
		emptyList()
	}

	val exporter = IngredientSnapshotExporter(Db.create().dataSource)
	val lineOverridesFile = File(snapshotDir, IngredientLineOverridesLoader.LINE_OVERRIDES_FILE_NAME)
	val lineOverrides = if (lineOverridesFile.isFile) {
		IngredientLineOverridesLoader.loadOverrides(lineOverridesFile.toPath())
	} else {
		IngredientLineOverridesLoader.loadDefaultOverrides()
	}
	val actual = exporter.collect(
		existingNames = existingNames,
		seedPins = seedPins,
		verifyPinnedMatchesAgainstDb = (mode == MODE_DIFF),
		lineOverrides = lineOverrides,
	)

	if (namesFile.isFile || linesFile.isFile) {
		val expected = IngredientSnapshotData(names = existingNames, lines = existingLines)
		val diff = IngredientSnapshotExporter.diff(expected = expected, actual = actual)
		print(diff.formatSummary())
		if (mode == MODE_DIFF) {
			if (failOnDiff && !diff.isEmpty) {
				exitProcess(1)
			}
			return
		}
	} else if (mode == MODE_DIFF) {
		println("Snapshot files not found in ${snapshotDir.absolutePath}")
		if (failOnDiff) {
			exitProcess(1)
		}
		return
	}

	snapshotDir.mkdirs()
	IngredientLineOverridesLoader.ensureFileExists(snapshotDir.toPath())
	namesFile.writeText(IngredientSnapshotExporter.formatNamesTsv(actual.names))
	linesFile.writeText(IngredientSnapshotExporter.formatLinesTsv(actual.lines))
	val pinnedCount = actual.names.count { it.status == IngredientSnapshotExporter.STATUS_PINNED }
	val pantryMatchedCount = actual.names.count { it.pantryItem.isNotEmpty() }
	val foodMatchedCount = actual.names.count { it.fdcId.isNotEmpty() }
	println(
		"Wrote ${actual.names.size} names ($pinnedCount pinned, $pantryMatchedCount pantry, $foodMatchedCount food) " +
			"and ${actual.lines.size} lines to ${snapshotDir.absolutePath}",
	)
}

private fun resolveSnapshotDir(explicitPath: String?): File {
	if (!explicitPath.isNullOrBlank()) {
		return File(explicitPath)
	}
	val isBackendWorkingDir = File("build.gradle.kts").isFile && File("../settings.gradle.kts").isFile
	return if (isBackendWorkingDir) {
		File("data/ingredients")
	} else {
		File("backend/data/ingredients")
	}
}
