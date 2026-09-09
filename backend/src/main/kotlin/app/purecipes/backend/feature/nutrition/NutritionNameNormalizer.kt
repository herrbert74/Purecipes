package app.purecipes.backend.feature.nutrition

import java.text.Normalizer

internal object NutritionNameNormalizer {

	private val NON_ALPHANUMERIC = Regex("[^a-z0-9]+")

	private val SUCH_AS_CLAUSE = Regex(""",?\s*such as\b.*""", RegexOption.IGNORE_CASE)

	private val OR_CLAUSE = Regex("""\s+or\b.*""", RegexOption.IGNORE_CASE)

	private val YOUR_CLAUSE = Regex("""\s+your\b.*""", RegexOption.IGNORE_CASE)

	private val YOU_CLAUSE = Regex("""\s+you\b.*""", RegexOption.IGNORE_CASE)

	private val LIKE_CLAUSE = Regex("""\s+like\b.*""", RegexOption.IGNORE_CASE)

	private val RECOMMENDED_CLAUSE = Regex("""\s+(?:but\s+)?recommended\b.*""", RegexOption.IGNORE_CASE)

	private val AVAILABLE_CLAUSE = Regex("""\s+available\b.*""", RegexOption.IGNORE_CASE)

	private val PREFERRED_CLAUSE = Regex("""\s+preferred\b.*""", RegexOption.IGNORE_CASE)

	private val MADE_CLAUSE = Regex("""\s+made\b.*""", RegexOption.IGNORE_CASE)

	private val ANY_CLAUSE = Regex("""\s+any\b.*""", RegexOption.IGNORE_CASE)

	private val WHAT_CLAUSE = Regex("""\s+what\b.*""", RegexOption.IGNORE_CASE)

	private val WHATEVER_CLAUSE = Regex("""\s+whatever\b.*""", RegexOption.IGNORE_CASE)

	private val ADD_CLAUSE = Regex("""\s+add\b.*""", RegexOption.IGNORE_CASE)

	private val DRIZZLED_CLAUSE = Regex("""\s+drizzled\b.*""", RegexOption.IGNORE_CASE)

	private val TABLE_SALT_CLAUSE = Regex("""\s+table\s+salt\b.*""", RegexOption.IGNORE_CASE)

	private val SUGGESTED_CLAUSE = Regex("""(?i)(?:^|\s+)suggested\b.*""")

	private val SUGGESTIONS_CLAUSE = Regex("""(?i)(?:^|\s+)suggestions?\b.*""")

	private val THIS_USED_CLAUSE = Regex("""(?i)^this\b.*\bused\b.*""")

	private val COMBINING_MARKS = Regex("\\p{M}+")

	private val preparationTokens = setOf(
		"batons",
		"beaten",
		"bias",
		"bite",
		"blanched",
		"boned",
		"broken",
		"butterflied",
		"charred",
		"chilled",
		"chiffonade",
		"chopped",
		"chunks",
		"cleaned",
		"coarsely",
		"cold",
		"concentrated",
		"cooled",
		"cored",
		"crispy",
		"crosswise",
		"crusted",
		"crushed",
		"cubed",
		"cubes",
		"cut",
		"cutlet",
		"cutlets",
		"debearded",
		"defrosted",
		"deseeded",
		"desiccated",
		"deveined",
		"diagonal",
		"diagonally",
		"dice",
		"diced",
		"divided",
		"drained",
		"dried",
		"drizzled",
		"fillet",
		"fillets",
		"fine",
		"finely",
		"florets",
		"fresh",
		"freshly",
		"grated",
		"ground",
		"halved",
		"halves",
		"hearts",
		"homemade",
		"hulled",
		"inch",
		"inches",
		"juiced",
		"julienned",
		"knob",
		"knobs",
		"large",
		"leaves",
		"lengthwise",
		"lightly",
		"mashed",
		"medium",
		"melted",
		"minced",
		"moons",
		"needed",
		"note",
		"notes",
		"peeled",
		"picked",
		"pitted",
		"pounded",
		"preferably",
		"quartered",
		"remove",
		"removed",
		"ribbons",
		"rinsed",
		"room",
		"roughly",
		"rounds",
		"scraped",
		"scrubbed",
		"see",
		"seeded",
		"separated",
		"shaken",
		"shredded",
		"shucked",
		"sifted",
		"sized",
		"skinned",
		"sliced",
		"slices",
		"slightly",
		"small",
		"smashed",
		"smoked",
		"snipped",
		"soaked",
		"softened",
		"spooned",
		"squeezed",
		"stem",
		"stemmed",
		"stems",
		"stewing",
		"stirred",
		"strips",
		"temperature",
		"tenderized",
		"thawed",
		"thick",
		"thin",
		"thinly",
		"toasted",
		"torn",
		"trimmed",
		"uncooked",
		"undrained",
		"unpeeled",
		"untoasted",
		"warmed",
		"washed",
		"wedges",
		"whisked",
		"wide",
		"wilted",
		"zested",
	)

