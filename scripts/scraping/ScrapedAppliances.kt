private val airFryerRegex = Regex("""\bair[\s-]fry(?:er|ing|ed)?\b""", RegexOption.IGNORE_CASE)
private val immersionBlenderRegex = Regex("""\b(?:immersion|hand|stick)[\s-]blender\b""", RegexOption.IGNORE_CASE)
private val generalBlenderRegex =
	Regex("""\b(?:countertop\s|high-speed\s|pitcher\s)?blender\b""", RegexOption.IGNORE_CASE)
private val blendActionRegex = Regex("""\b(?:blend until smooth|puree in a blender)\b""", RegexOption.IGNORE_CASE)
private val deepFryerRegex = Regex("""\bdeep[\s-]fryer\b""", RegexOption.IGNORE_CASE)
private val foodProcessorRegex =
	Regex("""\b(?:food[\s-]processor|pulse in (?:a|the) processor)\b""", RegexOption.IGNORE_CASE)
private val grillRegex = Regex(
	"""\b(?:(?:outdoor|charcoal|gas|indoor)\s)?grill(?:ed|ing)?\b|\bbarbecue\b|\bbbq\b""",
	RegexOption.IGNORE_CASE,
)
private val handMixerRegex = Regex(
	"""\b(?:electric[\s-]hand|electric|hand)[\s-]mixer\b|\belectric beaters\b""",
	RegexOption.IGNORE_CASE,
)
private val standMixerRegex = Regex(
	"""\bstand[\s-]mixer\b|\bkitchenaid\b|\bpaddle attachment\b|\bdough hook\b""",
	RegexOption.IGNORE_CASE,
)
private val juicerRegex = Regex("""\b(?:citrus\s)?juicer\b|\bjuice extractor\b""", RegexOption.IGNORE_CASE)
private val kettleRegex = Regex("""\b(?:electric\s)?kettle\b""", RegexOption.IGNORE_CASE)
private val microwaveRegex = Regex("""\bmicrowave(?:d|ing|-safe)?\b""", RegexOption.IGNORE_CASE)
private val ovenRegex = Regex(
	"""\boven\b|\bpreheat(?:ed)?\b|\bbake at\b|\broast at\b|\broast in (?:the|an) oven\b|\bbroil(?:er|ed|ing)?\b""",
	RegexOption.IGNORE_CASE,
)
private val dutchOvenRegex = Regex("""\bdutch\s+oven\b""", RegexOption.IGNORE_CASE)
private val pastaMakerRegex = Regex("""\bpasta[\s-](?:maker|machine|roller)\b""", RegexOption.IGNORE_CASE)
private val pressureCookerRegex =
	Regex("""\bpressure[\s-]cook(?:er|ing)?\b|\binstant[\s-]pot\b|\bmulti[\s-]cooker\b""", RegexOption.IGNORE_CASE)
private val sandwichMakerRegex = Regex(
	"""\b(?:sandwich[\s-]maker|panini[\s-]press|toastie[\s-]maker|sandwich[\s-]press)\b""",
	RegexOption.IGNORE_CASE,
)
private val slowCookerRegex = Regex("""\bslow[\s-]cook(?:er|ing)?\b|\bcrock[\s-]?pot\b""", RegexOption.IGNORE_CASE)
private val spiceGrinderRegex = Regex("""\b(?:spice|coffee)[\s-]grinder\b""", RegexOption.IGNORE_CASE)
private val steamerRegex = Regex("""\b(?:food\s|bamboo\s)?steamer(?:\sbasket)?\b""", RegexOption.IGNORE_CASE)
private val stovetopRegex = Regex(
	"""\b(?:stove[\s-]?top|stove|hob|burner|cooktop)\b|""" +
		"""\b(?:in|into) a (?:skillet|saucepan|frying pan|wok|pot)\b|""" +
		"""\b(?:skillet|saucepan|pot|pan) over (?:medium|low|high|medium-high|medium-low) heat\b""",
	RegexOption.IGNORE_CASE,
)
private val waffleMakerRegex = Regex("""\bwaffle[\s-](?:maker|iron)\b""", RegexOption.IGNORE_CASE)

