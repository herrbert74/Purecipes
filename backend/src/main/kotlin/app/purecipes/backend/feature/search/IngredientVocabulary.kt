package app.purecipes.backend.feature.search

import app.purecipes.shared.domain.ingredient.IngredientNameMatching

internal object IngredientVocabulary {

	private val ignorableLinePrefixFilters = listOf(
		"for ",
		"special equipment",
		"equipment list",
		"in the box",
		"from your cupboard",
		"shopping list",
		"optional",
		"serve with",
		"to garnish",
	)

	private val ignorableLineExactFilters = setOf(
		"for",
		"dough",
		"filling",
		"garnish",
		"garnishes",
		"marinade",
		"sauce",
		"salad",
		"toppings",
	)

	private val ignorableEquipmentKeywords = listOf(
		"baking sheet",
		"blender",
		"board",
		"bowl",
		"cutter",
		"colander",
		"food processor",
		"grill pan",
		"instant pot",
		"kitchen paper",
		"knife",
		"mandoline",
		"microplane",
		"pan",
		"pastry bag",
		"pot",
		"pressure cooker",
		"saucepan",
		"sheet",
		"skewer",
		"slotted spoon",
		"spoon",
		"toothpick",
		"whisk",
	)

	val defaultPantryIngredients: Set<String> = setOf(
		"salt",
		"water",
		"vegetable oil",
		"baking soda",
		"bicarbonate of soda",
	)

	fun isPantryIngredient(ingredientLine: String): Boolean =
		IngredientNameMatching.matchesAnyIngredient(
			ingredientLine = ingredientLine,
			ingredientNames = defaultPantryIngredients,
		)

	fun isCoveredByAvailableIngredients(
		ingredientLine: String,
		availableIngredients: Collection<String>,
	): Boolean {
		return isIgnorableIngredientLine(ingredientLine) ||
			isPantryIngredient(ingredientLine) ||
			IngredientNameMatching.isCoveredByAvailableIngredients(
				ingredientLine = ingredientLine,
				availableIngredients = availableIngredients,
			)
	}

	fun matchesAnyIngredient(
		ingredientLine: String,
		ingredientNames: Collection<String>,
	): Boolean {
		if (isIgnorableIngredientLine(ingredientLine)) {
			return false
		}

		return IngredientNameMatching.matchesAnyIngredient(
			ingredientLine = ingredientLine,
			ingredientNames = ingredientNames,
		)
	}

	fun isIgnorableIngredientLine(ingredientLine: String): Boolean {
		val trimmed = ingredientLine.trim().removePrefix("-").removePrefix("*").trim()
		if (trimmed.isBlank()) {
			return true
		}

		val lower = trimmed.lowercase()
		val hasDigit = lower.any(Char::isDigit)
		val headingLike =
			lower.endsWith(':') ||
			ignorableLinePrefixFilters.any { lower.startsWith(it) } ||
			ignorableLineExactFilters.contains(lower) ||
			lower.contains("recipe follows")
		val normalized = IngredientNameMatching.normalizeIngredientText(trimmed)
		val equipmentLike = !hasDigit && ignorableEquipmentKeywords.any { keyword -> normalized.contains(keyword) }

		return headingLike || equipmentLike
	}
}