	private val measureLeftoverTokens = setOf(
		"about",
		"and",
		"approx",
		"approximately",
		"cup",
		"cups",
		"each",
		"fluid",
		"gram",
		"grams",
		"kilo",
		"kilogram",
		"kilograms",
		"liter",
		"liters",
		"litre",
		"litres",
		"milliliter",
		"milliliters",
		"millilitre",
		"millilitres",
		"only",
		"or",
		"ounce",
		"ounces",
		"plus",
		"pound",
		"pounds",
		"sprigs",
		"tablespoon",
		"tablespoons",
		"tbsp",
		"tbsps",
		"teaspoon",
		"teaspoons",
		"tender",
		"total",
		"tsp",
		"tsps",
		"weight",
		"with",
	)

	private val fillerTokens = setOf(
		"above",
		"according",
		"additional",
		"against",
		"along",
		"between",
		"bottled",
		"bottoms",
		"bought",
		"brushing",
		"bulbs",
		"buttery",
		"changes",
		"clear",
		"combination",
		"cook",
		"cooking",
		"couple",
		"crusts",
		"cutting",
		"day",
		"deep",
		"depending",
		"desired",
		"diameter",
		"directions",
		"discarded",
		"dredging",
		"drizzling",
		"dusting",
		"easy",
		"ends",
		"english",
		"equal",
		"evenly",
		"excess",
		"eyeball",
		"fashioned",
		"few",
		"fillings",
		"fitted",
		"flavored",
		"flavoured",
		"flatten",
		"for",
		"fork",
		"from",
		"frying",
		"garnish",
		"garnishing",
		"good",
		"granules",
		"grater",
		"greasing",
		"griddling",
		"grinder",
		"hand",
		"heavier",
		"high",
		"holes",
		"hours",
		"husk",
		"husked",
		"husks",
		"individual",
		"inedible",
		"instructions",
		"into",
		"larger",
		"layers",
		"lean",
		"least",
		"left",
		"lengths",
		"less",
		"liberal",
		"loosely",
		"lumps",
		"may",
		"minutes",
		"more",
		"mortar",
		"much",
		"multiple",
		"natural",
		"need",
		"oiling",
		"old",
		"ones",
		"optional",
		"organic",
		"packed",
		"pale",
		"palm",
		"palmfuls",
		"pan",
		"part",
		"parts",
		"percent",
		"percentage",
		"pestle",
		"piece",
		"pieces",
		"pint",
		"poaching",
		"poked",
		"preference",
		"prepared",
		"pressed",
		"pure",
		"quality",
		"recipe",
		"remaining",
		"rendered",
		"reserved",
		"ripe",
		"rolling",
		"runs",
		"sauteing",
		"seam",
		"sections",
		"segments",
		"serve",
		"serving",
		"sieve",
		"size",
		"smaller",
		"soften",
		"some",
		"spicy",
		"sprinkling",
		"stick",
		"sticks",
		"store",
		"strained",
		"style",
		"surface",
		"taste",
		"than",
		"the",
		"then",
		"thickness",
		"thoroughly",
		"through",
		"tips",
		"tops",
		"tough",
		"turn",
		"turns",
		"until",
		"using",
		"variety",
		"very",
		"volume",
		"way",
		"well",
		"will",
		"without",
		"wooden",
	)

	private val brandTokens = setOf(
		"aldi",
		"asda",
		"baker",
		"bens",
		"bisquick",
		"brand",
		"cape",
		"coop",
		"cooperative",
		"estate",
		"fairtrade",
		"ghirardelli",
		"hellmann",
		"hellmanns",
		"knorr",
		"kraft",
		"lidl",
		"louis",
		"marks",
		"morrison",
		"morrisons",
		"operative",
		"philadelphia",
		"sainsbury",
		"sainsburys",
		"sargento",
		"spencer",
		"tabasco",
		"tesco",
		"uncle",
		"waitrose",
	)

