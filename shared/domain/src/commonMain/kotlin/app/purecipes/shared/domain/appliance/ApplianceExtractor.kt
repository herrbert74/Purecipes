package app.purecipes.shared.domain.appliance

import app.purecipes.shared.domain.model.Appliance
import app.purecipes.shared.domain.model.CookingMethod

object ApplianceExtractor {

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

	private data class Rule(
		val appliance: Appliance,
		val regex: Regex,
		val matchingCookingMethods: Set<CookingMethod> = emptySet(),
	)

	private val simpleRules = listOf(
		Rule(Appliance.AIR_FRYER, airFryerRegex, setOf(CookingMethod.AIR_FRY)),
		Rule(Appliance.DEEP_FRYER, deepFryerRegex),
		Rule(Appliance.FOOD_PROCESSOR, foodProcessorRegex),
		Rule(Appliance.GRILL, grillRegex, setOf(CookingMethod.GRILL)),
		Rule(Appliance.HAND_MIXER, handMixerRegex),
		Rule(Appliance.STAND_MIXER, standMixerRegex),
		Rule(Appliance.JUICER, juicerRegex),
		Rule(Appliance.KETTLE, kettleRegex),
		Rule(Appliance.MICROWAVE, microwaveRegex, setOf(CookingMethod.MICROWAVE)),
		Rule(Appliance.PASTA_MAKER, pastaMakerRegex),
		Rule(Appliance.PRESSURE_COOKER, pressureCookerRegex, setOf(CookingMethod.PRESSURE_COOK)),
		Rule(Appliance.SANDWICH_MAKER, sandwichMakerRegex),
		Rule(Appliance.SLOW_COOKER, slowCookerRegex, setOf(CookingMethod.SLOW_COOK)),
		Rule(Appliance.SPICE_GRINDER, spiceGrinderRegex),
		Rule(Appliance.STEAMER, steamerRegex, setOf(CookingMethod.STEAM)),
		Rule(Appliance.STOVETOP, stovetopRegex, setOf(CookingMethod.STIR_FRY, CookingMethod.BOIL)),
		Rule(Appliance.WAFFLE_MAKER, waffleMakerRegex),
	)

	fun extractAppliances(
		instructions: List<String> = emptyList(),
		instructionsText: String? = null,
		title: String? = null,
		description: String? = null,
		cookingMethod: CookingMethod? = null,
	): Set<Appliance> {
		val corpus = buildCorpus(instructions, instructionsText, title, description)
		val appliances = mutableSetOf<Appliance>()

		for (rule in simpleRules) {
			if (cookingMethod in rule.matchingCookingMethods || rule.regex.containsMatchIn(corpus)) {
				appliances += rule.appliance
			}
		}

		detectBlenders(corpus, appliances)
		detectOven(corpus, cookingMethod, appliances)

		return appliances
	}

	private fun buildCorpus(
		instructions: List<String>,
		instructionsText: String?,
		title: String?,
		description: String?,
	): String = buildString {
		title?.let { append(it).append(' ') }
		description?.let { append(it).append(' ') }
		instructionsText?.let { append(it).append(' ') }
		instructions.forEach { append(it).append(' ') }
	}

	private fun detectBlenders(corpus: String, target: MutableSet<Appliance>) {
		if (immersionBlenderRegex.containsMatchIn(corpus)) {
			target += Appliance.IMMERSION_BLENDER
		}
		val textWithoutImmersion = corpus.replace(immersionBlenderRegex, " ")
		if (generalBlenderRegex.containsMatchIn(textWithoutImmersion) || blendActionRegex.containsMatchIn(corpus)) {
			target += Appliance.BLENDER
		}
	}

	private fun detectOven(corpus: String, cookingMethod: CookingMethod?, target: MutableSet<Appliance>) {
		val textWithoutDutchOven = corpus.replace(dutchOvenRegex, " ")
		val isOvenCookingMethod = cookingMethod == CookingMethod.BAKE || cookingMethod == CookingMethod.ROAST
		if (isOvenCookingMethod || ovenRegex.containsMatchIn(textWithoutDutchOven)) {
			target += Appliance.OVEN
		}
	}
}
