package app.purecipes.backend

import app.purecipes.backend.db.Db
import app.purecipes.backend.fake.FakeSessionService
import io.kotest.matchers.shouldBe
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.routing.get
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import org.h2.jdbcx.JdbcDataSource
import kotlin.test.Test

class RecipeSearchRouteTest {

	@Test
	fun `missing query yields 400`() = testApplication {
		application { module() }

		val response = client.get("/recipes/search")
		response.status shouldBe HttpStatusCode.BadRequest
		response.bodyAsText() shouldBe """{"message":"Invalid request","detail":"Missing query parameter: query"}"""
	}

	@Test
	fun `health endpoint returns ok`() = testApplication {
		application { module() }

		val response = client.get("/health")
		response.status shouldBe HttpStatusCode.OK
		response.bodyAsText()
	}

	@Test
	fun `unhandled exception yields message and detail`() = testApplication {
		application {
			module(extraRoutes = {
				get("/boom") {
					error("kaboom")
				}
			})
		}

		val response = client.get("/boom")
		response.status shouldBe HttpStatusCode.InternalServerError
		response.bodyAsText() shouldBe """{"message":"Unexpected error","detail":"kaboom"}"""
	}

	@Test
	fun `invalid recipe id yields 400`() = testApplication {
		application { module() }

		val response = client.get("/recipes/not-a-number")
		response.status shouldBe HttpStatusCode.BadRequest
		response.bodyAsText() shouldBe """{"message":"Invalid request","detail":"Recipe id must be a number"}"""
	}

	@Test
	fun `invalid favorite recipe id yields 400 on add`() = testApplication {
		application { module() }

		val response = client.post("/favorites/not-a-number")
		response.status shouldBe HttpStatusCode.BadRequest
		response.bodyAsText() shouldBe """{"message":"Invalid request","detail":"Recipe id must be a number"}"""
	}

	@Test
	fun `invalid favorite recipe id yields 400 on delete`() = testApplication {
		application { module() }

		val response = client.delete("/favorites/not-a-number")
		response.status shouldBe HttpStatusCode.BadRequest
		response.bodyAsText() shouldBe """{"message":"Invalid request","detail":"Recipe id must be a number"}"""
	}

