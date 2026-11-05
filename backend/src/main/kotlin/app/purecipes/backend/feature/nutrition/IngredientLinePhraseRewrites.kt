package app.purecipes.backend.feature.nutrition

internal object IngredientLinePhraseRewrites {

	private const val REGEX_GROUP_FIRST = 1
	private const val REGEX_GROUP_SECOND = 2
	private const val REGEX_GROUP_THIRD = 3
	private const val REGEX_GROUP_FOURTH = 4
	private const val GRAMS_PER_PUNNET = 250

	private val leadingInstructionPattern = Regex(
		"""^to\s+(?:brush|sprinkle|top|dust|coat)\b[^,]*,\s*(.+)$""",
		RegexOption.IGNORE_CASE,
	)

	private val sizeAlternativePattern = Regex(
		"""^(\d+)\s+(?:large|medium|small)\s+or\s+\d+\s+(?:large|medium|small)\s+(.+)$""",
		RegexOption.IGNORE_CASE,
	)

	private val mobDuplicatedCutPattern = Regex(
		"""^(\d+(?:\.\d+)?)\s+(breast|breasts|thigh|thighs)\s+""" +
			"""(?:skinless\s+|boneless\s+)?chicken(?:\s+(?:breasts?|thighs?))?\b(.*)$""",
		RegexOption.IGNORE_CASE,
	)

	private val allSpicePattern = Regex(
		"""\ball\s+spice\b""",
		RegexOption.IGNORE_CASE,
	)

	private val tabascoSauceInlinePattern = Regex(
		"""\btabasco(?:®|\(r\))?(?:\s+original)?\s+sauce\b""",
		RegexOption.IGNORE_CASE,
	)

	private val anotherFlourPattern = Regex(
		"""\banother\s+flour(?:\s+of\s+your\s+choosing)?\b""",
		RegexOption.IGNORE_CASE,
	)

	private val lowSodiumChickenBrothPattern = Regex(
		"""\b((?:store-bought\s+)?low-sodium\s+chicken)\b""" +
			"""(?!\s+(?:breast|thigh|wing|drumstick|leg|meat|cutlet|stock|broth))""",
		RegexOption.IGNORE_CASE,
	)

	private val springGreensPattern = Regex(
		"""\bspring\s+greens\b""",
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
		pattern = """^(.*)[,;]\s+plus\s+(?:extra\b.*|a\s+(?:little\s+)?extra\b.*|a\s+drizzle\b.*|more\b.*|""" +
			"""\d+\s*(?:tbsp|tsp|tablespoons?|teaspoons?)\s+(?:for|to)\b.*)$""",
		options = setOf(RegexOption.IGNORE_CASE),
	)

