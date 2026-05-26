package app.purecipes.backend

import app.purecipes.backend.db.Db
import app.purecipes.backend.feature.favorites.CookbookRepository
import app.purecipes.backend.feature.favorites.CookbookShareRepository
import app.purecipes.backend.feature.favorites.FavoritesRepository
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeEmpty
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.Assume.assumeTrue
import org.testcontainers.DockerClientFactory
import org.testcontainers.postgresql.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import kotlin.test.Test

class CookbookSharePostgresIntegrationTest {

	@Test
	fun `import share copies cookbook recipes for another user`() {
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
			seedRecipeAndUsers(db)

			val favoritesRepository = FavoritesRepository(db.dataSource)
			val cookbookRepository = CookbookRepository(db.dataSource)
			val shareRepository = CookbookShareRepository(db.dataSource)

			favoritesRepository.addFavorite(OWNER_USER_ID, RECIPE_ID) shouldBe true
			val ownerCookbook = when (
				val created = cookbookRepository.createCookbook(OWNER_USER_ID, "Weeknight dinners")
			) {
				is CookbookRepository.CreateCookbookResult.Created -> created.cookbook
				CookbookRepository.CreateCookbookResult.DuplicateName -> error("Unexpected duplicate cookbook name")
			}
			cookbookRepository.addRecipeToCookbook(OWNER_USER_ID, ownerCookbook.id, RECIPE_ID) shouldBe
				CookbookRepository.AddRecipeToCookbookResult.Added

			val share = when (val result = shareRepository.createOrGetShare(OWNER_USER_ID, ownerCookbook.id)) {
				is CookbookShareRepository.CreateShareResult.Created -> result.share
				CookbookShareRepository.CreateShareResult.CookbookNotFound -> error("Owner cookbook not found")
			}
			share.token.shouldNotBeEmpty()

			val firstImport = when (val result = shareRepository.importShare(RECIPIENT_USER_ID, share.token)) {
				is CookbookShareRepository.ImportShareResult.Imported -> result.result
				else -> error("Expected import to succeed: $result")
			}
			firstImport.alreadyImported shouldBe false
			firstImport.recipesImported shouldBe 1
			firstImport.cookbook.name shouldBe "Weeknight dinners"

			val secondImport = when (val result = shareRepository.importShare(RECIPIENT_USER_ID, share.token)) {
				is CookbookShareRepository.ImportShareResult.Imported -> result.result
				else -> error("Expected idempotent import: $result")
			}
			secondImport.alreadyImported shouldBe true
			secondImport.cookbook.id shouldBe firstImport.cookbook.id

			shareRepository.importShare(OWNER_USER_ID, share.token)
				.shouldBeInstanceOf<CookbookShareRepository.ImportShareResult.CannotImportOwnCookbook>()
		} finally {
			dataSource.close()
			container.close()
		}
	}

	private fun seedRecipeAndUsers(db: Db) {
		db.dataSource.connection.use { connection ->
			connection.createStatement().use { statement ->
				statement.execute(
					"""
						INSERT INTO recipes (
							id,
							title,
							description,
							instructions,
							total_time,
							yields,
							cuisine
						) VALUES (
							$RECIPE_ID,
							'Tomato Pasta',
							'Quick weeknight dinner.',
							'Cook pasta',
							25,
							'2 servings',
							'Italian'
						)
					""".trimIndent(),
				)
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
							($OWNER_USER_ID, 'GOOGLE', 'user-one', 'owner@example.com', 'Owner', 'Owner', 'User', NULL),
							($RECIPIENT_USER_ID, 'GOOGLE', 'user-two', 'recipient@example.com', 'Recipient', 'Recipient', 'User', NULL)
					""".trimIndent(),
				)
			}
		}
	}

	private fun createPostgresContainer(): PostgreSQLContainer =
		PostgreSQLContainer(DockerImageName.parse("postgres:17-alpine")).apply {
			withDatabaseName("purecipes_test")
			withUsername("postgres")
			withPassword("postgres")
			start()
		}

	private fun isDockerAvailable(): Boolean = DockerClientFactory.instance().isDockerAvailable

	private companion object {
		const val OWNER_USER_ID = 1L
		const val RECIPIENT_USER_ID = 2L
		const val RECIPE_ID = 1
	}
}
