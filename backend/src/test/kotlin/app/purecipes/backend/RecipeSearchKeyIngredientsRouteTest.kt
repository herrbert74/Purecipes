package app.purecipes.backend

import app.purecipes.backend.fake.FakeSessionService
import io.kotest.matchers.shouldBe
import io.ktor.server.testing.testApplication
import kotlin.test.Test

class RecipeSearchKeyIngredientsRouteTest {

	@Test
	fun `search with key ingredients returns only matching recipes`() = testApplication {
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
					"filters": {},
					"keyIngredients": ["Tomato", "Chicken"]
				}
			""".trimIndent(),
			accessToken = sessionService.session.accessToken,
		)

		responseBody.contains("Chicken Tomato Stew") shouldBe true
		responseBody.contains("Chicken Rice Bowl") shouldBe false
		responseBody.contains("Tomato Basil Soup") shouldBe false
		responseBody.contains("Veggie Omelette") shouldBe false
	}

	@Test
	fun `unauthenticated search applies key ingredients while temporary kill switch is on`() = testApplication {
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
					"filters": {},
					"keyIngredients": ["Garlic"]
				}
			""".trimIndent(),
		)

		responseBody.contains("Garlic Rice") shouldBe true
		responseBody.contains("Tomato Basil Soup") shouldBe true
		responseBody.contains("Chicken Tomato Stew") shouldBe false
		responseBody.contains("Veggie Omelette") shouldBe false
	}

	@Test
	fun `key ingredients compose with persisted pantry filtering`() = testApplication {
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
					"query": "",
					"filters": {},
					"keyIngredients": ["Tomato"]
				}
			""".trimIndent(),
			accessToken = sessionService.session.accessToken,
		)

		responseBody.contains("Chicken Tomato Stew") shouldBe true
		responseBody.contains("Tomato Basil Soup") shouldBe false
		responseBody.contains("Veggie Omelette") shouldBe false
	}
}
