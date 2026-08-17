package app.purecipes.feature.auth.data.session

import app.purecipes.feature.auth.data.datasource.AuthenticationStore
import app.purecipes.feature.auth.domain.model.AuthenticationState
import app.purecipes.shared.data.session.SessionTokenStore
import app.purecipes.shared.data.session.UnauthorizedSessionClearer
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@Inject
@ContributesBinding(AppScope::class)
class AuthenticationUnauthorizedSessionClearer(
	private val sessionTokenStore: SessionTokenStore,
	private val store: AuthenticationStore,
) : UnauthorizedSessionClearer {

	override fun clearUnauthorizedSession() {
		sessionTokenStore.clearSession()
		store.authenticationState.value = AuthenticationState.SignedOut
	}
}
