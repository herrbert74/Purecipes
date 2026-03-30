package com.purecipes.backend

import com.purecipes.backend.auth.SessionService
import com.purecipes.backend.db.Db
import com.purecipes.shared.domain.model.AuthenticatedBackendUser
import com.purecipes.shared.domain.model.AuthenticatedSession
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import org.h2.jdbcx.JdbcDataSource
import javax.sql.DataSource
import kotlin.test.Test
import kotlin.test.assertEquals

class FavoritesSessionIntegrationTest {

	@Test
	fun `favorites are isolated by authenticated session`() = testApplication {
		val dbName = "favorites_isolation_${System.nanoTime()}"
		val dataSource = JdbcDataSource().apply {
			setURL("jdbc:h2:mem:$dbName;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE")
			user = "sa"
			password = ""
		}
		seedRecipeTables(dataSource)
		val db = Db.fromDataSource(dataSource)
		seedAppUsers(db)
		val sessionService = FakeSessionService()
		val firstSession = sessionService.firstSession
		val secondSession = sessionService.secondSession
		seedFavorites(db)

		application {
			module(
				db = db,
				sessionService = sessionService,
			)
		}

		val firstFavoritesResponse = client.get("/favorites") {
			header(HttpHeaders.Authorization, "Bearer ${firstSession.accessToken}")
		}
		assertEquals(HttpStatusCode.OK, firstFavoritesResponse.status)
		val expectedFavoritesBody =
			"""
			[
				{
					"id":1,
					"title":"Tomato Pasta",
					"cuisine":"Italian",
					"imageUrl":"https://example.com/pasta.jpg",
					"totalTime":25,
					"isFavorite":true
				}
			]
			""".trimIndent().lines().joinToString(separator = "") {
				it.trim()
			}
		assertEquals(
			expectedFavoritesBody,
			firstFavoritesResponse.bodyAsText(),
		)

		val secondFavoritesResponse = client.get("/favorites") {
			header(HttpHeaders.Authorization, "Bearer ${secondSession.accessToken}")
		}
		assertEquals(HttpStatusCode.OK, secondFavoritesResponse.status)
		assertEquals("[]", secondFavoritesResponse.bodyAsText())

		val firstRecipeDetailsResponse = client.get("/recipes/1") {
			header(HttpHeaders.Authorization, "Bearer ${firstSession.accessToken}")
		}
		assertEquals(HttpStatusCode.OK, firstRecipeDetailsResponse.status)
		assertBodyContains(firstRecipeDetailsResponse.bodyAsText(), "\"isFavorite\":true")

		val secondRecipeDetailsResponse = client.get("/recipes/1") {
			header(HttpHeaders.Authorization, "Bearer ${secondSession.accessToken}")
		}
		assertEquals(HttpStatusCode.OK, secondRecipeDetailsResponse.status)
		assertBodyDoesNotContain(secondRecipeDetailsResponse.bodyAsText(), "\"isFavorite\":true")

		val signedOutRecipeDetailsResponse = client.get("/recipes/1")
		assertEquals(HttpStatusCode.OK, signedOutRecipeDetailsResponse.status)
		assertBodyDoesNotContain(signedOutRecipeDetailsResponse.bodyAsText(), "\"isFavorite\":true")

		val removeFavoriteResponse = client.delete("/favorites/1") {
			header(HttpHeaders.Authorization, "Bearer ${firstSession.accessToken}")
		}
		assertEquals(HttpStatusCode.NoContent, removeFavoriteResponse.status)

		val firstFavoritesAfterRemove = client.get("/favorites") {
			header(HttpHeaders.Authorization, "Bearer ${firstSession.accessToken}")
		}
		assertEquals(HttpStatusCode.OK, firstFavoritesAfterRemove.status)
		assertEquals("[]", firstFavoritesAfterRemove.bodyAsText())
	}

