package com.purecipes.enrichment

import com.purecipes.shared.domain.model.CookingMethod
import com.purecipes.shared.domain.model.Cuisine
import com.purecipes.shared.domain.model.DietaryPreference
import com.purecipes.shared.domain.model.DifficultyLevel
import com.purecipes.shared.domain.model.MealType

internal object SeedExamples {

	val cuisine: Map<Cuisine, List<String>> = mapOf(
		Cuisine.AMERICAN to listOf(
			"classic american burger and fries",
			"southern mac and cheese",
			"barbecue ribs and coleslaw",
		),
		Cuisine.ARGENTINE to listOf(
			"chimichurri steak and empanadas",
			"argentine grilled beef",
			"provoleta and asado",
		),
		Cuisine.BANGLADESHI to listOf(
			"bangladeshi fish curry and bhuna",
			"dal with mustard oil and green chilli",
			"bengali style rice and curry",
		),
		Cuisine.BRAZILIAN to listOf(
			"feijoada with black beans and pork",
			"pao de queijo cheese bread",
			"brazilian grilled meat and rice",
		),
		Cuisine.BRITISH to listOf(
			"shepherds pie and gravy",
			"fish and chips",
			"yorkshire pudding roast dinner",
		),
		Cuisine.CARIBBEAN to listOf(
			"jerk chicken with rice and peas",
			"caribbean curry goat",
			"plantain and island spices",
		),
		Cuisine.CHINESE to listOf(
			"kung pao chicken stir fry",
			"dumplings with soy sauce",
			"fried rice and noodles",
		),
		Cuisine.EASTERN_EUROPEAN to listOf(
			"pierogi and cabbage",
			"goulash with paprika",
			"hearty eastern european stew",
		),
		Cuisine.ETHIOPIAN to listOf(
			"injera with lentil wats",
			"ethiopian berbere stew",
			"spiced ethiopian platter",
		),
		Cuisine.FILIPINO to listOf(
			"chicken adobo with rice",
			"filipino pancit noodles",
			"sinigang tamarind soup",
		),
		Cuisine.FRENCH to listOf(
			"coq au vin",
			"ratatouille with herbs",
			"french onion soup",
		),
		Cuisine.GERMAN to listOf(
			"bratwurst with sauerkraut",
			"german potato salad",
			"schnitzel and spaetzle",
		),
		Cuisine.GREEK to listOf(
			"greek salad with feta",
			"moussaka and tzatziki",
			"lemony souvlaki and olives",
		),
		Cuisine.INDIAN to listOf(
			"chicken tikka masala",
			"dal and naan",
			"spiced indian curry",
		),
		Cuisine.INDONESIAN to listOf(
			"nasi goreng fried rice",
			"satay skewers with peanut sauce",
			"rendang and sambal",
		),
		Cuisine.ITALIAN to listOf(
			"spaghetti carbonara",
			"margherita pizza",
			"risotto with parmesan",
		),
		Cuisine.JAPANESE to listOf(
			"miso soup and sushi",
			"teriyaki chicken",
			"japanese ramen bowl",
		),
		Cuisine.KOREAN to listOf(
			"kimchi fried rice",
			"bulgogi beef",
			"gochujang korean stew",
		),
		Cuisine.MALAYSIAN to listOf(
			"laksa noodle soup",
			"malaysian coconut curry",
			"nasi lemak with sambal",
		),
		Cuisine.MEDITERRANEAN to listOf(
			"olive oil roasted vegetables",
			"mediterranean chickpea salad",
			"grilled fish with lemon and herbs",
		),
		Cuisine.MEXICAN to listOf(
			"tacos with salsa",
			"enchiladas and beans",
			"mexican chilli and tortillas",
		),
		Cuisine.MIDDLE_EASTERN to listOf(
			"falafel and hummus",
			"shawarma with tahini",
			"middle eastern spiced rice",
		),
		Cuisine.NORTH_AFRICAN to listOf(
			"tagine with apricots",
			"harissa roasted vegetables",
			"couscous and north african spices",
		),
		Cuisine.PAKISTANI to listOf(
			"pakistani biryani",
			"nihari and naan",
			"karahi curry with spices",
		),
		Cuisine.PERUVIAN to listOf(
			"ceviche with lime",
			"peruvian roast chicken",
			"aji amarillo sauce",
		),
		Cuisine.PORTUGUESE to listOf(
			"portuguese seafood rice",
			"peri peri chicken",
			"bacalhau salt cod",
		),
		Cuisine.RUSSIAN to listOf(
			"borscht beet soup",
			"beef stroganoff",
			"russian dumplings and sour cream",
		),
		Cuisine.SPANISH to listOf(
			"paella with saffron",
			"patatas bravas",
			"spanish tortilla and tapas",
		),
		Cuisine.THAI to listOf(
			"thai green curry",
			"pad thai noodles",
			"lemongrass coconut soup",
		),
		Cuisine.TURKISH to listOf(
			"turkish kebab and pide",
			"mercimek lentil soup",
			"yogurt and sumac mezze",
		),
		Cuisine.VIETNAMESE to listOf(
			"pho noodle soup",
			"banh mi sandwich",
			"fresh vietnamese spring rolls",
		),
		Cuisine.WEST_AFRICAN to listOf(
			"jollof rice and stew",
			"west african peanut soup",
			"spicy plantain and bean dishes",
		),
	)

