package app.purecipes.backend

import app.purecipes.backend.db.Db
import app.purecipes.backend.feature.auth.AccountDeletionRepository
import app.purecipes.backend.feature.auth.RETAINED_RECIPE_OWNER_EXTERNAL_USER_ID
import app.purecipes.backend.feature.auth.RETAINED_RECIPE_OWNER_PROVIDER
import app.purecipes.backend.tools.AccountDeletionRequest
import app.purecipes.backend.tools.AccountDeletionTool
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlin.test.Test

class AccountDeletionToolTest {

	@Test
	fun `request without a target is rejected`() {
		val (_, tool) = createTool()

		val report = tool.run(AccountDeletionRequest())

		report.deleted shouldBe false
		report.lines.single() shouldContain "Provide either --user-id"
	}

	@Test
	fun `request with both user id and email is rejected`() {
		val (_, tool) = createTool()

		val report = tool.run(AccountDeletionRequest(userId = 1L, email = "owner@example.com"))

		report.deleted shouldBe false
		report.lines.single() shouldContain "Provide only one of"
	}

	@Test
	fun `dry run reports affected data without deleting`() {
		val (db, tool) = createTool()
		val userId = db.insertAccount(email = "owner@example.com")
		db.insertRecipe(recipeId = RECIPE_ID, createdByUserId = userId)
		db.executeSql("INSERT INTO favorites (user_id, recipe_id) VALUES ($userId, $RECIPE_ID)")

		val report = tool.run(AccountDeletionRequest(email = "  Owner@Example.com "))

		report.deleted shouldBe false
		report.lines shouldContain "  created recipes (kept, reassigned to Purecipes): 1"
		report.lines shouldContain "  favourites (deleted): 1"
		report.lines.last() shouldContain "--execute"
		db.countRows("SELECT COUNT(*) FROM app_users WHERE id = $userId") shouldBe 1
	}

	@Test
	fun `execute deletes the account and reports the firebase follow up`() {
		val (db, tool) = createTool()
		val userId = db.insertAccount(email = "owner@example.com")
		db.insertRecipe(recipeId = RECIPE_ID, createdByUserId = userId)

		val report = tool.run(AccountDeletionRequest(userId = userId, execute = true))

		report.deleted shouldBe true
		report.lines shouldContain "Reassigned 1 recipes to Purecipes."
		report.lines.any { line -> line.contains("Firebase Console") } shouldBe true
		db.countRows("SELECT COUNT(*) FROM app_users WHERE id = $userId") shouldBe 0
	}

	@Test
	fun `ambiguous email matches are not deleted`() {
		val (db, tool) = createTool()
		val googleUserId = db.insertAccount(email = "owner@example.com", externalUserId = "google-owner")
		val emailUserId = db.insertAccount(
			email = "owner@example.com",
			provider = "EMAIL",
			externalUserId = "email-owner",
		)

		val report = tool.run(AccountDeletionRequest(email = "owner@example.com", execute = true))

		report.deleted shouldBe false
		report.lines.first() shouldContain "Found 2 accounts"
		db.countRows("SELECT COUNT(*) FROM app_users WHERE id IN ($googleUserId, $emailUserId)") shouldBe 2
	}

	@Test
	fun `provider narrows ambiguous email matches`() {
		val (db, tool) = createTool()
		db.insertAccount(email = "owner@example.com", externalUserId = "google-owner")
		val emailUserId = db.insertAccount(
			email = "owner@example.com",
			provider = "EMAIL",
			externalUserId = "email-owner",
		)

		val report = tool.run(
			AccountDeletionRequest(email = "owner@example.com", provider = "email", execute = true),
		)

		report.deleted shouldBe true
		db.countRows("SELECT COUNT(*) FROM app_users WHERE id = $emailUserId") shouldBe 0
	}

	@Test
	fun `unknown account is reported`() {
		val (_, tool) = createTool()

		val report = tool.run(AccountDeletionRequest(email = "missing@example.com", execute = true))

		report.deleted shouldBe false
		report.lines.single() shouldBe "No account found for email missing@example.com."
	}

	@Test
	fun `retained recipe owner is protected`() {
		val (db, tool) = createTool()
		val userId = db.insertAccount(
			email = "orphaned@example.com",
			provider = RETAINED_RECIPE_OWNER_PROVIDER,
			externalUserId = RETAINED_RECIPE_OWNER_EXTERNAL_USER_ID,
		)

		val report = tool.run(AccountDeletionRequest(userId = userId, execute = true))

		report.deleted shouldBe false
		report.lines.any { line -> line.contains("Refusing to delete account") } shouldBe true
		db.countRows("SELECT COUNT(*) FROM app_users WHERE id = $userId") shouldBe 1
	}

	private fun createTool(): Pair<Db, AccountDeletionTool> {
		val db = createInMemoryDb("account_deletion_tool")
		return db to AccountDeletionTool(AccountDeletionRepository(db.dataSource))
	}

	private fun Db.insertRecipe(recipeId: Int, createdByUserId: Long) {
		executeSql("INSERT INTO recipes (id, title, created_by_user_id) VALUES ($recipeId, 'Pasta', $createdByUserId)")
	}

	private companion object {

		const val RECIPE_ID = 1
	}
}
