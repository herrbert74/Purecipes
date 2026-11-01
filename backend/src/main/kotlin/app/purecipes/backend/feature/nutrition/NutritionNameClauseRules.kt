package app.purecipes.backend.feature.nutrition

internal object NutritionNameClauseRules {

	private val asteriskClause = Regex("""\s*\*.*$""")

	private val slashAlternativeClause = Regex("""\s+/\s+\d.*$""")

	private val exampleClause = Regex("""(?i)\s+e\.g\b.*$""")

	private val avoidingClause = Regex("""(?i),?\s+avoiding\b.*$""")

	private val seedsScrapedOutClause = Regex("""(?i),?\s+seeds\s+(?:scraped\s+)?out\b.*$""")

	private val butNotClause = Regex("""(?i),?\s+but\s+not\b.*$""")

	private val toVerbClause = Regex(
		"""(?i),?\s+to\s+(?:sprinkle|brush|thin|cut|top|dust|coat|garnish|serve|scatter|""" +
			"""drizzle|decorate|season|taste|glaze)\b.*$""",
	)

	private val forGerundClause = Regex(
		"""(?i),?\s+for\s+(?:brushing|dusting|sprinkling|serving|garnishing|greasing|coating|glazing|dipping)\b.*$""",
	)

	private val noNeedClause = Regex("""(?i),?\s+no\s+need\b.*$""")

	private val yeastSourdoughClause = Regex("""(?i)\s+for\s+(?:yeast|sourdough)\b.*$""")

	private val ideallyClause = Regex("""(?i),?\s+ideally\b.*$""")

	private val orAnyCombinationClause = Regex("""(?i),?\s+or\s+(?:any|a)\s+(?:combination|mixture)\b.*$""")

	private val forTheCrustClause = Regex("""(?i)\s+for\s+the\s+crust\b.*$""")

	private val afterBakingPrefix = Regex("""(?i)^after\s+baking,?\s*""")

	private val deSeededPhrase = Regex("""(?i)\bde[\s-]+seeded\b""")

	fun strip(value: String): String {
		var result = value
		result = asteriskClause.replace(result, "")
		result = slashAlternativeClause.replace(result, "")
		result = exampleClause.replace(result, "")
		result = avoidingClause.replace(result, "")
		result = seedsScrapedOutClause.replace(result, "")
		result = butNotClause.replace(result, "")
		result = toVerbClause.replace(result, "")
		result = forGerundClause.replace(result, "")
		result = noNeedClause.replace(result, "")
		result = yeastSourdoughClause.replace(result, "")
		result = ideallyClause.replace(result, "")
		result = orAnyCombinationClause.replace(result, "")
		result = forTheCrustClause.replace(result, "")
		result = afterBakingPrefix.replace(result, "")
		result = deSeededPhrase.replace(result, "")
		return result.trim()
	}
}
