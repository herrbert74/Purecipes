package app.purecipes.shared.domain.ingredient

import app.purecipes.shared.domain.model.IngredientRequirement
import app.purecipes.shared.domain.model.RecipeIngredient

object IngredientLineParser {

	private val optionalPrefixRegex = Regex(
		pattern = """^optional[:\s,-]+(.+)$""",
		options = setOf(RegexOption.IGNORE_CASE),
	)
	private val optionalParentheticalRegex = Regex(
		pattern = """\(\s*optional\s*\)""",
		options = setOf(RegexOption.IGNORE_CASE),
	)
	private val toGarnishOrServeSuffixRegex = Regex(
		pattern = """[,;]?\s*(to garnish|to serve|for garnish|for serving)\s*$""",
		options = setOf(RegexOption.IGNORE_CASE),
	)
	private val plusExtraToServeSuffixRegex = Regex(
		pattern = """\bplus extra\b.*\bto serve\s*$""",
		options = setOf(RegexOption.IGNORE_CASE),
	)

	fun parseLines(lines: List<String>): List<RecipeIngredient> {
		var nextAlternativeGroupKey = 1
		return lines.flatMap { line ->
			val parsed = parseLine(line)
			val alternativeCount = parsed.count { ingredient ->
				ingredient.requirement == IngredientRequirement.ALTERNATIVE
			}
			if (alternativeCount <= 1) {
				parsed
			} else {
				val groupKey = nextAlternativeGroupKey++
				parsed.map { ingredient ->
					if (ingredient.requirement == IngredientRequirement.ALTERNATIVE) {
						ingredient.copy(alternativeGroupKey = groupKey)
					} else {
						ingredient
					}
				}
			}
		}
	}

	fun parseLine(raw: String): List<RecipeIngredient> {
		val parsedRequirement = parseRequirement(raw)
		val alternativeTexts = IngredientAlternativeParsing.expandAlternativeParts(
			IngredientAlternativeParsing.splitAlternativeParts(parsedRequirement.text),
		)
		if (alternativeTexts.size <= 1) {
			return listOf(
				RecipeIngredient(
					text = parsedRequirement.text,
					requirement = parsedRequirement.requirement,
				),
			)
		}
		return alternativeTexts.map { text ->
			RecipeIngredient(
				text = text,
				requirement = IngredientRequirement.ALTERNATIVE,
			)
		}
	}

	fun toEditableLines(ingredients: List<RecipeIngredient>): List<String> {
		val lines = mutableListOf<String>()
		var index = 0
		while (index < ingredients.size) {
			val ingredient = ingredients[index]
			if (ingredient.requirement == IngredientRequirement.ALTERNATIVE && ingredient.alternativeGroupKey != null) {
				val groupKey = ingredient.alternativeGroupKey
				val group = mutableListOf<RecipeIngredient>()
				while (
					index < ingredients.size &&
					ingredients[index].requirement == IngredientRequirement.ALTERNATIVE &&
					ingredients[index].alternativeGroupKey == groupKey
				) {
					group += ingredients[index]
					index++
				}
				lines += group.joinToString(separator = " or ") { member -> member.text }
			} else {
				lines += toEditableLine(ingredient)
				index++
			}
		}
		return lines
	}

	fun toEditableLine(ingredient: RecipeIngredient): String = when (ingredient.requirement) {
		IngredientRequirement.OPTIONAL -> "optional: ${ingredient.text}"
		IngredientRequirement.REQUIRED,
		IngredientRequirement.ALTERNATIVE,
		-> ingredient.text
	}

	private fun parseRequirement(raw: String): RecipeIngredient {
		var text = raw.trim().removePrefix("-").removePrefix("*").trim()
		var requirement = IngredientRequirement.REQUIRED

		optionalPrefixRegex.matchEntire(text)?.let { match ->
			val stripped = match.groupValues[1].trim()
			if (stripped.isNotBlank()) {
				text = stripped
				requirement = IngredientRequirement.OPTIONAL
			}
		}

		if (requirement == IngredientRequirement.REQUIRED && optionalParentheticalRegex.containsMatchIn(text)) {
			text = optionalParentheticalRegex.replace(text, "").trim().trimEnd(',', ';')
			requirement = IngredientRequirement.OPTIONAL
		}

		if (
			requirement == IngredientRequirement.REQUIRED &&
			(
				toGarnishOrServeSuffixRegex.containsMatchIn(text) ||
					plusExtraToServeSuffixRegex.containsMatchIn(text)
				)
		) {
			requirement = IngredientRequirement.OPTIONAL
		}

		return RecipeIngredient(
			text = text,
			requirement = requirement,
		)
	}
}
