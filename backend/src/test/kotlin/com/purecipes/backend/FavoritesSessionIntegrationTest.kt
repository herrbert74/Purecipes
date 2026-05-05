package com.purecipes.backend

import com.purecipes.backend.db.Db
import com.purecipes.backend.fake.FakeSessionService
import com.purecipes.shared.domain.model.SearchResultsPage
import io.kotest.matchers.shouldBe
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import org.h2.jdbcx.JdbcDataSource
import javax.sql.DataSource
import kotlin.test.Test

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
		val firstSession = FakeSessionService.createSession(accessToken = "session-token-1")
		val secondSession = FakeSessionService.createSession(
			accessToken = "session-token-2",
			id = "2",
			email = "user-two@example.com",
			displayName = "User Two",
			familyName = "Two",
		)
		val sessionService = FakeSessionService(
			initialSessions = listOf(firstSession, secondSession),
			createMode = FakeSessionService.CreateMode.FAIL,
		)
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
		firstFavoritesResponse.status shouldBe HttpStatusCode.OK
		val firstPage = Json.decodeFromString<SearchResultsPage>(firstFavoritesResponse.bodyAsText())
		firstPage.items.size shouldBe 1
		firstPage.items[0].id shouldBe 1
		firstPage.items[0].isFavorite shouldBe true
		firstPage.totalMatches shouldBe 1
		firstPage.pageNumber shouldBe 1
		firstPage.pageSize shouldBe 20

		val secondFavoritesResponse = client.get("/favorites") {
			header(HttpHeaders.Authorization, "Bearer ${secondSession.accessToken}")
		}
		secondFavoritesResponse.status shouldBe HttpStatusCode.OK
		val secondPage = Json.decodeFromString<SearchResultsPage>(secondFavoritesResponse.bodyAsText())
		secondPage.items.isEmpty() shouldBe true
		secondPage.totalMatches shouldBe 0

		val firstRecipeDetailsResponse = client.get("/recipes/1") {
			header(HttpHeaders.Authorization, "Bearer ${firstSession.accessToken}")
		}
		firstRecipeDetailsResponse.status shouldBe HttpStatusCode.OK
		assertBodyContains(firstRecipeDetailsResponse.bodyAsText(), "\"isFavorite\":true")

		val secondRecipeDetailsResponse = client.get("/recipes/1") {
			header(HttpHeaders.Authorization, "Bearer ${secondSession.accessToken}")
		}
		secondRecipeDetailsResponse.status shouldBe HttpStatusCode.OK
		assertBodyDoesNotContain(secondRecipeDetailsResponse.bodyAsText(), "\"isFavorite\":true")

		val signedOutRecipeDetailsResponse = client.get("/recipes/1")
		signedOutRecipeDetailsResponse.status shouldBe HttpStatusCode.OK
		assertBodyDoesNotContain(signedOutRecipeDetailsResponse.bodyAsText(), "\"isFavorite\":true")

		val removeFavoriteResponse = client.delete("/favorites/1") {
			header(HttpHeaders.Authorization, "Bearer ${firstSession.accessToken}")
		}
		removeFavoriteResponse.status shouldBe HttpStatusCode.NoContent

		val firstFavoritesAfterRemove = client.get("/favorites") {
			header(HttpHeaders.Authorization, "Bearer ${firstSession.accessToken}")
		}
		firstFavoritesAfterRemove.status shouldBe HttpStatusCode.OK
		val afterRemovePage = Json.decodeFromString<SearchResultsPage>(firstFavoritesAfterRemove.bodyAsText())
		afterRemovePage.items.isEmpty() shouldBe true
		afterRemovePage.totalMatches shouldBe 0
	}

	@Test
	fun `creating duplicate cookbook names returns conflict`() = testApplication {
		val dbName = "cookbook_duplicate_${System.nanoTime()}"
		val dataSource = JdbcDataSource().apply {
			setURL("jdbc:h2:mem:$dbName;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE")
			user = "sa"
			password = ""
		}
		seedRecipeTables(dataSource)
		val db = Db.fromDataSource(dataSource)
		seedAppUsers(db)
		val session = FakeSessionService.createSession(accessToken = "session-token-1")
		val sessionService = FakeSessionService(
			initialSessions = listOf(session),
			createMode = FakeSessionService.CreateMode.FAIL,
		)

		application {
			module(
				db = db,
				sessionService = sessionService,
			)
		}

		val firstCreateResponse = client.post("/cookbooks") {
			header(HttpHeaders.Authorization, "Bearer ${session.accessToken}")
			header(HttpHeaders.ContentType, "application/json")
			setBody("""{"name":"Weeknight Dinners"}""")
		}
		firstCreateResponse.status shouldBe HttpStatusCode.Created

		val duplicateCreateResponse = client.post("/cookbooks") {
			header(HttpHeaders.Authorization, "Bearer ${session.accessToken}")
			header(HttpHeaders.ContentType, "application/json")
			setBody("""{"name":"  weeknight dinners  "}""")
		}
		duplicateCreateResponse.status shouldBe HttpStatusCode.Conflict
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
}