	val mealType: Map<MealType, List<String>> = mapOf(
		MealType.BREAKFAST to listOf(
			"scrambled eggs and toast",
			"overnight oats with fruit",
			"pancakes with maple syrup",
			"morning smoothie bowl",
			"bacon and egg breakfast burrito",
		),
		MealType.BRUNCH to listOf(
			"eggs benedict with hollandaise",
			"avocado toast with poached eggs",
			"french toast with berries",
			"smoked salmon bagel",
			"brunch frittata with vegetables",
		),
		MealType.LUNCH to listOf(
			"grilled chicken sandwich",
			"caesar salad with croutons",
			"tomato soup and grilled cheese",
			"quinoa bowl with roasted vegetables",
			"turkey and avocado wrap",
		),
		MealType.DINNER to listOf(
			"spaghetti bolognese",
			"roast chicken with vegetables",
			"beef stew with potatoes",
			"grilled salmon with asparagus",
			"chicken tikka masala with rice",
		),
		MealType.SNACK to listOf(
			"hummus with vegetable sticks",
			"trail mix with nuts and dried fruit",
			"apple slices with peanut butter",
			"cheese and crackers",
			"yogurt with granola",
		),
		MealType.DESSERT to listOf(
			"chocolate lava cake",
			"vanilla ice cream sundae",
			"strawberry cheesecake",
			"tiramisu",
			"apple pie with whipped cream",
		),
		MealType.APPETIZER to listOf(
			"bruschetta with tomato and basil",
			"shrimp cocktail",
			"stuffed mushrooms",
			"spinach artichoke dip",
			"caprese skewers",
		),
		MealType.DRINK to listOf(
			"mango smoothie",
			"lemonade with mint",
			"iced coffee",
			"green detox juice",
			"strawberry milkshake",
		),
		MealType.SIDE_DISH to listOf(
			"mashed potatoes with butter",
			"roasted garlic green beans",
			"steamed broccoli",
			"coleslaw",
			"garlic bread",
		),
	)

	val difficultyLevel: Map<DifficultyLevel, List<String>> = mapOf(
		DifficultyLevel.EASY to listOf(
			"simple five ingredient recipe",
			"quick and easy weeknight meal",
			"no cook beginner recipe",
			"microwave mug cake",
			"one pan easy dinner",
		),
		DifficultyLevel.MEDIUM to listOf(
			"requires some cooking technique",
			"homemade pasta from scratch",
			"stir fry with several components",
			"curry with toasted spices",
			"moderate effort baked dish",
		),
		DifficultyLevel.HARD to listOf(
			"advanced pastry and laminated dough",
			"multi-day fermentation and brining",
			"complex restaurant style plating",
			"precise temperature control sous vide",
			"elaborate multi-component dish",
		),
	)

