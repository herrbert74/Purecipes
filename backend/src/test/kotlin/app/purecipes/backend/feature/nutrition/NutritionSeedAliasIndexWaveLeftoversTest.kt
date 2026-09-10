package app.purecipes.backend.feature.nutrition

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import kotlin.test.Test

class NutritionSeedAliasIndexWaveLeftoversTest {

	@Test
	fun mergeAppliesWave5ReportLeftoverAliases() {
		val cardamom = food(
			id = 601,
			displayName = "Spices, cardamom",
			normalizedName = "spices cardamom",
		)
		val chickenBreast = food(
			id = 602,
			displayName = "Chicken, broiler or fryers, breast, skinless, boneless, meat only, raw",
			normalizedName = "chicken broiler or fryers breast skinless boneless meat only raw",
		)
		val salmon = food(
			id = 603,
			displayName = "Fish, salmon, Atlantic, farmed, raw",
			normalizedName = "fish salmon atlantic farmed raw",
		)
		val giblets = food(
			id = 604,
			displayName = "Chicken, broilers or fryers, giblets, raw",
			normalizedName = "chicken broilers or fryers giblets raw",
		)
		val salsaVerde = food(
			id = 605,
			displayName = "Salsa verde or salsa, green",
			normalizedName = "salsa verde or salsa green",
		)
		val cereal = food(
			id = 606,
			displayName = "Cereals ready-to-eat, GENERAL MILLS, CHEERIOS",
			normalizedName = "cereals ready to eat general mills cheerios",
		)
		val challah = food(
			id = 607,
			displayName = "Bread, egg, Challah",
			normalizedName = "bread egg challah",
		)
		val cod = food(
			id = 608,
			displayName = "Fish, cod, Atlantic, raw",
			normalizedName = "fish cod atlantic raw",
		)
		val kale = food(
			id = 609,
			displayName = "Kale, raw",
			normalizedName = "kale raw",
		)
		val whitefish = food(
			id = 610,
			displayName = "Fish, whitefish, mixed species, raw",
			normalizedName = "fish whitefish mixed species raw",
		)
		val chickenThigh = food(
			id = 611,
			displayName = "Chicken, broilers or fryers, dark meat, thigh, meat only, raw",
			normalizedName = "chicken broilers or fryers dark meat thigh meat only raw",
		)
		val bloodSausage = food(
			id = 612,
			displayName = "Blood sausage",
			normalizedName = "blood sausage",
		)
		val hotSauce = food(
			id = 613,
			displayName = "Sauce, ready-to-serve, pepper or hot",
			normalizedName = "sauce ready to serve pepper or hot",
		)
		val canola = food(
			id = 614,
			displayName = "Oil, canola",
			normalizedName = "oil canola",
		)
		val soybeanOil = food(
			id = 615,
			displayName = "Oil, soybean, salad or cooking",
			normalizedName = "oil soybean salad or cooking",
		)
		val chiliPowder = food(
			id = 616,
			displayName = "Spices, chili powder",
			normalizedName = "spices chili powder",
		)
		val paprika = food(
			id = 617,
			displayName = "Spices, paprika",
			normalizedName = "spices paprika",
		)
		val saltedCod = food(
			id = 618,
			displayName = "Fish, cod, Atlantic, dried and salted",
			normalizedName = "fish cod atlantic dried and salted",
		)
		val molasses = food(
			id = 619,
			displayName = "Molasses",
			normalizedName = "molasses",
		)
		val aliases = NutritionSeedAliasIndex.merge(
			foods = listOf(
				cardamom,
				chickenBreast,
				salmon,
				giblets,
				salsaVerde,
				cereal,
				challah,
				cod,
				kale,
				whitefish,
				chickenThigh,
				bloodSausage,
				hotSauce,
				canola,
				soybeanOil,
				chiliPowder,
				paprika,
				saltedCod,
				molasses,
			),
			storedAliases = emptyMap(),
		)

		aliases["seeds cardamom"] shouldBe 601
		aliases["green cardamom"] shouldBe 601
		aliases["british chicken breast"] shouldBe 602
		aliases["boneless center salmon"] shouldBe 603
		aliases["chicken giblets stock"] shouldBe 604
		aliases["chimichurri"] shouldBe 605
		aliases["chimichurri sauce"] shouldBe 605
		aliases["corn chex"] shouldBe 606
		aliases["rice chex"] shouldBe 606
		aliases["wheat chex"] shouldBe 606
		aliases["cocoa krispies"] shouldBe 606
		aliases["loaf challah"] shouldBe 607
		aliases["challah"] shouldBe 607
		aliases["skinless cod"] shouldBe 608
		aliases["tuscan kale"] shouldBe 609
		aliases["white fleshed fish"] shouldBe 610
		aliases["japanese karaage"] shouldBe 611
		aliases["morcilla burgos spanish black pudding"] shouldBe 612
		aliases["tapatio"] shouldBe 613
		aliases["herb oil"] shouldBe 614
		aliases["other neutral oil"] shouldBe 614
		aliases["hot vegetable"] shouldBe 615
		aliases["stonemill chilli powder"] shouldBe 616
		aliases["stonemill paprika"] shouldBe 617
		aliases["salt fish"] shouldBe 618
		aliases["cassareep"] shouldBe 619
		aliases["prague powder"].shouldBeNull()
		aliases["edible gold dust"].shouldBeNull()
		aliases["edible gold glitter"].shouldBeNull()
		aliases["bread butter table"].shouldBeNull()
		aliases["sausage casings"].shouldBeNull()
		aliases["nigella seeds"].shouldBeNull()
		aliases["perilla"].shouldBeNull()
		aliases["yellow food colouring"].shouldBeNull()
		aliases["mastic crystals"].shouldBeNull()
	}

