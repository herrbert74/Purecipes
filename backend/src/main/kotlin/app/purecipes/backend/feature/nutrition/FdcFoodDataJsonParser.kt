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

	fun parse(file: File): FdcFoodDataParseResult {
		val root = json.parseToJsonElement(file.readText()).jsonObject
		val dataset = FdcFoodDataset.entries.firstOrNull { entry -> root[entry.jsonRootKey] != null }
			?: error("Unrecognized FDC JSON file. Expected FoundationFoods or SRLegacyFoods root array.")
		val foods = root[dataset.jsonRootKey]?.jsonArray?.mapNotNull { element ->
			element.toFoundationFoodOrNull(sourceName = dataset.sourceName)
		}.orEmpty()
		return FdcFoodDataParseResult(dataset = dataset, foods = foods)
	}

	private fun JsonElement.toFoundationFoodOrNull(sourceName: String): FdcFoundationFood? {
		val foodObject = jsonObjectOrNull() ?: return null
		val fdcId = foodObject.longValue("fdcId")
		val description = foodObject.stringValue("description")?.takeIf { it.isNotBlank() }
		if (fdcId == null || description == null) {
			return null
		}
		val nutrients = foodObject["foodNutrients"]?.jsonArray?.mapNotNull { it.toNutrientAmountOrNull() }.orEmpty()
		val portions = foodObject["foodPortions"]?.jsonArray?.mapNotNull { it.toFoodPortionOrNull() }.orEmpty()
		return FdcFoundationFood(
			sourceName = sourceName,
			fdcId = fdcId,
			description = description,
			nutrients = nutrients,
			portions = portions,
		)
	}

	private fun JsonElement.toNutrientAmountOrNull(): FdcNutrientAmount? {
		val nutrientObject = jsonObjectOrNull() ?: return null
		val nutrientId = nutrientObject["nutrient"]?.jsonObjectOrNull()?.intValue("id")
		val amount = nutrientObject.decimalValue("amount")
		if (nutrientId == null || amount == null) {
			return null
		}
		return FdcNutrientAmount(nutrientId = nutrientId, amount = amount)
	}

	private fun JsonElement.toFoodPortionOrNull(): FdcFoodPortion? {
		val portionObject = jsonObjectOrNull() ?: return null
		val measureName = portionObject["measureUnit"]?.jsonObjectOrNull()?.stringValue("name")
		val gramsPerMeasure = portionObject.decimalValue("gramWeight")
		if (measureName == null || gramsPerMeasure == null) {
			return null
		}
		return FdcFoodPortion(
			measureName = NutritionMeasureNames.normalize(measureName),
			gramsPerMeasure = gramsPerMeasure,
		)
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
}