	val cookingMethod: Map<CookingMethod, List<String>> = mapOf(
		CookingMethod.BAKE to listOf(
			"bake in oven at 180 degrees",
			"baked casserole",
			"oven baked cookies",
			"baked pasta dish",
		),
		CookingMethod.GRILL to listOf(
			"grilled on barbecue",
			"charcoal grilled steaks",
			"grilled vegetables on outdoor grill",
			"grilled chicken skewers",
		),
		CookingMethod.FRY to listOf(
			"deep fried in oil",
			"pan fried crispy coating",
			"shallow fry until golden",
			"fried chicken",
		),
		CookingMethod.STIR_FRY to listOf(
			"stir fry in wok over high heat",
			"quick stir fry with vegetables",
			"wok tossed noodles",
			"Chinese stir fry",
		),
		CookingMethod.SLOW_COOK to listOf(
			"slow cooker all day recipe",
			"crockpot low and slow",
			"braised overnight in slow cooker",
			"slow cooked pulled pork",
		),
		CookingMethod.STEAM to listOf(
			"steamed in bamboo basket",
			"steam vegetables until tender",
			"steamed dumplings",
			"steamed fish with ginger",
		),
		CookingMethod.BOIL to listOf(
			"boil in salted water",
			"boiled pasta",
			"boiled potatoes",
			"simmered in broth",
		),
		CookingMethod.ROAST to listOf(
			"roasted in oven with high heat",
			"roast whole chicken",
			"oven roasted root vegetables",
			"roasted leg of lamb",
		),
		CookingMethod.PRESSURE_COOK to listOf(
			"instant pot pressure cook",
			"pressure cooker beans",
			"pressure cooked stew",
			"fast pressure cooking",
		),
		CookingMethod.AIR_FRY to listOf(
			"air fryer crispy fries",
			"air fry at 200 degrees",
			"air fried chicken wings",
			"air fryer vegetables",
		),
		CookingMethod.SMOKE to listOf(
			"smoked over hickory wood",
			"low and slow smoker brisket",
			"smoked salmon",
			"wood smoked ribs",
		),
		CookingMethod.MICROWAVE to listOf(
			"microwave on high for two minutes",
			"microwave mug cake",
			"steamed in microwave",
			"microwave scrambled eggs",
		),
		CookingMethod.RAW to listOf(
			"no cooking required raw salad",
			"raw vegan dish",
			"no heat cold preparation",
			"fresh uncooked ingredients only",
		),
	)

	val dietaryPreference: Map<DietaryPreference, List<String>> = mapOf(
		DietaryPreference.VEGAN to listOf(
			"no animal products whatsoever",
			"plant-based entirely vegan",
			"vegan no meat dairy or eggs",
			"whole food plant based",
		),
		DietaryPreference.VEGETARIAN to listOf(
			"vegetarian no meat or fish",
			"contains eggs and dairy no meat",
			"lacto-ovo vegetarian",
			"meatless vegetarian meal",
		),
		DietaryPreference.GLUTEN_FREE to listOf(
			"gluten free no wheat barley rye",
			"celiac safe gluten free",
			"certified gluten free ingredients",
			"no gluten containing grains",
		),
		DietaryPreference.DAIRY_FREE to listOf(
			"dairy free no milk cheese butter",
			"lactose free no dairy",
			"non-dairy plant milk",
			"no cream or milk products",
		),
		DietaryPreference.NUT_FREE to listOf(
			"nut free no peanuts or tree nuts",
			"safe for nut allergy",
			"no almonds cashews walnuts",
			"nut-free school safe recipe",
		),
		DietaryPreference.EGG_FREE to listOf(
			"egg free no eggs in recipe",
			"no eggs or egg products",
			"egg-free baking",
			"vegan egg substitutes used",
		),
		DietaryPreference.HALAL to listOf(
			"halal certified meat",
			"no pork no alcohol halal",
			"prepared according to halal standards",
			"halal butcher sourced ingredients",
		),
		DietaryPreference.KOSHER to listOf(
			"kosher certified ingredients",
			"no pork shellfish kosher",
			"kosher pareve no meat or dairy mixed",
			"kosher preparation guidelines",
		),
		DietaryPreference.LOW_FODMAP to listOf(
			"low FODMAP IBS friendly",
			"no garlic onion high fructose low fodmap",
			"digestive health low fermentable sugars",
			"monash low FODMAP certified",
		),
		DietaryPreference.PALEO to listOf(
			"paleo no grains legumes dairy",
			"caveman diet whole foods paleo",
			"grain free dairy free paleo",
			"ancestral eating paleo diet",
		),
		DietaryPreference.KETO to listOf(
			"ketogenic very low carb high fat",
			"keto under 20g carbs per serving",
			"high fat moderate protein ketosis",
			"keto friendly no sugar no starch",
		),
	)
}
