package app.purecipes.backend.feature.search

import app.purecipes.backend.feature.nutrition.NutritionNameNormalizer
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
		"toppings,",
		"topping,",
	)

	private val ignorableServingClauses = listOf(
		"(to serve)",
		"(for serving",
		", for serving",
		", for garnish",
		", for topping",
		", for griddle",
		", for shaking",
		", for frying",
		", enough for",
		"enough for deep-frying",
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
		"bone-in",
		"square",
		"very large",
		"hard boiled",
		"red yellow",
		"total",
		"warm",
		"two",
		"one",
		"quart",
		"quarts",
		"couple of",
		"cut into",
		"store-bought",
		"store bought",
	)

	private val orphanModifierTokens = setOf(
		"bone",
		"bone-in",
		"boiled",
		"bought",
		"each",
		"fresh",
		"frozen",
		"hard",
		"hot",
		"in",
		"kosher",
		"larger",
		"one",
		"peel",
		"red",
		"sandwich",
		"soft",
		"splash",
		"square",
		"store",
		"topping",
		"toppings",
		"total",
		"two",
		"vegetable",
		"very",
		"warm",
		"white",
		"whole",
		"yellow",
	)

	private val sizeOnlyTokens = setOf(
		"extra",
		"extra-large",
		"generous",
		"heaped",
		"heaping",
		"jumbo",
		"large",
		"larger",
		"level",
		"medium",
		"packed",
		"rounded",
		"scant",
		"small",
		"smaller",
		"thick",
		"thin",
		"very",
	)

	private val measureOnlyTokens = setOf(
		"bottle",
		"bottles",
		"can",
		"cans",
		"cup",
		"cups",
		"g",
		"gram",
		"grams",
		"head",
		"heads",
		"jar",
		"jars",
		"kg",
		"l",
		"lb",
		"lbs",
		"liter",
		"litre",
		"liters",
		"litres",
		"ml",
		"ounce",
		"ounces",
		"oz",
		"pack",
		"package",
		"packages",
		"packs",
		"pound",
		"pounds",
		"quart",
		"quarts",
		"tablespoon",
		"tablespoons",
		"tbsp",
		"teaspoon",
		"teaspoons",
		"tin",
		"tins",
		"tsp",
	)

	private val articleTokens = setOf(
		"a",
		"an",
	)

	private val ignorableNonFoodPhrases = setOf(
		"bread butter table",
		"edible gold dust",
		"edible gold glitter",
		"food grade lye",
		"food grade lye crystals",
		"sausage casing",
		"sausage casings",
		"something crunchy",
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

	private val leadingQuantityPattern = Regex(
		"""^(?:\d+\s+\d+\s*/\s*\d+|\d+\s*/\s*\d+|""" +
			"""\d+\s*[–-]\s*\d+(?:\.\d+)?|\d+(?:\.\d+)?|\.\d+|""" +
			"""a|an|one|two|three|four|five|six|eight)\b""",
		RegexOption.IGNORE_CASE,
	)

	private val leadingArticlePattern = Regex(
		"""^(?:a|an)\b""",
		RegexOption.IGNORE_CASE,
	)

	private val parentheticalPattern = Regex("""\([^)]*\)""")

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
		val orphanOnly = isOrphanModifierOnlyLine(lower)
		val quantityOnly = isQuantityOnlyMeasureLine(lower)
		val nonFoodLike = isIgnorableNonFoodLine(lower, normalized)

		return headingLike || equipmentLike || equipmentOnly || orphanOnly || quantityOnly || nonFoodLike
	}

	private fun isIgnorableNonFoodLine(lower: String, normalized: String): Boolean {
		val lookup = NutritionNameNormalizer.forLookup(lower)
		if (normalized in ignorableNonFoodPhrases ||
			lower in ignorableNonFoodPhrases ||
			lookup in ignorableNonFoodPhrases
		) {
			return true
		}
		val tokens = tokensAfterQuantityAndArticles(lower)
			.dropWhile { token ->
				token in sizeOnlyTokens || token in measureOnlyTokens || token in articleTokens
			}
		val afterQuantity = tokens.joinToString(" ")
		return afterQuantity in ignorableNonFoodPhrases ||
			NutritionNameNormalizer.forLookup(afterQuantity) in ignorableNonFoodPhrases
	}

	private fun isOrphanModifierOnlyLine(lower: String): Boolean {
		val tokens = tokensAfterQuantityAndArticles(lower)
		val withoutSizes = tokens.dropWhile { token -> token in sizeOnlyTokens || token in articleTokens }
		val withoutMeasures = withoutSizes.filterNot { token ->
			token in measureOnlyTokens || token in articleTokens
		}
		return when {
			withoutSizes.isEmpty() -> tokens.isNotEmpty()
			withoutMeasures.isEmpty() -> withoutSizes.isNotEmpty()
			else -> withoutMeasures.all { token -> token in orphanModifierTokens || token in sizeOnlyTokens }
		}
	}

	private fun isQuantityOnlyMeasureLine(lower: String): Boolean {
		val tokens = tokensAfterQuantityAndArticles(lower)
		val withoutSizes = tokens.filterNot { token ->
			token in sizeOnlyTokens || token in articleTokens
		}
		return withoutSizes.isNotEmpty() && withoutSizes.all { token -> token in measureOnlyTokens }
	}

	private fun tokensAfterQuantityAndArticles(lower: String): List<String> {
		val withoutParens = parentheticalPattern.replace(lower, " ")
		var rest = leadingQuantityPattern.replaceFirst(withoutParens.trim(), "").trim()
		rest = leadingArticlePattern.replaceFirst(rest, "").trim()
		rest = leadingQuantityPattern.replaceFirst(rest, "").trim()
		return IngredientNameMatching.normalizeIngredientText(rest)
			.split(' ')
			.filter { token -> token.isNotEmpty() }
			.filterNot { token -> token.all(Char::isDigit) || token in articleTokens }
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
