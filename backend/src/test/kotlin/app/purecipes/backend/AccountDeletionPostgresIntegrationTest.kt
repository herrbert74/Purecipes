package app.purecipes.backend

import app.purecipes.backend.auth.JdbcSessionService
import app.purecipes.backend.db.Db
import app.purecipes.backend.feature.auth.RETAINED_RECIPE_OWNER_DISPLAY_NAME
import app.purecipes.backend.feature.auth.RETAINED_RECIPE_OWNER_EXTERNAL_USER_ID
import app.purecipes.backend.feature.auth.RETAINED_RECIPE_OWNER_PROVIDER
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.kotest.matchers.shouldBe
import io.ktor.client.request.delete
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import org.junit.Assume.assumeTrue
import org.testcontainers.DockerClientFactory
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import kotlin.test.Test

class AccountDeletionPostgresIntegrationTest {

	@Test
	fun `delete account removes account data and retains created recipes`() {
		assumeTrue(isDockerAvailable())

		val container = createPostgresContainer()
		val dataSource = HikariDataSource(
			HikariConfig().apply {
				jdbcUrl = container.jdbcUrl
				username = container.username
				password = container.password
				maximumPoolSize = 2
				isAutoCommit = true
				validate()
			},
		)

		try {
			val db = Db.fromDataSource(dataSource)
			val sessionService = JdbcSessionService(db.dataSource)
			val session = sessionService.createSession(
				provider = "GOOGLE",
				externalUserId = "user-one",
				email = "owner@example.com",
				displayName = "Owner",
				firstName = "Owner",
				familyName = "User",
				profileImageUrl = null,
			)
			val userId = session.user.id.toLong()
			db.seedAccountData(userId)

			testApplication {
				application {
					module(
						db = db,
						sessionService = sessionService,
					)
				}

				val response = client.delete("/auth/account") {
					header(HttpHeaders.Authorization, "Bearer ${session.accessToken}")
				}
				response.status shouldBe HttpStatusCode.NoContent

				val retryResponse = client.delete("/auth/account") {
					header(HttpHeaders.Authorization, "Bearer ${session.accessToken}")
				}
				retryResponse.status shouldBe HttpStatusCode.Unauthorized
			}

			db.countRows("SELECT COUNT(*) FROM app_users WHERE id = $userId") shouldBe 0
			db.countRows("SELECT COUNT(*) FROM auth_sessions WHERE user_id = $userId") shouldBe 0
			db.countRows("SELECT COUNT(*) FROM favorites WHERE user_id = $userId") shouldBe 0
			db.countRows("SELECT COUNT(*) FROM cookbooks WHERE user_id = $userId") shouldBe 0
			db.countRows("SELECT COUNT(*) FROM cookbook_recipes") shouldBe 0
			db.countRows("SELECT COUNT(*) FROM user_pantry WHERE user_id = $userId") shouldBe 0
			db.countRows("SELECT COUNT(*) FROM user_excluded_ingredients WHERE user_id = $userId") shouldBe 0
			db.countRows("SELECT COUNT(*) FROM search_filters WHERE user_id = $userId") shouldBe 0
			db.countRows("SELECT COUNT(*) FROM measurement_preferences WHERE user_id = $userId") shouldBe 0

			val retainedOwnerId = db.retainedRecipeOwnerId()
			db.recipeCreatedByUserId(CREATED_RECIPE_ID) shouldBe retainedOwnerId
			db.recipeImageUrl(CREATED_RECIPE_ID) shouldBe CREATED_RECIPE_IMAGE_URL
			db.countRows("SELECT COUNT(*) FROM recipes WHERE id = $PRIVATE_RECIPE_ID") shouldBe 0
			db.countRows("SELECT COUNT(*) FROM recipes WHERE id = $SCRAPED_RECIPE_ID") shouldBe 1
			db.retainedRecipeOwnerDisplayName() shouldBe RETAINED_RECIPE_OWNER_DISPLAY_NAME
		} finally {
			dataSource.close()
			container.close()
		}
	}

