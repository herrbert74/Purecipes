package app.purecipes.backend

import app.purecipes.backend.db.Db
import app.purecipes.backend.fake.FakeSessionService
import io.kotest.matchers.shouldBe
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import org.h2.jdbcx.JdbcDataSource
import kotlin.io.path.createTempDirectory
import kotlin.test.Test

class RecipeUploadRouteTest {

	@Test
	fun `recipe upload endpoints require bearer token`() = testApplication {
		val db = createDb()
		val recipeImageStorage = RecipeImageStorage(createTempDirectory("recipe-images-test"))

		application {
			module(
				db = db,
				sessionService = FakeSessionService(),
				recipeImageStorage = recipeImageStorage,
			)
		}

		val request = """
			{
				"title": "Roasted Carrots",
				"description": "Sweet and savory side dish.",
				"steps": ["Trim the carrots", "Roast until tender"]
			}
		""".trimIndent()

		client.get("/recipes/mine").also {
			it.status shouldBe HttpStatusCode.Unauthorized
		}

		client.post("/recipes") {
			contentType(ContentType.Application.Json)
			setBody(request)
		}.also {
			it.status shouldBe HttpStatusCode.Unauthorized
		}

		client.put("/recipes/1") {
			contentType(ContentType.Application.Json)
			setBody(request)
		}.also {
			it.status shouldBe HttpStatusCode.Unauthorized
		}

		client.post("/recipe-images") {
			setBody(
				MultiPartFormDataContent(
					formData {
						append(
							"image",
							byteArrayOf(1, 2, 3),
							Headers.build {
								append(HttpHeaders.ContentDisposition, "filename=image.jpg")
								append(HttpHeaders.ContentType, ContentType.Image.JPEG.toString())
							},
						)
					},
				),
			)
		}.also {
			it.status shouldBe HttpStatusCode.Unauthorized
		}
	}

	@Test
	fun `authenticated user can upload and update recipe`() = testApplication {
		val db = createDb()
		seedAppUsers(db)
		val sessionService = FakeSessionService(
			initialSessions = listOf(
				FakeSessionService.createSession(),
			),
			createMode = FakeSessionService.CreateMode.RETURN_FIRST_OR_GENERATE,
		)
		val recipeImageStorage = RecipeImageStorage(createTempDirectory("recipe-images-test"))

		application {
			module(
				db = db,
				sessionService = sessionService,
				recipeImageStorage = recipeImageStorage,
			)
		}

		val createResponse = client.post("/recipes") {
			header(HttpHeaders.Authorization, "Bearer ${sessionService.session.accessToken}")
			contentType(ContentType.Application.Json)
			setBody(
				"""
					{
						"title": "Roasted Carrots",
						"description": "Sweet and savory side dish.",
						"ingredientGroups": [
							{
								"ingredients": ["8 cups (1.9L) vegetable stock"]
							}
						],
						"steps": ["Trim the carrots", "Roast until tender"],
						"imageUrl": "https://example.com/carrots.jpg",
						"cuisine": "Mediterranean",
						"yields": "4 servings"
					}
				""".trimIndent(),
			)
		}
		createResponse.status shouldBe HttpStatusCode.Created
		val createResponseBody = createResponse.bodyAsText()
		assertBodyContains(createResponseBody, jsonField("title", "Roasted Carrots"))
		assertBodyContains(
			createResponseBody,
			jsonField("description", "Sweet and savory side dish."),
		)
		assertBodyContains(createResponseBody, jsonField("measurementSystem", "MIXED"))
		db.dataSource.connection.use { connection ->
			connection.prepareStatement("SELECT measurement_system FROM recipes WHERE id = ?").use { statement ->
				statement.setInt(1, 1)
				val resultSet = statement.executeQuery()
				resultSet.next() shouldBe true
				resultSet.getString(1) shouldBe "MIXED"
			}
		}

		val mineResponse = client.get("/recipes/mine") {
			header(HttpHeaders.Authorization, "Bearer ${sessionService.session.accessToken}")
		}
		mineResponse.status shouldBe HttpStatusCode.OK
		assertBodyContains(mineResponse.bodyAsText(), jsonField("title", "Roasted Carrots"))

		val updateResponse = client.put("/recipes/1") {
			header(HttpHeaders.Authorization, "Bearer ${sessionService.session.accessToken}")
			contentType(ContentType.Application.Json)
			setBody(
				"""
					{
						"title": "Honey Roasted Carrots",
						"description": "Updated side dish.",
						"ingredientGroups": [
							{
								"ingredients": ["1900 mL vegetable stock"]
							}
						],
						"steps": ["Trim the carrots", "Roast with honey"]
					}
				""".trimIndent(),
			)
		}
		updateResponse.status shouldBe HttpStatusCode.OK
		val updateResponseBody = updateResponse.bodyAsText()
		assertBodyContains(updateResponseBody, jsonField("title", "Honey Roasted Carrots"))
		assertBodyContains(updateResponseBody, jsonField("description", "Updated side dish."))
		assertBodyContains(updateResponseBody, jsonField("measurementSystem", "METRIC"))
		db.dataSource.connection.use { connection ->
			connection.prepareStatement("SELECT measurement_system FROM recipes WHERE id = ?").use { statement ->
				statement.setInt(1, 1)
				val resultSet = statement.executeQuery()
				resultSet.next() shouldBe true
				resultSet.getString(1) shouldBe "METRIC"
			}
		}
	}

	@Test
	fun `authenticated user can upload recipe image`() = testApplication {
		val db = createDb()
		seedAppUsers(db)
		val sessionService = FakeSessionService(
			initialSessions = listOf(
				FakeSessionService.createSession(),
			),
			createMode = FakeSessionService.CreateMode.RETURN_FIRST_OR_GENERATE,
		)
		val recipeImageStorage = RecipeImageStorage(createTempDirectory("recipe-images-test"))

		application {
			module(
				db = db,
				sessionService = sessionService,
				recipeImageStorage = recipeImageStorage,
			)
		}

		val uploadResponse = client.post("/recipe-images") {
			header(HttpHeaders.Authorization, "Bearer ${sessionService.session.accessToken}")
			setBody(
				MultiPartFormDataContent(
					formData {
						append(
							"image",
							byteArrayOf(1, 2, 3, 4),
							Headers.build {
								append(HttpHeaders.ContentDisposition, "filename=carrots.jpg")
								append(HttpHeaders.ContentType, ContentType.Image.JPEG.toString())
							},
						)
					},
				),
			)
		}

		uploadResponse.status shouldBe HttpStatusCode.Created
		val responseBody = uploadResponse.bodyAsText()
		assertBodyContains(responseBody, """"imageUrl":""")
		assertBodyContains(responseBody, "/uploads/recipes/")
	}

	private fun createDb(): Db {
		val dbName = "recipe_upload_${System.nanoTime()}"
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

	private fun assertBodyContains(body: String, expectedFragment: String) {
		if (!body.contains(expectedFragment)) {
			throw AssertionError("Expected body to contain $expectedFragment but was: $body")
		}
	}

	private fun jsonField(name: String, value: String): String = """"$name":"$value"""
}
