package app.purecipes.backend.feature.search

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class IngredientVocabularyTest {

	@Test
	fun `marks section heading lines as ignorable`() {
		IngredientVocabulary.isIgnorableIngredientLine("For the topping and salsa") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("Dough:") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("Spicy BBQ aioli:") shouldBe true
	}

	@Test
	fun `marks suggestion and preheat instruction lines as ignorable`() {
		IngredientVocabulary.isIgnorableIngredientLine("Suggestion: crusty bread") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine(
			"Suggestions: buttered noodles, toast points, biscuits",
		) shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("Serving suggestion: Crusty bread") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine(
			"Serving suggestions: Dijon mustard",
		) shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine(
			"Suggested side for chicken tetrazzini: Mixed greens",
		) shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("Preheat oven to 400 degrees F.") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("Coleslaw (to serve)") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("Chips, for serving") shouldBe true
	}

	@Test
	fun `marks instruction and specialty market notes as ignorable`() {
		IngredientVocabulary.isIgnorableIngredientLine(
			"Can be found at specialty Asian markets.",
		) shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine(
			"This all purpose chicken stock can be used in recipes that call for stock",
		) shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine(
			"Homemade Falafel , prepared through Step 2 and chilled for 15 minutes",
		) shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine(
			"Sandwich fillings of your choice (crispy smoked bacon, vine-ripened tomatoes and lettuce)",
		) shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine(
			"Aromatic herbs and vegetables of your choosing, such as yellow (see notes)",
		) shouldBe true
	}

	@Test
	fun `marks bare orphan modifiers as ignorable without ignoring real foods`() {
		IngredientVocabulary.isIgnorableIngredientLine("Kosher") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("Vegetable") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("Red") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("white") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("very large") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("hard boiled") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("1 medium white") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("1 yellow") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("2 whole") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("6 hard boiled") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("1/2 teaspoon") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("1 large head") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("1 (350 ml) bottle") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("1 Tbsp. white") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("frozen") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("total") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("warm") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("two") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("one") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("quart") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("quarts") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("store-bought") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("store bought") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("285 g store-bought") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("2 fresh, frozen") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("Oil, for griddle") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("Ice, for shaking") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("Oil, enough for deep-frying") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("Vegetable fat, for frying") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("1/2 a large white (see above)") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("One 14-ounce can (see notes)") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("1–1.2 litres hot vegetable") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("5 tbsp vegetable") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("Kosher salt") shouldBe false
		IngredientVocabulary.isIgnorableIngredientLine("Vegetable oil") shouldBe false
		IngredientVocabulary.isIgnorableIngredientLine("white rice") shouldBe false
		IngredientVocabulary.isIgnorableIngredientLine("Red onion") shouldBe false
		IngredientVocabulary.isIgnorableIngredientLine("whole milk") shouldBe false
		IngredientVocabulary.isIgnorableIngredientLine("poppy seeds") shouldBe false
		IngredientVocabulary.isIgnorableIngredientLine("low sodium beef broth") shouldBe false
		IngredientVocabulary.isIgnorableIngredientLine("store-bought pie crust") shouldBe false
		IngredientVocabulary.isIgnorableIngredientLine("A couple of stalks of rosemary") shouldBe false
		IngredientVocabulary.isIgnorableIngredientLine("one egg") shouldBe false
		IngredientVocabulary.isIgnorableIngredientLine("One 14-ounce can coconut milk") shouldBe false
	}

	@Test
	fun `does not ignore real foods that merely contain pan or pot substrings`() {
		IngredientVocabulary.isIgnorableIngredientLine("japanese karaage") shouldBe false
		IngredientVocabulary.isIgnorableIngredientLine("morcilla spanish black pudding") shouldBe false
		IngredientVocabulary.isIgnorableIngredientLine(
			"3 Yukon gold potatoes (about 1 pound), soaked in a large bowl of water",
		) shouldBe false
	}

	@Test
	fun `optional ingredient lines are not treated as ignorable`() {
		IngredientVocabulary.isIgnorableIngredientLine("optional parsley, to garnish") shouldBe false
	}

	@Test
	fun `food sheet counts are not treated as equipment`() {
		IngredientVocabulary.isIgnorableIngredientLine("graham cracker sheets") shouldBe false
		IngredientVocabulary.isIgnorableIngredientLine("baking sheet") shouldBe true
	}

	@Test
	fun `marks equipment only lines as ignorable even with sizes or quantities`() {
		IngredientVocabulary.isIgnorableIngredientLine("A 10\"-diameter springform pan") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("A 12-cup Bundt pan") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("16 bamboo skewers") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("A 2½\"-diameter biscuit cutter") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("Cling film") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine(
			"10\"-diameter tart pan (preferably with a removable bottom)",
		) shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("4 individual casserole dishes") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("2 medium bowls") shouldBe true
	}

	@Test
	fun `short ambiguous equipment keywords still ignore digit free tools`() {
		IngredientVocabulary.isIgnorableIngredientLine("mixing bowl") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("whisk") shouldBe true
	}

	@Test
	fun `treats default pantry staples as covered`() {
		IngredientVocabulary.isCoveredByAvailableIngredients(
			ingredientLine = "salt and pepper",
			availableIngredients = emptyList(),
		) shouldBe true
	}

	@Test
	fun `marks decorative processing and junk non foods as ignorable`() {
		IngredientVocabulary.isIgnorableIngredientLine("Edible gold dust") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("Edible gold glitter") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("1 tsp edible gold dust") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("sausage casings") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("Sausage casing") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("natural sausage casings") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("1 package sausage casings") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("food grade lye crystals") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("1/4 tsp food grade lye") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("bread butter table") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("bread and butter table") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("Bread and butter for the table") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("something crunchy") shouldBe true
		IngredientVocabulary.isIgnorableIngredientLine("Something crunchy") shouldBe true
	}

	@Test
	fun `does not ignore prague powder or real foods near non food phrases`() {
		IngredientVocabulary.isIgnorableIngredientLine("prague powder") shouldBe false
		IngredientVocabulary.isIgnorableIngredientLine("1 teaspoon prague powder") shouldBe false
		IngredientVocabulary.isIgnorableIngredientLine("mild italian sausage casings") shouldBe false
		IngredientVocabulary.isIgnorableIngredientLine("gold potatoes") shouldBe false
		IngredientVocabulary.isIgnorableIngredientLine("lye rolls") shouldBe false
	}
}