	@Test
	fun mergeAppliesWave6ReportLeftoverAliases() {
		val ginger = food(
			id = 701,
			displayName = "Ginger root, raw",
			normalizedName = "ginger root raw",
		)
		val chiliPowder = food(
			id = 702,
			displayName = "Spices, chili powder",
			normalizedName = "spices chili powder",
		)
		val beer = food(
			id = 703,
			displayName = "Alcoholic beverage, beer, regular, all",
			normalizedName = "alcoholic beverage beer regular all",
		)
		val egg = food(
			id = 704,
			displayName = "Eggs, Grade A, Large, egg whole",
			normalizedName = "eggs grade a large egg whole",
		)
		val iceberg = food(
			id = 705,
			displayName = "Lettuce, iceberg (includes crisphead types), raw",
			normalizedName = "lettuce iceberg includes crisphead types raw",
		)
		val lime = food(
			id = 706,
			displayName = "Limes, raw",
			normalizedName = "limes raw",
		)
		val coffee = food(
			id = 707,
			displayName = "Beverages, coffee, instant, regular, powder",
			normalizedName = "beverages coffee instant regular powder",
		)
		val garlic = food(
			id = 708,
			displayName = "Garlic, raw",
			normalizedName = "garlic raw",
		)
		val curryPowder = food(
			id = 709,
			displayName = "Spices, curry powder",
			normalizedName = "spices curry powder",
		)
		val chickenThigh = food(
			id = 710,
			displayName = "Chicken, broilers or fryers, dark meat, thigh, meat only, raw",
			normalizedName = "chicken broilers or fryers dark meat thigh meat only raw",
		)
		val cassava = food(
			id = 711,
			displayName = "Cassava, raw",
			normalizedName = "cassava raw",
		)
		val stewMeat = food(
			id = 712,
			displayName = "Beef, stew meat",
			normalizedName = "beef stew meat",
		)
		val tomato = food(
			id = 713,
			displayName = "Tomatoes, red, ripe, raw, year round average",
			normalizedName = "tomatoes red ripe raw year round average",
		)
		val jalapeno = food(
			id = 714,
			displayName = "Peppers, jalapeno, raw",
			normalizedName = "peppers jalapeno raw",
		)
		val aliases = NutritionSeedAliasIndex.merge(
			foods = listOf(
				ginger,
				chiliPowder,
				beer,
				egg,
				iceberg,
				lime,
				coffee,
				garlic,
				curryPowder,
				chickenThigh,
				cassava,
				stewMeat,
				tomato,
				jalapeno,
			),
			storedAliases = emptyMap(),
		)

		aliases["ginger garlic paste"] shouldBe 701
		aliases["chilli paste"] shouldBe 702
		aliases["chili paste"] shouldBe 702
		aliases["tajin"] shouldBe 702
		aliases["lager beer"] shouldBe 703
		aliases["lager"] shouldBe 703
		aliases["free range woodland egg"] shouldBe 704
		aliases["hard hard steamed eggs"] shouldBe 704
		aliases["hard steamed eggs"] shouldBe 704
		aliases["regular green lettuce"] shouldBe 705
		aliases["green lettuce"] shouldBe 705
		aliases["whole limes"] shouldBe 706
		aliases["brewed shot espresso"] shouldBe 707
		aliases["shot espresso"] shouldBe 707
		aliases["herb garlic marinade"] shouldBe 708
		aliases["masala paste"] shouldBe 709
		aliases["chicken drumsticks patted dry paper towels"] shouldBe 710
		aliases["chicken drumsticks"] shouldBe 710
		aliases["chicken drumstick"] shouldBe 710
		aliases["cassava woody"] shouldBe 711
		aliases["loved beef mince fat"] shouldBe 712
		aliases["beef mince"] shouldBe 712
		aliases["tomatoes their juices alternatively use canned tomatoes"] shouldBe 713
		aliases["tomatoes their juices"] shouldBe 713
		aliases["pickled jalapenos pickling"] shouldBe 714
		aliases["pickled jalapeno pickling"] shouldBe 714
		aliases["prague powder"].shouldBeNull()
		aliases["edible gold dust"].shouldBeNull()
		aliases["cow foot"].shouldBeNull()
		aliases["food grade lye crystals"].shouldBeNull()
		aliases["sausage casings"].shouldBeNull()
		aliases["total"].shouldBeNull()
		aliases["warm"].shouldBeNull()
		aliases["two"].shouldBeNull()
	}

