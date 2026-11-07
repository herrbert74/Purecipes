package app.purecipes.backend.tools

data class AccountDeletionRequest(
	val userId: Long? = null,
	val email: String? = null,
	val provider: String? = null,
	val execute: Boolean = false,
)

data class AccountDeletionReport(
	val lines: List<String>,
	val deleted: Boolean,
)
