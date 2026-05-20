package app.purecipes.backend.feature.nutrition

internal object NutritionMeasureNames {
	fun normalize(rawMeasureName: String): String =
		when (rawMeasureName.lowercase()) {
			"tablespoon" -> "tbsp"
			"teaspoon" -> "tsp"
			"each" -> "piece"
			else -> rawMeasureName.lowercase()
		}
}