	private val packagingTokens = setOf(
		"bag",
		"bags",
		"block",
		"bottle",
		"bottles",
		"box",
		"boxes",
		"bunch",
		"bunches",
		"can",
		"cans",
		"carton",
		"container",
		"containers",
		"envelope",
		"head",
		"heads",
		"jar",
		"jars",
		"pack",
		"package",
		"packages",
		"packet",
		"packets",
		"pot",
		"tin",
		"tins",
	)

	private val sizeOnlyTokens = setOf(
		"extra",
		"extra-large",
		"jumbo",
		"large",
		"medium",
		"small",
	)

	private val presenceStripTokens = setOf(
		"bone",
		"bones",
		"flesh",
		"half",
		"one",
		"pit",
		"pits",
		"skin",
		"steak",
		"steaks",
	)

	private val trailingStripTokens = setOf(
		"all",
		"extra",
		"topping",
	)

	private val trailingLiquidTokens = setOf(
		"liquid",
		"water",
	)

	private val beverageWaterKeepers = setOf(
		"blossom",
		"coconut",
		"floral",
		"flower",
		"mineral",
		"orange",
		"rose",
		"soda",
		"sparkling",
		"tonic",
	)

	private val colorPartAnchors = setOf(
		"leek",
		"leeks",
		"lemongrass",
		"onion",
		"onions",
		"scallion",
		"scallions",
		"shallot",
		"shallots",
		"spring",
	)

	private val colorPartTokens = setOf(
		"dark",
		"green",
		"greens",
		"light",
		"white",
		"whites",
	)

	private val flourAnchors = setOf(
		"flour",
	)

	private val flourProteinStripTokens = setOf(
		"protein",
	)

	private val crustAnchors = setOf(
		"crust",
		"crusts",
		"pie",
	)

	private val crustDescriptorTokens = setOf(
		"double",
		"flaky",
	)

	private val grainCutAnchors = setOf(
		"brisket",
		"steak",
		"steaks",
	)

	private val grainCutStripTokens = setOf(
		"grain",
	)

	private val splitPrepAnchors = setOf(
		"baguette",
		"baguettes",
		"bap",
		"baps",
		"breast",
		"breasts",
		"bun",
		"buns",
		"chicken",
		"croissant",
		"croissants",
		"garlic",
		"orange",
		"roll",
		"rolls",
		"vanilla",
	)

	private val splitProtectTokens = setOf(
		"lentil",
		"lentils",
		"pea",
		"peas",
	)

	private val splitPrepTokens = setOf(
		"split",
	)

	private val dressingStripTriggers = setOf(
		"juice",
		"oil",
		"vinegar",
		"wine",
	)

	fun normalize(value: String): String {
		val ascii = COMBINING_MARKS.replace(
			Normalizer.normalize(value.lowercase(), Normalizer.Form.NFKD),
			"",
		)
		return NON_ALPHANUMERIC.replace(ascii, " ").trim()
	}

	fun tokens(normalized: String): List<String> =
		normalized.split(' ').filter { token -> token.length >= MIN_TOKEN_LENGTH }

	fun forLookup(value: String): String {
		val withoutClause = stripLookupClauses(value.substringBefore(';').trim())
		val normalized = normalize(withoutClause)
		val rawTokens = tokens(normalized)
		val hadGrinder = "grinder" in rawTokens
		val hadStewing = "stewing" in rawTokens
		val hadTips = "tips" in rawTokens
		val hadStem = "stem" in rawTokens || "stems" in rawTokens
		val filtered = rawTokens.filterNot { token ->
			token.all(Char::isDigit) ||
				token in preparationTokens ||
				token in measureLeftoverTokens ||
				token in fillerTokens ||
				token in brandTokens ||
				token in packagingTokens
		}
		val withoutSpiceGrinder = if (hadGrinder) {
			filtered.filterNot { token -> token == "spice" }
		} else {
			filtered
		}
		val withoutStewingMeat = if (hadStewing) {
			withoutSpiceGrinder.filterNot { token -> token == "meat" }
		} else {
			withoutSpiceGrinder
		}
		val withoutWingTips = if (hadTips) {
			withoutStewingMeat.filterNot { token -> token == "wing" || token == "wings" }
		} else {
			withoutStewingMeat
		}
		val withoutStemEnd = if (hadStem) {
			withoutWingTips.filterNot { token -> token == "end" }
		} else {
			withoutWingTips
		}
		return postFilterLookupTokens(withoutStemEnd).joinToString(" ")
	}

	fun hasMeaningfulFoodName(value: String): Boolean {
		val lookup = forLookup(value)
		if (lookup.isNotBlank()) {
			return true
		}
		val normalizedTokens = tokens(normalize(value)).filterNot { token -> token.all(Char::isDigit) }
		return normalizedTokens.any { token -> token !in sizeOnlyTokens && token !in preparationTokens }
	}

