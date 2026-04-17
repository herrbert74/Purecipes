package com.purecipes.enrichment

import com.purecipes.shared.domain.model.CookingMethod
import com.purecipes.shared.domain.model.DietaryPreference
import com.purecipes.shared.domain.model.DifficultyLevel
import com.purecipes.shared.domain.model.MealType

internal object SeedExamples {

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
