package app.purecipes.backend

import app.purecipes.backend.fake.FakeSessionService
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.ktor.server.testing.testApplication
import kotlin.test.Test

class RecipeSearchPremiumGatingRouteTest {

	@Test
	fun `free user search applies calorie filters while temporary kill switch is on`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		seedAppUsersForSearchRouteTest(db, isPremium = false)
		val sessionService = FakeSessionService(
			initialSessions = listOf(
				FakeSessionService.createSession(),
			),
			createMode = FakeSessionService.CreateMode.RETURN_FIRST_OR_GENERATE,
		)

		application {
			module(
				db = db,
				sessionService = sessionService,
			)
		}

		createRecipeForSearchRouteTest(
			accessToken = sessionService.session.accessToken,
			title = "Low Calorie Salad",
			ingredients = recipeIngredientsForRouteTest("Lettuce", "Tomato"),
		)
		createRecipeForSearchRouteTest(
			accessToken = sessionService.session.accessToken,
			title = "High Calorie Stew",
			ingredients = recipeIngredientsForRouteTest("Beef", "Potato"),
		)
		setRecipeCalorieRangeForSearchRouteTest(db, title = "Low Calorie Salad", calorieRange = "LOW")
		setRecipeCalorieRangeForSearchRouteTest(db, title = "High Calorie Stew", calorieRange = "HIGH")

		val freeResponseBody = searchWithFiltersForSearchRouteTest(
			"""
				{
					"query": "",
					"filters": {
						"calorieRanges": ["LOW"]
					}
				}
			""".trimIndent(),
			accessToken = sessionService.session.accessToken,
		)

		mainSearchItemTitles(freeResponseBody) shouldContain "Low Calorie Salad"
		mainSearchItemTitles(freeResponseBody) shouldNotContain "High Calorie Stew"
	}

	@Test
	fun `free user search applies nutrition filters while temporary kill switch is on`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		seedAppUsersForSearchRouteTest(db, isPremium = false)
		val sessionService = FakeSessionService(
			initialSessions = listOf(
				FakeSessionService.createSession(),
			),
			createMode = FakeSessionService.CreateMode.RETURN_FIRST_OR_GENERATE,
		)

		application {
			module(
				db = db,
				sessionService = sessionService,
			)
		}

		createRecipeForSearchRouteTest(
			accessToken = sessionService.session.accessToken,
			title = "High Protein Bowl",
			ingredients = recipeIngredientsForRouteTest("Chicken", "Egg"),
		)
		createRecipeForSearchRouteTest(
			accessToken = sessionService.session.accessToken,
			title = "Low Protein Soup",
			ingredients = recipeIngredientsForRouteTest("Carrot", "Onion"),
		)
		setRecipeNutritionForSearchRouteTest(db, title = "High Protein Bowl", protein = "30")
		setRecipeNutritionForSearchRouteTest(db, title = "Low Protein Soup", protein = "10")

		val freeResponseBody = searchWithFiltersForSearchRouteTest(
			"""
				{
					"query": "",
					"filters": {
						"nutritionFilters": ["HIGH_PROTEIN"]
					}
				}
			""".trimIndent(),
			accessToken = sessionService.session.accessToken,
		)

		mainSearchItemTitles(freeResponseBody) shouldContain "High Protein Bowl"
		mainSearchItemTitles(freeResponseBody) shouldNotContain "Low Protein Soup"
	}

	@Test
	fun `free user search still applies persisted pantry filtering`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		seedAppUsersForSearchRouteTest(db, isPremium = false)
		val sessionService = FakeSessionService(
			initialSessions = listOf(
				FakeSessionService.createSession(),
			),
			createMode = FakeSessionService.CreateMode.RETURN_FIRST_OR_GENERATE,
		)

		application {
			module(
				db = db,
				sessionService = sessionService,
			)
		}

		seedRecipeCatalogForSearchRouteTest(accessToken = sessionService.session.accessToken)
		updatePantryForSearchRouteTest(
			accessToken = sessionService.session.accessToken,
			add = listOf("Chicken", "Tomato", "Salt"),
		)

		val responseBody = searchWithFiltersForSearchRouteTest(
			"""
				{
					"query": "",
					"filters": {}
				}
			""".trimIndent(),
			accessToken = sessionService.session.accessToken,
		)

		mainSearchItemTitles(responseBody) shouldContain "Chicken Tomato Stew"
		mainSearchItemTitles(responseBody) shouldNotContain "Chicken Rice Bowl"
		mainSearchItemTitles(responseBody) shouldNotContain "Tomato Basil Soup"
		nearMissSearchItemTitles(responseBody) shouldContain "Chicken Rice Bowl"
	}

	@Test
	fun `free user search applies key ingredients while temporary kill switch is on`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		seedAppUsersForSearchRouteTest(db, isPremium = false)
		val sessionService = FakeSessionService(
			initialSessions = listOf(
				FakeSessionService.createSession(),
			),
			createMode = FakeSessionService.CreateMode.RETURN_FIRST_OR_GENERATE,
		)

		application {
			module(
				db = db,
				sessionService = sessionService,
			)
		}

		seedRecipeCatalogForSearchRouteTest(accessToken = sessionService.session.accessToken)

		val responseBody = searchWithFiltersForSearchRouteTest(
			"""
				{
					"query": "",
					"filters": {},
					"keyIngredients": ["Tomato", "Chicken"]
				}
			""".trimIndent(),
			accessToken = sessionService.session.accessToken,
		)

		mainSearchItemTitles(responseBody) shouldContain "Chicken Tomato Stew"
		mainSearchItemTitles(responseBody) shouldNotContain "Chicken Rice Bowl"
		mainSearchItemTitles(responseBody) shouldNotContain "Tomato Basil Soup"
		mainSearchItemTitles(responseBody) shouldNotContain "Veggie Omelette"
	}

	@Test
	fun `premium user search applies key ingredients`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		seedAppUsersForSearchRouteTest(db, isPremium = true)
		val sessionService = FakeSessionService(
			initialSessions = listOf(
				FakeSessionService.createSession(),
			),
			createMode = FakeSessionService.CreateMode.RETURN_FIRST_OR_GENERATE,
		)

		application {
			module(
				db = db,
				sessionService = sessionService,
			)
		}

		seedRecipeCatalogForSearchRouteTest(accessToken = sessionService.session.accessToken)

		val responseBody = searchWithFiltersForSearchRouteTest(
			"""
				{
					"query": "",
					"filters": {},
					"keyIngredients": ["Tomato", "Chicken"]
				}
			""".trimIndent(),
			accessToken = sessionService.session.accessToken,
		)

		mainSearchItemTitles(responseBody) shouldContain "Chicken Tomato Stew"
		mainSearchItemTitles(responseBody) shouldNotContain "Chicken Rice Bowl"
		mainSearchItemTitles(responseBody) shouldNotContain "Tomato Basil Soup"
		mainSearchItemTitles(responseBody) shouldNotContain "Veggie Omelette"
	}
}