	@Test
	fun mergeAppliesWave7HighConfidenceAliases() {
		val salsaVerde = food(
			id = 801,
			displayName = "Salsa verde or salsa, green",
			normalizedName = "salsa verde or salsa green",
		)
		val miso = food(
			id = 802,
			displayName = "Miso",
			normalizedName = "miso",
		)
		val hotChiliRed = food(
			id = 803,
			displayName = "Peppers, hot chili, red, raw",
			normalizedName = "peppers hot chili red raw",
		)
		val serrano = food(
			id = 804,
			displayName = "Peppers, serrano, raw",
			normalizedName = "peppers serrano raw",
		)
		val stripSteak = food(
			id = 805,
			displayName = "Beef, grass-fed, strip steaks, lean only, raw",
			normalizedName = "beef grass fed strip steaks lean only raw",
		)
		val garlic = food(
			id = 806,
			displayName = "Garlic, raw",
			normalizedName = "garlic raw",
		)
		val lime = food(
			id = 807,
			displayName = "Limes, raw",
			normalizedName = "limes raw",
		)
		val flour = food(
			id = 808,
			displayName = "Flour, wheat, all-purpose, enriched, unbleached",
			normalizedName = "flour wheat all purpose enriched unbleached",
		)
		val sugar = food(
			id = 809,
			displayName = "Sugars, granulated",
			normalizedName = "sugars granulated",
		)
		val oats = food(
			id = 810,
			displayName = "Cereals, oats, regular and quick, not fortified, dry",
			normalizedName = "cereals oats regular and quick not fortified dry",
		)
		val pinto = food(
			id = 811,
			displayName = "Beans, pinto, mature seeds, cooked, boiled, without salt",
			normalizedName = "beans pinto mature seeds cooked boiled without salt",
		)
		val bacon = food(
			id = 812,
			displayName = "Pork, cured, bacon, unprepared",
			normalizedName = "pork cured bacon unprepared",
		)
		val chickenBreast = food(
			id = 813,
			displayName = "Chicken, broiler or fryers, breast, skinless, boneless, meat only, raw",
			normalizedName = "chicken breast boneless skinless raw",
		)
		val porkRibs = food(
			id = 814,
			displayName = "Pork, fresh, spareribs, separable lean and fat, raw",
			normalizedName = "pork fresh spareribs separable lean and fat raw",
		)
		val pumpkinSeed = food(
			id = 815,
			displayName = "Seeds, pumpkin and squash seed kernels, dried",
			normalizedName = "seeds pumpkin and squash seed kernels dried",
		)
		val crushedTomato = food(
			id = 816,
			displayName = "Tomatoes, crushed, canned",
			normalizedName = "tomatoes crushed canned",
		)
		val greenTea = food(
			id = 817,
			displayName = "Beverages, tea, green, ready to drink, unsweetened",
			normalizedName = "beverages tea green ready to drink unsweetened",
		)
		val mixedGreens = food(
			id = 818,
			displayName = "Mixed salad greens, raw",
			normalizedName = "mixed salad greens raw",
		)
		val feta = food(
			id = 819,
			displayName = "Cheese, feta",
			normalizedName = "cheese feta",
		)
		val redWine = food(
			id = 820,
			displayName = "Alcoholic beverage, wine, table, red",
			normalizedName = "alcoholic beverage wine table red",
		)
		val burgerBun = food(
			id = 821,
			displayName = "Rolls, hamburger or hotdog, plain",
			normalizedName = "rolls hamburger or hotdog plain",
		)
		val romaine = food(
			id = 822,
			displayName = "Lettuce, cos or romaine, raw",
			normalizedName = "lettuce cos or romaine raw",
		)
		val tomato = food(
			id = 823,
			displayName = "Tomatoes, red, ripe, raw, year round average",
			normalizedName = "tomatoes red ripe raw year round average",
		)
		val blueCheese = food(
			id = 824,
			displayName = "Cheese, blue",
			normalizedName = "cheese blue",
		)
		val cabbage = food(
			id = 825,
			displayName = "Cabbage, green, raw",
			normalizedName = "cabbage green raw",
		)
		val potato = food(
			id = 826,
			displayName = "Potatoes, flesh and skin, raw",
			normalizedName = "potatoes flesh and skin raw",
		)
		val baguette = food(
			id = 827,
			displayName = "Bread, french or vienna (includes sourdough)",
			normalizedName = "bread french or vienna includes sourdough",
		)
		val shrimp = food(
			id = 828,
			displayName = "Crustaceans, shrimp, raw",
			normalizedName = "crustaceans shrimp raw",
		)
		val squid = food(
			id = 829,
			displayName = "Mollusks, squid, mixed species, raw",
			normalizedName = "mollusks squid mixed species raw",
		)
		val cornTortilla = food(
			id = 830,
			displayName = "Tortillas, ready-to-bake or -fry, corn",
			normalizedName = "tortillas ready to bake or fry corn",
		)
		val bulgur = food(
			id = 831,
			displayName = "Bulgur, dry",
			normalizedName = "bulgur dry",
		)
		val lentil = food(
			id = 832,
			displayName = "Lentils, mature seeds, cooked, boiled, without salt",
			normalizedName = "lentils mature seeds cooked boiled without salt",
		)
		val cornedBeef = food(
			id = 833,
			displayName = "Beef, cured, corned beef, brisket, raw",
			normalizedName = "beef cured corned beef brisket raw",
		)
		val bloodSausage = food(
			id = 834,
			displayName = "Blood sausage",
			normalizedName = "blood sausage",
		)
		val lamb = food(
			id = 835,
			displayName = "Lamb, ground, raw",
			normalizedName = "lamb ground raw",
		)
		val pickle = food(
			id = 836,
			displayName = "Pickles, cucumber, dill or kosher dill",
			normalizedName = "pickles cucumber dill or kosher dill",
		)
		val tahini = food(
			id = 837,
			displayName = "Seeds, sesame butter, tahini, from roasted and toasted kernels (most common type)",
			normalizedName = "seeds sesame butter tahini from roasted and toasted kernels",
		)
		val water = food(
			id = 838,
			displayName = "Water, tap",
			normalizedName = "water tap",
		)
		val cookedBacon = food(
			id = 839,
			displayName = "Pork, cured, bacon, cooked, baked",
			normalizedName = "pork cured bacon cooked baked",
		)
		val aliases = NutritionSeedAliasIndex.merge(
			foods = listOf(
				salsaVerde,
				miso,
				hotChiliRed,
				serrano,
				stripSteak,
				garlic,
				lime,
				flour,
				sugar,
				oats,
				pinto,
				bacon,
				chickenBreast,
				porkRibs,
				pumpkinSeed,
				crushedTomato,
				greenTea,
				mixedGreens,
				feta,
				redWine,
				burgerBun,
				romaine,
				tomato,
				blueCheese,
				cabbage,
				potato,
				baguette,
				shrimp,
				squid,
				cornTortilla,
				bulgur,
				lentil,
				cornedBeef,
				bloodSausage,
				lamb,
				pickle,
				tahini,
				water,
				cookedBacon,
			),
			storedAliases = emptyMap(),
		)

		aliases["zhug"] shouldBe 801
		aliases["yellow miso"] shouldBe 802
		aliases["scotch bonnet"] shouldBe 803
		aliases["wiri peppers"] shouldBe 803
		aliases["serrano chilli"] shouldBe 804
		aliases["filets mignon"] shouldBe 805
		aliases["filet mignon"] shouldBe 805
		aliases["garlic scapes"] shouldBe 806
		aliases["makrut lime"] shouldBe 807
		aliases["mochiko"] shouldBe 808
		aliases["sanding sugar"] shouldBe 809
		aliases["steel oats"] shouldBe 810
		aliases["canary beans"] shouldBe 811
		aliases["cranberry beans over"] shouldBe 811
		aliases["chinese bacon"] shouldBe 812
		aliases["chicken paillards"] shouldBe 813
		aliases["racks pork spareribs"] shouldBe 814
		aliases["raw shelled pumpkin seeds"] shouldBe 815
		aliases["whole tinned tomatoes"] shouldBe 816
		aliases["loose green tea"] shouldBe 817
		aliases["mesclun salad mix"] shouldBe 818
		aliases["garlic herb goat cheese"] shouldBe 819
		aliases["full bodied red wine"] shouldBe 820
		aliases["hawaiian sweet rolls buns all still attached"] shouldBe 821
		aliases["leaf lettuce vertically"] shouldBe 822
		aliases["vine ripened tomatoes"] shouldBe 823
		aliases["gorgonzola cheese crumbled"] shouldBe 824
		aliases["green cabbage core intact"] shouldBe 825
		aliases["green cabbage long strands"] shouldBe 825
		aliases["russet potatoes clean"] shouldBe 826
		aliases["russet potatoes fries"] shouldBe 826
		aliases["soft baguettes open"] shouldBe 827
		aliases["shrimp shells separately"] shouldBe 828
		aliases["shrimp tail"] shouldBe 828
		aliases["squid bodies rings tentacles whole"] shouldBe 829
		aliases["corn tortillas eighths stale"] shouldBe 830
		aliases["cracked freekeh over"] shouldBe 831
		aliases["french puy lentils over stones"] shouldBe 832
		aliases["red lentils over"] shouldBe 832
		aliases["cooked corned beef brisket fat meat"] shouldBe 833
		aliases["morcilla burgos"] shouldBe 834
		aliases["boneless lamb stew meat"] shouldBe 835
		aliases["dill pickles dill pickle juice"] shouldBe 836
		aliases["tahini before measuring"] shouldBe 837
		aliases["hot water around 200of"] shouldBe 838
		aliases["cured streaky bacon"] shouldBe 839
		aliases["prague powder"].shouldBeNull()
		aliases["edible gold dust"].shouldBeNull()
		aliases["nigella seeds"].shouldBeNull()
		aliases["perilla"].shouldBeNull()
		aliases["mastic crystals"].shouldBeNull()
		aliases["yellow food colouring"].shouldBeNull()
		aliases["bread butter table"].shouldBeNull()
		aliases["sausage casings"].shouldBeNull()
		aliases["oil chicken"].shouldBeNull()
		aliases["store-bought"].shouldBeNull()
		aliases["warm"].shouldBeNull()
	}

