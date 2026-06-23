package app.purecipes.backend.feature.ingredient

import app.purecipes.shared.domain.ingredient.IngredientNameMatching

internal object IngredientLineTokenExtractor {

	private const val MIN_TOKEN_LENGTH = 3

	private val quantityTokenRegex = Regex("""^\d+(/\d+)?(\.\d+)?$""")

	private val unitTokens = setOf(
		"bunch",
		"can",
		"cans",
		"clove",
		"cloves",
		"cup",
		"cups",
		"g",
		"gram",
		"grams",
		"kg",
		"l",
		"lb",
		"lbs",
		"liter",
		"liters",
		"litre",
		"litres",
		"ml",
		"ounce",
		"ounces",
		"oz",
		"package",
		"packages",
		"pinch",
		"piece",
		"pieces",
		"pound",
		"pounds",
		"slice",
		"slices",
		"tablespoon",
		"tablespoons",
		"tbsp",
		"tsp",
		"teaspoon",
		"teaspoons",
	)

	private val preparationTokens = setOf(
		"boneless",
		"chopped",
		"crushed",
		"diced",
		"dried",
		"finely",
		"fresh",
		"grated",
		"ground",
		"large",
		"medium",
		"minced",
		"optional",
		"peeled",
		"roughly",
		"seeded",
		"skinless",
		"small",
		"sliced",
		"thinly",
		"to",
		"taste",
		"whole",
	)

	fun extractTokens(line: String): List<String> {
		val normalized = IngredientNameMatching.normalizeIngredientText(line)
		val words = normalized.split(' ').filter { it.isNotBlank() }
		val significantWords = dropLeadingQuantityAndUnits(words)
			.filterNot { it in preparationTokens }
			.filter { it.length >= MIN_TOKEN_LENGTH }

		if (significantWords.isEmpty()) {
			return emptyList()
		}

		return buildList {
			significantWords.forEach { word ->
				add(titleCase(word))
			}
			if (significantWords.size > 1) {
				add(titleCase(significantWords.joinToString(separator = " ")))
			}
		}.distinct()
	}

	private fun dropLeadingQuantityAndUnits(words: List<String>): List<String> {
		var index = 0
		while (index < words.size) {
			val word = words[index]
			if (isQuantityToken(word) || word in unitTokens) {
				index++
			} else {
				break
			}
		}
		return words.drop(index)
	}

	private fun isQuantityToken(word: String): Boolean =
		quantityTokenRegex.matches(word) || word == "of"

	private fun titleCase(word: String): String =
		word.replaceFirstChar { character ->
			if (character.isLowerCase()) {
				character.titlecase()
			} else {
				character.toString()
			}
		}
}
