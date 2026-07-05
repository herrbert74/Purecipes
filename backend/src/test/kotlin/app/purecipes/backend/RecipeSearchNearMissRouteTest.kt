package app.purecipes.backend

import app.purecipes.backend.fake.FakeSessionService
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.ktor.server.testing.testApplication
import kotlin.test.Test

class RecipeSearchNearMissRouteTest {

	@Test
	fun `empty pantry-filtered search returns near miss recipes with one missing ingredient`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		seedAppUsersForSearchRouteTest(db)
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

		val accessToken = sessionService.session.accessToken
		createRecipeForSearchRouteTest(
			accessToken = accessToken,
			title = "Almost Stew",
			ingredients = recipeIngredientsForRouteTest("Chicken breast", "Tomato", "Basil"),
		)
		createRecipeForSearchRouteTest(
			accessToken = accessToken,
			title = "Two Missing Bowl",
			ingredients = recipeIngredientsForRouteTest("Chicken breast", "Rice", "Onion"),
		)
		createRecipeForSearchRouteTest(
			accessToken = accessToken,
			title = "Garlic Tomato Side",
			ingredients = recipeIngredientsForRouteTest("Tomato", "Garlic"),
		)
		updatePantryForSearchRouteTest(
			accessToken = accessToken,
			add = listOf("Chicken", "Tomato", "Salt"),
		)
		updateExcludedIngredientsForSearchRouteTest(
			accessToken = accessToken,
			add = listOf("Garlic"),
		)

		val responseBody = searchWithFiltersForSearchRouteTest(
			"""
				{
					"query": "",
					"filters": {}
				}
			""".trimIndent(),
			accessToken = accessToken,
		)

		responseBody.contains("\"totalMatches\":0") shouldBe true
		mainSearchItemTitles(responseBody) shouldBe emptyList()
		nearMissSearchItemTitles(responseBody) shouldContain "Almost Stew"
		responseBody.contains("\"missingIngredient\":\"Basil\"") shouldBe true
		nearMissSearchItemTitles(responseBody) shouldNotContain "Two Missing Bowl"
		nearMissSearchItemTitles(responseBody) shouldNotContain "Garlic Tomato Side"
	}

	@Test
	fun `sparse pantry-filtered search appends near miss recipes after matches`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		seedAppUsersForSearchRouteTest(db)
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

		val accessToken = sessionService.session.accessToken
		createRecipeForSearchRouteTest(
			accessToken = accessToken,
			title = "Chicken Tomato Stew",
			ingredients = recipeIngredientsForRouteTest("Chicken breast", "Tomato", "Salt"),
		)
		createRecipeForSearchRouteTest(
			accessToken = accessToken,
			title = "Almost Stew",
			ingredients = recipeIngredientsForRouteTest("Chicken breast", "Tomato", "Basil"),
		)
		updatePantryForSearchRouteTest(
			accessToken = accessToken,
			add = listOf("Chicken", "Tomato", "Salt"),
		)

		val responseBody = searchWithFiltersForSearchRouteTest(
			"""
				{
					"query": "",
					"filters": {}
				}
			""".trimIndent(),
			accessToken = accessToken,
		)

		responseBody.contains("\"totalMatches\":1") shouldBe true
		mainSearchItemTitles(responseBody) shouldBe listOf("Chicken Tomato Stew")
		nearMissSearchItemTitles(responseBody) shouldContain "Almost Stew"
		responseBody.contains("\"missingIngredient\":\"Basil\"") shouldBe true
	}
}