	@Test
	fun mergeAppliesWave8HighConfidenceUnmatchedAliases() {
		val crab = food(
			id = 901,
			displayName = "Crustaceans, crab, blue, canned",
			normalizedName = "crustaceans crab blue canned",
		)
		val pinto = food(
			id = 902,
			displayName = "Beans, pinto, mature seeds, cooked, boiled, without salt",
			normalizedName = "beans pinto mature seeds cooked boiled without salt",
		)
		val cabbage = food(
			id = 903,
			displayName = "Cabbage, green, raw",
			normalizedName = "cabbage green raw",
		)
		val chickenBreast = food(
			id = 904,
			displayName = "Chicken, broiler or fryers, breast, skinless, boneless, meat only, raw",
			normalizedName = "chicken breast boneless skinless raw",
		)
		val rhubarb = food(
			id = 905,
			displayName = "Rhubarb, raw",
			normalizedName = "rhubarb raw",
		)
		val rice = food(
			id = 906,
			displayName = "Rice, white, long grain, unenriched, raw",
			normalizedName = "rice white long grain unenriched raw",
		)
		val lamb = food(
			id = 907,
			displayName = "Lamb, ground, raw",
			normalizedName = "lamb ground raw",
		)
		val cornTortilla = food(
			id = 908,
			displayName = "Tortillas, ready-to-bake or -fry, corn",
			normalizedName = "tortillas ready to bake or fry corn",
		)
		val coconutOil = food(
			id = 909,
			displayName = "Oil, coconut",
			normalizedName = "oil coconut",
		)
		val condensedMilk = food(
			id = 910,
			displayName = "Milk, canned, condensed, sweetened",
			normalizedName = "milk canned condensed sweetened",
		)
		val ginger = food(
			id = 911,
			displayName = "Ginger root, raw",
			normalizedName = "ginger root raw",
		)
		val sugar = food(
			id = 912,
			displayName = "Sugars, granulated",
			normalizedName = "sugars granulated",
		)
		val tomato = food(
			id = 913,
			displayName = "Tomatoes, red, ripe, raw, year round average",
			normalizedName = "tomatoes red ripe raw year round average",
		)
		val radicchio = food(
			id = 914,
			displayName = "Radicchio, raw",
			normalizedName = "radicchio raw",
		)
		val thyme = food(
			id = 915,
			displayName = "Thyme, fresh",
			normalizedName = "thyme fresh",
		)
		val aliases = NutritionSeedAliasIndex.merge(
			foods = listOf(
				crab,
				pinto,
				cabbage,
				chickenBreast,
				rhubarb,
				rice,
				lamb,
				cornTortilla,
				coconutOil,
				condensedMilk,
				ginger,
				sugar,
				tomato,
				radicchio,
				thyme,
			),
			storedAliases = emptyMap(),
		)

		aliases["crabmeat"] shouldBe 901
		aliases["frijoles refritos"] shouldBe 902
		aliases["green cabbage core"] shouldBe 903
		aliases["rotisserie chicken meat still"] shouldBe 904
		aliases["rhubarb ten stalks"] shouldBe 905
		aliases["peri rice"] shouldBe 906
		aliases["boneless leg lamb shoulder neck"] shouldBe 907
		aliases["corn tortillas eighths"] shouldBe 908
		aliases["unrefined coconut oil solid but soft"] shouldBe 909
		aliases["sweetened condensed milk glue"] shouldBe 910
		aliases["ginger whacked flat side knife"] shouldBe 911
		aliases["granulated sugar cane suga"] shouldBe 912
		aliases["beefsteak tomato seeds"] shouldBe 913
		aliases["radicchio frisee"] shouldBe 914
		aliases["thyme shallots"] shouldBe 915
		aliases["oil chicken"].shouldBeNull()
		aliases["prague powder"].shouldBeNull()
		aliases["nigella seeds"].shouldBeNull()
	}

