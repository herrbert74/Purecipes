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

	fun parse(raw: String): RecipeIngredient {
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

	fun toEditableLine(ingredient: RecipeIngredient): String = when (ingredient.requirement) {
		IngredientRequirement.OPTIONAL -> "optional: ${ingredient.text}"
		IngredientRequirement.REQUIRED -> ingredient.text
	}
}
