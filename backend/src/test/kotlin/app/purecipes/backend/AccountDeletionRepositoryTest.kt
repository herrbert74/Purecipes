package app.purecipes.backend

import app.purecipes.backend.db.Db
import app.purecipes.backend.feature.auth.AccountDeletionRepository
import app.purecipes.backend.feature.auth.AccountDeletionResult
import app.purecipes.backend.feature.auth.RETAINED_RECIPE_OWNER_EXTERNAL_USER_ID
import app.purecipes.backend.feature.auth.RETAINED_RECIPE_OWNER_PROVIDER
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class AccountDeletionRepositoryTest {

	@Test
	fun `delete account reassigns created recipes to the retained owner`() {
		val db = createInMemoryDb("account_deletion_repository")
		val userId = db.insertAccount(email = "owner@example.com")
		db.insertRecipe(recipeId = RECIPE_ID, createdByUserId = userId)
		val repository = AccountDeletionRepository(db.dataSource)

		val result = repository.deleteAccount(userId)

		result shouldBe AccountDeletionResult.Deleted(reassignedRecipeCount = 1)
		db.countRows("SELECT COUNT(*) FROM app_users WHERE id = $userId") shouldBe 0
		db.recipeCreatedByUserId(RECIPE_ID) shouldBe db.retainedRecipeOwnerId()
	}

	@Test
	fun `delete account cascades account owned rows`() {
		val db = createInMemoryDb("account_deletion_repository")
		val userId = db.insertAccount(email = "owner@example.com")
		db.insertRecipe(recipeId = RECIPE_ID, createdByUserId = userId)
		db.executeSql("INSERT INTO favorites (user_id, recipe_id) VALUES ($userId, $RECIPE_ID)")
		db.executeSql("INSERT INTO cookbooks (id, user_id, name) VALUES ($COOKBOOK_ID, $userId, 'Dinners')")
		db.executeSql("INSERT INTO cookbook_recipes (cookbook_id, recipe_id) VALUES ($COOKBOOK_ID, $RECIPE_ID)")
		db.executeSql("INSERT INTO user_pantry (user_id, ingredient) VALUES ($userId, 'olive oil')")
		db.executeSql("INSERT INTO user_excluded_ingredients (user_id, ingredient) VALUES ($userId, 'peanuts')")
		db.executeSql("INSERT INTO search_filters (user_id, filters_json) VALUES ($userId, '{}')")
		db.executeSql("INSERT INTO measurement_preferences (user_id, preferred_system) VALUES ($userId, 'METRIC')")
		val repository = AccountDeletionRepository(db.dataSource)

		repository.deleteAccount(userId) shouldBe AccountDeletionResult.Deleted(reassignedRecipeCount = 1)

		db.countRows("SELECT COUNT(*) FROM favorites WHERE user_id = $userId") shouldBe 0
		db.countRows("SELECT COUNT(*) FROM cookbooks WHERE user_id = $userId") shouldBe 0
		db.countRows("SELECT COUNT(*) FROM cookbook_recipes") shouldBe 0
		db.countRows("SELECT COUNT(*) FROM user_pantry WHERE user_id = $userId") shouldBe 0
		db.countRows("SELECT COUNT(*) FROM user_excluded_ingredients WHERE user_id = $userId") shouldBe 0
		db.countRows("SELECT COUNT(*) FROM search_filters WHERE user_id = $userId") shouldBe 0
		db.countRows("SELECT COUNT(*) FROM measurement_preferences WHERE user_id = $userId") shouldBe 0
		db.countRows("SELECT COUNT(*) FROM recipes WHERE id = $RECIPE_ID") shouldBe 1
	}

	@Test
	fun `delete account reports missing account`() {
		val db = createInMemoryDb("account_deletion_repository")
		val repository = AccountDeletionRepository(db.dataSource)

		repository.deleteAccount(MISSING_USER_ID) shouldBe AccountDeletionResult.AccountNotFound
	}

	@Test
	fun `delete account refuses to delete the retained recipe owner`() {
		val db = createInMemoryDb("account_deletion_repository")
		val userId = db.insertAccount(
			email = "orphaned@example.com",
			provider = RETAINED_RECIPE_OWNER_PROVIDER,
			externalUserId = RETAINED_RECIPE_OWNER_EXTERNAL_USER_ID,
		)
		val repository = AccountDeletionRepository(db.dataSource)

		repository.deleteAccount(userId) shouldBe AccountDeletionResult.RetainedRecipeOwner
		db.countRows("SELECT COUNT(*) FROM app_users WHERE id = $userId") shouldBe 1
	}

	@Test
	fun `summarize account data counts owned rows`() {
		val db = createInMemoryDb("account_deletion_repository")
		val userId = db.insertAccount(email = "owner@example.com")
		db.insertRecipe(recipeId = RECIPE_ID, createdByUserId = userId)
		db.executeSql("INSERT INTO user_pantry (user_id, ingredient) VALUES ($userId, 'olive oil')")
		val repository = AccountDeletionRepository(db.dataSource)

		val summary = repository.summarizeAccountData(userId)

		summary.createdRecipeCount shouldBe 1
		summary.pantryIngredientCount shouldBe 1
		summary.favoriteCount shouldBe 0
		summary.cookbookCount shouldBe 0
	}

	@Test
	fun `find accounts by email ignores case and whitespace`() {
		val db = createInMemoryDb("account_deletion_repository")
		val userId = db.insertAccount(email = "owner@example.com")
		val repository = AccountDeletionRepository(db.dataSource)

		val accounts = repository.findAccountsByEmail("  Owner@Example.com ")

		accounts.size shouldBe 1
		accounts.first().id shouldBe userId
		accounts.first().provider shouldBe "GOOGLE"
	}

	private fun Db.insertRecipe(recipeId: Int, createdByUserId: Long) {
		dataSource.connection.use { connection ->
			connection.prepareStatement(
				"""
					INSERT INTO recipes (id, title, created_by_user_id)
					VALUES (?, 'Tomato Pasta', ?)
				""".trimIndent(),
			).use { statement ->
				statement.setInt(1, recipeId)
				statement.setLong(2, createdByUserId)
				statement.executeUpdate()
			}
		}
	}

	private fun Db.retainedRecipeOwnerId(): Long = queryLong(
		"""
			SELECT id FROM app_users
			WHERE provider = '$RETAINED_RECIPE_OWNER_PROVIDER'
				AND external_user_id = '$RETAINED_RECIPE_OWNER_EXTERNAL_USER_ID'
		""".trimIndent(),
	)

	private fun Db.recipeCreatedByUserId(recipeId: Int): Long =
		queryLong("SELECT created_by_user_id FROM recipes WHERE id = $recipeId")

	private companion object {

		const val MISSING_USER_ID = 4321L
		const val RECIPE_ID = 1
		const val COOKBOOK_ID = 1
	}
}