	private val plusExtraEggPattern = Regex(
		"""^(\d+)\s+((?:medium\s+|large\s+|small\s+)?eggs?)\b.*[,;]?\s+plus\s+an?\s+extra\s+egg\b.*""",
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

	private val citrusJuiceOfPattern = Regex(
		"""^(?:fresh\s+)?juice(?:\s+and\s+(?:(?:finely\s+)?grated\s+)?zest)?\s+(?:of|if|from)\s+""" +
			"""(½|1/2|¼|1/4|\d+|one|two|three)?\s*(?:large\s+|medium\s+|small\s+)?(lemon|lime|orange)s?\b.*$""",
		RegexOption.IGNORE_CASE,
	)

	private val citrusJuicePlusZestPattern = Regex(
		"""^(?:fresh\s+)?juice\s+of\s+([½¼⅓\d/]+|\d+)\s+(lemon|lime|orange)s?[,;]?\s+plus\s+.*$""",
		RegexOption.IGNORE_CASE,
	)

	private val citrusParentheticalJuicePattern = Regex(
		"""^(?:½|1/2|\d+(?:/\d+)?)\s*(?:a\s+)?(lemon|lime|orange)s?\s*\((.+?)\s+juice\)$""",
		RegexOption.IGNORE_CASE,
	)

	private val orangeJuiceAndZestMeasurePattern = Regex(
		"""^(\d+/\d+|\d+(?:\.\d+)?)\s+(cup|tbsp|tsp)\s+fresh\s+juice\s+and\s+.*?(orange|lemon|lime).*$""",
		RegexOption.IGNORE_CASE,
	)

	private val gingerPiecePattern = Regex(
		"""^thumb-sized piece of ginger.*$""",
		RegexOption.IGNORE_CASE,
	)

	private val drizzleOliveOilPattern = Regex(
		"""^(?:drizzle|glug)\s+of\s+olive\s+oil$""",
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

	private val extraSpacesPattern = Regex("""\s+""")

	private val noisePatterns = listOf(
		Regex(""",?\s*each\s+(?:breast|fillet|piece|thigh)\b.*""", RegexOption.IGNORE_CASE),
		Regex("""\bhigher-welfare\s+""", RegexOption.IGNORE_CASE),
		Regex("""\bgood-quality\s+""", RegexOption.IGNORE_CASE),
		Regex("""\bquality\s+""", RegexOption.IGNORE_CASE),
		Regex("""\b(?:vacuum-packed|vac-packed)\s+""", RegexOption.IGNORE_CASE),
		Regex("""\bcrunchy\s+""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*diced\b""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*from\s+sustainable\s+sources\b""", RegexOption.IGNORE_CASE),
		Regex("""\s*\(ask\s+your\s+fishmonger\)""", RegexOption.IGNORE_CASE),
		Regex("""\s*\(see\s+introduction\)""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*scaled\s+and\s+pin-boned\b""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*skin\s+removed\s+and\s+bones\s+removed\b""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*bones\s+removed,?\s*skin\s+on\b""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*bone-in,?\s*skin\s+on\b""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*skin\s+on,?\s*scaled,?\s*pin-boned\b""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*(?:stems?|ribs?)\s+and\s+seeds\s+removed\b""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*(?:middle\s+)?ribs\s+removed\b""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*seeds\s+removed\b""", RegexOption.IGNORE_CASE),
		Regex("""\s+from\s+a\s+jar\s+of\s+stem\s+ginger\b.*""", RegexOption.IGNORE_CASE),
		Regex("""\bwith\s+added\s+vital\s+wheat\s+gluten\b""", RegexOption.IGNORE_CASE),
		Regex(
			"""(?:,\s*)?(?:preferably\s+|about\s+|at\s+least\s+)?(?:\d+\s+and\s+)?\d+%(?:\s+to\s+\d+%)?\s+cacao\b""",
			RegexOption.IGNORE_CASE,
		),
		Regex("""\b(?:between\s+\d+\s+and\s+)?\d+%\s+cacao\b""", RegexOption.IGNORE_CASE),
		Regex("""\b(?:at\s+least\s+)?\d+\s+percent\s+cacao\b""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*preferably\s+white\b""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*seasoning\s+packets?\s+(?:discarded|reserved)\b""", RegexOption.IGNORE_CASE),
		Regex(
			""",?\s*giblets\s+removed(?:,?\s*and\s+wing\s+tips\s+trimmed)?(?:\s+and\s+reserved\s+for\s+stock)?\b""",
			RegexOption.IGNORE_CASE,
		),
		Regex("""\blow-fat\s+or\s+nonfat\s+""", RegexOption.IGNORE_CASE),
		Regex("""\bfresh\s+or\s+frozen\s+""", RegexOption.IGNORE_CASE),
		Regex("""\bfresh,\s*frozen,?\s*or\s+dried\s+""", RegexOption.IGNORE_CASE),
		Regex("""\b(?:lacinato|curly)\s+or\s+(?:1\s+)?(?:lacinato|curly)\s+""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*roughly\s+chopped\b""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*washed\s+and\s+dried\b""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*leaves\s+picked\s+and\s+chopped\b""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*leaves\s+picked\b""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*peeled,?\s+cored\s+and\s+cut\s+into\s+chunks\b""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*peeled\s+and\s+cut\s+into\s+fine\s+matchsticks\b""", RegexOption.IGNORE_CASE),
		Regex(
			""",?\s*cored,?\s+halved,?\s+and\s+finely\s+chopped,?\s*tossed\s+in\s+lemon\s+juice\b""",
			RegexOption.IGNORE_CASE,
		),
		Regex(""",?\s*quartered\s+and\s+cored\b""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*skinned,?\s+including\s+offal\b""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*plus\s+a\s+handful\s+for\s+serving\b""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*plus\s+a\s+block\s+for\s+grating\b""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*plus\s+a\s+little\s+extra\s+for\s+greasing\b""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*plus\s+more\s+for\s+greasing(?:\s+(?:the\s+)?dish)?\b""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*brought\s+to\s+a\s+boil\b""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*shaken\s+(?:well\s+)?before\s+(?:use|using)\b""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*whisked\s+until\s+smooth\b""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*plus\s+extra\s+for\s+the\s+dish\b""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*plus\s+extra\s+for\s+dusting\b""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*plus\s+a\s+drizzle\b""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*for\s+grating\b""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*for\s+dredging\b""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*blitzed\s+to\s+a\s+pur[eé]e\b""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*seasoned\s+with\s+salt\s+and\s+(?:black\s+)?pepper\b""", RegexOption.IGNORE_CASE),
		Regex("""\s*\(cold\)""", RegexOption.IGNORE_CASE),
		Regex(
			"""(?<=(?:lemon|lime|orange)\s+juice)\s*,?\s*from\s+(?:about\s+)?""" +
				"""(?:[½¼⅓\d/]+|\d+|one|two|three)\s*(?:large\s+|medium\s+|small\s+)?(?:lime|lemonc?|orange)s?\b""",
			RegexOption.IGNORE_CASE,
		),
		Regex(
			""",\s*plus\s+[^,;]*?\b(?:for\s+serving|to\s+serve|for\s+garnish|to\s+garnish)\b.*$""",
			RegexOption.IGNORE_CASE,
		),
		Regex(
			pattern = """[,;]?\s*(?:\b(?:half\s+a|\d+/\d+|\d+|a\s+(?:couple\s+of\s+)?)\s*palm\s*ful(?:l)?s?""" +
				"""|\bpalm\s*ful(?:l)?s?)\s*$""",
			options = setOf(RegexOption.IGNORE_CASE),
		),
		Regex(""",?\s*stripped\s+and\s+finely\s+chopped\b""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*grated\s+on\s+a\s+rasp(?:,?\s*separated)?\b""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*[¼1/4]+″?\s*cut\s+off\s+tops?\s+to\s+expose\s+cloves\b""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*plus\s+a\s+pinch\s+for\s+the\s+eggwash\b""", RegexOption.IGNORE_CASE),
		Regex("""\s+and\s+freshly\s+ground\s+peppe\b""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*bottoms\s+smashed\s+under\s+the\s+flat\s+side\s+of\s+a\s+knife\b""", RegexOption.IGNORE_CASE),
		Regex("""\bwith\s+a\s+few\s+drops\s+of\s+lemon\s+juice\b""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*cracked\s+from\s+skin\s+and\s+split\b""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*roasted\s*$""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*seed(?:ed)?\s+and\s+chopped\b""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*(?:remove\s+seeds|seeds\s+removed)\b""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*(?:cored\s+and\s+)?thinly\s+sliced\s+into\s+long\s+strands\b.*$""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*(?:halved|sliced|cut)\s+[^,;]*?\bacross\b""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*blanched\s+for\s+[^,;]*?\b(?:minutes?|seconds?|mins?|secs?)\b""", RegexOption.IGNORE_CASE),
		Regex("""\bmatchstick[\s-]+cut\b""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*(?:washed\s+and\s+)?rinsed\s+well\s+of\s+grit\b""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*(?:and\s+)?soaked\s+in\s+[^,;]*?\b(?:bowl|water)\b.*$""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*beaten\s+to\s+blend\b""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*mixed\s+with\s+[^,;]*?\bwater\b""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*beaten\s+[^,;]*?\bwith\s+[^,;]*?\bwater(?:,?\s*for\s+egg\s+wash)?\b""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*for\s+egg\s+wash\b""", RegexOption.IGNORE_CASE),
		Regex("""\s*\(pre[\s-]+soaked[^)]*\)""", RegexOption.IGNORE_CASE),
		Regex("""(?i)\b(?:fat[\s-]+free|nonfat|low[\s-]+fat)\s+(?=half[\s-]+and[\s-]+half\b)"""),
		Regex(""",?\s*(?:peeled\s+)?(?:but\s+)?left\s+whole\b.*$""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*cracked\s+into\s+pieces\b.*$""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*stems\s+and\s+caps\b.*$""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*quartered\s+through\s+root\s+ends?\b.*$""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*greens\s+included\b.*$""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*(?:thinly\s+)?sliced\s+cross-?wise\b.*$""", RegexOption.IGNORE_CASE),
		Regex(""",?\s*(?:unpeeled\s+and\s+)?scrubbed\s+clean\b.*$""", RegexOption.IGNORE_CASE),
		Regex(
			""",?\s*(?:peeled\s+and\s+)?cut\s+lengthwise\s+into\s+[^,;()]+(?:fries|sticks)\b.*$""",
			RegexOption.IGNORE_CASE,
		),
		Regex(
			""",?\s*(?:peeled\s+and\s+)?chopped\s+into\s+(?:big|large|small)\s+chunks\b.*$""",
			RegexOption.IGNORE_CASE,
		),
		Regex(""",?\s*heated\s+to\s+\d+.*$""", RegexOption.IGNORE_CASE),
		Regex(
			""",?\s*plus\s+[^,;()]*\bdill\s+pickle\s+juice\b.*$""",
			RegexOption.IGNORE_CASE,
		),
	)

	private val mugWithGramsPattern = Regex(
		"""^(\d+(?:/\d+)?|½|1/2)?\s*mugs?\s+of\s+(.+?)\s*\((\d+)\s*(g|ml)\)$""",
		RegexOption.IGNORE_CASE,
	)

	private val bunchWithGramsPattern = Regex(
		"""^(\d+(?:/\d+)?|½|1/2)?\s*(?:a\s+)?(?:small\s+|large\s+)?bunch(?:es)?\s+of\s+(.+?)\s*\((\d+)\s*(g|ml)\)$""",
		RegexOption.IGNORE_CASE,
	)

	private val parentheticalWeightPattern = Regex(
		"""^(\d+)\s+(.+?)\s*\((?:about\s+)?(\d+)\s*(g|ml)\s+total\)$""",
		RegexOption.IGNORE_CASE,
	)

	private val cabbageWeightPattern = Regex(
		"""^(?:½|1/2|\d+(?:/\d+)?)\s*(?:a\s+)?red\s+cabbage\s*\((\d+)\s*(g|ml)\)$""",
		RegexOption.IGNORE_CASE,
	)

	private val packMultiplierPattern = Regex(
		"""^(\d+)\s*x\s*(\d+(?:\.\d+)?)\s*(g|ml)\s+(?:packets?|tins?|pots?|tubs?)\s+of\s+(.+)$""",
		RegexOption.IGNORE_CASE,
	)

	private val containerOfWeightPattern = Regex(
		"""^(\d+)\s+(?:jars?|tins?|pots?|tubs?|packets?|cans?)\s+of\s+(\d+(?:\.\d+)?)\s*(g|ml)\s+(.+)$""",
		RegexOption.IGNORE_CASE,
	)

	private val swigOfRumPattern = Regex(
		"""^1\s+swig\s+of\s+rum$""",
		RegexOption.IGNORE_CASE,
	)

	private val wholeNutmegPattern = Regex(
		"""^1\s+whole\s+nutmeg.*$""",
		RegexOption.IGNORE_CASE,
	)

	private val freshBreadcrumbsHandfulsPattern = Regex(
		"""^3\s+big\s+handfuls\s+of\s+fresh\s+breadcrumbs$""",
		RegexOption.IGNORE_CASE,
	)

	private val bakingPowderSlightlyHeapedPattern = Regex(
		"""^1\s+slightly\s+heaped\s+teaspoon\s+baking\s+powder$""",
		RegexOption.IGNORE_CASE,
	)

	private val heapedTeaspoonPattern = Regex(
		"""^(\d+)\s+heaped\s+teaspoons?\s+(.+)$""",
		RegexOption.IGNORE_CASE,
	)

	private val pinchesCinnamonPattern = Regex(
		"""^a\s+few\s+good\s+pinches\s+of\s+ground\s+cinnamon$""",
		RegexOption.IGNORE_CASE,
	)

	private val punnetCressPattern = Regex(
		"""^1\s+punnet\s+of\s+cress$""",
		RegexOption.IGNORE_CASE,
	)

	private val horseradishPiecePattern = Regex(
		"""^2\s*1/2\s*cm\s+piece\s+of\s+fresh\s+horseradish$""",
		RegexOption.IGNORE_CASE,
	)

	private val monkeyNutsPattern = Regex(
		"""^100\s+g\s+shelled\s+roasted\s+monkey\s+nuts\s*\(skins\s+removed\)$""",
		RegexOption.IGNORE_CASE,
	)

	private val smoothAlePattern = Regex(
		"""^500\s+ml\s+light\s+smooth\s+beer/ale$""",
		RegexOption.IGNORE_CASE,
	)

	private val vacPackedChestnutsPattern = Regex(
		"""^10-12\s+vac-packed\s+cooked\s+chestnuts$""",
		RegexOption.IGNORE_CASE,
	)

	private val capersInBrinePattern = Regex(
		"""^(\d+)\s+tablespoons?\s+baby\s+capers\s+in\s+brine$""",
		RegexOption.IGNORE_CASE,
	)

	private val spanishQueenOlivesPattern = Regex(
		"""^16\s+large\s+Spanish\s+pitted\s+queen\s+green\s+olives.*$""",
		RegexOption.IGNORE_CASE,
	)

	private val violetArtichokesPattern = Regex(
		"""^6\s+small\s+or\s+violet\s+artichokes$""",
		RegexOption.IGNORE_CASE,
	)

	private val eatingApplesCoxPattern = Regex(
		"""^6\s+or\s+7\s+small/medium\s+eating\s+apples.*$""",
		RegexOption.IGNORE_CASE,
	)

	private val britishCheeseToServePattern = Regex(
		"""^crumbly\s+British\s+cheese\s+to\s+serve$""",
		RegexOption.IGNORE_CASE,
	)

	private val extraVirginLiveOilPattern = Regex(
		"""^extra\s+virgin\s+live\s+oil$""",
		RegexOption.IGNORE_CASE,
	)

	private val springPeasHandfulsPattern = Regex(
		"""^2\s+large\s+handfuls\s+of\s+spring\s+peas$""",
		RegexOption.IGNORE_CASE,
	)

	private val saladLeavesHandfulsPattern = Regex(
		"""^2\s+large\s+handfuls\s+of\s+salad\s+leaves$""",
		RegexOption.IGNORE_CASE,
	)

	private val wildRocketHandfulPattern = Regex(
		"""^(?:1|a)\s+handful\s+of\s+wild\s+rocket.*$""",
		RegexOption.IGNORE_CASE,
	)

	private val parmesanHandfulPattern = Regex(
		"""^1\s+small\s+handful\s+of\s+freshly\s+grated\s+Parmesan\s+cheese.*$""",
		RegexOption.IGNORE_CASE,
	)

	private val parmesanForGratingPattern = Regex(
		"""^Parmesan\s+cheese,\s*for\s+grating$""",
		RegexOption.IGNORE_CASE,
	)

	private val jacobsCrackersPattern = Regex(
		"""^12\s+Jacob's\s+cream\s+crackers$""",
		RegexOption.IGNORE_CASE,
	)

	private val ciabattaBreadPattern = Regex(
		"""^8\s+slices\s+of\s+ciabatta\s+bread$""",
		RegexOption.IGNORE_CASE,
	)

	private val buffaloMozzarellaBallsPattern = Regex(
		"""^2\s+balls\s+of\s+buffalo\s+mozzarella$""",
		RegexOption.IGNORE_CASE,
	)

	private val vinSantoBottlePattern = Regex(
		"""^1\s+bottle\s+of\s+Vin\s+Santo.*$""",
		RegexOption.IGNORE_CASE,
	)

	private val biscottiPackPattern = Regex(
		"""^1\s+pack\s+of\s+biscotti$""",
		RegexOption.IGNORE_CASE,
	)

	private val sherryOrPortPattern = Regex(
		"""^60\s+ml\s+sherry\s+or\s+port$""",
		RegexOption.IGNORE_CASE,
	)

	private val tabascoSaucePattern = Regex(
		"""^Tabasco\s+sauce$""",
		RegexOption.IGNORE_CASE,
	)

	private val hotVegetableStockPattern = Regex(
		"""^(\d+)\s+ml\s+hot\s+vegetable\s+stock$""",
		RegexOption.IGNORE_CASE,
	)

	private val prosciuttoHalvedPattern = Regex(
		"""^6\s+prosciutto\s+slices,?\s*halved$""",
		RegexOption.IGNORE_CASE,
	)

	private val bunchMarjoramLeavesPattern = Regex(
		"""^a\s+small\s+bunch\s+of\s+fresh\s+marjoram.*$""",
		RegexOption.IGNORE_CASE,
	)

	private val basicRisottoRecipePattern = Regex(
		"""^1\s*x\s*(?:basic\s+risotto\s+recipe|risotto\s+bianco)$""",
		RegexOption.IGNORE_CASE,
	)

	private val cremeFraicheToServePattern = Regex(
		"""^cr[èe]me\s+fra[îi]che,?\s+to\s+serve$""",
		RegexOption.IGNORE_CASE,
	)

	private val crostiniPattern = Regex(
		"""^crostini$""",
		RegexOption.IGNORE_CASE,
	)

	private val loafRusticBreadPattern = Regex(
		"""^(?:a\s+)?loaf\s+of\s+rustic\s+bread,?\s+to\s+serve$""",
		RegexOption.IGNORE_CASE,
	)

	private val sprigEachOfHerbsPattern = Regex(
		"""^1\s+sprig\s+each\s+of\s+fresh\s+mint,?\s+basil\s+and\s+rosemary$""",
		RegexOption.IGNORE_CASE,
	)

	private val vegetableOilForFryingPattern = Regex(
		"""^vegetable\s+oil,?\s+for\s+frying$""",
		RegexOption.IGNORE_CASE,
	)

	private val wineGlassesVermouthPattern = Regex(
		"""^(\d+(?:/\d+)?|½|1/2)?\s*wine\s+glasses?\s+of\s+dry\s+white\s+vermouth.*$""",
		RegexOption.IGNORE_CASE,
	)

	private val zestOfLemoPattern = Regex(
		"""^Finely grated zest of 1 lemo$""",
		RegexOption.IGNORE_CASE,
	)

	private val raspberryMeshPattern = Regex(
		"""^2 tsp the raspberry juice from about 1/2 cup frozen raspberries melted and pressed thru a mesh strainer$""",
		RegexOption.IGNORE_CASE,
	)

	private val bulkSweetSausagePattern = Regex(
		"""^(\d+.*?)\s+bulk\s+sweet$""",
		RegexOption.IGNORE_CASE,
	)

	private val preSlicedMozzarellaPattern = Regex(
		"""^5\s+ounces\s+pre[\s-]+sliced\s+thin.*$""",
		RegexOption.IGNORE_CASE,
	)

	private val tonsCilantroPattern = Regex(
		"""^Tons\s+of\s+chopped\s+fresh\s+cilantro$""",
		RegexOption.IGNORE_CASE,
	)

	private val persianLeeksPattern = Regex(
		"""^4\s+1/2\s+ounces\s+\(125\s+g\)\s+fresh\s+Persian\s+leekschives.*$""",
		RegexOption.IGNORE_CASE,
	)

	private val inclusionsChocolatePattern = Regex(
		"""^200\s+g\s+Inclusions\s*\(milk\s+and\s+white\s+chocolate\s+chips\)$""",
		RegexOption.IGNORE_CASE,
	)

	private val inclusionsMixturePattern = Regex(
		"""^300\s+g\s+Inclusions\s*mixture\s*\(.*chocolate\s+chips\)$""",
		RegexOption.IGNORE_CASE,
	)

	private val otherFieldCornPattern = Regex(
		"""^60\s+grams\s+other\s+field\s+corn\s*\(rounded\s+1/2\s+cup\)$""",
		RegexOption.IGNORE_CASE,
	)

	private val otherVegetableSpinachPattern = Regex(
		"""^470\s+grams\s+other\s+vegetable\s+like\s+spinach$""",
		RegexOption.IGNORE_CASE,
	)

	fun rewrite(value: String): String? {
		val trimmed = value.trim()
		val withBroth = if (lowSodiumChickenBrothPattern.containsMatchIn(trimmed)) {
			lowSodiumChickenBrothPattern.replace(trimmed, "$1 broth")
		} else {
			trimmed
		}
		val withAllspice = if (allSpicePattern.containsMatchIn(withBroth)) {
			allSpicePattern.replace(withBroth, "allspice")
		} else {
			withBroth
		}
		val withTabasco = if (tabascoSauceInlinePattern.containsMatchIn(withAllspice)) {
			tabascoSauceInlinePattern.replace(withAllspice, "chili sauce")
		} else {
			withAllspice
		}
		val withAnotherFlour = if (anotherFlourPattern.containsMatchIn(withTabasco)) {
			anotherFlourPattern.replace(withTabasco, "all-purpose flour")
		} else {
			withTabasco
		}
		val withSpringGreens = if (springGreensPattern.containsMatchIn(withAnotherFlour)) {
			springGreensPattern.replace(withAnotherFlour, "cabbage")
		} else {
			withAnotherFlour
		}
		val withTypos = withSpringGreens
			.replace(Regex("""\bckickpeas\b""", RegexOption.IGNORE_CASE), "chickpeas")
			.replace(Regex("""\bcorainder\b""", RegexOption.IGNORE_CASE), "coriander")
			.replace(Regex("""\bmaynnoaise\b""", RegexOption.IGNORE_CASE), "mayonnaise")
			.replace(Regex("""\bParma\s+han\b""", RegexOption.IGNORE_CASE), "Parma ham")
		val cleaned = stripNoisePhrases(withTypos)
		val target = cleaned ?: withTypos
		val rewritten = rewriteEarlyClauses(target)
			?: rewritePrepNoise(target)
			?: rewriteCountAndVolume(target)
			?: rewriteBritishSpecialties(target)
		return rewritten ?: if (withTypos != trimmed) target else cleaned
	}

	private fun stripNoisePhrases(value: String): String? {
		var current = value
		for (pattern in noisePatterns) {
			current = pattern.replace(current, " ")
		}
		current = extraSpacesPattern.replace(current, " ").trim().trimEnd(',', ';')
		return if (current != value) current else null
	}

	private fun rewritePlusExtraEgg(trimmed: String): String? {
		val match = plusExtraEggPattern.matchEntire(trimmed) ?: return null
		val count = (match.groupValues[REGEX_GROUP_FIRST].toIntOrNull() ?: 1) + 1
		val eggNoun = match.groupValues[REGEX_GROUP_SECOND].trim()
		val pluralEgg = if (count > 1 && !eggNoun.endsWith("s", ignoreCase = true)) "${eggNoun}s" else eggNoun
		return "$count $pluralEgg"
	}

	private fun rewriteEarlyClauses(trimmed: String): String? {
		val plusExtraEgg = rewritePlusExtraEgg(trimmed)
		val instruction = leadingInstructionPattern.matchEntire(trimmed)
		val sizeAlternative = sizeAlternativePattern.matchEntire(trimmed)
		val mobDuplicatedCut = mobDuplicatedCutPattern.matchEntire(trimmed)
		val slicedFruit = slicedFruitPattern.matchEntire(trimmed)
		val perItemRewrite = countWithPerItemWeightPattern.matchEntire(trimmed)?.let { formatPerItemWeight(it) }
		val extraTrailing = trailingPlusExtraPattern.matchEntire(trimmed)
		return when {
			trimmed == "500 g small new" -> "500 g small new potatoes"
			trimmed == "1 medium head of green (about 2 lb. total)" ->
				"1 medium head of green cabbage (about 2 lb. total)"

			trimmed == "8 slices grilled" -> "8 slices challah bread"
			trimmed == "250 g warmed in microwave" -> "250 g rotisserie chicken meat"
			plusExtraEgg != null -> plusExtraEgg
			plumsStonedPattern.matches(trimmed) -> "2 plums"
			perItemRewrite != null -> perItemRewrite
			countableJuniperBerriesPattern.matches(trimmed) -> "1 tsp juniper berries"
			countableCapersPattern.matches(trimmed) -> "1 tbsp capers"
			instruction != null -> instruction.groupValues[REGEX_GROUP_FIRST].trim()
			sizeAlternative != null -> listOf(
				sizeAlternative.groupValues[REGEX_GROUP_FIRST],
				sizeAlternative.groupValues[REGEX_GROUP_SECOND].trim(),
			).joinToString(" ")

			mobDuplicatedCut != null -> {
				val cutWord = mobDuplicatedCut.groupValues[REGEX_GROUP_SECOND].lowercase()
				val pluralCut = if (cutWord.startsWith("thigh")) "chicken thighs" else "chicken breasts"
				listOf(
					mobDuplicatedCut.groupValues[REGEX_GROUP_FIRST],
					pluralCut + mobDuplicatedCut.groupValues[REGEX_GROUP_THIRD],
				).joinToString(" ").trim()
			}

			slicedFruit != null -> "1 cup $trimmed"
			extraTrailing != null &&
				NutritionNameNormalizer.hasMeaningfulFoodName(extraTrailing.groupValues[REGEX_GROUP_FIRST]) ->
				extraTrailing.groupValues[REGEX_GROUP_FIRST].trim()

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
		val citrusJuiceOf = citrusJuiceOfPattern.matchEntire(trimmed)
		val citrusJuicePlus = citrusJuicePlusZestPattern.matchEntire(trimmed)
		val citrusParenJuice = citrusParentheticalJuicePattern.matchEntire(trimmed)
		val orangeMeasureJuice = orangeJuiceAndZestMeasurePattern.matchEntire(trimmed)
		val bulkSweet = bulkSweetSausagePattern.matchEntire(trimmed)
		return when {
			preSlicedMozzarellaPattern.matches(trimmed) -> "5 ounces pre-sliced thin mozzarella"
			bulkSweet != null -> "${bulkSweet.groupValues[REGEX_GROUP_FIRST]} bulk sweet Italian sausage"
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
			citrusJuicePlus != null -> listOf(
				citrusJuicePlus.groupValues[REGEX_GROUP_FIRST],
				citrusJuicePlus.groupValues[REGEX_GROUP_SECOND],
				"juice",
			).joinToString(" ")

			citrusParenJuice != null -> listOf(
				citrusParenJuice.groupValues[REGEX_GROUP_SECOND],
				citrusParenJuice.groupValues[REGEX_GROUP_FIRST],
				"juice",
			).joinToString(" ")

			orangeMeasureJuice != null ->
				listOf(
					orangeMeasureJuice.groupValues[REGEX_GROUP_FIRST],
					orangeMeasureJuice.groupValues[REGEX_GROUP_SECOND],
					orangeMeasureJuice.groupValues[REGEX_GROUP_THIRD],
					"juice",
				).joinToString(" ")

			citrusJuiceOf != null -> {
				val rawQty = citrusJuiceOf.groupValues[REGEX_GROUP_FIRST].lowercase()
				val count = when (rawQty) {
					"two" -> "2"
					"three" -> "3"
					"one", "" -> "1"
					else -> citrusJuiceOf.groupValues[REGEX_GROUP_FIRST]
				}
				"$count ${citrusJuiceOf.groupValues[REGEX_GROUP_SECOND]} juice"
			}

			else -> null
		}
	}

	private fun rewriteBritishSpecialties(trimmed: String): String? {
		val mug = mugWithGramsPattern.matchEntire(trimmed)
		val bunch = bunchWithGramsPattern.matchEntire(trimmed)
		val weight = parentheticalWeightPattern.matchEntire(trimmed)
		val cabbage = cabbageWeightPattern.matchEntire(trimmed)
		val pack = packMultiplierPattern.matchEntire(trimmed) ?: containerOfWeightPattern.matchEntire(trimmed)
		val heaped = heapedTeaspoonPattern.matchEntire(trimmed)
		val capers = capersInBrinePattern.matchEntire(trimmed)
		val stock = hotVegetableStockPattern.matchEntire(trimmed)
		return when {
			mug != null -> listOf(
				mug.groupValues[REGEX_GROUP_THIRD],
				mug.groupValues[REGEX_GROUP_FOURTH],
				mug.groupValues[REGEX_GROUP_SECOND].trim(),
			).joinToString(" ")

			bunch != null -> listOf(
				bunch.groupValues[REGEX_GROUP_THIRD],
				bunch.groupValues[REGEX_GROUP_FOURTH],
				bunch.groupValues[REGEX_GROUP_SECOND].trim(),
			).joinToString(" ")

			weight != null -> listOf(
				weight.groupValues[REGEX_GROUP_THIRD],
				weight.groupValues[REGEX_GROUP_FOURTH],
				weight.groupValues[REGEX_GROUP_SECOND].trim(),
			).joinToString(" ")

			cabbage != null -> listOf(
				cabbage.groupValues[REGEX_GROUP_FIRST],
				cabbage.groupValues[REGEX_GROUP_SECOND],
				"red cabbage",
			).joinToString(" ")

			pack != null -> formatPackMultiplier(pack)
			heaped != null -> listOf(
				heaped.groupValues[REGEX_GROUP_FIRST],
				"tsp",
				heaped.groupValues[REGEX_GROUP_SECOND].trim(),
			).joinToString(" ")

			capers != null -> "${capers.groupValues[REGEX_GROUP_FIRST]} tbsp capers"
			stock != null -> "${stock.groupValues[REGEX_GROUP_FIRST]} ml vegetable stock"
			swigOfRumPattern.matches(trimmed) -> "1 tbsp rum"
			wholeNutmegPattern.matches(trimmed) -> "1 piece nutmeg"
			freshBreadcrumbsHandfulsPattern.matches(trimmed) -> "75 g fresh breadcrumbs"
			bakingPowderSlightlyHeapedPattern.matches(trimmed) -> "1 tsp baking powder"
			pinchesCinnamonPattern.matches(trimmed) -> "1 pinch ground cinnamon"
			punnetCressPattern.matches(trimmed) -> "50 g cress"
			horseradishPiecePattern.matches(trimmed) -> "15 g fresh horseradish"
			monkeyNutsPattern.matches(trimmed) -> "100 g shelled roasted peanuts"
			smoothAlePattern.matches(trimmed) -> "500 ml light ale"
			vacPackedChestnutsPattern.matches(trimmed) -> "11 piece chestnuts"
			spanishQueenOlivesPattern.matches(trimmed) -> "16 green olives"
			violetArtichokesPattern.matches(trimmed) -> "6 artichokes"
			eatingApplesCoxPattern.matches(trimmed) -> "6 eating apples"
			britishCheeseToServePattern.matches(trimmed) -> "100 g British cheese"
			extraVirginLiveOilPattern.matches(trimmed) -> "1 tbsp extra virgin olive oil"
			springPeasHandfulsPattern.matches(trimmed) -> "150 g spring peas"
			saladLeavesHandfulsPattern.matches(trimmed) -> "50 g salad leaves"
			wildRocketHandfulPattern.matches(trimmed) -> "25 g wild rocket"
			parmesanHandfulPattern.matches(trimmed) -> "25 g Parmesan cheese"
			parmesanForGratingPattern.matches(trimmed) -> "1 tbsp Parmesan cheese"
			jacobsCrackersPattern.matches(trimmed) -> "12 cream crackers"
			ciabattaBreadPattern.matches(trimmed) -> "8 slices ciabatta"
			buffaloMozzarellaBallsPattern.matches(trimmed) -> "250 g mozzarella"
			vinSantoBottlePattern.matches(trimmed) -> "750 ml Vin Santo"
			biscottiPackPattern.matches(trimmed) -> "200 g biscotti"
			sherryOrPortPattern.matches(trimmed) -> "60 ml sherry"
			tabascoSaucePattern.matches(trimmed) -> "1 tsp chili sauce"
			prosciuttoHalvedPattern.matches(trimmed) -> "6 slices prosciutto"
			bunchMarjoramLeavesPattern.matches(trimmed) -> "1 small bunch fresh marjoram"
			basicRisottoRecipePattern.matches(trimmed) -> "400 g risotto rice"
			cremeFraicheToServePattern.matches(trimmed) -> "2 tbsp creme fraiche"
			crostiniPattern.matches(trimmed) -> "12 crostini"
			loafRusticBreadPattern.matches(trimmed) -> "500 g white bread"
			sprigEachOfHerbsPattern.matches(trimmed) -> "1 sprig fresh mint"
			vegetableOilForFryingPattern.matches(trimmed) -> "2 tbsp vegetable oil"
			wineGlassesVermouthPattern.matches(trimmed) -> {
				val qty = wineGlassesVermouthPattern.matchEntire(trimmed)?.groupValues?.getOrNull(1)
				if (qty == "2") "300 ml dry vermouth" else "150 ml dry vermouth"
			}

			zestOfLemoPattern.matches(trimmed) -> "finely grated zest of 1 lemon"
			raspberryMeshPattern.matches(trimmed) -> "2 tsp raspberry juice"
			tonsCilantroPattern.matches(trimmed) -> "1 handful chopped fresh cilantro"
			persianLeeksPattern.matches(trimmed) -> "4 1/2 ounces fresh Persian leeks"
			inclusionsChocolatePattern.matches(trimmed) -> "200 g chocolate chips"
			inclusionsMixturePattern.matches(trimmed) -> "300 g chocolate chips"
			otherFieldCornPattern.matches(trimmed) -> "60 grams cornmeal"
			otherVegetableSpinachPattern.matches(trimmed) -> "470 grams spinach"

			else -> null
		}
	}

	private fun formatPackMultiplier(match: MatchResult): String? {
		val count = match.groupValues[REGEX_GROUP_FIRST].toDoubleOrNull()
		val size = match.groupValues[REGEX_GROUP_SECOND].toDoubleOrNull()
		val unit = match.groupValues[REGEX_GROUP_THIRD]
		val rawItem = match.groupValues[REGEX_GROUP_FOURTH].trim()
		val item = when {
			rawItem.equals("ready-rolled all-butter puff pastry", ignoreCase = true) -> "puff pastry"
			rawItem.equals(
				"Fish Tales jack mackerel in sunflower oil",
				ignoreCase = true,
			) -> "mackerel in sunflower oil"

			rawItem.equals("Fish Tales anchovies in olive oil", ignoreCase = true) -> "anchovies in olive oil"
			rawItem.equals("Fish Tales tuna in olive oil", ignoreCase = true) -> "tuna in olive oil"
			rawItem.equals("Jamie Oliver supergrains", ignoreCase = true) -> "grains"
			else -> rawItem
		}
		return if (count != null && size != null) {
			"${(count * size).toInt()} $unit $item"
		} else {
			null
		}
	}

	private fun formatPerItemWeight(match: MatchResult): String? {
		val count = match.groupValues[REGEX_GROUP_FIRST].toDoubleOrNull()
		val weight = match.groupValues[REGEX_GROUP_THIRD].toDoubleOrNull()
		val unit = match.groupValues[REGEX_GROUP_FOURTH]
		val item = match.groupValues[REGEX_GROUP_SECOND].trim()
		return if (count != null && weight != null) {
			"${(count * weight).toInt()} $unit $item"
		} else {
			null
		}
	}
}
