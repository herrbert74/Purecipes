package app.purecipes.shared.domain.ingredient

import app.purecipes.shared.domain.model.MeasurementSystem

object IngredientUnitTokens {

	private val metricTokens = setOf(
		"g",
		"gram",
		"grams",
		"kg",
		"kilogram",
		"kilograms",
		"mg",
		"milligram",
		"milligrams",
		"ml",
		"milliliter",
		"millilitre",
		"milliliters",
		"millilitres",
		"l",
		"liter",
		"litre",
		"liters",
		"litres",
	)

	private val imperialTokens = setOf(
		"oz",
		"ounce",
		"ounces",
		"lb",
		"lbs",
		"pound",
		"pounds",
	)

	private val commonTokens = setOf(
		"bunch",
		"can",
		"cans",
		"clove",
		"cloves",
		"cup",
		"cups",
		"egg",
		"eggs",
		"pack",
		"packet",
		"package",
		"packages",
		"pinch",
		"pinches",
		"piece",
		"pieces",
		"slice",
		"slices",
		"tablespoon",
		"tablespoons",
		"tbsp",
		"teaspoon",
		"teaspoons",
		"tsp",
	)

	val allTokens: Set<String> = metricTokens + imperialTokens + commonTokens

	private val metricSuggestions = listOf("mg", "g", "kg", "ml", "l")

	private val imperialSuggestions = listOf("tsp", "tbsp", "cup", "oz", "lb")

	private val commonSuggestions = listOf(
		"pinch",
		"clove",
		"can",
		"slice",
		"piece",
		"bunch",
		"pack",
	)

	fun isKnownUnit(token: String): Boolean = token.lowercase() in allTokens

	fun canonicalUnit(token: String): String {
		val trimmed = token.trim()
		if (trimmed.isEmpty()) {
			return ""
		}
		return when (trimmed.lowercase().trimEnd('.')) {
			"g", "gram", "grams" -> "g"
			"kg", "kilogram", "kilograms" -> "kg"
			"mg", "milligram", "milligrams" -> "mg"
			"ml", "milliliter", "millilitre", "milliliters", "millilitres" -> "ml"
			"l", "liter", "litre", "liters", "litres" -> "l"
			"tsp", "teaspoon", "teaspoons" -> "tsp"
			"tbsp", "tablespoon", "tablespoons" -> "tbsp"
			"cup", "cups" -> "cup"
			"oz", "ounce", "ounces" -> "oz"
			"lb", "lbs", "pound", "pounds" -> "lb"
			"pinch", "pinches" -> "pinch"
			"clove", "cloves" -> "clove"
			"can", "cans" -> "can"
			"slice", "slices" -> "slice"
			"piece", "pieces" -> "piece"
			"egg", "eggs" -> "egg"
			"bunch", "bunches" -> "bunch"
			"pack", "packet", "package", "packages" -> "pack"
			"tablespoon", "tablespoons" -> "tbsp"
			"teaspoon", "teaspoons" -> "tsp"
			else -> trimmed
		}
	}

	fun suggestedUnits(preferredSystem: MeasurementSystem): List<String> = when (preferredSystem) {
		MeasurementSystem.METRIC -> metricSuggestions + commonSuggestions
		MeasurementSystem.IMPERIAL -> imperialSuggestions + commonSuggestions
		MeasurementSystem.MIXED -> metricSuggestions + imperialSuggestions + commonSuggestions
	}
}
