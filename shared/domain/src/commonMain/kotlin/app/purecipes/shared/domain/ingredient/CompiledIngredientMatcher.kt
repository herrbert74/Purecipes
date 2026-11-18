package app.purecipes.shared.domain.ingredient

class CompiledIngredientMatcher internal constructor(
	private val root: PrefixTrieNode,
	val isEmpty: Boolean,
) {

	fun matchesIngredientLine(ingredientLine: String): Boolean =
		!isEmpty && matchesNormalizedLine(IngredientNameMatching.normalizeIngredientText(ingredientLine))

	fun isCoveredByAvailable(ingredientLine: String): Boolean {
		val normalized = IngredientNameMatching.normalizeIngredientText(ingredientLine)
		return normalized.isBlank() || (!isEmpty && matchesNormalizedLine(normalized))
	}

	fun matchesNormalizedLine(normalizedIngredientLine: String): Boolean {
		val ingredientWords = if (isEmpty || normalizedIngredientLine.isBlank()) {
			emptyList()
		} else {
			normalizedIngredientLine.split(' ').filter(String::isNotBlank)
		}
		if (ingredientWords.isEmpty()) {
			return false
		}
		val singularWords = ingredientWords.map(IngredientNameMatching::singularizeWord)
		return ingredientWords.indices.any { index ->
			val word = ingredientWords[index]
			val singular = singularWords[index]
			matchesFromWord(word, index, ingredientWords, singularWords) ||
				(singular != word && matchesFromWord(singular, index, ingredientWords, singularWords))
		}
	}

	private fun matchesFromWord(
		word: String,
		startIndex: Int,
		ingredientWords: List<String>,
		singularWords: List<String>,
	): Boolean {
		var node: PrefixTrieNode? = root
		var matched = false
		var charIndex = 0
		while (charIndex < word.length && !matched && node != null) {
			node = node.children[word[charIndex]]
			if (node != null) {
				val tails = node.multiWordTails
				matched = node.matchesSingleWord ||
					(tails != null && matchesAnyTail(tails, startIndex, ingredientWords, singularWords))
			}
			charIndex += 1
		}
		return matched
	}

	private fun matchesAnyTail(
		tails: List<List<String>>,
		startIndex: Int,
		ingredientWords: List<String>,
		singularWords: List<String>,
	): Boolean = tails.any { tail ->
		startIndex + tail.size < ingredientWords.size &&
			matchesTail(tail, startIndex, ingredientWords, singularWords)
	}

	private fun matchesTail(
		tail: List<String>,
		startIndex: Int,
		ingredientWords: List<String>,
		singularWords: List<String>,
	): Boolean = tail.indices.all { offset ->
		val wordIndex = startIndex + 1 + offset
		val phraseWord = tail[offset]
		ingredientWords[wordIndex].startsWith(phraseWord) ||
			singularWords[wordIndex].startsWith(phraseWord)
	}
}

internal class PrefixTrieNode {
	val children: MutableMap<Char, PrefixTrieNode> = HashMap()
	var matchesSingleWord: Boolean = false
	var multiWordTails: MutableList<List<String>>? = null
}
