package com.purecipes.backend

import com.purecipes.backend.auth.SessionService
import com.purecipes.backend.db.Db
import com.purecipes.shared.domain.model.AuthenticatedBackendUser
import com.purecipes.shared.domain.model.AuthenticatedSession
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import org.h2.jdbcx.JdbcDataSource
import kotlin.test.Test
import kotlin.test.assertEquals

class RecipeUploadRouteTest {

	@Test
	fun `recipe upload endpoints require bearer token`() = testApplication {
		val db = createDb()

		application {
			module(
				db = db,
				sessionService = FakeSessionService(),
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
	}

	@Test
	fun `authenticated user can upload and update recipe`() = testApplication {
		val db = createDb()
		seedAppUsers(db)
		val sessionService = FakeSessionService()

		application {
			module(
				db = db,
				sessionService = sessionService,
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
		assertBodyContains(createResponse.bodyAsText(), """"title":"Roasted Carrots"""")
		assertBodyContains(createResponse.bodyAsText(), """"description":"Sweet and savory side dish."""")

		val mineResponse = client.get("/recipes/mine") {
			header(HttpHeaders.Authorization, "Bearer ${sessionService.session.accessToken}")
		}
		assertEquals(HttpStatusCode.OK, mineResponse.status)
		assertBodyContains(mineResponse.bodyAsText(), """"title":"Roasted Carrots"""")

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
		assertBodyContains(updateResponse.bodyAsText(), """"title":"Honey Roasted Carrots"""")
		assertBodyContains(updateResponse.bodyAsText(), """"description":"Updated side dish."""")
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

		override fun getSession(accessToken: String): AuthenticatedSession? = session.takeIf { it.accessToken == accessToken }

		override fun revokeSession(accessToken: String): Boolean = accessToken == session.accessToken
	}
}
