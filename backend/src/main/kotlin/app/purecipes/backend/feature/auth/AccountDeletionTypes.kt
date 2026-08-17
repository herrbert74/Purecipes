package app.purecipes.backend.feature.auth

sealed interface AccountDeletionResult {

	data class Deleted(val reassignedRecipeCount: Int) : AccountDeletionResult

	data object AccountNotFound : AccountDeletionResult

	data object RetainedRecipeOwner : AccountDeletionResult
}

data class AccountRecord(
	val id: Long,
	val provider: String,
	val email: String,
	val displayName: String,
)

data class AccountDataSummary(
	val createdRecipeCount: Int,
	val favoriteCount: Int,
	val cookbookCount: Int,
	val activeSessionCount: Int,
	val pantryIngredientCount: Int,
	val excludedIngredientCount: Int,
)
