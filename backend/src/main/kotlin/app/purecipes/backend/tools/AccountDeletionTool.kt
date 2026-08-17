package app.purecipes.backend.tools

import app.purecipes.backend.feature.auth.AccountDataSummary
import app.purecipes.backend.feature.auth.AccountDeletionRepository
import app.purecipes.backend.feature.auth.AccountDeletionResult
import app.purecipes.backend.feature.auth.AccountRecord
import app.purecipes.backend.feature.auth.RETAINED_RECIPE_OWNER_DISPLAY_NAME

const val ARG_USER_ID = "--user-id"
const val ARG_EMAIL = "--email"
const val ARG_PROVIDER = "--provider"
const val ARG_EXECUTE = "--execute"

class AccountDeletionTool(
	private val repository: AccountDeletionRepository,
) {

	fun run(request: AccountDeletionRequest): AccountDeletionReport {
		val requestProblems = validate(request)
		return when {
			requestProblems.isNotEmpty() -> AccountDeletionReport(lines = requestProblems, deleted = false)
			else -> runValidatedRequest(request)
		}
	}

	private fun validate(request: AccountDeletionRequest): List<String> {
		val hasEmail = !request.email.isNullOrBlank()
		return when {
			request.userId == null && !hasEmail ->
				listOf("Provide either $ARG_USER_ID=<id> or $ARG_EMAIL=<email>.")

			request.userId != null && hasEmail ->
				listOf("Provide only one of $ARG_USER_ID and $ARG_EMAIL.")

			request.userId != null && !request.provider.isNullOrBlank() ->
				listOf("$ARG_PROVIDER only applies together with $ARG_EMAIL.")

			else -> emptyList()
		}
	}

	private fun runValidatedRequest(request: AccountDeletionRequest): AccountDeletionReport {
		val candidates = findCandidates(request)
		return when {
			candidates.isEmpty() -> AccountDeletionReport(
				lines = listOf("No account found for ${request.describeTarget()}."),
				deleted = false,
			)

			candidates.size > 1 -> AccountDeletionReport(
				lines = ambiguousCandidateLines(request, candidates),
				deleted = false,
			)

			else -> reportAccount(candidates.single(), request.execute)
		}
	}

	private fun findCandidates(request: AccountDeletionRequest): List<AccountRecord> {
		val userId = request.userId
		if (userId != null) {
			return listOfNotNull(repository.findAccount(userId))
		}
		val accounts = repository.findAccountsByEmail(request.email.orEmpty())
		val provider = request.provider?.trim()?.uppercase()
		return if (provider.isNullOrBlank()) {
			accounts
		} else {
			accounts.filter { account -> account.provider == provider }
		}
	}

	private fun reportAccount(account: AccountRecord, execute: Boolean): AccountDeletionReport {
		val accountLines = accountLines(account, repository.summarizeAccountData(account.id))
		return if (execute) {
			val result = repository.deleteAccount(account.id)
			AccountDeletionReport(
				lines = accountLines + resultLines(account, result),
				deleted = result is AccountDeletionResult.Deleted,
			)
		} else {
			AccountDeletionReport(
				lines = accountLines + dryRunLines(account),
				deleted = false,
			)
		}
	}

	private fun accountLines(account: AccountRecord, summary: AccountDataSummary): List<String> = listOf(
		"Account ${account.id}",
		"  provider: ${account.provider}",
		"  email: ${account.email}",
		"  display name: ${account.displayName}",
		"Account data",
		"  created public recipes (kept, reassigned to $RETAINED_RECIPE_OWNER_DISPLAY_NAME): " +
			"${summary.createdPublicRecipeCount}",
		"  created private recipes (deleted): ${summary.createdPrivateRecipeCount}",
		"  favourites (deleted): ${summary.favoriteCount}",
		"  cookbooks (deleted): ${summary.cookbookCount}",
		"  active sessions (deleted): ${summary.activeSessionCount}",
		"  pantry ingredients (deleted): ${summary.pantryIngredientCount}",
		"  excluded ingredients (deleted): ${summary.excludedIngredientCount}",
	)

	private fun dryRunLines(account: AccountRecord): List<String> = listOf(
		"Dry run: nothing was deleted.",
		"Re-run with $ARG_USER_ID=${account.id} $ARG_EXECUTE to delete this account.",
	)

	private fun resultLines(account: AccountRecord, result: AccountDeletionResult): List<String> = when (result) {
		is AccountDeletionResult.Deleted -> listOf(
			"Deleted account ${account.id} and its account-owned data.",
			"Reassigned ${result.reassignedRecipeCount} recipes to $RETAINED_RECIPE_OWNER_DISPLAY_NAME.",
			"Deleted ${result.deletedPrivateRecipeCount} private recipes.",
			"Next step: delete the Firebase Authentication user for ${account.email} in the Firebase Console.",
			"The backend has no Firebase Admin credentials, so that step is not automated.",
		)

		AccountDeletionResult.AccountNotFound -> listOf(
			"Account ${account.id} no longer exists. Nothing was deleted.",
		)

		AccountDeletionResult.RetainedRecipeOwner -> listOf(
			"Refusing to delete account ${account.id}.",
			"It is the reserved $RETAINED_RECIPE_OWNER_DISPLAY_NAME owner of recipes retained after deletion.",
		)
	}

	private fun ambiguousCandidateLines(
		request: AccountDeletionRequest,
		candidates: List<AccountRecord>,
	): List<String> = buildList {
		add("Found ${candidates.size} accounts for ${request.describeTarget()}.")
		candidates.forEach { account ->
			add("  $ARG_USER_ID=${account.id} provider=${account.provider} name=${account.displayName}")
		}
		add("Re-run with $ARG_USER_ID=<id> or $ARG_PROVIDER=<provider> to pick one account.")
	}

	private fun AccountDeletionRequest.describeTarget(): String = when {
		userId != null -> "user id $userId"
		provider.isNullOrBlank() -> "email ${email.orEmpty().trim()}"
		else -> "email ${email.orEmpty().trim()} and provider ${provider.trim().uppercase()}"
	}
}