	@Test
	fun mergeAppliesWave9HighConfidenceUnmatchedAliases() {
		val baguette = food(
			id = 1001,
			displayName = "Bread, french or vienna (includes sourdough)",
			normalizedName = "bread french or vienna includes sourdough",
		)
		val chickenBreast = food(
			id = 1002,
			displayName = "Chicken, broiler or fryers, breast, skinless, boneless, meat only, raw",
			normalizedName = "chicken broiler or fryers breast skinless boneless meat only raw",
		)
		val cream = food(
			id = 1003,
			displayName = "Cream, heavy",
			normalizedName = "cream heavy",
		)
		val burgerBun = food(
			id = 1004,
			displayName = "Rolls, hamburger or hotdog, plain",
			normalizedName = "rolls hamburger or hotdog plain",
		)
		val lemongrass = food(
			id = 1005,
			displayName = "Lemon grass (citronella), raw",
			normalizedName = "lemon grass citronella raw",
		)
		val onion = food(
			id = 1006,
			displayName = "Onions, raw",
			normalizedName = "onions raw",
		)
		val aliases = NutritionSeedAliasIndex.merge(
			foods = listOf(
				baguette,
				chickenBreast,
				cream,
				burgerBun,
				lemongrass,
				onion,
			),
			storedAliases = emptyMap(),
		)

		aliases["baguettes"] shouldBe 1001
		aliases["butter chicken"] shouldBe 1002
		aliases["double cream whipped peaks"] shouldBe 1003
		aliases["hawaiian sweet rolls buns all attached"] shouldBe 1004
		aliases["lemongrass outer top third rest"] shouldBe 1005
		aliases["lemongrass under"] shouldBe 1005
		aliases["rotisserie chicken breast yield"] shouldBe 1002
		aliases["spanish onion unevenly"] shouldBe 1006
		aliases["oil chicken"].shouldBeNull()
		aliases["prague powder"].shouldBeNull()
		aliases["nigella seeds"].shouldBeNull()
		aliases["edible gold dust"].shouldBeNull()
		aliases["sausage casings"].shouldBeNull()
	}

