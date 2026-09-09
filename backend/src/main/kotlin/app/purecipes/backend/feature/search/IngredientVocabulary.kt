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
		"serve with",
		"to garnish",
		"serving suggestion",
		"suggestion",
		"other suggested",
		"suggested",
		"preheat ",
		"can be found",
		"this all purpose",
	)

	private val ignorableServingClauses = listOf(
		"(to serve)",
		"(for serving",
		", for serving",
		", for garnish",
		", for topping",
	)

	private val ignorableInstructionClauses = listOf(
		"specialty asian market",
		"specialty asian store",
		"can be used in recipes that call",
		"prepared through step",
		"fillings of your choice",
		"of your choosing",
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
		"topping",
		"kosher",
		"vegetable",
		"red",
		"white",
		"yellow",
		"whole",
		"soft",
		"frozen",
		"peel",
		"sandwich",
		"bone",
		"square",
	)

	private val ignorableEquipmentKeywords = listOf(
		"baking sheet",
		"biscuit cutter",
		"blender",
		"board",
		"bowl",
		"bundt",
		"casserole dish",
		"cling film",
		"colander",
		"cutter",
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
		"removable bottom",
		"saucepan",
		"skewer",
		"slotted spoon",
		"spoon",
		"springform",
		"star tip",
		"toothpick",
		"whisk",
	)

	private val digitSensitiveEquipmentKeywords = setOf(
		"board",
		"bowl",
		"cutter",
		"knife",
		"pan",
		"pot",
		"spoon",
		"whisk",
	)

	private val equipmentLineFillerTokens = setOf(
		"a",
		"an",
		"and",
		"cup",
		"cups",
		"diameter",
		"inch",
		"inches",
		"individual",
		"large",
		"medium",
		"of",
		"or",
		"preferably",
		"small",
		"the",
		"with",
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
				lower.contains("recipe follows") ||
				ignorableServingClauses.any { lower.contains(it) } ||
				ignorableInstructionClauses.any { lower.contains(it) }
		val normalized = IngredientNameMatching.normalizeIngredientText(trimmed)
		val normalizedTokens = normalized.split(' ').filter { token -> token.isNotEmpty() }.toSet()
		val equipmentLike = ignorableEquipmentKeywords.any { keyword ->
			if (!equipmentKeywordMatches(normalized, normalizedTokens, keyword)) {
				return@any false
			}
			!hasDigit || keyword !in digitSensitiveEquipmentKeywords
		}
		val equipmentOnly = isEquipmentOnlyLine(normalizedTokens)

		return headingLike || equipmentLike || equipmentOnly
	}

	private fun isEquipmentOnlyLine(normalizedTokens: Set<String>): Boolean {
		val equipmentRoots = ignorableEquipmentKeywords
			.flatMap { keyword ->
				keyword.split(' ').filter { part -> part.isNotEmpty() }.flatMap { part ->
					listOf(part, "${part}s", "${part}es")
				}
			}
			.toSet()
		val remaining = normalizedTokens.filterNot { token ->
			token.all(Char::isDigit) || token in equipmentLineFillerTokens
		}
		return remaining.isNotEmpty() && remaining.all { token -> token in equipmentRoots }
	}

	private fun equipmentKeywordMatches(
		normalized: String,
		normalizedTokens: Set<String>,
		keyword: String,
	): Boolean {
		val parts = keyword.split(' ').filter { part -> part.isNotEmpty() }
		if (parts.size == 1) {
			val root = parts[0]
			return root in normalizedTokens ||
				"${root}s" in normalizedTokens ||
				"${root}es" in normalizedTokens
		}
		val padded = " $normalized "
		val last = parts.last()
		val lastForms = listOf(last, "${last}s", "${last}es")
		return lastForms.any { form ->
			val candidate = (parts.dropLast(1) + form).joinToString(" ")
			padded.contains(" $candidate ")
		}
	}
}
