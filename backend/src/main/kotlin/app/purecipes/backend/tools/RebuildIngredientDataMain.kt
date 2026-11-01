package app.purecipes.backend.tools

import app.purecipes.backend.db.Db
import java.io.File
import java.nio.file.Path

private const val ARG_JSON_DIR_PREFIX = "--json-dir="
private const val ARG_RESTORE_LINES_TSV_PREFIX = "--restore-lines-tsv="
private const val ARG_FROM_JSON = "--from-json"
private const val ARG_NO_SYNC_JSON = "--no-sync-json"
private const val ARG_ONLY_CHANGED_LINES = "--only-changed-lines"
private const val ARG_NO_JSON = "--no-json"

fun main(args: Array<String>) {
	val explicitJsonDir = args.firstOrNull { it.startsWith(ARG_JSON_DIR_PREFIX) }
		?.removePrefix(ARG_JSON_DIR_PREFIX)
		?.takeIf(String::isNotBlank)
	val restoreLinesTsvPath = args.firstOrNull { it.startsWith(ARG_RESTORE_LINES_TSV_PREFIX) }
		?.removePrefix(ARG_RESTORE_LINES_TSV_PREFIX)
		?.takeIf(String::isNotBlank)
	val skipJson = args.contains(ARG_NO_JSON)
	val fromJson = args.contains(ARG_FROM_JSON)
	val syncJson = !args.contains(ARG_NO_SYNC_JSON)
	val onlyChangedLines = args.contains(ARG_ONLY_CHANGED_LINES)

	val jsonDir = when {
		skipJson -> null
		explicitJsonDir != null -> expandHomePath(explicitJsonDir)
		else -> IngredientDataRebuilder.defaultJsonArchiveDir()
	}

	val rebuilder = IngredientDataRebuilder(Db.create().dataSource)
	if (restoreLinesTsvPath != null) {
		val linesFile = File(restoreLinesTsvPath)
		val lines = IngredientSnapshotExporter.parseLinesTsv(linesFile.readText())
		val restoredCount = rebuilder.restoreFromLinesSnapshot(lines)
		println("Restored ingredient lines for $restoredCount recipes from ${linesFile.absolutePath}")
	}

	val summary = rebuilder.rebuild(
		jsonArchiveDir = jsonDir,
		useJsonAsInput = fromJson,
		syncJsonArchive = syncJson,
		recalculateAllNutrition = !onlyChangedLines,
	)

	println(
		"Rebuilt scraped ingredient data: " +
			"scrapedRecipes=${summary.scrapedRecipeCount}, " +
			"matchedJson=${summary.matchedJsonRecipeCount}, " +
			"updatedLines=${summary.updatedLineRecipeCount}, " +
			"syncedJsonFiles=${summary.syncedJsonFileCount}, " +
			"recalculatedNutrition=${summary.recalculatedNutritionRecipeCount}",
	)
}

private fun expandHomePath(rawPath: String): Path {
	if (!rawPath.startsWith("~/")) {
		return Path.of(rawPath)
	}
	val userHome = System.getProperty("user.home").orEmpty()
	return Path.of(userHome, rawPath.removePrefix("~/"))
}