	@Test
	fun mergeAppliesWave10HighConfidenceUnmatchedAliases() {
		val powderedSugar = food(
			id = 1101,
			displayName = "Sugars, powdered",
			normalizedName = "sugars powdered",
		)
		val cornmeal = food(
			id = 1102,
			displayName = "Cornmeal, degermed, enriched, yellow",
			normalizedName = "cornmeal degermed enriched yellow",
		)
		val eggYolk = food(
			id = 1103,
			displayName = "Eggs, Grade A, Large, egg yolk",
			normalizedName = "eggs grade a large egg yolk",
		)
		val walnut = food(
			id = 1104,
			displayName = "Nuts, walnuts, english",
			normalizedName = "nuts walnuts english",
		)
		val oregano = food(
			id = 1105,
			displayName = "Spices, oregano, dried",
			normalizedName = "spices oregano dried",
		)
		val mixedHerbs = food(
			id = 1106,
			displayName = "MIXED HERBS",
			normalizedName = "mixed herbs",
		)
		val sweetAndSour = food(
			id = 1107,
			displayName = "Sauce, sweet and sour, ready-to-serve",
			normalizedName = "sauce sweet and sour ready to serve",
		)
		val ciderVinegar = food(
			id = 1108,
			displayName = "Vinegar, cider",
			normalizedName = "vinegar cider",
		)
		val sazon = food(
			id = 1109,
			displayName = "Seasoning mix, dry, sazon, coriander & annatto",
			normalizedName = "seasoning mix dry sazon coriander annatto",
		)
		val breadcrumbs = food(
			id = 1110,
			displayName = "Bread, crumbs, dry, grated, plain",
			normalizedName = "bread crumbs dry grated plain",
		)
		val biscuitMix = food(
			id = 1111,
			displayName = "Biscuits, plain or buttermilk, dry mix",
			normalizedName = "biscuits plain or buttermilk dry mix",
		)
		val chiliConCarne = food(
			id = 1112,
			displayName = "Chili con carne with beans, canned entree",
			normalizedName = "chili con carne with beans canned entree",
		)
		val mixedVegetables = food(
			id = 1113,
			displayName = "Vegetables, mixed, frozen, unprepared",
			normalizedName = "vegetables mixed frozen unprepared",
		)
		val aliases = NutritionSeedAliasIndex.merge(
			foods = listOf(
				powderedSugar,
				cornmeal,
				eggYolk,
				walnut,
				oregano,
				mixedHerbs,
				sweetAndSour,
				ciderVinegar,
				sazon,
				breadcrumbs,
				biscuitMix,
				chiliConCarne,
				mixedVegetables,
			),
			storedAliases = emptyMap(),
		)

		aliases["confectioners sugar cinnamon"] shouldBe 1101
		aliases["corn flour yellow cornmeal"] shouldBe 1102
		aliases["egg yolk milk"] shouldBe 1103
		aliases["walnuts pecans"] shouldBe 1104
		aliases["herbs oregano thyme"] shouldBe 1105
		aliases["herb spice blend"] shouldBe 1106
		aliases["herbs mix"] shouldBe 1106
		aliases["sweet sour mix"] shouldBe 1107
		aliases["pickled apple brine"] shouldBe 1108
		aliases["sazon sin achiote seasoning"] shouldBe 1109
		aliases["bread sauce mix"] shouldBe 1110
		aliases["pastry baking mix"] shouldBe 1111
		aliases["chilli con carne sauce"] shouldBe 1112
		aliases["chinese stir fry vegetables"] shouldBe 1113
		aliases["prague powder"].shouldBeNull()
		aliases["bread butter table"].shouldBeNull()
		aliases["sausage casings"].shouldBeNull()
		aliases["oil chicken"].shouldBeNull()
		aliases["nigella seeds"].shouldBeNull()
		aliases["nigella seed"].shouldBeNull()
		aliases["perilla"].shouldBeNull()
		aliases["mastic crystals"].shouldBeNull()
		aliases["yellow food colouring"].shouldBeNull()
		aliases["cow foot"].shouldBeNull()
	}

