package app.purecipes.backend.tools

import extractScrapedDietaryPreferences
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldNotContain
import kotlin.test.Test

class ScrapedDietaryPreferencesTest {

	@Test
	fun chickenRecipeIsNotVeganOrVegetarianOrPescatarian() {
		val prefs = extractScrapedDietaryPreferences(
			title = "Roasted Lemon Chicken",
			category = "Dinner, Easy",
			ingredients = listOf("500g chicken thighs", "2 tbsp olive oil", "1 lemon"),
		)

		prefs shouldNotContain "VEGAN"
		prefs shouldNotContain "VEGETARIAN"
		prefs shouldNotContain "PESCATARIAN"
		prefs shouldContainAll listOf("GLUTEN_FREE", "DAIRY_FREE", "PALEO", "KETO")
	}

	@Test
	fun veganSubstitutesAreAllowedForVeganAndDairyFreeDoesNotExemptHoney() {
		val veganPrefs = extractScrapedDietaryPreferences(
			title = "Vegan Chicken Alfredo",
			ingredients = listOf(
				"200g plant-based chicken pieces",
				"200ml oat milk",
				"2 tbsp vegan butter",
				"200g gluten-free plain flour",
			),
		)

		veganPrefs shouldContainAll listOf("VEGAN", "VEGETARIAN", "PESCATARIAN", "GLUTEN_FREE", "DAIRY_FREE")

		val honeyPrefs = extractScrapedDietaryPreferences(
			title = "Glazed Carrots",
			ingredients = listOf("300g carrots", "200ml dairy-free milk", "1 tbsp honey"),
		)

		honeyPrefs shouldContain "DAIRY_FREE"
		honeyPrefs shouldNotContain "VEGAN"
		honeyPrefs shouldContain "VEGETARIAN"
	}

	@Test
	fun paleoExcludesGrainsLegumesAndDairyButAllowsPaleoStaples() {
		val grainPrefs = extractScrapedDietaryPreferences(
			title = "Beef and Rice Bowl",
			ingredients = listOf("300g beef mince", "200g basmati rice", "100g black beans"),
		)
		grainPrefs shouldNotContain "PALEO"

		val dairyPrefs = extractScrapedDietaryPreferences(
			title = "Cheesy Chicken Bake",
			ingredients = listOf("400g chicken breast", "100g cheddar cheese"),
		)
		dairyPrefs shouldNotContain "PALEO"

		val paleoPrefs = extractScrapedDietaryPreferences(
			title = "Garlic Herb Steak with Sweet Potato and Green Beans",
			ingredients = listOf(
				"400g ribeye steak",
				"2 sweet potatoes",
				"150g green beans",
				"1 tbsp olive oil",
				"2 cloves garlic",
			),
		)
		paleoPrefs shouldContainAll listOf("PALEO", "GLUTEN_FREE", "DAIRY_FREE")
	}

	@Test
	fun gingerBeerAndGlutenFreeFlourAreGlutenFree() {
		val prefs = extractScrapedDietaryPreferences(
			title = "Crispy Cauliflower",
			ingredients = listOf("1 head cauliflower", "100g gluten-free plain flour", "100ml ginger beer"),
		)

		prefs shouldContain "GLUTEN_FREE"
	}

	@Test
	fun explicitLabelsRespectIngredientGuardsForHalalAndKosher() {
		val halalWithPork = extractScrapedDietaryPreferences(
			title = "Bacon Wrap",
			ingredients = listOf("4 rashers bacon"),
			explicitLabels = listOf("Halal"),
		)
		halalWithPork shouldNotContain "HALAL"

		val halalClean = extractScrapedDietaryPreferences(
			title = "Lamb Kebab",
			ingredients = listOf("500g lamb shoulder", "1 tsp cumin"),
			explicitLabels = listOf("Halal", "Ketogenic"),
		)
		halalClean shouldContainAll listOf("HALAL", "KETO", "PALEO")
	}
}
