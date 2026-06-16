package app.purecipes.backend

import app.purecipes.backend.fake.FakeSessionService
import io.kotest.matchers.shouldBe
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.routing.get
import io.ktor.server.testing.testApplication
import kotlin.test.Test

class RecipeSearchRouteTest {

	@Test
	fun `missing query yields 400`() = testApplication {
		application { module(db = createInMemoryDb("recipe_search_route")) }

		val response = client.get("/recipes/search")
		response.status shouldBe HttpStatusCode.BadRequest
		response.bodyAsText() shouldBe """{"message":"Invalid request","detail":"Missing query parameter: query"}"""
	}

	@Test
	fun `health endpoint returns ok`() = testApplication {
		application { module(db = createInMemoryDb("recipe_search_route")) }

		val response = client.get("/health")
		response.status shouldBe HttpStatusCode.OK
		response.bodyAsText()
	}

	@Test
	fun `unhandled exception yields message and detail`() = testApplication {
		application {
			module(
				db = createInMemoryDb("recipe_search_route"),
				extraRoutes = {
					get("/boom") {
						error("kaboom")
					}
				},
			)
		}

		val response = client.get("/boom")
		response.status shouldBe HttpStatusCode.InternalServerError
		response.bodyAsText() shouldBe
			"""{"message":"Something went wrong. Please try again.","detail":"kaboom"}"""
	}

	@Test
	fun `invalid recipe id yields 400`() = testApplication {
		application { module(db = createInMemoryDb("recipe_search_route")) }

		val response = client.get("/recipes/not-a-number")
		response.status shouldBe HttpStatusCode.BadRequest
		response.bodyAsText() shouldBe """{"message":"Invalid request","detail":"Recipe id must be a number"}"""
	}

	@Test
	fun `invalid favorite recipe id yields 400 on add`() = testApplication {
		application { module(db = createInMemoryDb("recipe_search_route")) }

		val response = client.post("/favorites/not-a-number")
		response.status shouldBe HttpStatusCode.BadRequest
		response.bodyAsText() shouldBe """{"message":"Invalid request","detail":"Recipe id must be a number"}"""
	}

	@Test
	fun `invalid favorite recipe id yields 400 on delete`() = testApplication {
		application { module(db = createInMemoryDb("recipe_search_route")) }

		val response = client.delete("/favorites/not-a-number")
		response.status shouldBe HttpStatusCode.BadRequest
		response.bodyAsText() shouldBe """{"message":"Invalid request","detail":"Recipe id must be a number"}"""
	}

	@Test
	fun `authenticated search uses persisted pantry filters`() = testApplication {
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
					"filters": {}
				}
			""".trimIndent(),
			accessToken = sessionService.session.accessToken,
		)

		responseBody.contains("Chicken Tomato Stew") shouldBe true
		responseBody.contains("Chicken Rice Bowl") shouldBe false
		responseBody.contains("Tomato Basil Soup") shouldBe false
	}

	@Test
	fun `unauthenticated search ignores persisted pantry filtering`() = testApplication {
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
					"filters": {}
				}
			""".trimIndent(),
		)

		responseBody.contains("Chicken Tomato Stew") shouldBe true
		responseBody.contains("Chicken Rice Bowl") shouldBe true
		responseBody.contains("Tomato Basil Soup") shouldBe true
	}

	@Test
	fun `keyword search response omits has next page and includes page metadata`() = testApplication {
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

		repeat(3) { index ->
			createRecipeForSearchRouteTest(
				accessToken = sessionService.session.accessToken,
				title = "Keyword Match $index",
				ingredients = listOf("Tomato", "Salt"),
			)
		}

		val response = client.get("/recipes/search?query=keyword&pageNumber=1&pageSize=2")

		response.status shouldBe HttpStatusCode.OK
		val responseBody = response.bodyAsText()
		responseBody.contains("\"pageNumber\":1") shouldBe true
		responseBody.contains("\"pageSize\":2") shouldBe true
		responseBody.contains("\"totalMatches\":3") shouldBe true
	}

	@Test
	fun `persisted pantry supports case insensitive partial matching`() = testApplication {
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
			add = listOf("chIck", "ToMa", "saL"),
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
		responseBody.contains("Chicken Rice Bowl") shouldBe false
	}

	@Test
	fun `persisted pantry ignores blank values while preserving pantry semantics`() = testApplication {
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
			add = listOf("  ", "Chicken", "Tomato", "Salt"),
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
		responseBody.contains("Chicken Rice Bowl") shouldBe false
		responseBody.contains("Tomato Basil Soup") shouldBe false
	}

	@Test
	fun `persisted pantry ignores default pantry ingredients`() = testApplication {
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
			title = "Chicken Tomato Broth",
			ingredients = listOf("Chicken breast", "Tomato", "Salt", "Water", "Vegetable Oil"),
		)
		updatePantryForSearchRouteTest(
			accessToken = sessionService.session.accessToken,
			add = listOf("Chicken", "Tomato"),
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

		responseBody.contains("Chicken Tomato Broth") shouldBe true
	}

	@Test
	fun `persisted pantry matches alternative ingredient names`() = testApplication {
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
			ingredients = listOf("Rice", "Cilantro"),
		)
		updatePantryForSearchRouteTest(
			accessToken = sessionService.session.accessToken,
			add = listOf("Rice", "Coriander"),
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

		responseBody.contains("Cilantro Rice") shouldBe true
	}

	@Test
	fun `persisted pantry matches singular and plural forms`() = testApplication {
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
			ingredients = listOf("Egg", "Pea", "Rice"),
		)
		updatePantryForSearchRouteTest(
			accessToken = sessionService.session.accessToken,
			add = listOf("Eggs", "Peas", "Rice"),
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

		responseBody.contains("Egg Fried Rice") shouldBe true
	}

	@Test
	fun `search ignores request available ingredients and uses persisted pantry only`() = testApplication {
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
			ingredients = listOf("Tomato", "Basil"),
		)
		updatePantryForSearchRouteTest(
			accessToken = sessionService.session.accessToken,
			add = listOf("Tomato", "Basil"),
		)

		val response = client.post("/recipes/search") {
			header(HttpHeaders.Authorization, "Bearer ${sessionService.session.accessToken}")
			contentType(ContentType.Application.Json)
			setBody(
				"""
					{
						"query": "",
						"filters": {
							"availableIngredients": ["Rice"]
						}
					}
				""".trimIndent(),
			)
		}

		response.status shouldBe HttpStatusCode.OK
		response.bodyAsText().contains("Tomato Soup") shouldBe true
	}

	@Test
	fun `persisted pantry filtering applies before paging and returns total matches`() =
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
					title = "Non match $index",
					ingredients = listOf("Rice", "Butter"),
				)
			}

			repeat(4) { index ->
				createRecipeForSearchRouteTest(
					accessToken = sessionService.session.accessToken,
					title = "Match $index",
					ingredients = listOf("Chicken breast", "Tomato", "Salt"),
				)
			}
			updatePantryForSearchRouteTest(
				accessToken = sessionService.session.accessToken,
				add = listOf("Chicken", "Tomato", "Salt"),
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
			responseBody.contains("Match 0") shouldBe true
			responseBody.contains("Match 3") shouldBe true
		}

}