	private fun stripLookupClauses(value: String): String {
		var result = SUCH_AS_CLAUSE.replace(value, "")
		result = OR_CLAUSE.replace(result, "")
		result = YOUR_CLAUSE.replace(result, "")
		result = YOU_CLAUSE.replace(result, "")
		result = LIKE_CLAUSE.replace(result, "")
		result = RECOMMENDED_CLAUSE.replace(result, "")
		result = AVAILABLE_CLAUSE.replace(result, "")
		result = PREFERRED_CLAUSE.replace(result, "")
		result = MADE_CLAUSE.replace(result, "")
		result = ANY_CLAUSE.replace(result, "")
		result = WHAT_CLAUSE.replace(result, "")
		result = WHATEVER_CLAUSE.replace(result, "")
		result = ADD_CLAUSE.replace(result, "")
		result = DRIZZLED_CLAUSE.replace(result, "")
		result = TABLE_SALT_CLAUSE.replace(result, "")
		result = SUGGESTED_CLAUSE.replace(result, "")
		result = SUGGESTIONS_CLAUSE.replace(result, "")
		result = THIS_USED_CLAUSE.replace(result, "")
		return result.trim()
	}

	private fun postFilterLookupTokens(tokens: List<String>): List<String> {
		var result = stripGrainCut(tokens)
		result = stripWhenOthersRemain(result, presenceStripTokens)
		result = stripTrailingColorParts(result)
		result = stripSplitPrep(result)
		result = stripFlourProtein(result)
		result = stripCrustDescriptors(result)
		result = stripTrailingWhenOthersRemain(result, trailingStripTokens)
		result = stripTrailingDressing(result)
		result = stripTrailingLiquid(result)
		return result
	}

	private fun stripCrustDescriptors(tokens: List<String>): List<String> {
		val canStrip = tokens.size >= 2 &&
			tokens.any { token -> token in crustAnchors }
		return if (canStrip) stripWhenOthersRemain(tokens, crustDescriptorTokens) else tokens
	}

	private fun stripGrainCut(tokens: List<String>): List<String> {
		val canStrip = tokens.size >= 2 &&
			tokens.any { token -> token in grainCutAnchors }
		return if (canStrip) stripWhenOthersRemain(tokens, grainCutStripTokens) else tokens
	}

	private fun stripSplitPrep(tokens: List<String>): List<String> {
		val canStrip = tokens.size >= 2 &&
			tokens.none { token -> token in splitProtectTokens } &&
			tokens.any { token -> token in splitPrepAnchors }
		return if (canStrip) stripWhenOthersRemain(tokens, splitPrepTokens) else tokens
	}

	private fun stripFlourProtein(tokens: List<String>): List<String> {
		val canStrip = tokens.size >= 2 &&
			tokens.any { token -> token in flourAnchors }
		return if (canStrip) stripWhenOthersRemain(tokens, flourProteinStripTokens) else tokens
	}

	private fun stripWhenOthersRemain(tokens: List<String>, removable: Set<String>): List<String> {
		val kept = tokens.filterNot { token -> token in removable }
		return if (kept.isNotEmpty()) kept else tokens
	}

	private fun stripTrailingWhenOthersRemain(tokens: List<String>, removable: Set<String>): List<String> {
		if (tokens.size < 2) {
			return tokens
		}
		var end = tokens.size
		while (end > 1 && tokens[end - 1] in removable) {
			end--
		}
		return tokens.subList(0, end)
	}

	private fun stripTrailingColorParts(tokens: List<String>): List<String> {
		if (tokens.size < 2 || tokens.none { token -> token in colorPartAnchors }) {
			return tokens
		}
		return stripTrailingWhenOthersRemain(tokens, colorPartTokens)
	}

	private fun stripTrailingDressing(tokens: List<String>): List<String> {
		val canStrip = tokens.size >= 2 &&
			tokens.last() == "dressing" &&
			tokens.any { token -> token in dressingStripTriggers }
		return if (canStrip) tokens.dropLast(1) else tokens
	}

	private fun stripTrailingLiquid(tokens: List<String>): List<String> {
		val canStrip = tokens.size >= 2 &&
			tokens.last() in trailingLiquidTokens &&
			tokens.dropLast(1).none { token -> token in beverageWaterKeepers }
		return if (canStrip) tokens.dropLast(1) else tokens
	}

	private const val MIN_TOKEN_LENGTH = 3
}
