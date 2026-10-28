package app.purecipes.backend.feature.nutrition

internal object IngredientLinePhraseRewrites {

	private const val REGEX_GROUP_FIRST = 1
	private const val REGEX_GROUP_SECOND = 2
	private const val GRAMS_PER_PUNNET = 250

	private val leadingInstructionPattern = Regex(
		"""^to\s+(?:brush|sprinkle|top|dust|coat)\b[^,]*,\s*(.+)$""",
		RegexOption.IGNORE_CASE,
	)

	private val sizeAlternativePattern = Regex(
		"""^(\d+)\s+(?:large|medium|small)\s+or\s+\d+\s+(?:large|medium|small)\s+(.+)$""",
		RegexOption.IGNORE_CASE,
	)

	private val slicedFruitPattern = Regex(
		"""^(?:sliced\s+fruit(?:\s+of\s+your\s+choosing)?)$""",
		RegexOption.IGNORE_CASE,
	)

	private val countWithPerItemWeightPattern = Regex(
		"""^(\d+)\s+(pigeons?)\s*\((?:about|approx\.?)\s*(\d+(?:\.\d+)?)\s*([a-zA-Z]+)\s+each\)$""",
		RegexOption.IGNORE_CASE,
	)

	private val countableJuniperBerriesPattern = Regex(
		"""^\d+\s+juniper\s+berries$""",
		RegexOption.IGNORE_CASE,
	)

	private val countableCapersPattern = Regex(
		"""^\d+\s+capers$""",
		RegexOption.IGNORE_CASE,
	)

	private val trailingPlusExtraPattern = Regex(
		"""^(.*)[,;]?\s+plus\s+(?:extra\b.*|a\s+drizzle\b.*|\d+\s*(?:tbsp|tsp|tablespoons?|teaspoons?)\b.*)$""",
		RegexOption.IGNORE_CASE,
	)

	private val punnetPattern = Regex(
		"""^(\d+)\s+punnets?\s+(.+?)(?:,?\s+cut into quarters)?$""",
		RegexOption.IGNORE_CASE,
	)

	private val cassiaBarkPattern = Regex(
		"""^\d+(?:\.\d+)?\s*cm\s+cassia bark$""",
		RegexOption.IGNORE_CASE,
	)

	private val teabagPattern = Regex(
		"""^(\d+)\s+(?:camomile|chamomile)\s+teabags?$""",
		RegexOption.IGNORE_CASE,
	)

	private val saltinePattern = Regex(
		"""^(\d+(?:-\d+)?)\s+saltine$""",
		RegexOption.IGNORE_CASE,
	)

	private val otherSaltedCrackersPattern = Regex(
		"""^(\d+(?:-\d+)?)\s+other salted crackers$""",
		RegexOption.IGNORE_CASE,
	)

	private val meringuesPattern = Regex(
		"""^(\d+(?:-\d+)?)\s+meringues(?:\s+broken)?$""",
		RegexOption.IGNORE_CASE,
	)

	private val spongeFingersPattern = Regex(
		"""^(\d+)\s+sponge\s+fingers$""",
		RegexOption.IGNORE_CASE,
	)

	private val lightChickenPattern = Regex(
		"""^850ml-1\.2litres/1\.5-2 pts light chicken$""",
		RegexOption.IGNORE_CASE,
	)

	private val bakingPowderMixedPattern = Regex(
		"""^(¼\s+tsp|\d+(?:/\d+)?\s+tsp)\s+baking powder\s+mixed with\s+\d+.*$""",
		RegexOption.IGNORE_CASE,
	)

	private val matchsticksCarrotsPattern = Regex(
		"""^(\d+)\s+carrots\s+cut into matchsticks.*$""",
		RegexOption.IGNORE_CASE,
	)

	private val whitePartsLeeksPattern = Regex(
		"""^(\d+\s*g)\s+white parts of leeks.*$""",
		RegexOption.IGNORE_CASE,
	)

	private val theWhitePartOfLeekPattern = Regex(
		"""^the white part of (\d+)\s+leek$""",
		RegexOption.IGNORE_CASE,
	)