	@Test
	fun `authenticated search uses persisted pantry filters`() = testApplication {
		val db = createDb()
		seedAppUsers(db)
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

		seedRecipeCatalog(accessToken = sessionService.session.accessToken)
		updatePantry(
			accessToken = sessionService.session.accessToken,
			add = listOf("Chicken", "Tomato", "Salt"),
		)

		val responseBody = searchWithFilters(
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
		val db = createDb()
		seedAppUsers(db)
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

		seedRecipeCatalog(accessToken = sessionService.session.accessToken)
		updatePantry(
			accessToken = sessionService.session.accessToken,
			add = listOf("Chicken", "Tomato", "Salt"),
		)

		val responseBody = searchWithFilters(
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
		val db = createDb()
		seedAppUsers(db)
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
			createRecipe(
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
		val db = createDb()
		seedAppUsers(db)
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

		seedRecipeCatalog(accessToken = sessionService.session.accessToken)
		updatePantry(
			accessToken = sessionService.session.accessToken,
			add = listOf("chIck", "ToMa", "saL"),
		)

		val responseBody = searchWithFilters(
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
		val db = createDb()
		seedAppUsers(db)
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

		seedRecipeCatalog(accessToken = sessionService.session.accessToken)
		updatePantry(
			accessToken = sessionService.session.accessToken,
			add = listOf("  ", "Chicken", "Tomato", "Salt"),
		)

		val responseBody = searchWithFilters(
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
		val db = createDb()
		seedAppUsers(db)
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

		createRecipe(
			accessToken = sessionService.session.accessToken,
			title = "Chicken Tomato Broth",
			ingredients = listOf("Chicken breast", "Tomato", "Salt", "Water", "Vegetable Oil"),
		)
		updatePantry(
			accessToken = sessionService.session.accessToken,
			add = listOf("Chicken", "Tomato"),
		)

		val responseBody = searchWithFilters(
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
		val db = createDb()
		seedAppUsers(db)
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

		createRecipe(
			accessToken = sessionService.session.accessToken,
			title = "Cilantro Rice",
			ingredients = listOf("Rice", "Cilantro"),
		)
		updatePantry(
			accessToken = sessionService.session.accessToken,
			add = listOf("Rice", "Coriander"),
		)

		val responseBody = searchWithFilters(
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
		val db = createDb()
		seedAppUsers(db)
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

		createRecipe(
			accessToken = sessionService.session.accessToken,
			title = "Egg Fried Rice",
			ingredients = listOf("Egg", "Pea", "Rice"),
		)
		updatePantry(
			accessToken = sessionService.session.accessToken,
			add = listOf("Eggs", "Peas", "Rice"),
		)

		val responseBody = searchWithFilters(
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
		val db = createDb()
		seedAppUsers(db)
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

		createRecipe(
			accessToken = sessionService.session.accessToken,
			title = "Tomato Soup",
			ingredients = listOf("Tomato", "Basil"),
		)
		updatePantry(
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
		val db = createDb()
		seedAppUsers(db)
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
			createRecipe(
				accessToken = sessionService.session.accessToken,
				title = "Non match $index",
				ingredients = listOf("Rice", "Butter"),
			)
		}

		repeat(4) { index ->
			createRecipe(
				accessToken = sessionService.session.accessToken,
				title = "Match $index",
				ingredients = listOf("Chicken breast", "Tomato", "Salt"),
			)
		}
		updatePantry(
			accessToken = sessionService.session.accessToken,
			add = listOf("Chicken", "Tomato", "Salt"),
		)

		val responseBody = searchWithFilters(
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

	private suspend fun ApplicationTestBuilder.createRecipe(
		accessToken: String,
		title: String,
		ingredients: List<String>,
	) {
		val ingredientsJson = ingredients.joinToString(separator = ",") { "\"$it\"" }
		val response = client.post("/recipes") {
			header(HttpHeaders.Authorization, "Bearer $accessToken")
			contentType(ContentType.Application.Json)
			setBody(
				"""
					{
						"title": "$title",
						"description": "Recipe for $title",
						"ingredientGroups": [
							{
								"ingredients": [$ingredientsJson]
							}
						],
						"steps": ["Step 1"]
					}
				""".trimIndent(),
			)
		}
		response.status shouldBe HttpStatusCode.Created
	}

	private suspend fun ApplicationTestBuilder.seedRecipeCatalog(accessToken: String) {
		createRecipe(
			accessToken = accessToken,
			title = "Chicken Tomato Stew",
			ingredients = listOf("Chicken breast", "Tomato", "Salt"),
		)
		createRecipe(
			accessToken = accessToken,
			title = "Chicken Rice Bowl",
			ingredients = listOf("Chicken breast", "Rice", "Salt"),
		)
		createRecipe(
			accessToken = accessToken,
			title = "Tomato Basil Soup",
			ingredients = listOf("Tomato", "Basil", "Garlic"),
		)
		createRecipe(
			accessToken = accessToken,
			title = "Garlic Rice",
			ingredients = listOf("Garlic", "Rice", "Butter"),
		)
		createRecipe(
			accessToken = accessToken,
			title = "Veggie Omelette",
			ingredients = listOf("Eggs", "Tomato", "Onion"),
		)
	}

	private suspend fun ApplicationTestBuilder.searchWithFilters(
		requestBody: String,
		accessToken: String? = null,
	): String {
		val response = client.post("/recipes/search") {
			if (accessToken != null) {
				header(HttpHeaders.Authorization, "Bearer $accessToken")
			}
			contentType(ContentType.Application.Json)
			setBody(requestBody)
		}
		response.status shouldBe HttpStatusCode.OK
		return response.bodyAsText()
	}

	private suspend fun ApplicationTestBuilder.updatePantry(
		accessToken: String,
		add: List<String>,
	) {
		val addJson = add.joinToString(separator = ",") { "\"$it\"" }
		val response = client.patch("/settings/pantry") {
			header(HttpHeaders.Authorization, "Bearer $accessToken")
			contentType(ContentType.Application.Json)
			setBody(
				"""
					{
						"add": [$addJson],
						"remove": []
					}
				""".trimIndent(),
			)
		}
		response.status shouldBe HttpStatusCode.OK
	}

	private fun createDb(): Db {
		val dbName = "recipe_search_${System.nanoTime()}"
		val dataSource = JdbcDataSource().apply {
			setURL("jdbc:h2:mem:$dbName;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE")
			user = "sa"
			password = ""
		}
		return Db.fromDataSource(dataSource)
	}

	private fun seedAppUsers(db: Db) {
		db.dataSource.connection.use { connection ->
			connection.createStatement().use { statement ->
				statement.execute(
					"""
						INSERT INTO app_users (
							id,
							provider,
							external_user_id,
							email,
							display_name,
							first_name,
							family_name,
							profile_image_url
						) VALUES (
							1,
							'GOOGLE',
							'user-one',
							'user-one@example.com',
							'User One',
							'User',
							'One',
							NULL
						)
					""".trimIndent(),
				)
			}
		}
	}

}
