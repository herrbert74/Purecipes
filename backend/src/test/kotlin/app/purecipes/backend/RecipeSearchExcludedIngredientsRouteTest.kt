package app.purecipes.backend

import app.purecipes.backend.fake.FakeSessionService
import app.purecipes.shared.domain.model.RecipeIngredient
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlin.test.Test

class RecipeSearchExcludedIngredientsRouteTest {

	@Test
	fun `authenticated search uses persisted excluded ingredients filters`() = testApplication {
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
					"query": "",
					"filters": {}
				}
			""".trimIndent(),
			accessToken = sessionService.session.accessToken,
		)

		responseBody.contains("Chicken Tomato Stew") shouldBe true
		responseBody.contains("Tomato Basil Soup") shouldBe false
		responseBody.contains("Garlic Rice") shouldBe false
	}

	@Test
	fun `unauthenticated search ignores persisted excluded ingredients filtering`() = testApplication {
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
					"query": "",
					"filters": {}
				}
			""".trimIndent(),
		)

		responseBody.contains("Tomato Basil Soup") shouldBe true
		responseBody.contains("Garlic Rice") shouldBe true
	}

	@Test
	fun `persisted excluded ingredients match alternative ingredient names`() = testApplication {
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

		createRecipeForSearchRouteTest(
			accessToken = sessionService.session.accessToken,
			title = "Cilantro Rice",
			ingredients = recipeIngredientsForRouteTest("Rice", "Cilantro"),
		)
		updateExcludedIngredientsForSearchRouteTest(
			accessToken = sessionService.session.accessToken,
			add = listOf("Coriander"),
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

		responseBody.contains("Cilantro Rice") shouldBe false
	}

	@Test
	fun `persisted excluded ingredients match singular and plural forms`() = testApplication {
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

		createRecipeForSearchRouteTest(
			accessToken = sessionService.session.accessToken,
			title = "Egg Fried Rice",
			ingredients = recipeIngredientsForRouteTest("Egg", "Pea", "Rice"),
		)
		updateExcludedIngredientsForSearchRouteTest(
			accessToken = sessionService.session.accessToken,
			add = listOf("Eggs"),
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

		responseBody.contains("Egg Fried Rice") shouldBe false
	}

	@Test
	fun `search ignores request excluded ingredients and uses persisted exclusions only`() = testApplication {
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

		createRecipeForSearchRouteTest(
			accessToken = sessionService.session.accessToken,
			title = "Tomato Soup",
			ingredients = recipeIngredientsForRouteTest("Tomato", "Basil"),
		)
		updateExcludedIngredientsForSearchRouteTest(
			accessToken = sessionService.session.accessToken,
			add = listOf("Basil"),
		)

		val response = client.post("/recipes/search") {
			header(HttpHeaders.Authorization, "Bearer ${sessionService.session.accessToken}")
			contentType(ContentType.Application.Json)
			setBody(
				"""
					{
						"query": "",
						"filters": {
							"excludedIngredients": ["Garlic"]
						}
					}
				""".trimIndent(),
			)
		}

		response.status shouldBe HttpStatusCode.OK
		response.bodyAsText().contains("Tomato Soup") shouldBe false
	}

	@Test
	fun `persisted exclusion and pantry filters apply together`() = testApplication {
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
		updateExcludedIngredientsForSearchRouteTest(
			accessToken = sessionService.session.accessToken,
			add = listOf("Tomato"),
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

		mainSearchItemTitles(responseBody) shouldBe emptyList()
		mainSearchItemTitles(responseBody) shouldNotContain "Chicken Tomato Stew"
		mainSearchItemTitles(responseBody) shouldNotContain "Tomato Basil Soup"
		nearMissSearchItemTitles(responseBody) shouldContain "Chicken Rice Bowl"
		nearMissSearchItemTitles(responseBody) shouldNotContain "Chicken Tomato Stew"
		nearMissSearchItemTitles(responseBody) shouldNotContain "Tomato Basil Soup"
	}

	@Test
	fun `persisted excluded ingredients filtering applies before paging and returns total matches`() =
		testApplication {
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

			repeat(26) { index ->
				createRecipeForSearchRouteTest(
					accessToken = sessionService.session.accessToken,
					title = "Garlic dish $index",
					ingredients = recipeIngredientsForRouteTest("Garlic", "Butter"),
				)
			}

			repeat(4) { index ->
				createRecipeForSearchRouteTest(
					accessToken = sessionService.session.accessToken,
					title = "Safe dish $index",
					ingredients = recipeIngredientsForRouteTest("Chicken breast", "Tomato", "Salt"),
				)
			}
			updateExcludedIngredientsForSearchRouteTest(
				accessToken = sessionService.session.accessToken,
				add = listOf("Garlic"),
			)

			val responseBody = searchWithFiltersForSearchRouteTest(
				"""
				{
					"query": "",
					"pageNumber": 1,
					"pageSize": 20,
					"filters": {}
				}
			""".trimIndent(),
				accessToken = sessionService.session.accessToken,
			)

			responseBody.contains("\"totalMatches\":4") shouldBe true
			responseBody.contains("Safe dish 0") shouldBe true
			responseBody.contains("Safe dish 3") shouldBe true
			responseBody.contains("Garlic dish 0") shouldBe false
		}

	@Test
	fun `persisted exclusion ignores recipe when all alternatives are excluded`() = testApplication {
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

		createRecipeForSearchRouteTest(
			accessToken = sessionService.session.accessToken,
			title = "Herb Chicken",
			ingredients = listOf(
				RecipeIngredient(text = "Chicken breast"),
			) + alternativeRecipeIngredientsForRouteTest("Parsley", "Tarragon"),
		)
		updateExcludedIngredientsForSearchRouteTest(
			accessToken = sessionService.session.accessToken,
			add = listOf("Parsley", "Tarragon"),
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

		responseBody.contains("Herb Chicken") shouldBe false
	}

	@Test
	fun `persisted exclusion keeps recipe when only one alternative is excluded`() = testApplication {
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

		createRecipeForSearchRouteTest(
			accessToken = sessionService.session.accessToken,
			title = "Herb Rice",
			ingredients = listOf(
				RecipeIngredient(text = "Rice"),
			) + alternativeRecipeIngredientsForRouteTest("Parsley", "Tarragon"),
		)
		updateExcludedIngredientsForSearchRouteTest(
			accessToken = sessionService.session.accessToken,
			add = listOf("Parsley"),
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

		responseBody.contains("Herb Rice") shouldBe true
	}
}