	@Test
	fun `second account deletion reuses the retained recipe owner`() {
		assumeTrue(isDockerAvailable())

		val container = createPostgresContainer()
		val dataSource = HikariDataSource(
			HikariConfig().apply {
				jdbcUrl = container.jdbcUrl
				username = container.username
				password = container.password
				maximumPoolSize = 2
				isAutoCommit = true
				validate()
			},
		)

		try {
			val db = Db.fromDataSource(dataSource)
			val sessionService = JdbcSessionService(db.dataSource)
			val firstSession = sessionService.createSession(
				provider = "GOOGLE",
				externalUserId = "user-one",
				email = "owner-one@example.com",
				displayName = "Owner One",
				firstName = "Owner",
				familyName = "One",
				profileImageUrl = null,
			)
			val secondSession = sessionService.createSession(
				provider = "GOOGLE",
				externalUserId = "user-two",
				email = "owner-two@example.com",
				displayName = "Owner Two",
				firstName = "Owner",
				familyName = "Two",
				profileImageUrl = null,
			)
			db.seedAccountData(firstSession.user.id.toLong())
			db.insertRecipe(
				recipeId = SECOND_CREATED_RECIPE_ID,
				createdByUserId = secondSession.user.id.toLong(),
			)

			testApplication {
				application {
					module(
						db = db,
						sessionService = sessionService,
					)
				}

				client.delete("/auth/account") {
					header(HttpHeaders.Authorization, "Bearer ${firstSession.accessToken}")
				}.status shouldBe HttpStatusCode.NoContent

				client.delete("/auth/account") {
					header(HttpHeaders.Authorization, "Bearer ${secondSession.accessToken}")
				}.status shouldBe HttpStatusCode.NoContent
			}

			val retainedOwnerId = db.retainedRecipeOwnerId()
			db.countRows(
				"SELECT COUNT(*) FROM app_users WHERE provider = '$RETAINED_RECIPE_OWNER_PROVIDER'",
			) shouldBe 1
			db.recipeCreatedByUserId(CREATED_RECIPE_ID) shouldBe retainedOwnerId
			db.recipeCreatedByUserId(SECOND_CREATED_RECIPE_ID) shouldBe retainedOwnerId
		} finally {
			dataSource.close()
			container.close()
		}
	}

	private fun Db.seedAccountData(userId: Long) {
		insertRecipe(recipeId = CREATED_RECIPE_ID, createdByUserId = userId)
		insertRecipe(recipeId = PRIVATE_RECIPE_ID, createdByUserId = userId, isPrivate = true)
		insertRecipe(recipeId = SCRAPED_RECIPE_ID, createdByUserId = null)
		executeSql("INSERT INTO favorites (user_id, recipe_id) VALUES ($userId, $SCRAPED_RECIPE_ID)")
		executeSql("INSERT INTO cookbooks (id, user_id, name) VALUES ($COOKBOOK_ID, $userId, 'Weeknight dinners')")
		executeSql("INSERT INTO cookbook_recipes (cookbook_id, recipe_id) VALUES ($COOKBOOK_ID, $SCRAPED_RECIPE_ID)")
		executeSql("INSERT INTO user_pantry (user_id, ingredient) VALUES ($userId, 'olive oil')")
		executeSql("INSERT INTO user_excluded_ingredients (user_id, ingredient) VALUES ($userId, 'peanuts')")
		executeSql("INSERT INTO search_filters (user_id, filters_json) VALUES ($userId, '{}')")
		executeSql("INSERT INTO measurement_preferences (user_id, preferred_system) VALUES ($userId, 'METRIC')")
	}

	private fun Db.insertRecipe(recipeId: Int, createdByUserId: Long?, isPrivate: Boolean = false) {
		val createdBy = createdByUserId?.toString() ?: "NULL"
		val privateSql = if (isPrivate) "TRUE" else "FALSE"
		executeSql(
			"""
				INSERT INTO recipes (
					id,
					title,
					description,
					instructions,
					total_time,
					image_url,
					created_by_user_id,
					is_private
				)
				VALUES (
					$recipeId,
					'Tomato Pasta',
					'Quick weeknight dinner.',
					'Cook pasta',
					25,
					'$CREATED_RECIPE_IMAGE_URL',
					$createdBy,
					$privateSql
				)
			""".trimIndent(),
		)
	}

	private fun Db.retainedRecipeOwnerId(): Long = queryLong(
		"""
			SELECT id FROM app_users
			WHERE provider = '$RETAINED_RECIPE_OWNER_PROVIDER'
				AND external_user_id = '$RETAINED_RECIPE_OWNER_EXTERNAL_USER_ID'
		""".trimIndent(),
	)

	private fun Db.retainedRecipeOwnerDisplayName(): String = queryString(
		"""
			SELECT display_name FROM app_users
			WHERE provider = '$RETAINED_RECIPE_OWNER_PROVIDER'
				AND external_user_id = '$RETAINED_RECIPE_OWNER_EXTERNAL_USER_ID'
		""".trimIndent(),
	)

	private fun Db.recipeCreatedByUserId(recipeId: Int): Long =
		queryLong("SELECT created_by_user_id FROM recipes WHERE id = $recipeId")

	private fun Db.recipeImageUrl(recipeId: Int): String =
		queryString("SELECT image_url FROM recipes WHERE id = $recipeId")

	private fun createPostgresContainer(): PostgreSQLContainer =
		PostgreSQLContainer(DockerImageName.parse("postgres:17-alpine")).apply {
			withDatabaseName("purecipes_test")
			withUsername("postgres")
			withPassword("postgres")
			start()
		}

	private fun isDockerAvailable(): Boolean = runCatching {
		DockerClientFactory.instance().isDockerAvailable
	}.getOrDefault(false)

	private companion object {

		const val CREATED_RECIPE_ID = 1
		const val SCRAPED_RECIPE_ID = 2
		const val SECOND_CREATED_RECIPE_ID = 3
		const val PRIVATE_RECIPE_ID = 4
		const val COOKBOOK_ID = 1
		const val CREATED_RECIPE_IMAGE_URL = "https://purecipes.app/uploads/recipes/pasta.jpg"
	}
}
