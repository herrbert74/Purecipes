package app.purecipes.backend.tools

import app.purecipes.backend.db.Db
import java.nio.file.Path

private const val ARG_JSON_DIR_PREFIX = "--json-dir="
private const val ARG_UPSERT_EXISTING = "--upsert-existing"
private const val ARG_CALCULATE_NUTRITION = "--calculate-nutrition"

fun main(args: Array<String>) {
	val explicitJsonDir = args.firstOrNull { it.startsWith(ARG_JSON_DIR_PREFIX) }
		?.removePrefix(ARG_JSON_DIR_PREFIX)
		?.takeIf(String::isNotBlank)
	val upsertExisting = args.contains(ARG_UPSERT_EXISTING)
	val calculateNutrition = args.contains(ARG_CALCULATE_NUTRITION)

	val jsonDir = explicitJsonDir?.let(::expandHomePath)
		?: IngredientDataRebuilder.defaultJsonArchiveDir()
		?: error("JSON archive directory not found. Pass --json-dir=<path> or -PimportJson.dir=<path>.")

	val importer = ScrapedRecipeJsonImporter(Db.create().dataSource)
	val summary = importer.importDirectory(
		jsonArchiveDir = jsonDir,
		upsertExisting = upsertExisting,
		calculateImportedNutrition = calculateNutrition,
	)

	println(
		"Scraped JSON import complete: " +
			"files=${summary.totalFileCount}, " +
			"imported=${summary.importedCount}, " +
			"skippedExisting=${summary.skippedExistingCount}, " +
			"errors=${summary.errorCount}",
	)
}

private fun expandHomePath(rawPath: String): Path {
	if (!rawPath.startsWith("~/")) {
		return Path.of(rawPath)
	}
	val userHome = System.getProperty("user.home").orEmpty()
	return Path.of(userHome, rawPath.removePrefix("~/"))
}
