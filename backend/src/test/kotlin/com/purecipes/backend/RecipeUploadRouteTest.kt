package com.purecipes.backend

import com.purecipes.backend.auth.SessionService
import com.purecipes.backend.db.Db
import com.purecipes.shared.domain.model.AuthenticatedBackendUser
import com.purecipes.shared.domain.model.AuthenticatedSession
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
import kotlin.test.assertEquals

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
			assertEquals(HttpStatusCode.Unauthorized, it.status)
		}

		client.post("/recipes") {
			contentType(ContentType.Application.Json)
			setBody(request)
		}.also {
			assertEquals(HttpStatusCode.Unauthorized, it.status)
		}

		client.put("/recipes/1") {
			contentType(ContentType.Application.Json)
			setBody(request)
		}.also {
			assertEquals(HttpStatusCode.Unauthorized, it.status)
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
			assertEquals(HttpStatusCode.Unauthorized, it.status)
		}
	}

	@Test
	fun `authenticated user can upload and update recipe`() = testApplication {
		val db = createDb()
		seedAppUsers(db)
		val sessionService = FakeSessionService()
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
						"steps": ["Trim the carrots", "Roast until tender"],
						"imageUrl": "https://example.com/carrots.jpg",
						"cuisine": "Mediterranean",
						"yields": "4 servings"
					}
				""".trimIndent(),
			)
		}
		assertEquals(HttpStatusCode.Created, createResponse.status)
		assertBodyContains(createResponse.bodyAsText(), jsonField("title", "Roasted Carrots"))
		assertBodyContains(
			createResponse.bodyAsText(),
			jsonField("description", "Sweet and savory side dish."),
		)

		val mineResponse = client.get("/recipes/mine") {
			header(HttpHeaders.Authorization, "Bearer ${sessionService.session.accessToken}")
		}
		assertEquals(HttpStatusCode.OK, mineResponse.status)
		assertBodyContains(mineResponse.bodyAsText(), jsonField("title", "Roasted Carrots"))

		val updateResponse = client.put("/recipes/1") {
			header(HttpHeaders.Authorization, "Bearer ${sessionService.session.accessToken}")
			contentType(ContentType.Application.Json)
			setBody(
				"""
					{
						"title": "Honey Roasted Carrots",
						"description": "Updated side dish.",
						"steps": ["Trim the carrots", "Roast with honey"]
					}
				""".trimIndent(),
			)
		}
		assertEquals(HttpStatusCode.OK, updateResponse.status)
		assertBodyContains(updateResponse.bodyAsText(), jsonField("title", "Honey Roasted Carrots"))
		assertBodyContains(updateResponse.bodyAsText(), jsonField("description", "Updated side dish."))
	}

	@Test
	fun `authenticated user can upload recipe image`() = testApplication {
		val db = createDb()
		seedAppUsers(db)
		val sessionService = FakeSessionService()
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

		assertEquals(HttpStatusCode.Created, uploadResponse.status)
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

	private class FakeSessionService : SessionService {

		val session = AuthenticatedSession(
			accessToken = "session-token",
			expiresAtEpochSeconds = 4_102_444_800,
			user = AuthenticatedBackendUser(
				id = "1",
				email = "user-one@example.com",
				displayName = "User One",
				firstName = "User",
				familyName = "One",
				profileImageUrl = null,
				provider = "GOOGLE",
			),
		)

		override fun ensureSchema() = Unit

		override fun createSession(
			provider: String,
			externalUserId: String,
			email: String,
			displayName: String,
			firstName: String?,
			familyName: String?,
			profileImageUrl: String?,
		): AuthenticatedSession {
			return session
		}

		override fun getSession(accessToken: String): AuthenticatedSession? {
			return session.takeIf { it.accessToken == accessToken }
		}

		override fun revokeSession(accessToken: String): Boolean = accessToken == session.accessToken
	}
}