	private val parsleySprigsPattern = Regex(
		"""^(small bunch)\s+parsley\s+a couple of sprigs.*$""",
		RegexOption.IGNORE_CASE,
	)

	private val redOnionsPastePattern = Regex(
		"""^(\d+)\s+(?:large\s+)?red onions\s+one sliced.*$""",
		RegexOption.IGNORE_CASE,
	)

	private val yogurtSmoothPattern = Regex(
		"""^(\d+\s*g)\s+plain yogurt\s+beaten to make it smooth$""",
		RegexOption.IGNORE_CASE,
	)

	private val hotChilliAdjustPattern = Regex(
		"""^(\d+\s*tsp)\s+hot chilli powder\s+adjust.*$""",
		RegexOption.IGNORE_CASE,
	)

	private val italianSausageSkinPattern = Regex(
		"""^(\d+\s*g)\s+Italian-style sausages\s+meat squeezed from the skins.*$""",
		RegexOption.IGNORE_CASE,
	)

	private val springOnionsWhitesPattern = Regex(
		"""^(\d+)\s+spring onions\s+whites left whole.*$""",
		RegexOption.IGNORE_CASE,
	)

	private val gingerPiecePattern = Regex(
		"""^thumb-sized piece of ginger.*$""",
		RegexOption.IGNORE_CASE,
	)

	private val drizzleOliveOilPattern = Regex(
		"""^drizzle\s+of\s+olive\s+oil$""",
		RegexOption.IGNORE_CASE,
	)

	private val parsnipsBatonsPattern = Regex(
		"""^(\d+\s*g)\s+parsnips\s+peeled and chopped into chunky batons$""",
		RegexOption.IGNORE_CASE,
	)

	private val onionCutPattern = Regex(
		"""^(\d+\s+small\s+onion)\s+cut the same size.*$""",
		RegexOption.IGNORE_CASE,
	)

	private val neutralOilPattern = Regex(
		"""^neutral-tasting oil.*$""",
		RegexOption.IGNORE_CASE,
	)

	private val plumsStonedPattern = Regex(
		"""^(?:2 large or 4 small|\d+)\s+plums\s+(?:halved and stoned|stoned and halved|halved|stoned)$""",
		RegexOption.IGNORE_CASE,
	)

	fun rewrite(value: String): String? {
		val trimmed = value.trim()
		return rewriteEarlyClauses(trimmed)
			?: rewritePrepNoise(trimmed)
			?: rewriteCountAndVolume(trimmed)
	}

	private fun rewriteEarlyClauses(trimmed: String): String? {
		val instruction = leadingInstructionPattern.matchEntire(trimmed)
		val sizeAlternative = sizeAlternativePattern.matchEntire(trimmed)
		val slicedFruit = slicedFruitPattern.matchEntire(trimmed)
		val perItemRewrite = countWithPerItemWeightPattern.matchEntire(trimmed)?.let { formatPerItemWeight(it) }
		val extraTrailing = trailingPlusExtraPattern.matchEntire(trimmed)
		return when {
			plumsStonedPattern.matches(trimmed) -> "2 plums"
			perItemRewrite != null -> perItemRewrite
			countableJuniperBerriesPattern.matches(trimmed) -> "1 tsp juniper berries"
			countableCapersPattern.matches(trimmed) -> "1 tbsp capers"
			instruction != null -> instruction.groupValues[REGEX_GROUP_FIRST].trim()
			sizeAlternative != null -> listOf(
				sizeAlternative.groupValues[REGEX_GROUP_FIRST],
				sizeAlternative.groupValues[REGEX_GROUP_SECOND].trim(),
			).joinToString(" ")
			slicedFruit != null -> "1 cup $trimmed"
			extraTrailing != null -> extraTrailing.groupValues[REGEX_GROUP_FIRST].trim()
			else -> null
		}
	}

