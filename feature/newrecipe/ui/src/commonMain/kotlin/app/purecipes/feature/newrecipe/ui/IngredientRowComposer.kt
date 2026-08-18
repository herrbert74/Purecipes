package app.purecipes.feature.newrecipe.ui

internal object IngredientRowComposer {

	private val amountRegex = Regex(
		pattern = """^((?:\d+\s+\d+/\d+)|\d+/\d+|\d+(?:[.,]\d+)?)\s+(.*)$""",
	)

	fun isAllowedAmountInput(value: String): Boolean =
		value.all { character ->
			character.isDigit() || character == ' ' || character == '/' || character == '.' || character == ','
		}

	private val knownUnits = setOf(
		"g",
		"kg",
		"mg",
		"ml",
		"l",
		"oz",
		"lb",
		"tsp",
		"tbsp",
		"cup",
		"cups",
		"teaspoon",
		"teaspoons",
		"tablespoon",
		"tablespoons",
		"pinch",
		"pinches",
		"clove",
		"cloves",
		"can",
		"cans",
		"pack",
		"packet",
		"slice",
		"slices",
	)

	fun collapsedHeadline(index: Int, row: IngredientRowInput): String {
		val line = composeLine(row.copy(isOptional = false))
			.ifBlank { "Ingredient ${index + 1}" }
		return if (row.isOptional) {
			"$line · Optional"
		} else {
			line
		}
	}

	fun composePart(part: IngredientPartInput): String =
		listOf(part.amount, part.unit, part.name)
			.map(String::trim)
			.filter(String::isNotEmpty)
			.joinToString(separator = " ")

	fun composeLine(row: IngredientRowInput): String {
		val joined = (listOf(row.primary) + row.alternatives)
			.map(::composePart)
			.filter(String::isNotEmpty)
			.joinToString(separator = " or ")
		return when {
			joined.isEmpty() -> ""
			row.isOptional -> "optional: $joined"
			else -> joined
		}
	}

	fun toLines(rows: List<IngredientRowInput>): List<String> =
		rows.map(::composeLine).map(String::trim).filter(String::isNotEmpty)

	fun fromEditableLines(lines: List<String>): List<IngredientRowInput> =
		lines
			.map(String::trim)
			.filter(String::isNotEmpty)
			.map(::fromEditableLine)
			.ifEmpty { listOf(IngredientRowInput()) }

	fun fromEditableLine(line: String): IngredientRowInput {
		val trimmed = line.trim()
		val isOptional = trimmed.startsWith("optional:", ignoreCase = true)
		val body = if (isOptional) {
			trimmed.substringAfter(delimiter = ':').trim()
		} else {
			trimmed
		}
		val parts = body
			.split(Regex("""\s+or\s+""", RegexOption.IGNORE_CASE))
			.map(String::trim)
			.filter(String::isNotEmpty)
			.map(::splitPart)
		return IngredientRowInput(
			primary = parts.firstOrNull() ?: IngredientPartInput(),
			isOptional = isOptional,
			alternatives = parts.drop(n = 1),
		)
	}

	fun fromPasteText(text: String): List<IngredientRowInput> =
		fromEditableLines(
			text.lineSequence()
				.map(String::trim)
				.filter(String::isNotEmpty)
				.toList(),
		)

	fun splitPart(text: String): IngredientPartInput {
		val trimmed = text.trim()
		val match = amountRegex.matchEntire(trimmed)
		return when {
			trimmed.isEmpty() -> IngredientPartInput()
			match == null -> IngredientPartInput(name = trimmed)
			else -> splitAmountAndRemainder(
				amount = match.groupValues[1].trim(),
				remainder = match.groupValues[2].trim(),
			)
		}
	}

	private fun splitAmountAndRemainder(amount: String, remainder: String): IngredientPartInput {
		if (remainder.isEmpty()) {
			return IngredientPartInput(name = amount)
		}
		val tokens = remainder.split(Regex("""\s+"""), limit = 2)
		val maybeUnit = tokens.first()
		return if (maybeUnit.lowercase() in knownUnits) {
			IngredientPartInput(
				amount = amount,
				unit = maybeUnit,
				name = tokens.getOrElse(1) { "" },
			)
		} else {
			IngredientPartInput(
				amount = amount,
				name = remainder,
			)
		}
	}
}
