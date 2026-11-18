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

	fun compileMatcher(ingredientNames: Collection<String>): CompiledIngredientMatcher {
		val root = PrefixTrieNode()
		var hasAnyVariant = false
		val seenVariants = HashSet<String>()
		ingredientNames.forEach { ingredientName ->
			ingredientVariants(ingredientName).forEach { variant ->
				if (variant.isNotBlank() && seenVariants.add(variant)) {
					val words = variant.split(' ').filter(String::isNotBlank)
					if (words.isNotEmpty()) {
						hasAnyVariant = true
						insertVariantIntoTrie(root, words)
					}
				}
			}
		}
		return CompiledIngredientMatcher(root = root, isEmpty = !hasAnyVariant)
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

	fun normalizeIngredientName(value: String): String = normalizeAlphanumericTokens(value)

	fun normalizeIngredientText(value: String): String = normalizeAlphanumericTokens(value)

	private fun normalizeAlphanumericTokens(value: String): String = buildString(value.length) {
		var pendingSpace = false
		for (rawChar in value) {
			val mapped = mapNormalizedChar(rawChar.lowercaseChar())
			if (mapped in 'a'..'z' || mapped in '0'..'9') {
				if (pendingSpace && isNotEmpty()) {
					append(' ')
				}
				pendingSpace = false
				append(mapped)
			} else {
				pendingSpace = true
			}
		}
	}

	private fun mapNormalizedChar(char: Char): Char = when (char) {
		'á', 'à', 'ä', 'â', 'ã', 'å', 'ā' -> 'a'
		'é', 'è', 'ë', 'ê', 'ē' -> 'e'
		'í', 'ì', 'ï', 'î', 'ī' -> 'i'
		'ó', 'ò', 'ö', 'ô', 'õ', 'ø', 'ō' -> 'o'
		'ú', 'ù', 'ü', 'û', 'ū' -> 'u'
		'ñ' -> 'n'
		'ç' -> 'c'
		'ý', 'ÿ' -> 'y'
		else -> char
	}

	private fun insertVariantIntoTrie(root: PrefixTrieNode, words: List<String>) {
		var node = root
		for (char in words[0]) {
			node = node.children.getOrPut(char) { PrefixTrieNode() }
		}
		if (words.size == 1) {
			node.matchesSingleWord = true
		} else {
			val tails = node.multiWordTails ?: mutableListOf<List<String>>().also { node.multiWordTails = it }
			tails.add(words.drop(1))
		}
	}

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
	): Boolean = when {
		normalizedPhrase.isBlank() -> false
		containsExactIngredientPhrase(normalizedIngredientLine, normalizedPhrase) -> true
		else -> {
			val ingredientWords = normalizedIngredientLine.split(' ').filter(String::isNotBlank)
			val phraseWords = normalizedPhrase.split(' ').filter(String::isNotBlank)
			ingredientWords.size >= phraseWords.size && phraseWords.isNotEmpty() &&
				ingredientWords.windowed(size = phraseWords.size, step = 1).any { window ->
					window.zip(phraseWords).all { (ingredientWord, phraseWord) ->
						wordMatchesSearchPhraseWord(ingredientWord, phraseWord)
					}
				}
		}
	}

	private fun wordMatchesSearchPhraseWord(ingredientWord: String, phraseWord: String): Boolean {
		if (ingredientWord.startsWith(phraseWord)) {
			return true
		}
		val singularWord = singularizeWord(ingredientWord)
		return singularWord.startsWith(phraseWord)
	}

	internal fun singularizeWord(word: String): String = when {
		word.endsWith("ies") && word.length > IES_SUFFIX_LENGTH ->
			word.dropLast(IES_SUFFIX_LENGTH) + "y"

		word.endsWith("ses") && word.length > IES_SUFFIX_LENGTH ->
			word.dropLast(SES_DROP_LENGTH)

		word.endsWith("oes") && word.length > IES_SUFFIX_LENGTH ->
			word.dropLast(IES_SUFFIX_LENGTH) + "o"

		word.endsWith("ves") && word.length > IES_SUFFIX_LENGTH ->
			word.dropLast(IES_SUFFIX_LENGTH) + "f"

		word.endsWith("s") && !word.endsWith("ss") && word.length > S_SUFFIX_LENGTH ->
			word.dropLast(S_SUFFIX_LENGTH)

		else -> word
	}

	private fun singularizeLastWord(value: String): String {
		val words = value.split(' ').filter(String::isNotBlank)
		if (words.isEmpty()) {
			return value
		}
		return (words.dropLast(1) + singularizeWord(words.last())).joinToString(separator = " ")
	}
}
