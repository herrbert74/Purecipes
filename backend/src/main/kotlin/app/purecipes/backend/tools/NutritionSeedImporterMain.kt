package app.purecipes.backend.tools

import app.purecipes.backend.db.Db
import app.purecipes.backend.feature.nutrition.NutritionFoodSeedRepository
import app.purecipes.backend.feature.nutrition.NutritionSeedImporter
import java.io.File

private const val ARG_FDC_JSON = "--fdc-json"
private const val ARG_DRY_RUN = "--dry-run"
private const val ARG_REPLACE = "--replace"
private const val ARG_SKIP_ALIASES = "--skip-aliases"

fun main(args: Array<String>) {
	val fdcJsonPath = readArgumentValue(args, ARG_FDC_JSON)
		?: error("Missing required argument: $ARG_FDC_JSON=/path/to/FoodData_Central.json")
	val fdcJsonFile = File(fdcJsonPath)
	if (!fdcJsonFile.exists()) {
		error("FDC JSON file does not exist: ${fdcJsonFile.absolutePath}")
	}

	val dryRun = args.contains(ARG_DRY_RUN)
	val replaceExisting = args.contains(ARG_REPLACE)
	val seedCatalogueAliases = !args.contains(ARG_SKIP_ALIASES)
	val db = Db.create()
	val repository = NutritionFoodSeedRepository(db.dataSource)
	val importer = NutritionSeedImporter(repository)
	val result = importer.importFdcJson(
		fdcJsonFile = fdcJsonFile,
		replaceExisting = replaceExisting,
		dryRun = dryRun,
		seedCatalogueAliases = seedCatalogueAliases,
	)

	println("Nutrition seed import${if (dryRun) " (dry run)" else ""} [${result.dataset.name}]")
	println("Foods imported: ${result.foodsImported}")
	println("Foods skipped (missing energy): ${result.foodsSkipped}")
	println("Measures imported: ${result.measuresImported}")
	println("Catalogue aliases imported: ${result.catalogueAliasesImported}")
	println("Extra aliases imported: ${result.extraAliasesImported}")
	println("Unmatched catalogue names: ${result.unmatchedCatalogueNames.size}")
	if (result.unmatchedCatalogueNames.isNotEmpty()) {
		println()
		result.unmatchedCatalogueNames.forEach { catalogueName ->
			println("- $catalogueName")
		}
	}

	if (!dryRun) {
		println()
		println("Database totals: foods=${repository.countFoods()} aliases=${repository.countAliases()}")
	}
}

private fun readArgumentValue(args: Array<String>, key: String): String? {
	args.forEach { arg ->
		if (arg.startsWith("$key=")) {
			return arg.removePrefix("$key=").trim()
		}
	}
	return args.toList()
		.windowed(size = 2, step = 1)
		.firstOrNull { it.first() == key }
		?.last()
}
