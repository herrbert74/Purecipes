package app.purecipes.feature.auth.data.datasource

data class EmailPasswordSignInResult(
	val idToken: String? = null,
	val emailNotVerified: Boolean = false,
	val errorMessage: String? = null,
)
