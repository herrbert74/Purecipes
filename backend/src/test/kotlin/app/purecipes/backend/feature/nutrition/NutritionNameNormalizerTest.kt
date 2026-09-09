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
		NutritionNameNormalizer.forLookup("chicken thigh bone removed") shouldBe "chicken thigh"
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
		NutritionNameNormalizer.forLookup("butter extra greasing") shouldBe "butter"
		NutritionNameNormalizer.forLookup("cinnamon stick broken small") shouldBe "cinnamon"
		NutritionNameNormalizer.forLookup("ancho chile hand") shouldBe "ancho chile"
	}

	@Test
	fun forLookupKeepsFoodMeaningfulTokens() {
		NutritionNameNormalizer.forLookup("flat leaf parsley") shouldBe "flat leaf parsley"
		NutritionNameNormalizer.forLookup("bay leaf") shouldBe "bay leaf"
		NutritionNameNormalizer.forLookup("black pepper") shouldBe "black pepper"
		NutritionNameNormalizer.forLookup("sesame seeds") shouldBe "sesame seeds"
		NutritionNameNormalizer.forLookup("oil") shouldBe "oil"
		NutritionNameNormalizer.forLookup("egg") shouldBe "egg"
		NutritionNameNormalizer.forLookup("ham") shouldBe "ham"
		NutritionNameNormalizer.forLookup("pea") shouldBe "pea"
		NutritionNameNormalizer.forLookup("rye") shouldBe "rye"
		NutritionNameNormalizer.forLookup("water") shouldBe "water"
		NutritionNameNormalizer.forLookup("sparkling water") shouldBe "sparkling water"
		NutritionNameNormalizer.forLookup("coconut water") shouldBe "coconut water"
		NutritionNameNormalizer.forLookup("rose water") shouldBe "rose water"
		NutritionNameNormalizer.forLookup("half and half") shouldBe "half half"
		NutritionNameNormalizer.forLookup("bone") shouldBe "bone"
		NutritionNameNormalizer.forLookup("all purpose flour") shouldBe "all purpose flour"
		NutritionNameNormalizer.forLookup("green beans") shouldBe "green beans"
		NutritionNameNormalizer.forLookup("white rice") shouldBe "white rice"
		NutritionNameNormalizer.forLookup("pumpkin spice") shouldBe "pumpkin spice"
		NutritionNameNormalizer.forLookup("split peas") shouldBe "split peas"
		NutritionNameNormalizer.forLookup("yellow split peas") shouldBe "yellow split peas"
	}

	@Test
	fun forLookupStripsReportLeftoverPrepAndInstructionTokens() {
		NutritionNameNormalizer.forLookup("carrots holes grater") shouldBe "carrots"
		NutritionNameNormalizer.forLookup("extra sharp cheddar holes grater") shouldBe
			"extra sharp cheddar"
		NutritionNameNormalizer.forLookup("scallions white green") shouldBe "scallions"
		NutritionNameNormalizer.forLookup("spring onions white green") shouldBe "spring onions"
		NutritionNameNormalizer.forLookup("scallions white light green parts") shouldBe "scallions"
		NutritionNameNormalizer.forLookup("green onions white light green") shouldBe "green onions"
		NutritionNameNormalizer.forLookup("leek white light green part") shouldBe "leek"
		NutritionNameNormalizer.forLookup("seeds cardamom pods using pestle mortar") shouldBe
			"seeds cardamom pods"
		NutritionNameNormalizer.forLookup("tomatillos husk") shouldBe "tomatillos"
		NutritionNameNormalizer.forLookup("tomatillos husks half") shouldBe "tomatillos"
		NutritionNameNormalizer.forLookup("bone chicken breast skin") shouldBe "chicken breast"
		NutritionNameNormalizer.forLookup("bone skin chicken thighs") shouldBe "chicken thighs"
		NutritionNameNormalizer.forLookup("mixed skinless bone chicken thighs drumsticks") shouldBe
			"mixed skinless chicken thighs drumsticks"
		NutritionNameNormalizer.forLookup("ancho chiles water") shouldBe "ancho chiles"
		NutritionNameNormalizer.forLookup("basmati rice water") shouldBe "basmati rice"
		NutritionNameNormalizer.forLookup("chickpea water") shouldBe "chickpea"
		NutritionNameNormalizer.forLookup("frozen puff pastry one half") shouldBe "frozen puff pastry"
		NutritionNameNormalizer.forLookup("pasta dough wide ribbons") shouldBe "pasta dough"
		NutritionNameNormalizer.forLookup("anise seeds spice grinder") shouldBe "anise seeds"
		NutritionNameNormalizer.forLookup("baguette half split all") shouldBe "baguette"
		NutritionNameNormalizer.forLookup("operative easy long grain rice") shouldBe "long grain rice"
		NutritionNameNormalizer.forLookup("chilli powder palmfuls") shouldBe "chilli powder"
		NutritionNameNormalizer.forLookup("garlic wooden mortar pestle") shouldBe "garlic"
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
			"british chicken breast"
		NutritionNameNormalizer.forLookup(
			"seeds cardamom pods using pestle mortar",
		) shouldBe "seeds cardamom pods"
		NutritionNameNormalizer.forLookup("operative easy long grain rice") shouldBe "long grain rice"
	}

	@Test
	fun forLookupStripsWave2InstructionBrandAndDualFoodLeftovers() {
		NutritionNameNormalizer.forLookup(
			"unsweetened creamy sunflower butter or peanut butter or almond butter",
		) shouldBe "unsweetened creamy sunflower butter"
		NutritionNameNormalizer.forLookup("vinegar like cider or balsamic") shouldBe "vinegar"
		NutritionNameNormalizer.forLookup("neutral oil like corn or vegetable oil") shouldBe "neutral oil"
		NutritionNameNormalizer.forLookup("beef or you can use half pork half beef") shouldBe "beef"
		NutritionNameNormalizer.forLookup("herb spice blend your choice") shouldBe "herb spice blend"
		NutritionNameNormalizer.forLookup(
			"tortilla chips your choice flavored not topping salad",
		) shouldBe "tortilla chips"
		NutritionNameNormalizer.forLookup(
			"garlic herb goat cheese recommended montrachet",
		) shouldBe "garlic herb goat cheese"
		NutritionNameNormalizer.forLookup(
			"mixed baby salad greens available produce section",
		) shouldBe "mixed baby salad greens"
		NutritionNameNormalizer.forLookup("raspberries preferred") shouldBe "raspberries"
		NutritionNameNormalizer.forLookup(
			"knorr chicken stock made knorr chicken stock diluted",
		) shouldBe "chicken stock"
		NutritionNameNormalizer.forLookup(
			"diamond crystal kosher salt table salt use much volume",
		) shouldBe "diamond crystal kosher salt"
		NutritionNameNormalizer.forLookup(
			"white wine vinegar extra virgin olive oil dressing",
		) shouldBe "white wine vinegar extra virgin olive oil"
		NutritionNameNormalizer.forLookup("ranch dressing") shouldBe "ranch dressing"
		NutritionNameNormalizer.forLookup("ranch dressing bottled") shouldBe "ranch dressing"
		NutritionNameNormalizer.forLookup("flaky sea salt topping") shouldBe "flaky sea salt"
		NutritionNameNormalizer.forLookup("white onion topping") shouldBe "white onion"
		NutritionNameNormalizer.forLookup(
			"apricot preserves pressed through a sieve to remove lumps",
		) shouldBe "apricot preserves"
		NutritionNameNormalizer.forLookup("baker german sweet chocolate brand") shouldBe
			"german sweet chocolate"
		NutritionNameNormalizer.forLookup("philadelphia cream cheese") shouldBe "cream cheese"
		NutritionNameNormalizer.forLookup("tabasco brand chipotle pepper sauce") shouldBe
			"chipotle pepper sauce"
		NutritionNameNormalizer.forLookup("cape estate extra virgin olive oil") shouldBe
			"extra virgin olive oil"
		NutritionNameNormalizer.forLookup("uncle bens chilli con carne sauce") shouldBe
			"chilli con carne sauce"
		NutritionNameNormalizer.forLookup("good quality white chocolate") shouldBe "white chocolate"
		NutritionNameNormalizer.forLookup("high quality dark chocolate percent cacao") shouldBe
			"dark chocolate cacao"
		NutritionNameNormalizer.forLookup("organic chicken thigh") shouldBe "chicken thigh"
		NutritionNameNormalizer.forLookup("chicken percent lean") shouldBe "chicken"
		NutritionNameNormalizer.forLookup("desiccated coconut") shouldBe "coconut"
		NutritionNameNormalizer.forLookup("uncooked wild rice") shouldBe "wild rice"
		NutritionNameNormalizer.forLookup("untoasted almonds") shouldBe "almonds"
		NutritionNameNormalizer.forLookup("uncooked old fashioned rolled oats") shouldBe "rolled oats"
		NutritionNameNormalizer.forLookup("celery poaching liquid") shouldBe "celery"
		NutritionNameNormalizer.forLookup("chicken livers cleaned") shouldBe "chicken livers"
		NutritionNameNormalizer.forLookup(
			"crusty baps split drizzled extra virgin olive oil",
		) shouldBe "crusty baps"
		NutritionNameNormalizer.forLookup("whole split chicken breasts") shouldBe "whole chicken breasts"
		NutritionNameNormalizer.forLookup("whole chicken breasts bone skin split half") shouldBe
			"whole chicken breasts"
		NutritionNameNormalizer.forLookup("croissants split") shouldBe "croissants"
		NutritionNameNormalizer.forLookup("vanilla bean split scraped") shouldBe "vanilla bean"
		NutritionNameNormalizer.forLookup("sour orange split half") shouldBe "sour orange"
		NutritionNameNormalizer.forLookup("garlic cracked skin split") shouldBe "garlic cracked"
		NutritionNameNormalizer.forLookup("split peas") shouldBe "split peas"
		NutritionNameNormalizer.forLookup("yellow split peas") shouldBe "yellow split peas"
		NutritionNameNormalizer.forLookup(
			"mexican green onions cebollitas white bulbs individual layers greens",
		) shouldBe "mexican green onions cebollitas"
		NutritionNameNormalizer.forLookup("lemongrass stalk pale white part segments") shouldBe
			"lemongrass stalk"
		NutritionNameNormalizer.forLookup("cucumber whatever shape you like") shouldBe "cucumber"
		NutritionNameNormalizer.forLookup("milk any percentage will do") shouldBe "milk"
		NutritionNameNormalizer.forLookup("salsa add few drops tabasco sauce") shouldBe "salsa"
		NutritionNameNormalizer.forLookup("suggestions white rice sour cream coriander cheddar") shouldBe
			""
		NutritionNameNormalizer.forLookup(
			"this all purpose chicken stock used recipes that call stock",
		) shouldBe ""
		NutritionNameNormalizer.forLookup("baguette bread day old") shouldBe "baguette bread"
		NutritionNameNormalizer.forLookup("very ripe bananas") shouldBe "bananas"
	}

	@Test
	fun forLookupStripsWave3PartStyleAndFlourLeftovers() {
		NutritionNameNormalizer.forLookup("protein all purpose flour") shouldBe "all purpose flour"
		NutritionNameNormalizer.forLookup("red pepper flakes heavier extra") shouldBe "red pepper flakes"
		NutritionNameNormalizer.forLookup("scallions whites") shouldBe "scallions"
		NutritionNameNormalizer.forLookup("spring onions whites") shouldBe "spring onions"
		NutritionNameNormalizer.forLookup("avocado flesh") shouldBe "avocado"
		NutritionNameNormalizer.forLookup("avocados pit") shouldBe "avocados"
		NutritionNameNormalizer.forLookup("bananas flesh") shouldBe "bananas"
		NutritionNameNormalizer.forLookup("louis ribs") shouldBe "ribs"
		NutritionNameNormalizer.forLookup("louis barbecue sauce") shouldBe "barbecue sauce"
		NutritionNameNormalizer.forLookup("bread butter table") shouldBe "bread butter table"
		NutritionNameNormalizer.forLookup("protein powder") shouldBe "protein powder"
		NutritionNameNormalizer.forLookup("egg whites") shouldBe "egg whites"
		NutritionNameNormalizer.forLookup("split peas") shouldBe "split peas"
		NutritionNameNormalizer.forLookup("yellow split peas") shouldBe "yellow split peas"
		NutritionNameNormalizer.forLookup("extra virgin olive oil") shouldBe "extra virgin olive oil"
		NutritionNameNormalizer.forLookup("all purpose flour extra flour rolling") shouldBe
			"all purpose flour extra flour"
	}

	@Test
	fun forLookupStripsWave4PackagingCutAndInstructionLeftovers() {
		NutritionNameNormalizer.forLookup("block halloumi") shouldBe "halloumi"
		NutritionNameNormalizer.forLookup("halloumi cheese block") shouldBe "halloumi cheese"
		NutritionNameNormalizer.forLookup("bunch cilantro") shouldBe "cilantro"
		NutritionNameNormalizer.forLookup("bunch lacinato kale") shouldBe "lacinato kale"
		NutritionNameNormalizer.forLookup("bunch swiss chard") shouldBe "swiss chard"
		NutritionNameNormalizer.forLookup("bunch escarole stem end wilted multiple changes") shouldBe
			"escarole"
		NutritionNameNormalizer.forLookup("head radicchio") shouldBe "radicchio"
		NutritionNameNormalizer.forLookup("beef stewing meat") shouldBe "beef"
		NutritionNameNormalizer.forLookup("beef brisket grain steaks") shouldBe "beef brisket"
		NutritionNameNormalizer.forLookup("chicken cutlets") shouldBe "chicken"
		NutritionNameNormalizer.forLookup("catfish fillets along natural seam") shouldBe "catfish"
		NutritionNameNormalizer.forLookup("fillets red snapper") shouldBe "red snapper"
		NutritionNameNormalizer.forLookup("buttery flaky pie crust") shouldBe "pie crust"
		NutritionNameNormalizer.forLookup("buttery flaky crust double crusted pie") shouldBe "crust pie"
		NutritionNameNormalizer.forLookup("chicken giblets wing tips stock") shouldBe
			"chicken giblets stock"
		NutritionNameNormalizer.forLookup("flaky sea salt") shouldBe "flaky sea salt"
		NutritionNameNormalizer.forLookup("long grain rice") shouldBe "long grain rice"
		NutritionNameNormalizer.forLookup("split peas") shouldBe "split peas"
		NutritionNameNormalizer.forLookup("yellow split peas") shouldBe "yellow split peas"
		NutritionNameNormalizer.forLookup("protein powder") shouldBe "protein powder"
		NutritionNameNormalizer.forLookup("egg whites") shouldBe "egg whites"
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
