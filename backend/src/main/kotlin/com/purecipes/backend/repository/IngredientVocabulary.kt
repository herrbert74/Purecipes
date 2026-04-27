package com.purecipes.backend.repository

internal object IngredientVocabulary {

	private const val IES_SUFFIX_LENGTH = 3
	private const val S_SUFFIX_LENGTH = 1
	private const val SES_DROP_LENGTH = 2

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

	private val aliasGroups: List<Set<String>> = listOf(
		setOf("chile", "chili", "chilli"),
		setOf("cilantro", "coriander", "corainder"),
		setOf("yogurt", "yoghurt", "jogurt", "greek yogurt", "greek yoghurt"),
		setOf("creme fraiche", "crème fraiche", "crème fraîche"),
		setOf("petit pois", "pea", "peas"),
		setOf("bay leaf", "bay leaves", "bayleaves"),
		setOf("cornstarch", "cornflour"),
		setOf("corn", "sweetcorn"),
		setOf("ice", "water"),
		setOf("oil", "vegetable oil", "cooking spray"),
		setOf("prawn", "prawns", "shrimp"),
		setOf("scallion", "scallions", "spring onion", "green onion"),
		setOf("courgette", "zucchini"),
		setOf("aubergine", "eggplant"),
		setOf("arugula", "rocket"),
	)

	private val aliasIndex: Map<String, Set<String>> = buildAliasIndex()

	fun isPantryIngredient(ingredientLine: String): Boolean =
		matchesAnyIngredient(ingredientLine = ingredientLine, ingredientNames = defaultPantryIngredients)

	fun isCoveredByAvailableIngredients(
		ingredientLine: String,
		availableIngredients: Collection<String>,
	): Boolean {
		val normalizedIngredientLine = normalizeIngredientText(ingredientLine)
		val matchedAvailableIngredient = availableIngredients.any { ingredientName ->
			val normalizedIngredientName = normalizeIngredientName(ingredientName)
			containsSearchPhrase(normalizedIngredientLine, normalizedIngredientName) ||
				ingredientVariants(ingredientName).any { variant ->
					containsExactIngredientPhrase(normalizedIngredientLine, variant)
				}
		}

		return isIgnorableIngredientLine(ingredientLine) ||
			isPantryIngredient(ingredientLine) ||
			normalizedIngredientLine.isBlank() ||
			matchedAvailableIngredient
	}

	fun matchesAnyIngredient(
		ingredientLine: String,
		ingredientNames: Collection<String>,
	): Boolean {
		if (isIgnorableIngredientLine(ingredientLine)) {
			return false
		}

		val normalizedIngredientLine = normalizeIngredientText(ingredientLine)
		if (normalizedIngredientLine.isBlank()) {
			return false
		}

		return ingredientNames
			.asSequence()
			.flatMap { ingredientName -> ingredientVariants(ingredientName).asSequence() }
			.any { variant -> containsSearchPhrase(normalizedIngredientLine, variant) }
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
		val normalized = normalizeIngredientText(trimmed)
		val equipmentLike = !hasDigit && ignorableEquipmentKeywords.any { keyword -> normalized.contains(keyword) }

		return headingLike || equipmentLike
	}

	private fun ingredientVariants(ingredientName: String): Set<String> {
		val normalizedName = normalizeIngredientName(ingredientName)
		if (normalizedName.isBlank()) {
			return emptySet()
		}

		return buildSet {
			add(normalizedName)
			add(singularizeLastWord(normalizedName))
			aliasIndex[normalizedName].orEmpty().forEach { alias ->
				add(alias)
				add(singularizeLastWord(alias))
			}
		}
	}

	private fun buildAliasIndex(): Map<String, Set<String>> = buildMap {
		aliasGroups.forEach { group ->
			val normalizedGroup = group.flatMap { alias ->
				listOf(normalizeIngredientName(alias), singularizeLastWord(normalizeIngredientName(alias)))
			}.filter(String::isNotBlank).toSet()

			normalizedGroup.forEach { alias ->
				put(alias, normalizedGroup)
			}
		}
	}

	private fun containsExactIngredientPhrase(
		normalizedIngredientLine: String,
		normalizedPhrase: String,
	): Boolean {
		if (normalizedPhrase.isBlank()) {
			return false
		}

		val paddedIngredientLine = " $normalizedIngredientLine "
		val paddedPhrase = " $normalizedPhrase "
		return paddedIngredientLine.contains(paddedPhrase)
	}

	private fun containsSearchPhrase(
		normalizedIngredientLine: String,
		normalizedPhrase: String,
	): Boolean {
		val exactMatch = containsExactIngredientPhrase(normalizedIngredientLine, normalizedPhrase)
		val ingredientWords = normalizedIngredientLine.split(' ').filter(String::isNotBlank)
		val phraseWords = normalizedPhrase.split(' ').filter(String::isNotBlank)
		val prefixMatch = ingredientWords.size >= phraseWords.size && phraseWords.isNotEmpty() &&
			ingredientWords.windowed(size = phraseWords.size, step = 1).any { window ->
			window.zip(phraseWords).all { (ingredientWord, phraseWord) ->
				ingredientWord.startsWith(phraseWord)
			}
		}

		return normalizedPhrase.isNotBlank() && (exactMatch || prefixMatch)
	}

	private fun normalizeIngredientName(value: String): String =
		normalizeWhitespace(value.lowercase().replace(Regex("[^a-z0-9]+"), " "))

	private fun normalizeIngredientText(value: String): String =
		normalizeWhitespace(value.lowercase().replace(Regex("[^a-z0-9]+"), " "))

	private fun singularizeLastWord(value: String): String {
		val words = value.split(' ').filter(String::isNotBlank)
		if (words.isEmpty()) {
			return value
		}

		val lastWord = words.last()
		val singularLastWord = when {
			lastWord.endsWith("ies") && lastWord.length > IES_SUFFIX_LENGTH ->
				lastWord.dropLast(IES_SUFFIX_LENGTH) + "y"
			lastWord.endsWith("ses") && lastWord.length > IES_SUFFIX_LENGTH ->
				lastWord.dropLast(SES_DROP_LENGTH)
			lastWord.endsWith("oes") && lastWord.length > IES_SUFFIX_LENGTH ->
				lastWord.dropLast(IES_SUFFIX_LENGTH) + "o"
			lastWord.endsWith("ves") && lastWord.length > IES_SUFFIX_LENGTH ->
				lastWord.dropLast(IES_SUFFIX_LENGTH) + "f"
			lastWord.endsWith("s") && !lastWord.endsWith("ss") && lastWord.length > S_SUFFIX_LENGTH ->
				lastWord.dropLast(S_SUFFIX_LENGTH)
			else -> lastWord
		}

		return (words.dropLast(1) + singularLastWord).joinToString(separator = " ")
	}

	private fun normalizeWhitespace(value: String): String =
		value.trim().replace(Regex("\\s+"), " ")
}
