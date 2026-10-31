package app.purecipes.backend.feature.nutrition

internal object NutritionGrainFlourRules {

	private val flourEndings = setOf("flour", "flours", "meal")

	private val berryEndings = setOf("berries", "berry")

	private val distinctiveGrainTokens = setOf(
		"einkorn",
		"emmer",
		"fife",
		"khorasan",
		"kamut",
		"pima",
		"rojo",
		"spelt",
		"yecora",
	)

	private val wholeGrainModifierTokens = setOf(
		"club",
		"grain",
		"hard",
		"home",
		"homemilled",
		"milled",
		"soft",
		"spring",
		"sprouted",
		"whole",
		"winter",
	)

	private val wheatKindTokens = setOf("flour", "red", "white", "wheat")

	private val wholeWheatFlour = listOf("whole", "wheat", "flour")

	fun rewrite(tokens: List<String>): List<String> {
		val set = tokens.toSet()
		val last = tokens.lastOrNull()
		return when {
			last == null || tokens == wholeWheatFlour -> tokens
			isDistinctiveGrainOnly(tokens) -> wholeWheatFlour
			last !in flourEndings && last !in berryEndings -> tokens
			"rye" in set && ("grain" in set || last in berryEndings) -> listOf("whole", "rye", "flour")
			"durum" in set -> listOf("semolina", "flour")
			"chestnut" in set && last in flourEndings -> listOf("chestnut", "flour")
			isWholeGrainWheat(set, last) -> wholeWheatFlour
			else -> tokens
		}
	}

	private fun isDistinctiveGrainOnly(tokens: List<String>): Boolean =
		tokens.any { token -> token in distinctiveGrainTokens } &&
			tokens.all { token ->
				token in distinctiveGrainTokens ||
					token in wholeGrainModifierTokens ||
					token in wheatKindTokens ||
					token in berryEndings
			}

	private fun isWholeGrainWheat(set: Set<String>, last: String): Boolean {
		val hasDistinctiveGrain = set.any { token -> token in distinctiveGrainTokens }
		val isTurkeyRed = "turkey" in set && "red" in set
		val hasWheat = "wheat" in set
		val hasWholeGrainModifier = set.any { token -> token in wholeGrainModifierTokens }
		return hasDistinctiveGrain ||
			isTurkeyRed ||
			(hasWheat && (last in berryEndings || hasWholeGrainModifier)) ||
			("grain" in set && "whole" in set)
	}
}
