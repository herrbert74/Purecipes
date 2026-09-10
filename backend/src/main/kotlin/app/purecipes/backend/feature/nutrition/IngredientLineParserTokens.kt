package app.purecipes.backend.feature.nutrition

import java.math.BigDecimal

internal object IngredientLineParserTokens {

	val extraWordQuantities = mapOf(
		"three" to BigDecimal("3"),
		"four" to BigDecimal("4"),
		"five" to BigDecimal("5"),
		"six" to BigDecimal("6"),
		"eight" to BigDecimal("8"),
	)

	val extraCountNounTokens = setOf(
		"ball",
		"balls",
		"bar",
		"bars",
		"block",
		"blocks",
		"chop",
		"chops",
		"cutlet",
		"cutlets",
		"drop",
		"drops",
		"drumstick",
		"drumsticks",
		"gallon",
		"gallons",
		"glass",
		"glasses",
		"jigger",
		"jiggers",
		"leg",
		"legs",
		"loaf",
		"loaves",
		"pack",
		"packs",
		"packet",
		"packets",
		"pint",
		"pints",
		"pod",
		"pods",
		"pouch",
		"pouches",
		"quart",
		"quarts",
		"rack",
		"racks",
		"sachet",
		"sachets",
		"scoop",
		"scoops",
		"shell",
		"shells",
		"shot",
		"shots",
		"splash",
		"splashes",
		"steak",
		"steaks",
		"tub",
		"tubs",
		"wing",
		"wings",
	)

	val teaspoonExtraCountNounTokens = setOf(
		"drop",
		"drops",
		"pod",
		"pods",
		"splash",
		"splashes",
	)

	val tablespoonExtraCountNounTokens = setOf(
		"jigger",
		"jiggers",
		"scoop",
		"scoops",
		"shot",
		"shots",
	)

	val cupExtraCountNounTokens = setOf(
		"glass",
		"glasses",
	)

	val extraCountableMeatTokens = setOf(
		"chop",
		"chops",
		"cutlet",
		"cutlets",
		"drumstick",
		"drumsticks",
		"filet",
		"filets",
		"fillet",
		"fillets",
		"leg",
		"legs",
		"paillard",
		"paillards",
		"steak",
		"steaks",
		"supreme",
		"supremes",
		"tenderloin",
		"tenderloins",
		"wing",
		"wings",
	)

	val cutCountNounTokens = setOf(
		"chop",
		"chops",
		"cutlet",
		"cutlets",
		"drumstick",
		"drumsticks",
		"filet",
		"filets",
		"fillet",
		"fillets",
		"leg",
		"legs",
		"paillard",
		"paillards",
		"steak",
		"steaks",
		"supreme",
		"supremes",
		"tenderloin",
		"tenderloins",
		"wing",
		"wings",
	)

	val volumeCountNounTokens = setOf(
		"drop",
		"drops",
		"glass",
		"glasses",
		"jigger",
		"jiggers",
		"shot",
		"shots",
		"splash",
		"splashes",
	)

	val poultryPrepModifierTokens = setOf(
		"boiled",
		"cooked",
		"eighths",
		"free",
		"fryer",
		"low",
		"range",
		"raw",
		"roasting",
		"rotisserie",
		"sodium",
		"whole",
	)

	val countablePoultryTokens = setOf(
		"chicken",
		"chickens",
		"duck",
		"ducks",
	)

	val wholeBirdTokens = setOf(
		"fryer",
		"roasting",
		"rotisserie",
		"whole",
	)

	val bareBreastOrThighTokens = setOf(
		"breast",
		"breasts",
		"thigh",
		"thighs",
	)

	val defaultPieceProteinTokens = setOf(
		"artichoke",
		"artichokes",
		"ham",
		"oyster",
		"oysters",
		"prawn",
		"prawns",
		"prosciutto",
		"shrimp",
		"shrimps",
		"squid",
	)

	val defaultPieceCheeseTokens = setOf(
		"burrata",
		"cheese",
		"cotija",
		"feta",
		"halloumi",
		"mascarpone",
		"mozzarella",
	)

	val defaultPieceBreadTokens = setOf(
		"baguette",
		"baguettes",
		"bap",
		"baps",
		"biscuit",
		"biscuits",
		"bread",
		"brioche",
		"challah",
		"ciabatta",
		"cookie",
		"cookies",
		"crepe",
		"crepes",
		"crostini",
		"croissant",
		"croissants",
		"crumpet",
		"crumpets",
		"ladyfinger",
		"ladyfingers",
		"oreo",
		"oreos",
		"pancake",
		"pancakes",
		"pita",
		"pitas",
		"roll",
		"rolls",
		"wonton",
		"wontons",
	)

	val defaultTablespoonNutSeedTokens = setOf(
		"almond",
		"almonds",
		"cashew",
		"cashews",
		"peanut",
		"peanuts",
		"pecan",
		"pecans",
		"pistachio",
		"pistachios",
		"poppy",
		"sesame",
		"walnut",
		"walnuts",
	)

	val defaultPiecePepperTokens = setOf(
		"habanero",
		"habaneros",
		"poblano",
		"poblanos",
		"scotch",
	)

	val defaultTablespoonSauceTokens = setOf(
		"brine",
		"chimichurri",
		"chocolate",
		"crema",
		"fat",
		"glaze",
		"gochujang",
		"guacamole",
		"marinade",
		"marinara",
		"miso",
		"paste",
		"sauce",
		"sriracha",
		"syrup",
		"tapatio",
		"verjus",
		"vinegar",
		"zhug",
	)

	val defaultTablespoonDrinkTokens = setOf(
		"ale",
		"brandy",
		"lager",
		"prosecco",
		"tequila",
		"vodka",
		"whiskey",
		"whisky",
		"wine",
	)

	val defaultTeaspoonPantryTokens = setOf(
		"dust",
		"glitter",
		"powder",
		"seasoning",
		"spice",
		"spices",
		"yeast",
	)

	val defaultCupLiquidTokens = setOf(
		"espresso",
		"ice",
		"soda",
		"tea",
		"water",
	)

	val defaultCupGrainBeanTokens = setOf(
		"butterbeans",
		"cannellini",
		"couscous",
		"drippings",
		"hominy",
		"okra",
		"spaghetti",
	)

	val defaultPiecePreparedTokens = setOf(
		"cane",
		"canes",
		"capsicum",
		"capsicums",
		"cherry",
		"cherries",
		"chip",
		"chips",
		"coleslaw",
		"crust",
		"dough",
		"flower",
		"flowers",
		"hash",
		"karaage",
		"morcilla",
		"pastry",
		"quesadilla",
		"quesadillas",
		"salad",
		"taco",
		"tacos",
		"tzatziki",
	)

	val countableMeatCutTokens = setOf(
		"brisket",
		"butt",
		"chuck",
		"ribeye",
		"roast",
		"shoulder",
		"tongue",
	)

	val orphanModifierOnlyTokens = setOf(
		"bone",
		"bone-in",
		"boiled",
		"bought",
		"each",
		"fresh",
		"frozen",
		"hard",
		"hot",
		"in",
		"kosher",
		"larger",
		"one",
		"peel",
		"red",
		"sandwich",
		"soft",
		"splash",
		"square",
		"store",
		"topping",
		"toppings",
		"total",
		"two",
		"vegetable",
		"very",
		"warm",
		"white",
		"whole",
		"yellow",
	)

}
