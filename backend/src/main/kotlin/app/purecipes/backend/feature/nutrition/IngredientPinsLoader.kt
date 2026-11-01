package app.purecipes.backend.feature.nutrition

import java.io.File

internal data class NutritionFoodPin(
	val lookupName: String,
	val fdcSource: String,
	val fdcId: String,
)

internal data class IngredientNamePins(
	val foodPinsByLookupName: Map<String, NutritionFoodPin>,
	val pantryPinsByLookupName: Map<String, String>,
) {

	companion object {

		val EMPTY = IngredientNamePins(
			foodPinsByLookupName = emptyMap(),
			pantryPinsByLookupName = emptyMap(),
		)
	}
}

internal object IngredientPinsLoader {

	private const val NAMES_TSV_PROPERTY = "purecipes.ingredients.namesTsv"
	private const val STATUS_PINNED = "pinned"
	private const val TSV_SEPARATOR = "\t"
	private const val MIN_PIN_COLUMNS = 5
	private const val COL_LOOKUP_NAME = 0
	private const val COL_STATUS = 1
	private const val COL_PANTRY_ITEM = 2
	private const val COL_FDC_SOURCE = 3
	private const val COL_FDC_ID = 4

	fun defaultNamesTsvFile(): File {
		val configuredPath = System.getProperty(NAMES_TSV_PROPERTY)?.trim().orEmpty()
		if (configuredPath.isNotEmpty()) {
			return File(configuredPath)
		}
		val isBackendWorkingDir = File("build.gradle.kts").isFile && File("../settings.gradle.kts").isFile
		return if (isBackendWorkingDir) {
			File("data/ingredients/names.tsv")
		} else {
			File("backend/data/ingredients/names.tsv")
		}
	}

	fun loadPins(file: File = defaultNamesTsvFile()): IngredientNamePins =
		if (!file.isFile) {
			IngredientNamePins.EMPTY
		} else {
			parsePinsFromNamesTsv(file.readText())
		}

	fun loadDefaultFoodPins(): Map<String, NutritionFoodPin> =
		loadPins().foodPinsByLookupName

	fun loadDefaultPantryPins(): Map<String, String> =
		loadPins().pantryPinsByLookupName

	fun parsePinsFromNamesTsv(content: String): IngredientNamePins {
		val foodPins = linkedMapOf<String, NutritionFoodPin>()
		val pantryPins = linkedMapOf<String, String>()

		content.lineSequence()
			.drop(1)
			.filter { line -> line.isNotBlank() }
			.forEach { line ->
				val parts = line.split(TSV_SEPARATOR)
				if (parts.size < MIN_PIN_COLUMNS) {
					return@forEach
				}
				val lookupName = parts[COL_LOOKUP_NAME].trim()
				val status = parts[COL_STATUS].trim()
				if (lookupName.isEmpty() || status != STATUS_PINNED) {
					return@forEach
				}
				val pantryItem = parts[COL_PANTRY_ITEM].trim()
				if (pantryItem.isNotEmpty()) {
					pantryPins[lookupName] = pantryItem
				}
				val fdcSource = parts[COL_FDC_SOURCE].trim()
				val fdcId = parts[COL_FDC_ID].trim()
				if (fdcSource.isNotEmpty() && fdcId.isNotEmpty()) {
					foodPins[lookupName] = NutritionFoodPin(
						lookupName = lookupName,
						fdcSource = fdcSource,
						fdcId = fdcId,
					)
				}
			}

		return IngredientNamePins(
			foodPinsByLookupName = foodPins,
			pantryPinsByLookupName = pantryPins,
		)
	}
}