	private fun seedRecipeTables(dataSource: DataSource) {
		dataSource.connection.use { connection ->
			connection.createStatement().use { statement ->
				statement.execute(
					"""
						CREATE TABLE recipes (
							id INTEGER PRIMARY KEY,
							title VARCHAR(255) NOT NULL,
							instructions TEXT,
							total_time INTEGER,
							yields VARCHAR(255),
							image_url VARCHAR(512),
							cuisine VARCHAR(255),
							category VARCHAR(255),
							created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
						)
					""".trimIndent(),
				)
				statement.execute(
					"""
						CREATE TABLE ingredient_groups (
							id INTEGER PRIMARY KEY,
							recipe_id INTEGER NOT NULL,
							name VARCHAR(255),
							order_index INTEGER NOT NULL
						)
					""".trimIndent(),
				)
				statement.execute(
					"""
						CREATE TABLE ingredients (
							id INTEGER PRIMARY KEY,
							ingredient_group_id INTEGER NOT NULL,
							ingredient VARCHAR(255),
							order_index INTEGER NOT NULL
						)
					""".trimIndent(),
				)
				statement.execute(
					"""
						CREATE TABLE instruction_steps (
							id INTEGER PRIMARY KEY,
							recipe_id INTEGER NOT NULL,
							step TEXT,
							order_index INTEGER NOT NULL
						)
					""".trimIndent(),
				)
				statement.execute(
					"""
						INSERT INTO recipes (
							id,
							title,
							instructions,
							total_time,
							yields,
							image_url,
							cuisine,
							category
						) VALUES (
							1,
							'Tomato Pasta',
							'Boil pasta\nMake sauce',
							25,
							'2 servings',
							'https://example.com/pasta.jpg',
							'Italian',
							'Dinner'
						)
					""".trimIndent(),
				)
			}
		}
	}

	private fun seedFavorites(db: Db) {
		db.dataSource.connection.use { connection ->
			connection.createStatement().use { statement ->
				statement.execute(
					"""
						INSERT INTO favorites (user_id, recipe_id, created_at)
						VALUES (1, 1, CURRENT_TIMESTAMP)
					""".trimIndent(),
				)
			}
		}
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
						) VALUES
							(1, 'GOOGLE', 'user-one', 'user-one@example.com', 'User One', 'User', 'One', NULL),
							(2, 'GOOGLE', 'user-two', 'user-two@example.com', 'User Two', 'User', 'Two', NULL)
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

	private fun assertBodyDoesNotContain(body: String, unexpectedFragment: String) {
		if (body.contains(unexpectedFragment)) {
			throw AssertionError("Expected body to not contain $unexpectedFragment but was: $body")
		}
	}

	private class FakeSessionService : SessionService {

		val firstSession = createSession(
			accessToken = "session-token-1",
			id = "1",
			email = "user-one@example.com",
			displayName = "User One",
			firstName = "User",
			familyName = "One",
		)

		val secondSession = createSession(
			accessToken = "session-token-2",
			id = "2",
			email = "user-two@example.com",
			displayName = "User Two",
			firstName = "User",
			familyName = "Two",
		)

		private val sessionsByToken = mapOf(
			firstSession.accessToken to firstSession,
			secondSession.accessToken to secondSession,
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
			error("Not needed in this test")
		}

		override fun getSession(accessToken: String): AuthenticatedSession? = sessionsByToken[accessToken]

		override fun revokeSession(accessToken: String): Boolean = accessToken in sessionsByToken

		private fun createSession(
			accessToken: String,
			id: String,
			email: String,
			displayName: String,
			firstName: String,
			familyName: String,
		): AuthenticatedSession {
			return AuthenticatedSession(
				accessToken = accessToken,
				expiresAtEpochSeconds = 4_102_444_800,
				user = AuthenticatedBackendUser(
					id = id,
					email = email,
					displayName = displayName,
					firstName = firstName,
					familyName = familyName,
					profileImageUrl = null,
					provider = "GOOGLE",
				),
			)
		}
	}
}
