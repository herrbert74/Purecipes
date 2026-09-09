package app.purecipes.backend.feature.nutrition

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class NutritionNameNormalizerTest {

	@Test
	fun forLookupStripsPreparationWordsAndInstructionClauses() {
		NutritionNameNormalizer.forLookup("freshly ground black pepper") shouldBe "black pepper"
		NutritionNameNormalizer.forLookup(
			"Diamond Crystal kosher salt; for table salt, use half as much by volume",
		) shouldBe "diamond crystal kosher salt"
		NutritionNameNormalizer.forLookup("olive oil, divided") shouldBe "olive oil"
		NutritionNameNormalizer.forLookup("unsalted butter, melted") shouldBe "unsalted butter"
		NutritionNameNormalizer.forLookup("unsalted butter, room temperature") shouldBe "unsalted butter"
		NutritionNameNormalizer.forLookup("homemade chicken stock") shouldBe "chicken stock"
		NutritionNameNormalizer.forLookup("eggs, beaten") shouldBe "eggs"
		NutritionNameNormalizer.forLookup("lime, juiced") shouldBe "lime"
		NutritionNameNormalizer.forLookup("neutral oil such as canola") shouldBe "neutral oil"
		NutritionNameNormalizer.forLookup("double-concentrated tomato paste") shouldBe "double tomato paste"
		NutritionNameNormalizer.forLookup("chickpeas, drained") shouldBe "chickpeas"
		NutritionNameNormalizer.forLookup("boneless skinless chicken breast halves") shouldBe
			"boneless skinless chicken breast"
		NutritionNameNormalizer.forLookup("unsalted butter thick") shouldBe "unsalted butter"
		NutritionNameNormalizer.forLookup("carrots inch thick rounds") shouldBe "carrots"
		NutritionNameNormalizer.forLookup("chicken thigh bone removed") shouldBe "chicken thigh bone"
		NutritionNameNormalizer.forLookup("inch knob ginger") shouldBe "ginger"
		NutritionNameNormalizer.forLookup("boneless skinless chicken breasts bite sized") shouldBe
			"boneless skinless chicken breasts"
	}

	@Test
	fun forLookupStripsMeasureLeftoversAndLeafStemWords() {
		NutritionNameNormalizer.forLookup("yellow onion about 8 ounces") shouldBe "yellow onion"
		NutritionNameNormalizer.forLookup("unsalted butter ounces") shouldBe "unsalted butter"
		NutritionNameNormalizer.forLookup("parsley leaves") shouldBe "parsley"
		NutritionNameNormalizer.forLookup("cilantro leaves and tender stems") shouldBe "cilantro"
		NutritionNameNormalizer.forLookup("bay leaves") shouldBe "bay"
		NutritionNameNormalizer.forLookup("sprigs rosemary") shouldBe "rosemary"
	}

	@Test
	fun forLookupStripsFillerAndInstructionLeftovers() {
		NutritionNameNormalizer.forLookup("diamond crystal kosher salt more taste") shouldBe
			"diamond crystal kosher salt"
		NutritionNameNormalizer.forLookup("black pepper more taste") shouldBe "black pepper"
		NutritionNameNormalizer.forLookup("kosher salt taste") shouldBe "kosher salt"
		NutritionNameNormalizer.forLookup("unsalted butter cut into pieces") shouldBe "unsalted butter"
		NutritionNameNormalizer.forLookup("chilled unsalted butter cut into pieces") shouldBe "unsalted butter"
		NutritionNameNormalizer.forLookup("eggs separated") shouldBe "eggs"
		NutritionNameNormalizer.forLookup("olive oil for serving") shouldBe "olive oil"
		NutritionNameNormalizer.forLookup("parsley optional") shouldBe "parsley"
		NutritionNameNormalizer.forLookup("flour for dusting surface") shouldBe "flour"
		NutritionNameNormalizer.forLookup("oil for frying") shouldBe "oil"
		NutritionNameNormalizer.forLookup("honey for drizzling") shouldBe "honey"
		NutritionNameNormalizer.forLookup("breadcrumbs for dredging") shouldBe "breadcrumbs"
		NutritionNameNormalizer.forLookup("cilantro for garnish") shouldBe "cilantro"
		NutritionNameNormalizer.forLookup("tomatoes rinsed deseeded") shouldBe "tomatoes"
		NutritionNameNormalizer.forLookup("cold water") shouldBe "water"
		NutritionNameNormalizer.forLookup("extra virgin olive oil liberal") shouldBe "extra virgin olive oil"
		NutritionNameNormalizer.forLookup("loosely packed flat leaf parsley") shouldBe "flat leaf parsley"
		NutritionNameNormalizer.forLookup("store bought passata pomodoro") shouldBe "passata pomodoro"
		NutritionNameNormalizer.forLookup("lime juice from limes") shouldBe "lime juice limes"
		NutritionNameNormalizer.forLookup("garlic left whole") shouldBe "garlic whole"
		NutritionNameNormalizer.forLookup("garlic crosswise") shouldBe "garlic"
		NutritionNameNormalizer.forLookup("strawberries hulled") shouldBe "strawberries"
		NutritionNameNormalizer.forLookup("chinese broccoli bias") shouldBe "chinese broccoli"
		NutritionNameNormalizer.forLookup("slices bacon") shouldBe "bacon"
		NutritionNameNormalizer.forLookup("bacon slices") shouldBe "bacon"
		NutritionNameNormalizer.forLookup("frozen peas couple") shouldBe "frozen peas"
		NutritionNameNormalizer.forLookup("defrosted frozen peas") shouldBe "frozen peas"
		NutritionNameNormalizer.forLookup("pure pumpkin puree") shouldBe "pumpkin puree"
		NutritionNameNormalizer.forLookup("almond flour sifted") shouldBe "almond flour"
		NutritionNameNormalizer.forLookup("butternut squash cubes") shouldBe "butternut squash"
		NutritionNameNormalizer.forLookup("black beans undrained") shouldBe "black beans"
		NutritionNameNormalizer.forLookup("salt sprinkling") shouldBe "salt"
		NutritionNameNormalizer.forLookup("extra virgin olive oil turns the pan") shouldBe
			"extra virgin olive oil"
		NutritionNameNormalizer.forLookup("all purpose flour cook") shouldBe "all purpose flour"
		NutritionNameNormalizer.forLookup("all purpose flour rolling") shouldBe "all purpose flour"
		NutritionNameNormalizer.forLookup("all purpose flour spooned") shouldBe "all purpose flour"
		NutritionNameNormalizer.forLookup("all purpose flour extra flour rolling") shouldBe
			"all purpose flour extra flour"
		NutritionNameNormalizer.forLookup("celery sticks") shouldBe "celery"
		NutritionNameNormalizer.forLookup("white sandwich bread crusts") shouldBe "white sandwich bread"
		NutritionNameNormalizer.forLookup("skinless boneless chicken breast pounded") shouldBe
			"skinless boneless chicken breast"
		NutritionNameNormalizer.forLookup("carrots julienned") shouldBe "carrots"
		NutritionNameNormalizer.forLookup("butter extra greasing") shouldBe "butter extra"
		NutritionNameNormalizer.forLookup("cinnamon stick broken small") shouldBe "cinnamon"
		NutritionNameNormalizer.forLookup("ancho chile hand") shouldBe "ancho chile"
	}

	@Test
	fun forLookupKeepsFoodMeaningfulTokens() {
		NutritionNameNormalizer.forLookup("flat leaf parsley") shouldBe "flat leaf parsley"
		NutritionNameNormalizer.forLookup("bay leaf") shouldBe "bay leaf"
		NutritionNameNormalizer.forLookup("black pepper") shouldBe "black pepper"
		NutritionNameNormalizer.forLookup("sesame seeds") shouldBe "sesame seeds"
	}

	@Test
	fun forLookupFoldsUnicodeAndDropsBareNumbers() {
		NutritionNameNormalizer.forLookup("pickled jalapeño") shouldBe "pickled jalapeno"
		NutritionNameNormalizer.forLookup("jalapeño peppers") shouldBe "jalapeno peppers"
		NutritionNameNormalizer.forLookup("120 corn oil") shouldBe "corn oil"
		NutritionNameNormalizer.forLookup("all purpose flour 125") shouldBe "all purpose flour"
		NutritionNameNormalizer.forLookup("12 large") shouldBe ""
		NutritionNameNormalizer.forLookup("5") shouldBe ""
	}

	@Test
	fun forLookupStripsRetailerBrandsAndOrphanUnits() {
		NutritionNameNormalizer.forLookup("spinach sainsbury") shouldBe "spinach"
		NutritionNameNormalizer.forLookup("basmati rice by Sainsbury's") shouldBe "basmati rice"
		NutritionNameNormalizer.forLookup("extra virgin olive oil tbsp") shouldBe "extra virgin olive oil"
		NutritionNameNormalizer.forLookup("tbsp coriander") shouldBe "coriander"
		NutritionNameNormalizer.forLookup("corainder") shouldBe "corainder"
		NutritionNameNormalizer.forLookup("can pumpkin puree") shouldBe "pumpkin puree"
		NutritionNameNormalizer.forLookup("bag baby spinach") shouldBe "baby spinach"
		NutritionNameNormalizer.forLookup("package feta cheese") shouldBe "feta cheese"
		NutritionNameNormalizer.forLookup("garlic fine") shouldBe "garlic"
		NutritionNameNormalizer.forLookup(
			"instant ramen noodles seasoning packets discarded",
		) shouldBe "instant ramen noodles seasoning"
		NutritionNameNormalizer.forLookup("envelope instant yeast") shouldBe "instant yeast"
		NutritionNameNormalizer.forLookup("chilli powder palmfuls") shouldBe "chilli powder"
		NutritionNameNormalizer.forLookup("tomatillos husk") shouldBe "tomatillos"
		NutritionNameNormalizer.forLookup("operative british chicken breast fillets") shouldBe
			"british chicken breast fillets"
		NutritionNameNormalizer.forLookup(
			"seeds cardamom pods using pestle mortar",
		) shouldBe "seeds cardamom pods"
	}

	@Test
	fun hasMeaningfulFoodNameRejectsQuantityOnlyJunk() {
		NutritionNameNormalizer.hasMeaningfulFoodName("12 large") shouldBe false
		NutritionNameNormalizer.hasMeaningfulFoodName("2") shouldBe false
		NutritionNameNormalizer.hasMeaningfulFoodName("5") shouldBe false
		NutritionNameNormalizer.hasMeaningfulFoodName("corn oil") shouldBe true
		NutritionNameNormalizer.hasMeaningfulFoodName("pickled jalapeño") shouldBe true
	}
}