private data class ScrapedApplianceRule(
	val applianceName: String,
	val regex: Regex,
	val matchingCookingMethods: Set<String> = emptySet(),
)

private val simpleScrapedRules = listOf(
	ScrapedApplianceRule("AIR_FRYER", airFryerRegex, setOf("AIR_FRY")),
	ScrapedApplianceRule("DEEP_FRYER", deepFryerRegex),
	ScrapedApplianceRule("FOOD_PROCESSOR", foodProcessorRegex),
	ScrapedApplianceRule("GRILL", grillRegex, setOf("GRILL")),
	ScrapedApplianceRule("HAND_MIXER", handMixerRegex),
	ScrapedApplianceRule("STAND_MIXER", standMixerRegex),
	ScrapedApplianceRule("JUICER", juicerRegex),
	ScrapedApplianceRule("KETTLE", kettleRegex),
	ScrapedApplianceRule("MICROWAVE", microwaveRegex, setOf("MICROWAVE")),
	ScrapedApplianceRule("PASTA_MAKER", pastaMakerRegex),
	ScrapedApplianceRule("PRESSURE_COOKER", pressureCookerRegex, setOf("PRESSURE_COOK")),
	ScrapedApplianceRule("SANDWICH_MAKER", sandwichMakerRegex),
	ScrapedApplianceRule("SLOW_COOKER", slowCookerRegex, setOf("SLOW_COOK")),
	ScrapedApplianceRule("SPICE_GRINDER", spiceGrinderRegex),
	ScrapedApplianceRule("STEAMER", steamerRegex, setOf("STEAM")),
	ScrapedApplianceRule("STOVETOP", stovetopRegex, setOf("STIR_FRY", "BOIL")),
	ScrapedApplianceRule("WAFFLE_MAKER", waffleMakerRegex),
)

fun extractScrapedAppliances(
	instructionList: List<String> = emptyList(),
	instructions: String? = null,
	title: String? = null,
	description: String? = null,
	cookingMethod: String? = null,
): List<String> {
	val corpus = buildScrapedCorpus(instructionList, instructions, title, description)
	val appliances = mutableSetOf<String>()
	val normalizedMethod = cookingMethod?.trim()?.uppercase()

	for (rule in simpleScrapedRules) {
		if (normalizedMethod in rule.matchingCookingMethods || rule.regex.containsMatchIn(corpus)) {
			appliances += rule.applianceName
		}
	}

	detectScrapedBlenders(corpus, appliances)
	detectScrapedOven(corpus, normalizedMethod, appliances)

	return appliances.toList()
}

private fun buildScrapedCorpus(
	instructionList: List<String>,
	instructions: String?,
	title: String?,
	description: String?,
): String = buildString {
	title?.let { append(it).append(' ') }
	description?.let { append(it).append(' ') }
	instructions?.let { append(it).append(' ') }
	instructionList.forEach { append(it).append(' ') }
}

private fun detectScrapedBlenders(corpus: String, target: MutableSet<String>) {
	if (immersionBlenderRegex.containsMatchIn(corpus)) {
		target += "IMMERSION_BLENDER"
	}
	val textWithoutImmersion = corpus.replace(immersionBlenderRegex, " ")
	if (generalBlenderRegex.containsMatchIn(textWithoutImmersion) || blendActionRegex.containsMatchIn(corpus)) {
		target += "BLENDER"
	}
}

private fun detectScrapedOven(corpus: String, normalizedMethod: String?, target: MutableSet<String>) {
	val textWithoutDutchOven = corpus.replace(dutchOvenRegex, " ")
	val isOvenCookingMethod = normalizedMethod == "BAKE" || normalizedMethod == "ROAST"
	if (isOvenCookingMethod || ovenRegex.containsMatchIn(textWithoutDutchOven)) {
		target += "OVEN"
	}
}
