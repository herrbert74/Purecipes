package com.purecipes.backend

import com.purecipes.backend.db.Db
import com.purecipes.backend.fake.FakeSessionService
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import org.h2.jdbcx.JdbcDataSource
import javax.sql.DataSource
import kotlin.test.Test

class SettingsSessionIntegrationTest {

	private val expectedSavedPreferencesJson = Json.parseToJsonElement(
		"""
		{
			"preferredSystem":"IMPERIAL",
			"formatHandling":"CONVERT_TO_PREFERRED",
			"detectedCountryCode":"US",
			"notificationSeenRecipeIds":[1,2]
		}
		""".trimIndent(),
	)

	@Test
	fun `measurement settings are isolated by authenticated session`() = testApplication {
		val dbName = "measurement_settings_isolation_${System.nanoTime()}"
		val dataSource = JdbcDataSource().apply {
			setURL("jdbc:h2:mem:$dbName;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE")
			user = "sa"
			password = ""
		}
		seedRecipeTables(dataSource)
		val db = Db.fromDataSource(dataSource)
		seedAppUsers(db)
		val firstSession = FakeSessionService.createSession(
			accessToken = "session-token-1",
			id = "1",
			email = "user-one@example.com",
			displayName = "User One",
			firstName = "User",
			familyName = "One",
		)
		val secondSession = FakeSessionService.createSession(
			accessToken = "session-token-2",
			id = "2",
			email = "user-two@example.com",
			displayName = "User Two",
			firstName = "User",
			familyName = "Two",
		)
		val sessionService = FakeSessionService(
			initialSessions = listOf(firstSession, secondSession),
			createMode = FakeSessionService.CreateMode.GENERATE_AND_STORE,
		)

		application {
			module(
				db = db,
				sessionService = sessionService,
			)
		}

		val missingResponse = client.get("/settings/measurement") {
			header(HttpHeaders.Authorization, "Bearer ${firstSession.accessToken}")
		}
		missingResponse.status shouldBe HttpStatusCode.NotFound

		val saveResponse = client.put("/settings/measurement") {
			header(HttpHeaders.Authorization, "Bearer ${firstSession.accessToken}")
			contentType(ContentType.Application.Json)
			setBody(
				"""
				{
					"preferredSystem":"IMPERIAL",
					"formatHandling":"CONVERT_TO_PREFERRED",
					"detectedCountryCode":"US",
					"notificationSeenRecipeIds":[1,2]
				}
				""".trimIndent(),
			)
		}
		saveResponse.status shouldBe HttpStatusCode.OK
		Json.parseToJsonElement(saveResponse.bodyAsText()) shouldBe expectedSavedPreferencesJson

		val firstSettingsResponse = client.get("/settings/measurement") {
			header(HttpHeaders.Authorization, "Bearer ${firstSession.accessToken}")
		}
		firstSettingsResponse.status shouldBe HttpStatusCode.OK
		Json.parseToJsonElement(firstSettingsResponse.bodyAsText()) shouldBe expectedSavedPreferencesJson

		val secondSettingsResponse = client.get("/settings/measurement") {
			header(HttpHeaders.Authorization, "Bearer ${secondSession.accessToken}")
		}
		secondSettingsResponse.status shouldBe HttpStatusCode.NotFound

		val updateResponse = client.put("/settings/measurement") {
			header(HttpHeaders.Authorization, "Bearer ${firstSession.accessToken}")
			contentType(ContentType.Application.Json)
			setBody(
				"""
				{
					"preferredSystem":"METRIC",
					"formatHandling":"FILTER_OUT",
					"detectedCountryCode":"GB",
					"notificationSeenRecipeIds":[2]
				}
				""".trimIndent(),
			)
		}
		updateResponse.status shouldBe HttpStatusCode.OK

		val updatedSettingsResponse = client.get("/settings/measurement") {
			header(HttpHeaders.Authorization, "Bearer ${firstSession.accessToken}")
		}
		updatedSettingsResponse.status shouldBe HttpStatusCode.OK
		Json.parseToJsonElement(updatedSettingsResponse.bodyAsText()) shouldBe Json.parseToJsonElement(
			"""
				{
					"preferredSystem":"METRIC",
					"formatHandling":"FILTER_OUT",
					"detectedCountryCode":"GB",
					"notificationSeenRecipeIds":[2]
				}
				""".trimIndent(),
		)
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
}
