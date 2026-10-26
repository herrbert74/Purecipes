package app.purecipes.backend.feature.nutrition

import java.text.Normalizer

internal object NutritionNameNormalizer {

	private val NON_ALPHANUMERIC = Regex("[^a-z0-9]+")

	private val SUCH_AS_CLAUSE = Regex(""",?\s*such as\b.*""", RegexOption.IGNORE_CASE)

	private val OR_SPLIT_CLAUSE = Regex("""\s+or\b(.*)""", RegexOption.IGNORE_CASE)

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

	private val ALTERNATIVELY_CLAUSE = Regex("""\s+alternatively\b.*""", RegexOption.IGNORE_CASE)

	private val ARE_USED_CLAUSE = Regex("""\s+are\s+used\b.*""", RegexOption.IGNORE_CASE)

	private val DRIZZLED_CLAUSE = Regex("""\s+drizzled\b.*""", RegexOption.IGNORE_CASE)

	private val TABLE_SALT_CLAUSE = Regex("""\s+table\s+salt\b.*""", RegexOption.IGNORE_CASE)

	private val SUGGESTED_CLAUSE = Regex("""(?i)(?:^|\s+)suggested\b.*""")

	private val SUGGESTIONS_CLAUSE = Regex("""(?i)(?:^|\s+)suggestions?\b.*""")

	private val THIS_USED_CLAUSE = Regex("""(?i)^this\b.*\bused\b.*""")

	private val PICKED_OVER_PHRASE = Regex("""(?i)\bpicked\s+over\b""")

	private val SORTED_OVER_PHRASE = Regex("""(?i)\bsorted\s+over\b""")

	private val VINE_RIPENED_PHRASE = Regex("""(?i)\bvine[\s-]*ripened\b""")

	private val BEFORE_MEASURING_PHRASE = Regex("""(?i)\bbefore\s+measuring\b""")

	private val FOR_FLOURING_PHRASE = Regex("""(?i)\bfor\s+flouring\b""")

	private val FOR_SHAPING_PHRASE = Regex("""(?i)\bfor\s+shaping\b""")

	private val FOR_CAKE_PANS_PHRASE = Regex("""(?i)\bfor\s+cake\s+pans?\b""")

	private val CREAM_OF_PHRASE = Regex("""(?i)\bcream\s+of\b""")

	private val BLUE_DRAGON_BRAND = Regex("""(?i)\bblue\s+dragon\b""")

	private val EAST_END_BRAND = Regex("""(?i)\beast\s+end\b""")

	private val HEINZ_CREATIONZ_BRAND = Regex("""(?i)\bheinz\s+creationz\b""")

	private val TEMPERATURE_LEFTOVER = Regex("""^\d+o(?:f)?$""")

	private val COMBINING_MARKS = Regex("\\p{M}+")

	private val presenceStripTokens = setOf(
		"bone",
		"bones",
		"flesh",
		"half",
		"meat",
		"msg",
		"one",
		"open",
		"pit",
		"pits",
		"skin",
		"steak",
		"steaks",
		"tail",
	)

	private val unsweetenedStripTokens = setOf(
		"unsweetened",
	)

	private val gluedOnPrefixKeepWhole = setOf(
		"onion",
		"onions",
		"only",
	)

	private val flatKeepTokens = setOf(
		"leaf",
		"leaves",
	)

	private val flatStripTokens = setOf(
		"flat",
	)

	private val sugarJunkAnchors = setOf(
		"sugar",
	)

	private val sugarJunkStripTokens = setOf(
		"cane",
		"suga",
	)

	private val lambCutAnchors = setOf(
		"lamb",
	)

	private val lambCutStripTokens = setOf(
		"boneless",
		"leg",
		"neck",
		"shoulder",
	)

	private val shrimpTailAnchors = setOf(
		"prawn",
		"prawns",
		"shrimp",
	)

	private val trailingStripTokens = setOf(
		"all",
		"extra",
		"fat",
		"off",
		"pickling",
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
		"spring",
		"tonic",
	)

	private val plainWaterModifierTokens = setOf(
		"first",
		"mineral",
		"second",
		"spring",
		"tap",
	)

