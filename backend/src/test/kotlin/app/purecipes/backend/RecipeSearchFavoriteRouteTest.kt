package app.purecipes.backend

import app.purecipes.backend.fake.FakeSessionService
import app.purecipes.shared.domain.model.SearchResultsPage
import io.kotest.matchers.shouldBe
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlin.test.Test

class RecipeSearchFavoriteRouteTest {

	private val json = Json {
		ignoreUnknownKeys = true
		explicitNulls = false
	}

	@Test
	fun `authenticated search marks favorited recipes`() = testApplication {
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
		seedRecipeCatalogForSearchRouteTest(accessToken = accessToken)

		val initialPage = json.decodeFromString<SearchResultsPage>(
			searchWithFiltersForSearchRouteTest(
				"""
					{
						"query": "",
						"filters": {}
					}
				""".trimIndent(),
				accessToken = accessToken,
			),
		)
		val stew = initialPage.items.single { item -> item.title == "Chicken Tomato Stew" }
		stew.isFavorite shouldBe false
		initialPage.items.none { item -> item.isFavorite } shouldBe true

		seedFavoriteForSearchRouteTest(db = db, userId = 1L, recipeId = stew.id)

		val favoritePage = json.decodeFromString<SearchResultsPage>(
			searchWithFiltersForSearchRouteTest(
				"""
					{
						"query": "",
						"filters": {}
					}
				""".trimIndent(),
				accessToken = accessToken,
			),
		)
		favoritePage.items.single { item -> item.title == "Chicken Tomato Stew" }.isFavorite shouldBe true
		favoritePage.items.filter { item -> item.title != "Chicken Tomato Stew" }
			.none { item -> item.isFavorite } shouldBe true
	}

	@Test
	fun `unauthenticated search does not mark recipes as favorite`() = testApplication {
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
		seedRecipeCatalogForSearchRouteTest(accessToken = accessToken)
		val stewId = json.decodeFromString<SearchResultsPage>(
			searchWithFiltersForSearchRouteTest(
				"""
					{
						"query": "",
						"filters": {}
					}
				""".trimIndent(),
				accessToken = accessToken,
			),
		).items.single { item -> item.title == "Chicken Tomato Stew" }.id
		seedFavoriteForSearchRouteTest(db = db, userId = 1L, recipeId = stewId)

		val signedOutPage = json.decodeFromString<SearchResultsPage>(
			searchWithFiltersForSearchRouteTest(
				"""
					{
						"query": "",
						"filters": {}
					}
				""".trimIndent(),
			),
		)
		signedOutPage.items.none { item -> item.isFavorite } shouldBe true
	}
}
