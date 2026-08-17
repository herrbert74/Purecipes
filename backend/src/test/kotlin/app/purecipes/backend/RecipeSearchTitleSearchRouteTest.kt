package app.purecipes.backend

import app.purecipes.backend.fake.FakeSessionService
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.ktor.server.testing.testApplication
import kotlin.test.Test

class RecipeSearchTitleSearchRouteTest {

	@Test
	fun `authenticated title search ranks pantry matches instead of hiding them`() = testApplication {
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

		seedRecipeCatalogForSearchRouteTest(accessToken = sessionService.session.accessToken)
		updatePantryForSearchRouteTest(
			accessToken = sessionService.session.accessToken,
			add = listOf("Chicken", "Tomato", "Salt"),
		)

		val responseBody = searchWithFiltersForSearchRouteTest(
			"""
				{
					"query": "Chicken",
					"filters": {}
				}
			""".trimIndent(),
			accessToken = sessionService.session.accessToken,
		)

		mainSearchItemTitles(responseBody) shouldBe listOf(
			"Chicken Tomato Stew",
			"Chicken Rice Bowl",
		)
		nearMissSearchItemTitles(responseBody) shouldBe emptyList()
	}

	@Test
	fun `authenticated title search still hides excluded ingredients`() = testApplication {
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

		seedRecipeCatalogForSearchRouteTest(accessToken = sessionService.session.accessToken)
		updateExcludedIngredientsForSearchRouteTest(
			accessToken = sessionService.session.accessToken,
			add = listOf("Garlic"),
		)

		val responseBody = searchWithFiltersForSearchRouteTest(
			"""
				{
					"query": "Tomato",
					"filters": {}
				}
			""".trimIndent(),
			accessToken = sessionService.session.accessToken,
		)

		mainSearchItemTitles(responseBody) shouldBe listOf("Chicken Tomato Stew")
		responseBody.contains("Tomato Basil Soup") shouldBe false
	}

	@Test
	fun `authenticated title search applies recipe filters by default`() = testApplication {
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

		seedRecipeCatalogForSearchRouteTest(accessToken = sessionService.session.accessToken)

		val responseBody = searchWithFiltersForSearchRouteTest(
			"""
				{
					"query": "Chicken",
					"filters": {
						"dietaryPreferences": ["PALEO"]
					}
				}
			""".trimIndent(),
			accessToken = sessionService.session.accessToken,
		)

		mainSearchItemTitles(responseBody) shouldBe emptyList()
	}

	@Test
	fun `authenticated title search can skip recipe filters when requested`() = testApplication {
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

		seedRecipeCatalogForSearchRouteTest(accessToken = sessionService.session.accessToken)

		val responseBody = searchWithFiltersForSearchRouteTest(
			"""
				{
					"query": "Chicken",
					"filters": {
						"dietaryPreferences": ["PALEO"]
					},
					"applyRecipeFilters": false
				}
			""".trimIndent(),
			accessToken = sessionService.session.accessToken,
		)

		mainSearchItemTitles(responseBody) shouldContain "Chicken Tomato Stew"
		mainSearchItemTitles(responseBody) shouldContain "Chicken Rice Bowl"
	}

	@Test
	fun `blank query still applies recipe filters when skip flag is set`() = testApplication {
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

		seedRecipeCatalogForSearchRouteTest(accessToken = sessionService.session.accessToken)

		val responseBody = searchWithFiltersForSearchRouteTest(
			"""
				{
					"query": "",
					"filters": {
						"dietaryPreferences": ["PALEO"]
					},
					"applyRecipeFilters": false
				}
			""".trimIndent(),
			accessToken = sessionService.session.accessToken,
		)

		responseBody.contains("Chicken Tomato Stew") shouldBe false
		responseBody.contains("Tomato Basil Soup") shouldBe false
		responseBody.contains("Garlic Rice") shouldBe false
	}
}
