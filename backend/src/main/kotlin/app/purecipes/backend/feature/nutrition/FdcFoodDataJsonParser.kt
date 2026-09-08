package app.purecipes.backend.feature.nutrition

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import java.math.BigDecimal

internal object FdcFoodDataJsonParser {

	private val json = Json {
		ignoreUnknownKeys = true
	}

	fun peekDataset(file: File): FdcFoodDataset {
		val header = file.inputStream().use { input ->
			val bytes = ByteArray(HEADER_BYTES)
			val read = input.read(bytes)
			String(bytes, 0, read.coerceAtLeast(0), Charsets.UTF_8)
		}
		return datasetFromHeader(header)
	}

	fun parse(file: File, neededQueries: Set<String>? = null): FdcFoodDataParseResult {
		val dataset = peekDataset(file)
		return when {
			dataset == FdcFoodDataset.BRANDED ->
				parseStreaming(
					file = file,
					dataset = dataset,
					neededQueries = neededQueries ?: emptySet(),
				)

			neededQueries != null ->
				parseStreaming(
					file = file,
					dataset = dataset,
					neededQueries = neededQueries,
				)

			else -> parseBuffered(file = file, dataset = dataset)
		}
	}

	private fun parseBuffered(file: File, dataset: FdcFoodDataset): FdcFoodDataParseResult {
		val root = json.parseToJsonElement(file.readText()).jsonObject
		val foods = root[dataset.jsonRootKey]?.jsonArray?.mapNotNull { element ->
			element.toFoundationFoodOrNull(sourceName = dataset.sourceName)
		}.orEmpty()
		return FdcFoodDataParseResult(dataset = dataset, foods = foods)
	}

	private fun parseStreaming(
		file: File,
		dataset: FdcFoodDataset,
		neededQueries: Set<String>?,
	): FdcFoodDataParseResult {
		val accumulator = FdcNeededFoodAccumulator(neededQueries)
		val foodsScanned = FdcJsonRootArrayReader.forEachObject(file, dataset.jsonRootKey) { objectText ->
			val food = json.parseToJsonElement(objectText).toFoundationFoodOrNull(sourceName = dataset.sourceName)
			if (food != null) {
				accumulator.consider(food)
			}
		}
		return FdcFoodDataParseResult(
			dataset = dataset,
			foods = accumulator.selectedFoods(),
			foodsScanned = foodsScanned,
			neededNameMatches = accumulator.neededNameMatches(),
		)
	}

	private fun datasetFromHeader(header: String): FdcFoodDataset =
		FdcFoodDataset.entries.firstOrNull { dataset -> header.contains("\"${dataset.jsonRootKey}\"") }
			?: error(
				"Unrecognized FDC JSON file. Expected FoundationFoods, SRLegacyFoods, " +
					"SurveyFoods, or BrandedFoods root array.",
			)

	private fun JsonElement.toFoundationFoodOrNull(sourceName: String): FdcFoundationFood? {
		val foodObject = jsonObjectOrNull() ?: return null
		val fdcId = foodObject.longValue("fdcId")
		val description = foodObject.stringValue("description")?.takeIf { it.isNotBlank() }
		return if (fdcId == null || description == null) {
			null
		} else {
			val nutrients = foodObject["foodNutrients"]?.jsonArray?.mapNotNull { it.toNutrientAmountOrNull() }.orEmpty()
			val portions = foodObject.toPortions()
			FdcFoundationFood(
				sourceName = sourceName,
				fdcId = fdcId,
				description = description,
				nutrients = nutrients,
				portions = portions,
			)
		}
	}

	private fun JsonObject.toPortions(): List<FdcFoodPortion> {
		val imported = this["foodPortions"]?.jsonArray
			?.mapNotNull { element -> element.toRankedFoodPortionOrNull() }
			.orEmpty()
			.sortedByDescending { ranked -> ranked.preference }
			.distinctBy { ranked -> ranked.portion.measureName }
			.map { ranked -> ranked.portion }
			.toMutableList()
		val brandedPortion = NutritionMeasureNames.brandedHouseholdPortion(
			servingSize = decimalValue("servingSize"),
			servingSizeUnit = stringValue("servingSizeUnit"),
			householdServingFullText = stringValue("householdServingFullText"),
		)
		if (brandedPortion != null && imported.none { portion -> portion.measureName == brandedPortion.measureName }) {
			imported += brandedPortion
		}
		return imported
	}

	private fun JsonElement.toNutrientAmountOrNull(): FdcNutrientAmount? {
		val nutrientObject = jsonObjectOrNull() ?: return null
		val nutrientId = nutrientObject["nutrient"]?.jsonObjectOrNull()?.intValue("id")
		val amount = nutrientObject.decimalValue("amount")
		return if (nutrientId == null || amount == null) {
			null
		} else {
			FdcNutrientAmount(nutrientId = nutrientId, amount = amount)
		}
	}

	private fun JsonElement.toRankedFoodPortionOrNull(): RankedFoodPortion? {
		val portionObject = jsonObjectOrNull()
		if (portionObject == null) {
			return null
		}
		val measureUnitName = portionObject["measureUnit"]?.jsonObjectOrNull()?.stringValue("name")
		val modifier = portionObject.stringValue("modifier")
		val gramWeight = portionObject.decimalValue("gramWeight")
		val measureName = NutritionMeasureNames.resolveImportedName(measureUnitName, modifier)
		val surveyPortion = if (measureName == null) {
			NutritionMeasureNames.surveyPortionFromDescription(
				portionDescription = portionObject.stringValue("portionDescription"),
				gramWeight = gramWeight,
			)
		} else {
			null
		}
		val portion = when {
			measureName != null && gramWeight != null ->
				FdcFoodPortion(
					measureName = measureName,
					gramsPerMeasure = NutritionMeasureNames.gramsPerSingleMeasure(
						gramWeight = gramWeight,
						amount = portionObject.decimalValue("amount"),
					),
				)

			surveyPortion != null -> surveyPortion
			else -> null
		}
		return portion?.let { resolved ->
			RankedFoodPortion(
				preference = NutritionMeasureNames.pieceImportPreference(modifier)
					.takeIf { measureName != null }
					?: NutritionMeasureNames.pieceImportPreference(resolved.measureName),
				portion = resolved,
			)
		}
	}

	private fun JsonElement.jsonObjectOrNull(): JsonObject? =
		when (this) {
			is JsonObject -> this
			else -> null
		}

	private fun JsonObject.longValue(key: String): Long? =
		this[key]?.jsonPrimitive?.content?.toLongOrNull()

	private fun JsonObject.intValue(key: String): Int? =
		this[key]?.jsonPrimitive?.content?.toIntOrNull()

	private fun JsonObject.stringValue(key: String): String? =
		this[key]?.jsonPrimitive?.content

	private fun JsonObject.decimalValue(key: String): BigDecimal? =
		this[key]?.jsonPrimitive?.content?.toBigDecimalOrNull()

	private const val HEADER_BYTES = 512
}

private data class RankedFoodPortion(
	val preference: Int,
	val portion: FdcFoodPortion,
)