	@Test
	fun mergeAppliesWave11HighConfidenceUnmatchedAliases() {
		val sugar = food(
			id = 1201,
			displayName = "Sugars, granulated",
			normalizedName = "sugars granulated",
		)
		val tomato = food(
			id = 1202,
			displayName = "Tomatoes, red, ripe, raw, year round average",
			normalizedName = "tomatoes red ripe raw year round average",
		)
		val bakingPowder = food(
			id = 1203,
			displayName = "Leavening agents, baking powder, double-acting, sodium aluminum sulfate",
			normalizedName = "leavening agents baking powder double acting sodium aluminum sulfate",
		)
		val baguette = food(
			id = 1204,
			displayName = "Bread, french or vienna (includes sourdough)",
			normalizedName = "bread french or vienna includes sourdough",
		)
		val plum = food(
			id = 1205,
			displayName = "Plums, raw",
			normalizedName = "plums raw",
		)
		val italianSeasoning = food(
			id = 1206,
			displayName = "ITALIAN SEASONING BLEND, ITALIAN",
			normalizedName = "italian seasoning blend italian",
		)
		val duck = food(
			id = 1207,
			displayName = "Duck, domesticated, meat only, raw",
			normalizedName = "duck domesticated meat only raw",
		)
		val soySauce = food(
			id = 1208,
			displayName = "Soy sauce made from soy and wheat (shoyu)",
			normalizedName = "soy sauce made from soy and wheat shoyu",
		)
		val hoisin = food(
			id = 1209,
			displayName = "Sauce, hoisin, ready-to-serve",
			normalizedName = "sauce hoisin ready to serve",
		)
		val blackBean = food(
			id = 1210,
			displayName = "Beans, black, mature seeds, cooked, boiled, without salt",
			normalizedName = "beans black mature seeds cooked boiled without salt",
		)
		val cheddar = food(
			id = 1211,
			displayName = "Cheese, cheddar",
			normalizedName = "cheese cheddar",
		)
		val aliases = NutritionSeedAliasIndex.merge(
			foods = listOf(
				sugar,
				tomato,
				bakingPowder,
				baguette,
				plum,
				italianSeasoning,
				duck,
				soySauce,
				hoisin,
				blackBean,
				cheddar,
			),
			storedAliases = emptyMap(),
		)

		aliases["granulated sugar light brown sugar"] shouldBe 1201
		aliases["tomatoes cucumbers"] shouldBe 1202
		aliases["baking powder baking soda"] shouldBe 1203
		aliases["crusty bread couscous"] shouldBe 1204
		aliases["red plums apricots"] shouldBe 1205
		aliases["tomato oil anchovy"] shouldBe 1202
		aliases["all seasoning blend"] shouldBe 1206
		aliases["chinese duck marinade"] shouldBe 1207
		aliases["chinese light soy sauce chinese dark soy sauce"] shouldBe 1208
		aliases["hoisin garlic stir fry sauce"] shouldBe 1209
		aliases["cuban black beans rice"] shouldBe 1210
		aliases["cheddar provolone monterey jack muenster cheese"] shouldBe 1211
		aliases["prague powder"].shouldBeNull()
		aliases["cow foot"].shouldBeNull()
		aliases["mastic crystals"].shouldBeNull()
		aliases["nigella seed"].shouldBeNull()
		aliases["nigella seeds"].shouldBeNull()
		aliases["perilla"].shouldBeNull()
		aliases["oil chicken"].shouldBeNull()
		aliases["unsalted beef chicken"].shouldBeNull()
		aliases["milk vegetable oil"].shouldBeNull()
		aliases["white wine vinegar extra virgin olive oil"].shouldBeNull()
		aliases["unsweetened creamy sunflower butter peanut butter almond butter"].shouldBeNull()
		aliases["unsweetened blueberries raisins currents figs"].shouldBeNull()
		aliases["raw sesame seeds parmesan furikake everything bagel seasoning poppy seeds"]
			.shouldBeNull()
		aliases["pickled turnips pickled mixed vegetables tahini sauce garlic lemon"].shouldBeNull()
		aliases["sour cream radishes guacamole"].shouldBeNull()
		aliases["tomato cucumber lettuce red onion plain yogurt"].shouldBeNull()
		aliases["something crunchy"].shouldBeNull()
		aliases["yellow food colouring"].shouldBeNull()
		aliases["blue dragon satay season stir fry"].shouldBeNull()
		aliases["blue dragon stir fry chow mein sauce"].shouldBeNull()
		aliases["east end fried onion curry base"].shouldBeNull()
		aliases["east end masala mix"].shouldBeNull()
		aliases["east end nishaan ginger garlic"].shouldBeNull()
		aliases["heinz creationz mexican beanz"].shouldBeNull()
		aliases["patak tikka masala sauce"].shouldBeNull()
		aliases["assorted fruit"].shouldBeNull()
		aliases["tomatoes onions middle eastern cucumber pickles"].shouldBeNull()
	}

	private fun food(id: Int, displayName: String, normalizedName: String): NutritionFoodRecord =
		NutritionFoodRecord(
			id = id,
			displayName = displayName,
			normalizedName = normalizedName,
			nutrients = FdcNutrientsPer100g(
				calories = BigDecimal.ZERO,
				protein = null,
				carbohydrates = null,
				fat = null,
				fiber = null,
				sugar = null,
				sodium = null,
			),
		)
}
