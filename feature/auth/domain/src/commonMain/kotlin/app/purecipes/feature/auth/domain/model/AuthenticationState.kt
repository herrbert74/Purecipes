package app.purecipes.feature.auth.domain.model

import app.purecipes.shared.domain.model.AuthenticatedSession

sealed interface AuthenticationState {
	data object SignedOut : AuthenticationState

	data class SignedIn(val user: AuthUser) : AuthenticationState
}

fun AuthenticatedSession.toAuthenticationState(): AuthenticationState {
	return AuthenticationState.SignedIn(user.toAuthUser())
}
