package app.purecipes.shared.domain.ingredient

object IngredientNameMatching {

	private const val IES_SUFFIX_LENGTH = 3
	private const val S_SUFFIX_LENGTH = 1
	private const val SES_DROP_LENGTH = 2

	private val aliasIndex: Map<String, Set<String>> = buildAliasIndex()

	fun ingredientVariants(ingredientName: String): Set<String> {
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

	fun catalogueAliasSiblings(
		ingredientName: String,
		catalogueItems: Collection<String>,
	): Set<String> {
		val variants = ingredientVariants(ingredientName)
		return catalogueItems.filterTo(mutableSetOf()) { catalogueItem ->
			catalogueItem != ingredientName && ingredientVariants(catalogueItem).any { it in variants }
		}
	}

	fun catalogueAliasSiblingsIndex(
		catalogueItems: Collection<String>,
	): Map<String, Set<String>> {
		val variantsByItem = catalogueItems.associateWith { ingredientName ->
			ingredientVariants(ingredientName)
		}
		return catalogueItems.associateWith { ingredientName ->
			val variants = variantsByItem.getValue(ingredientName)
			catalogueItems.filterTo(mutableSetOf()) { catalogueItem ->
				catalogueItem != ingredientName &&
					variantsByItem.getValue(catalogueItem).any { it in variants }
			}
		}
	}

	fun matchesAnyIngredient(
		ingredientLine: String,
		ingredientNames: Collection<String>,
	): Boolean {
		val normalizedIngredientLine = normalizeIngredientText(ingredientLine)
		if (normalizedIngredientLine.isBlank()) {
			return false
		}

		return ingredientNames
			.asSequence()
			.flatMap { ingredientName -> ingredientVariants(ingredientName).asSequence() }
			.any { variant -> containsSearchPhrase(normalizedIngredientLine, variant) }
	}

	fun isCoveredByAvailableIngredients(
		ingredientLine: String,
		availableIngredients: Collection<String>,
	): Boolean {
		val normalizedIngredientLine = normalizeIngredientText(ingredientLine)
		val matchedAvailableIngredient = availableIngredients.any { ingredientName ->
			val normalizedIngredientName = normalizeIngredientName(ingredientName)
			containsSearchPhrase(normalizedIngredientLine, normalizedIngredientName) ||
				ingredientVariants(ingredientName).any { variant ->
					containsSearchPhrase(normalizedIngredientLine, variant)
				}
		}

		return normalizedIngredientLine.isBlank() || matchedAvailableIngredient
	}

	fun normalizeIngredientName(value: String): String =
		normalizeWhitespace(value.lowercase().replace(Regex("[^a-z0-9]+"), " "))

	fun normalizeIngredientText(value: String): String =
		normalizeWhitespace(value.lowercase().replace(Regex("[^a-z0-9]+"), " "))

	private fun buildAliasIndex(): Map<String, Set<String>> = buildMap {
		IngredientAliasGroups.groups.forEach { group ->
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