	private val hotWaterKeepAnchors = setOf(
		"chili",
		"chilli",
		"paprika",
		"pepper",
		"peppers",
		"sauce",
		"smoke",
		"smoked",
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
		"outer",
		"rest",
		"third",
		"top",
		"under",
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

	private val rollAnchors = setOf(
		"roll",
		"rolls",
	)

	private val bunStripTokens = setOf(
		"bun",
		"buns",
	)

	private val PREP_WITH_TRAILING_DIGITS = Regex("""^([a-z]+)\d+$""")

	private val dressingStripTriggers = setOf(
		"juice",
		"oil",
		"vinegar",
		"wine",
	)

	private val fishPrepAnchors = setOf(
		"cod",
		"haddock",
		"halibut",
		"salmon",
		"snapper",
		"tilapia",
		"trout",
		"tuna",
	)

	private val poultryPrepAnchors = setOf(
		"chicken",
		"chickens",
		"duck",
		"ducks",
		"hen",
		"hens",
		"turkey",
		"turkeys",
	)

	private val fishPrepStripTokens = setOf(
		"boneless",
		"skinless",
	)

	private val beefBonelessAnchors = setOf(
		"beef",
		"roast",
		"sirloin",
		"steak",
		"steaks",
	)

	private val beefBonelessStripTokens = setOf(
		"boneless",
	)

	private val spiceVarietyAnchors = setOf(
		"cardamom",
	)

	private val spiceVarietyStripTokens = setOf(
		"green",
		"pod",
		"pods",
		"seeds",
	)

	private val pastryAnchors = setOf(
		"pastry",
	)

	private val pastryStripTokens = setOf(
		"frozen",
	)

	private val productNounCutTokens = setOf(
		"marinade",
		"paste",
		"zest",
	)

	private val juiceZestProductTokens = setOf(
		"juice",
		"zest",
	)

	private val citrusNameTokens = setOf(
		"grapefruit",
		"grapefruits",
		"lemon",
		"lemons",
		"lime",
		"limes",
		"orange",
		"oranges",
	)

	private val primarySeasoningTokens = setOf(
		"tajin",
	)

	private val lettuceAnchors = setOf(
		"lettuce",
		"lettuces",
	)

	private val lettuceStripTokens = setOf(
		"green",
		"greens",
	)

	private val wholeStripAnchors = setOf(
		"lemon",
		"lemons",
		"lime",
		"limes",
		"skirt",
	)

	private val wholeStripTokens = setOf(
		"whole",
	)

	private val soloHerbTokens = setOf(
		"basil",
		"chives",
		"cilantro",
		"coriander",
		"dill",
		"marjoram",
		"mint",
		"oregano",
		"parsley",
		"rosemary",
		"sage",
		"tarragon",
		"thyme",
	)

	private val herbCategoryTokens = setOf(
		"herb",
		"herbs",
	)

	private val sugarSpiceStripTokens = setOf(
		"cinnamon",
		"nutmeg",
	)

	private val grainProductTokens = setOf(
		"cornmeal",
		"flour",
		"meal",
		"polenta",
	)

	private val eggPartTokens = setOf(
		"white",
		"whites",
		"yolk",
		"yolks",
	)

	private val eggAnchorTokens = setOf(
		"egg",
		"eggs",
	)

	private val nutTokens = setOf(
		"almond",
		"almonds",
		"cashew",
		"cashews",
		"hazelnut",
		"hazelnuts",
		"macadamia",
		"macadamias",
		"peanut",
		"peanuts",
		"pecan",
		"pecans",
		"pistachio",
		"pistachios",
		"walnut",
		"walnuts",
	)

	private val breadPairAnchors = setOf(
		"bread",
		"breads",
	)

	private val breadPairSecondaries = setOf(
		"couscous",
	)

	private val producePairTokens = setOf(
		"apricot",
		"apricots",
		"avocado",
		"avocados",
		"banana",
		"bananas",
		"blueberry",
		"blueberries",
		"carrot",
		"carrots",
		"cucumber",
		"cucumbers",
		"currant",
		"currants",
		"currents",
		"fig",
		"figs",
		"grape",
		"grapes",
		"lemon",
		"lemons",
		"lettuce",
		"lettuces",
		"lime",
		"limes",
		"mango",
		"mangoes",
		"mangos",
		"onion",
		"onions",
		"orange",
		"oranges",
		"peach",
		"peaches",
		"pear",
		"pears",
		"pickle",
		"pickles",
		"plum",
		"plums",
		"radish",
		"radishes",
		"raisin",
		"raisins",
		"strawberry",
		"strawberries",
		"tomato",
		"tomatoes",
		"turnip",
		"turnips",
	)

	private val proteinFoodHeads = setOf(
		"beef",
		"chicken",
		"lamb",
		"pork",
		"turkey",
		"veal",
	)

	private val productCloserTokens = setOf(
		"butter",
		"cream",
		"oil",
		"seed",
		"seeds",
		"vinegar",
	)

	private val secondaryFoodHints = setOf(
		"bagel",
		"furikake",
		"garlic",
		"guacamole",
		"parmesan",
		"seasoning",
		"tahini",
		"vegetables",
		"yogurt",
		"yoghurt",
	)

	private val standaloneFirstFoods = setOf(
		"milk",
		"oil",
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
		val hadCreamOf = CREAM_OF_PHRASE.containsMatchIn(normalized)
		val hadGrinder = "grinder" in rawTokens
		val hadStewing = "stewing" in rawTokens
		val hadTips = "tips" in rawTokens
		val hadStem = "stem" in rawTokens || "stems" in rawTokens
		val filtered = rawTokens.filterNot { token ->
			token.all(Char::isDigit) ||
				TEMPERATURE_LEFTOVER.matches(token) ||
				token in NutritionNameNormalizerTokens.preparationTokens ||
				token in NutritionNameNormalizerTokens.measureLeftoverTokens ||
				token in NutritionNameNormalizerTokens.fillerTokens ||
				token in NutritionNameNormalizerTokens.brandTokens ||
				token in NutritionNameNormalizerTokens.packagingTokens
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
		return postFilterLookupTokens(withoutStemEnd, hadCreamOf).joinToString(" ")
	}

	fun hasMeaningfulFoodName(value: String): Boolean {
		val lookup = forLookup(value)
		if (lookup.isNotBlank()) {
			return lookup != "all"
		}
		val normalizedTokens = tokens(normalize(value)).filterNot { token -> token.all(Char::isDigit) }
		return normalizedTokens.any { token ->
			token !in NutritionNameNormalizerTokens.sizeOnlyTokens &&
				token !in NutritionNameNormalizerTokens.preparationTokens
		}
	}

	private fun stripLookupClauses(value: String): String {
		var result = SUCH_AS_CLAUSE.replace(value, "")
		result = preferCompleteOrClause(result)
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
		result = ALTERNATIVELY_CLAUSE.replace(result, "")
		result = ARE_USED_CLAUSE.replace(result, "")
		result = DRIZZLED_CLAUSE.replace(result, "")
		result = TABLE_SALT_CLAUSE.replace(result, "")
		result = SUGGESTED_CLAUSE.replace(result, "")
		result = SUGGESTIONS_CLAUSE.replace(result, "")
		result = THIS_USED_CLAUSE.replace(result, "")
		result = PICKED_OVER_PHRASE.replace(result, "")
		result = SORTED_OVER_PHRASE.replace(result, "")
		result = VINE_RIPENED_PHRASE.replace(result, "")
		result = BEFORE_MEASURING_PHRASE.replace(result, "")
		result = FOR_FLOURING_PHRASE.replace(result, "")
		result = FOR_SHAPING_PHRASE.replace(result, "")
		result = FOR_CAKE_PANS_PHRASE.replace(result, "")
		result = BLUE_DRAGON_BRAND.replace(result, " ")
		result = EAST_END_BRAND.replace(result, " ")
		result = HEINZ_CREATIONZ_BRAND.replace(result, " ")
		return result.trim()
	}

	private fun preferCompleteOrClause(value: String): String {
		val match = OR_SPLIT_CLAUSE.find(value)
		if (match == null) {
			return value
		}
		val before = value.substring(0, match.range.first).trim()
		val after = match.groupValues[1].trim()
		val beforeHasFood = clauseHasFoodToken(before)
		val afterTokens = tokens(normalize(after))
		val beforeTokens = tokens(normalize(before))
		val missingSharedFlour = beforeHasFood &&
			afterTokens.lastOrNull() == "flour" &&
			"flour" !in beforeTokens
		val preferBefore = after.isBlank() ||
			beforeHasFood ||
			!clauseHasFoodToken(after)
		return when {
			missingSharedFlour -> "$before flour"
			preferBefore -> before
			else -> after
		}
	}

	private fun clauseHasFoodToken(value: String): Boolean {
		val normalized = normalize(value)
		return tokens(normalized).any { token ->
			token !in NutritionNameNormalizerTokens.preparationTokens &&
				token !in NutritionNameNormalizerTokens.sizeOnlyTokens &&
				token !in NutritionNameNormalizerTokens.fillerTokens &&
				token !in NutritionNameNormalizerTokens.measureLeftoverTokens &&
				token !in NutritionNameNormalizerTokens.brandTokens &&
				token !in NutritionNameNormalizerTokens.packagingTokens &&
				token !in NutritionNameNormalizerTokens.incompleteOrClauseTokens &&
				!token.all(Char::isDigit)
		}
	}

	private fun postFilterLookupTokens(tokens: List<String>, hadCreamOf: Boolean): List<String> {
		var result = rewriteLookupTokens(tokens)
		result = restoreCreamOfConnector(result, hadCreamOf)
		result = stripGluedPrepAndFollowing(result)
		result = collapseRepeatedPhrase(result)
		result = dedupeAdjacentExceptHalf(result)
		result = collapseJuiceZestProduct(result)
		result = stripAfterShrimpTail(result)
		result = stripGrainCut(result)
		result = stripWhenOthersRemain(result, presenceStripTokens)
		result = stripTrailingColorParts(result)
		result = stripLettuceColor(result)
		result = stripSplitPrep(result)
		result = stripBunsWhenRolls(result)
		result = stripFlourProtein(result)
		result = stripStarterModifiers(result)
		result = stripCrustDescriptors(result)
		result = stripFishPrep(result)
		result = stripPoultryPrep(result)
		result = stripBeefBoneless(result)
		result = stripSpiceVariety(result)
		result = stripPastryFrozen(result)
		result = stripAfterProductNoun(result)
		result = stripToPrimarySeasoning(result)
		result = collapsePieCrust(result)
		result = stripWholeWithAnchor(result)
		result = stripSecondaryHerb(result)
		result = stripHerbCategoryToSolo(result)
		result = stripSecondaryNut(result)
		result = stripAfterEggPart(result)
		result = stripAfterFirstGrainProduct(result)
		result = stripTrailingSpiceAfterSugar(result)
		result = stripAfterFirstSugar(result)
		result = stripBakingLeavenerPair(result)
		result = stripAfterBreadPair(result)
		result = stripSecondaryProduce(result)
		result = stripUnsweetenedProduce(result)
		result = keepFirstCoherentFood(result)
		result = dedupeWhenSkirt(result)
		result = stripTrailingCakePans(result)
		result = stripLeadingAllWhenNotPurpose(result)
		result = stripTrailingWhenOthersRemain(result, trailingStripTokens)
		result = stripTrailingDressing(result)
		result = stripHotFromWater(result)
		result = stripPlainWaterModifiers(result)
		result = stripTrailingLiquid(result)
		result = stripOrphanFlat(result)
		result = stripSugarJunk(result)
		result = stripLambCuts(result)
		return result
	}

	private fun rewriteLookupTokens(tokens: List<String>): List<String> =
		tokens.flatMap { token ->
			when {
				token == "confectioner" -> listOf("confectioners")
				token.startsWith("on") &&
					token.length >= MIN_GLUED_ON_TOKEN_LENGTH &&
					token !in gluedOnPrefixKeepWhole -> listOf(token.removePrefix("on"))

				else -> listOf(token)
			}
		}

	private fun restoreCreamOfConnector(tokens: List<String>, hadCreamOf: Boolean): List<String> {
		val creamIndex = tokens.indexOfFirst { token -> token == "cream" }
		val canRestore = hadCreamOf &&
			creamIndex in 0 until tokens.lastIndex &&
			tokens[creamIndex + 1] != "of"
		return if (canRestore) {
			tokens.subList(0, creamIndex + 1) + "of" + tokens.subList(creamIndex + 1, tokens.size)
		} else {
			tokens
		}
	}

	private fun collapseRepeatedPhrase(tokens: List<String>): List<String> {
		if (tokens.size < MIN_REPEATED_PHRASE_TOKENS || tokens.size % 2 != 0) {
			return tokens
		}
		val mid = tokens.size / 2
		val first = tokens.subList(0, mid)
		return if (first == tokens.subList(mid, tokens.size)) first.toList() else tokens
	}

	private fun collapseJuiceZestProduct(tokens: List<String>): List<String> {
		val productIndexes = tokens.mapIndexedNotNull { index, token ->
			if (token in juiceZestProductTokens) index else null
		}
		if (productIndexes.size < 2) {
			return tokens
		}
		val zestBeforeJuice = "zest" in tokens &&
			"juice" in tokens &&
			tokens.indexOf("zest") < tokens.indexOf("juice")
		val citrus = tokens.firstOrNull { token -> token in citrusNameTokens }
		return if (zestBeforeJuice && citrus != null) {
			listOf(singularCitrus(citrus), "juice")
		} else {
			tokens.subList(0, productIndexes.first() + 1)
		}
	}

	private fun stripTrailingCakePans(tokens: List<String>): List<String> {
		val canStrip = tokens.size >= 3 &&
			tokens[tokens.lastIndex - 1] == "cake" &&
			tokens.last() == "pans"
		return if (canStrip) tokens.dropLast(2) else tokens
	}

	private fun stripHotFromWater(tokens: List<String>): List<String> {
		val canStrip = tokens.size >= 2 &&
			"hot" in tokens &&
			"water" in tokens &&
			tokens.none { token -> token in hotWaterKeepAnchors }
		return if (canStrip) tokens.filterNot { token -> token == "hot" } else tokens
	}

	private fun stripPlainWaterModifiers(tokens: List<String>): List<String> {
		val canStrip = tokens.size >= 2 &&
			"water" in tokens &&
			tokens.all { token -> token == "water" || token in plainWaterModifierTokens }
		return if (canStrip) listOf("water") else tokens
	}

	private fun stripLeadingAllWhenNotPurpose(tokens: List<String>): List<String> {
		val canStrip = tokens.size >= 2 &&
			tokens.first() == "all" &&
			tokens[1] != "purpose"
		return if (canStrip) tokens.drop(1) else tokens
	}

	private fun dedupeAdjacentExceptHalf(tokens: List<String>): List<String> {
		if (tokens.size < 2) {
			return tokens
		}
		val result = ArrayList<String>(tokens.size)
		for (token in tokens) {
			val skip = token != "half" && result.isNotEmpty() && result.last() == token
			if (!skip) {
				result.add(token)
			}
		}
		return result
	}

	private fun stripLettuceColor(tokens: List<String>): List<String> {
		val canStrip = tokens.size >= 2 &&
			tokens.any { token -> token in lettuceAnchors }
		return if (canStrip) stripWhenOthersRemain(tokens, lettuceStripTokens) else tokens
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

	private fun stripBunsWhenRolls(tokens: List<String>): List<String> {
		val canStrip = tokens.size >= 2 &&
			tokens.any { token -> token in rollAnchors }
		return if (canStrip) stripWhenOthersRemain(tokens, bunStripTokens) else tokens
	}

	private fun stripGluedPrepAndFollowing(tokens: List<String>): List<String> {
		val cutIndex = tokens.indexOfFirst { token ->
			val match = PREP_WITH_TRAILING_DIGITS.matchEntire(token)
			match != null && match.groupValues[1] in NutritionNameNormalizerTokens.preparationTokens
		}
		return if (cutIndex >= 0) tokens.subList(0, cutIndex) else tokens
	}

	private fun stripFlourProtein(tokens: List<String>): List<String> {
		val canStrip = tokens.size >= 2 &&
			tokens.any { token -> token in flourAnchors }
		return if (canStrip) stripWhenOthersRemain(tokens, flourProteinStripTokens) else tokens
	}

	private fun stripStarterModifiers(tokens: List<String>): List<String> {
		val canStrip = tokens.size >= 2 &&
			tokens.any { token -> token in NutritionNameNormalizerTokens.starterAnchorTokens }
		return if (canStrip) {
			stripWhenOthersRemain(tokens, NutritionNameNormalizerTokens.starterModifierTokens)
		} else {
			tokens
		}
	}

	private fun stripFishPrep(tokens: List<String>): List<String> {
		val canStrip = tokens.size >= 2 &&
			tokens.any { token -> token in fishPrepAnchors }
		return if (canStrip) stripWhenOthersRemain(tokens, fishPrepStripTokens) else tokens
	}

	private fun stripPoultryPrep(tokens: List<String>): List<String> {
		val canStrip = tokens.size >= 2 &&
			tokens.any { token -> token in poultryPrepAnchors }
		return if (canStrip) stripWhenOthersRemain(tokens, fishPrepStripTokens) else tokens
	}

	private fun stripBeefBoneless(tokens: List<String>): List<String> {
		val canStrip = tokens.size >= 2 &&
			tokens.any { token -> token in beefBonelessAnchors }
		return if (canStrip) stripWhenOthersRemain(tokens, beefBonelessStripTokens) else tokens
	}

	private fun stripSpiceVariety(tokens: List<String>): List<String> {
		val canStrip = tokens.size >= 2 &&
			tokens.any { token -> token in spiceVarietyAnchors }
		return if (canStrip) stripWhenOthersRemain(tokens, spiceVarietyStripTokens) else tokens
	}

	private fun stripPastryFrozen(tokens: List<String>): List<String> {
		val canStrip = tokens.size >= 2 &&
			tokens.any { token -> token in pastryAnchors }
		return if (canStrip) stripWhenOthersRemain(tokens, pastryStripTokens) else tokens
	}

	private fun stripAfterProductNoun(tokens: List<String>): List<String> {
		val cutIndex = tokens.indexOfFirst { token -> token in productNounCutTokens }
		val canCut = cutIndex in 0 until tokens.lastIndex
		return if (!canCut) {
			tokens
		} else if (tokens[cutIndex] == "zest") {
			keepCitrusAfterZest(tokens, cutIndex)
		} else {
			tokens.subList(0, cutIndex + 1)
		}
	}

	private fun keepCitrusAfterZest(tokens: List<String>, cutIndex: Int): List<String> {
		val head = tokens.subList(0, cutIndex + 1)
		val citrus = tokens.subList(cutIndex + 1, tokens.size).firstOrNull { token ->
			token in citrusNameTokens
		}
		val kept = if (citrus != null && citrus !in head) head + citrus else head
		return if ("juice" in kept) kept else citrusThenZest(kept)
	}

	private fun citrusThenZest(tokens: List<String>): List<String> {
		val citrus = tokens.firstOrNull { token -> token in citrusNameTokens } ?: return tokens
		val rest = tokens.filter { token -> token != "zest" && token !in citrusNameTokens }
		return listOf(singularCitrus(citrus), "zest") + rest
	}

	private fun singularCitrus(token: String): String =
		if (token.endsWith("s") && !token.endsWith("ss") && token.length > MIN_TOKEN_LENGTH) {
			token.removeSuffix("s")
		} else {
			token
		}

	private fun stripToPrimarySeasoning(tokens: List<String>): List<String> {
		val primary = tokens.firstOrNull { token -> token in primarySeasoningTokens }
		return if (primary != null && tokens.size > 1) listOf(primary) else tokens
	}

	private fun collapsePieCrust(tokens: List<String>): List<String> {
		val pieCount = tokens.count { token -> token == "pie" }
		val hasCrust = tokens.any { token -> token == "crust" || token == "crusts" }
		val hasDough = "dough" in tokens
		val redundant = pieCount >= 2 || (hasCrust && hasDough)
		return when {
			!redundant || "pie" !in tokens -> tokens
			hasCrust -> listOf("pie", "crust")
			else -> listOf("pie", "dough")
		}
	}

	private fun stripWholeWithAnchor(tokens: List<String>): List<String> {
		val canStrip = tokens.size >= 2 &&
			tokens.any { token -> token in wholeStripAnchors }
		return if (canStrip) stripWhenOthersRemain(tokens, wholeStripTokens) else tokens
	}

	private fun dedupeWhenSkirt(tokens: List<String>): List<String> =
		if (tokens.any { token -> token == "skirt" }) tokens.distinct() else tokens

	private fun stripSecondaryHerb(tokens: List<String>): List<String> {
		val canStrip = tokens.size == 2 &&
			tokens[0] in soloHerbTokens &&
			tokens[1] in soloHerbTokens
		return if (canStrip) listOf(tokens[0]) else tokens
	}

	private fun stripHerbCategoryToSolo(tokens: List<String>): List<String> {
		val hasCategory = tokens.any { token -> token in herbCategoryTokens }
		val firstSolo = tokens.firstOrNull { token -> token in soloHerbTokens }
		return if (hasCategory && firstSolo != null) listOf(firstSolo) else tokens
	}

	private fun stripSecondaryNut(tokens: List<String>): List<String> {
		val canStrip = tokens.size >= 2 &&
			tokens[0] in nutTokens &&
			tokens[1] in nutTokens &&
			tokens.drop(2).all { token -> token in nutTokens }
		return if (canStrip) listOf(tokens[0]) else tokens
	}

	private fun stripAfterEggPart(tokens: List<String>): List<String> {
		val eggIndex = tokens.indexOfFirst { token -> token in eggAnchorTokens }
		if (eggIndex < 0 || eggIndex >= tokens.lastIndex) {
			return tokens
		}
		val partIndex = eggIndex + 1
		val canStrip = tokens[partIndex] in eggPartTokens && partIndex < tokens.lastIndex
		return if (canStrip) tokens.subList(0, partIndex + 1) else tokens
	}

	private fun stripAfterFirstGrainProduct(tokens: List<String>): List<String> {
		val firstProduct = tokens.indexOfFirst { token -> token in grainProductTokens }
		if (firstProduct < 0) {
			return tokens
		}
		val firstKind = tokens[firstProduct]
		val hasLaterDifferent = tokens.withIndex().any { (index, token) ->
			index > firstProduct && token in grainProductTokens && token != firstKind
		}
		return if (hasLaterDifferent) tokens.subList(0, firstProduct + 1) else tokens
	}

	private fun stripTrailingSpiceAfterSugar(tokens: List<String>): List<String> {
		val sugarIndex = tokens.indexOfLast { token -> token in sugarJunkAnchors }
		val canStrip = sugarIndex >= 0 &&
			sugarIndex < tokens.lastIndex &&
			tokens.drop(sugarIndex + 1).all { token -> token in sugarSpiceStripTokens }
		return if (canStrip) tokens.subList(0, sugarIndex + 1) else tokens
	}

	private fun stripAfterFirstSugar(tokens: List<String>): List<String> {
		val firstSugar = tokens.indexOfFirst { token -> token in sugarJunkAnchors }
		if (firstSugar < 0) {
			return tokens
		}
		val hasLaterSugar = tokens.withIndex().any { (index, token) ->
			index > firstSugar && token in sugarJunkAnchors
		}
		return if (hasLaterSugar) tokens.subList(0, firstSugar + 1) else tokens
	}

	private fun stripBakingLeavenerPair(tokens: List<String>): List<String> {
		val powderIndex = tokens.indexOfFirst { token -> token == "powder" }
		val sodaIndex = tokens.indexOfFirst { token -> token == "soda" }
		val canStrip = "baking" in tokens &&
			powderIndex >= 0 &&
			sodaIndex > powderIndex
		return if (canStrip) tokens.subList(0, powderIndex + 1) else tokens
	}

	private fun stripAfterBreadPair(tokens: List<String>): List<String> {
		val breadIndex = tokens.indexOfFirst { token -> token in breadPairAnchors }
		if (breadIndex < 0) {
			return tokens
		}
		val hasSecondary = tokens.drop(breadIndex + 1).any { token -> token in breadPairSecondaries }
		return if (hasSecondary) tokens.subList(0, breadIndex + 1) else tokens
	}

	private fun stripSecondaryProduce(tokens: List<String>): List<String> {
		val headIndexes = tokens.mapIndexedNotNull { index, token ->
			if (token in producePairTokens) index else null
		}
		if (headIndexes.size < 2) {
			return tokens
		}
		val firstIndex = headIndexes.first()
		val firstHead = tokens[firstIndex]
		val canStrip = headIndexes.drop(1).none { index -> sameFoodStem(firstHead, tokens[index]) }
		return if (canStrip) tokens.subList(0, firstIndex + 1) else tokens
	}

	private fun stripUnsweetenedProduce(tokens: List<String>): List<String> {
		val canStrip = tokens.size >= 2 &&
			tokens.any { token -> token in producePairTokens }
		return if (canStrip) stripWhenOthersRemain(tokens, unsweetenedStripTokens) else tokens
	}

	private fun keepFirstCoherentFood(tokens: List<String>): List<String> {
		val cutIndex = firstCoherentFoodCutIndex(tokens)
		return if (cutIndex != null) tokens.subList(0, cutIndex + 1) else tokens
	}

	private fun firstCoherentFoodCutIndex(tokens: List<String>): Int? {
		if (tokens.size < 2) {
			return null
		}
		val candidates = ArrayList<Int>(4)
		val proteinIndexes = tokens.mapIndexedNotNull { index, token ->
			if (token in proteinFoodHeads) index else null
		}
		if (proteinIndexes.size >= 2) {
			candidates.add(proteinIndexes.first())
		}
		val closerIndex = tokens.indexOfFirst { token -> token in productCloserTokens }
		val creamOfCloser = closerIndex in 0 until tokens.lastIndex &&
			tokens[closerIndex] == "cream" &&
			tokens[closerIndex + 1] == "of"
		val canCutAtCloser = closerIndex in 0 until tokens.lastIndex && !creamOfCloser
		if (canCutAtCloser && looksLikeFoodProduct(tokens.subList(closerIndex + 1, tokens.size))) {
			candidates.add(closerIndex)
		}
		val standaloneIndex = tokens.indexOfFirst { token -> token in standaloneFirstFoods }
		if (standaloneIndex >= 0 && standaloneIndex < tokens.lastIndex &&
			looksLikeFoodProduct(tokens.subList(standaloneIndex + 1, tokens.size))
		) {
			candidates.add(standaloneIndex)
		}
		return candidates.minOrNull()
	}

	private fun looksLikeFoodProduct(tokens: List<String>): Boolean =
		tokens.any { token ->
			token in productCloserTokens ||
				token in producePairTokens ||
				token in proteinFoodHeads ||
				token in secondaryFoodHints ||
				token in standaloneFirstFoods
		}

	private fun sameFoodStem(left: String, right: String): Boolean {
		if (left == right) {
			return true
		}
		return isPluralOf(left, right) || isPluralOf(right, left)
	}

	private fun isPluralOf(singular: String, plural: String): Boolean =
		plural == "${singular}s" ||
			plural == "${singular}es" ||
			(singular.endsWith("y") && plural == "${singular.dropLast(1)}ies")

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

	private fun stripOrphanFlat(tokens: List<String>): List<String> {
		val canStrip = tokens.size >= 2 &&
			tokens.any { token -> token in flatStripTokens } &&
			tokens.none { token -> token in flatKeepTokens }
		return if (canStrip) stripWhenOthersRemain(tokens, flatStripTokens) else tokens
	}

	private fun stripSugarJunk(tokens: List<String>): List<String> {
		val canStrip = tokens.size >= 2 &&
			tokens.any { token -> token in sugarJunkAnchors }
		return if (canStrip) stripWhenOthersRemain(tokens, sugarJunkStripTokens) else tokens
	}

	private fun stripLambCuts(tokens: List<String>): List<String> {
		val canStrip = tokens.size >= 2 &&
			tokens.any { token -> token in lambCutAnchors }
		return if (canStrip) stripWhenOthersRemain(tokens, lambCutStripTokens) else tokens
	}

	private fun stripAfterShrimpTail(tokens: List<String>): List<String> {
		val shrimpIndex = tokens.indexOfFirst { token -> token in shrimpTailAnchors }
		val tailIndex = tokens.indexOf("tail")
		val canStrip = shrimpIndex >= 0 && tailIndex > shrimpIndex
		return if (canStrip) tokens.subList(0, tailIndex) else tokens
	}

	private const val MIN_GLUED_ON_TOKEN_LENGTH = 6
	private const val MIN_REPEATED_PHRASE_TOKENS = 4
	private const val MIN_TOKEN_LENGTH = 3
}