	private fun rewriteCountAndVolume(trimmed: String): String? {
		val punnet = punnetPattern.matchEntire(trimmed)
		val teabag = teabagPattern.matchEntire(trimmed)
		val saltine = saltinePattern.matchEntire(trimmed)
		val otherCrackers = otherSaltedCrackersPattern.matchEntire(trimmed)
		val meringues = meringuesPattern.matchEntire(trimmed)
		val fingers = spongeFingersPattern.matchEntire(trimmed)
		return when {
			punnet != null -> {
				val count = punnet.groupValues[REGEX_GROUP_FIRST].toIntOrNull() ?: 1
				"${count * GRAMS_PER_PUNNET} g ${punnet.groupValues[REGEX_GROUP_SECOND].trim()}"
			}
			cassiaBarkPattern.matches(trimmed) -> "1 piece cassia bark"
			teabag != null -> "${teabag.groupValues[REGEX_GROUP_FIRST]} piece chamomile tea"
			saltine != null -> "${saltine.groupValues[REGEX_GROUP_FIRST]} piece saltine crackers"
			otherCrackers != null -> "${otherCrackers.groupValues[REGEX_GROUP_FIRST]} piece other salted crackers"
			meringues != null -> "${meringues.groupValues[REGEX_GROUP_FIRST]} piece meringues"
			fingers != null -> "${fingers.groupValues[REGEX_GROUP_FIRST]} piece sponge fingers"
			lightChickenPattern.matches(trimmed) -> "1 l light chicken stock"
			else -> null
		}
	}

	private fun rewritePrepNoise(trimmed: String): String? {
		val bakingPowder = bakingPowderMixedPattern.matchEntire(trimmed)
		val carrots = matchsticksCarrotsPattern.matchEntire(trimmed)
		val whiteLeeks = whitePartsLeeksPattern.matchEntire(trimmed)
		val whiteLeek = theWhitePartOfLeekPattern.matchEntire(trimmed)
		val parsley = parsleySprigsPattern.matchEntire(trimmed)
		val onions = redOnionsPastePattern.matchEntire(trimmed)
		val yogurt = yogurtSmoothPattern.matchEntire(trimmed)
		val chilli = hotChilliAdjustPattern.matchEntire(trimmed)
		val sausage = italianSausageSkinPattern.matchEntire(trimmed)
		val springOnions = springOnionsWhitesPattern.matchEntire(trimmed)
		val parsnips = parsnipsBatonsPattern.matchEntire(trimmed)
		val onion = onionCutPattern.matchEntire(trimmed)
		return when {
			bakingPowder != null -> "${bakingPowder.groupValues[REGEX_GROUP_FIRST]} baking powder"
			carrots != null -> "${carrots.groupValues[REGEX_GROUP_FIRST]} carrots"
			whiteLeeks != null -> "${whiteLeeks.groupValues[REGEX_GROUP_FIRST]} leeks"
			whiteLeek != null -> "${whiteLeek.groupValues[REGEX_GROUP_FIRST]} leek"
			parsley != null -> "small bunch parsley"
			onions != null -> "${onions.groupValues[REGEX_GROUP_FIRST]} red onions"
			yogurt != null -> "${yogurt.groupValues[REGEX_GROUP_FIRST]} plain yogurt"
			chilli != null -> "${chilli.groupValues[REGEX_GROUP_FIRST]} hot chilli powder"
			sausage != null -> "${sausage.groupValues[REGEX_GROUP_FIRST]} Italian-style sausages"
			springOnions != null -> "${springOnions.groupValues[REGEX_GROUP_FIRST]} spring onions"
			gingerPiecePattern.matches(trimmed) -> "1 piece ginger"
			drizzleOliveOilPattern.matches(trimmed) -> "1 tsp olive oil"
			parsnips != null -> "${parsnips.groupValues[REGEX_GROUP_FIRST]} parsnips"
			onion != null -> onion.groupValues[REGEX_GROUP_FIRST]
			neutralOilPattern.matches(trimmed) -> "1 tbsp neutral-tasting oil"
			else -> null
		}
	}

	private fun formatPerItemWeight(match: MatchResult): String? {
		val count = match.groupValues[REGEX_GROUP_FIRST].toDoubleOrNull()
		val weight = match.groupValues[3].toDoubleOrNull()
		val unit = match.groupValues[4]
		val item = match.groupValues[REGEX_GROUP_SECOND].trim()
		return if (count != null && weight != null) {
			"${(count * weight).toInt()} $unit $item"
		} else {
			null
		}
	}
}
