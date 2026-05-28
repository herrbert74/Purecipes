package app.purecipes.feature.auth.data.repository

import app.purecipes.feature.auth.data.datasource.AuthenticationStore
import app.purecipes.feature.auth.data.datasource.AuthenticationStoreHolder
import app.purecipes.feature.auth.domain.model.toAuthenticationState
import app.purecipes.shared.data.session.SessionTokenStore
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface AuthenticationDataModule {

	@Provides
	fun provideAuthenticationStore(sessionTokenStore: SessionTokenStore): AuthenticationStore {
		return AuthenticationStoreHolder.store.apply {
			authenticationState.value = sessionTokenStore.currentSession()?.toAuthenticationState()
				?: authenticationState.value
		}
	}
}
