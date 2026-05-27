package app.purecipes.feature.auth.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object AccountDestination : NavKey

@Serializable
data object EmailRegistrationDestination : NavKey

@Serializable
data class EmailSignInDestination(
	val prefilledEmail: String = "",
	val showRegistrationSuccessMessage: Boolean = false,
) : NavKey
