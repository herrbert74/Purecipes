package app.purecipes.backend

import app.purecipes.backend.auth.JdbcSessionService
import app.purecipes.backend.db.Db
import app.purecipes.shared.domain.model.SearchResultsPage
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.kotest.matchers.shouldBe
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assume.assumeTrue
import org.testcontainers.DockerClientFactory
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import javax.sql.DataSource
import kotlin.test.Test

class FavoritesPostgresIntegrationTest {

	@Test
	fun `postgres favorites endpoints require bearer token`() {
		assumeTrue(isDockerAvailable())

		val container = createPostgresContainer()

		val dataSource = HikariDataSource(
			HikariConfig().apply {
				jdbcUrl = container.getJdbcUrl()
				username = container.username
				password = container.password
				maximumPoolSize = 2
				isAutoCommit = true
				validate()
			},
		)

		try {
			seedRecipeTables(dataSource)
			val db = Db.fromDataSource(dataSource)
			val sessionService = JdbcSessionService(db.dataSource)

			testApplication {
				application {
					module(
						db = db,
						sessionService = sessionService,
					)
				}

				val favoritesResponse = client.get("/favorites")
				favoritesResponse.status shouldBe HttpStatusCode.Unauthorized
				favoritesResponse.bodyAsText() shouldBe
					"""{"message":"Unauthorized","detail":"Missing bearer token"}"""

				val addFavoriteResponse = client.post("/favorites/1")
				addFavoriteResponse.status shouldBe HttpStatusCode.Unauthorized
				addFavoriteResponse.bodyAsText() shouldBe
					"""{"message":"Unauthorized","detail":"Missing bearer token"}"""

				val removeFavoriteResponse = client.delete("/favorites/1")
				removeFavoriteResponse.status shouldBe HttpStatusCode.Unauthorized
				removeFavoriteResponse.bodyAsText() shouldBe
					"""{"message":"Unauthorized","detail":"Missing bearer token"}"""
			}
		} finally {
			dataSource.close()
			container.close()
		}
	}

	@Test
	fun `postgres add favorite is scoped to authenticated user`() {
		assumeTrue(isDockerAvailable())

		val container = createPostgresContainer()

		val dataSource = HikariDataSource(
			HikariConfig().apply {
				jdbcUrl = container.getJdbcUrl()
				username = container.username
				password = container.password
				maximumPoolSize = 2
				isAutoCommit = true
				validate()
			},
		)

		try {
			seedRecipeTables(dataSource)
			val db = Db.fromDataSource(dataSource)
			val sessionService = JdbcSessionService(db.dataSource)

			val firstSession = sessionService.createSession(
				provider = "GOOGLE",
				externalUserId = "user-one",
				email = "user-one@example.com",
				displayName = "User One",
				firstName = "User",
				familyName = "One",
				profileImageUrl = null,
			)
			val secondSession = sessionService.createSession(
				provider = "GOOGLE",
				externalUserId = "user-two",
				email = "user-two@example.com",
				displayName = "User Two",
				firstName = "User",
				familyName = "Two",
				profileImageUrl = null,
			)

			testApplication {
				application {
					module(
						db = db,
						sessionService = sessionService,
					)
				}

				val addFavoriteResponse = client.post("/favorites/1") {
					header(HttpHeaders.Authorization, "Bearer ${firstSession.accessToken}")
				}
				addFavoriteResponse.status shouldBe HttpStatusCode.NoContent

				val firstFavoritesBody = client.get("/favorites") {
					header(HttpHeaders.Authorization, "Bearer ${firstSession.accessToken}")
				}.also {
					it.status shouldBe HttpStatusCode.OK
				}.bodyAsText()
				val firstFavorites = Json.decodeFromString<SearchResultsPage>(firstFavoritesBody)
				firstFavorites.items.size shouldBe 1
				firstFavorites.items[0].id shouldBe 1
				firstFavorites.items[0].isFavorite shouldBe true

				val secondFavoritesBody = client.get("/favorites") {
					header(HttpHeaders.Authorization, "Bearer ${secondSession.accessToken}")
				}.also {
					it.status shouldBe HttpStatusCode.OK
				}.bodyAsText()
				val secondFavorites = Json.decodeFromString<SearchResultsPage>(secondFavoritesBody)
				secondFavorites.items.isEmpty() shouldBe true

				val firstDetails = parseJsonObject(
					client.get("/recipes/1") {
						header(HttpHeaders.Authorization, "Bearer ${firstSession.accessToken}")
					}.also {
						it.status shouldBe HttpStatusCode.OK
					}.bodyAsText(),
				)
				firstDetails["isFavorite"]?.jsonPrimitive?.booleanOrNull shouldBe true

				val secondDetails = parseJsonObject(
					client.get("/recipes/1") {
						header(HttpHeaders.Authorization, "Bearer ${secondSession.accessToken}")
					}.also {
						it.status shouldBe HttpStatusCode.OK
					}.bodyAsText(),
				)
				secondDetails.containsKey("isFavorite") shouldBe false

				val signedOutDetails = parseJsonObject(
					client.get("/recipes/1").also {
						it.status shouldBe HttpStatusCode.OK
					}.bodyAsText(),
				)
				signedOutDetails.containsKey("isFavorite") shouldBe false

				val removeFavoriteResponse = client.delete("/favorites/1") {
					header(HttpHeaders.Authorization, "Bearer ${firstSession.accessToken}")
				}
				removeFavoriteResponse.status shouldBe HttpStatusCode.NoContent

				val firstFavoritesAfterDeleteBody = client.get("/favorites") {
					header(HttpHeaders.Authorization, "Bearer ${firstSession.accessToken}")
				}.also {
					it.status shouldBe HttpStatusCode.OK
				}.bodyAsText()
				val firstFavoritesAfterDelete = Json.decodeFromString<SearchResultsPage>(firstFavoritesAfterDeleteBody)
				firstFavoritesAfterDelete.items.isEmpty() shouldBe true
			}
		} finally {
			dataSource.close()
			container.close()
		}
	}

	private fun createPostgresContainer(): PostgreSQLContainer =
		PostgreSQLContainer(DockerImageName.parse("postgres:17-alpine")).apply {
			withDatabaseName("purecipes_test")
			withUsername("postgres")
			withPassword("postgres")
			start()
		}

	private fun seedRecipeTables(dataSource: DataSource) {
		dataSource.connection.use { connection ->
			connection.createStatement().use { statement ->
				statement.execute(
					"""
						CREATE TABLE recipes (
							id INTEGER PRIMARY KEY,
							title VARCHAR(255) NOT NULL,
							description TEXT,
							instructions TEXT,
							total_time INTEGER,
							yields VARCHAR(255),
							image_url VARCHAR(512),
							cuisine VARCHAR(255),
							category VARCHAR(255),
							created_by_user_id BIGINT,
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
							order_index INTEGER NOT NULL,
							requirement VARCHAR(20) NOT NULL DEFAULT 'REQUIRED'
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
							description,
							instructions,
							total_time,
							yields,
							image_url,
							cuisine,
							category
						) VALUES (
							1,
							'Tomato Pasta',
							'Quick weeknight dinner.',
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

	private fun isDockerAvailable(): Boolean {
		return runCatching {
			DockerClientFactory.instance().isDockerAvailable
		}.getOrDefault(false)
	}

	private fun parseJsonObject(body: String) = Json.parseToJsonElement(body).jsonObject
}
