package app.purecipes.feature.auth.data.repository

import app.purecipes.feature.auth.data.datasource.AuthenticationDataSource
import app.purecipes.feature.auth.data.datasource.AuthenticationRemoteDataSource
import app.purecipes.feature.auth.data.datasource.AuthenticationStore
import app.purecipes.feature.auth.data.datasource.AuthenticationStoreHolder
import app.purecipes.feature.auth.data.datasource.FirebaseAuthenticationLocalDataSource
import app.purecipes.feature.auth.domain.model.toAuthenticationState
import app.purecipes.feature.auth.domain.repository.AuthenticationRepository
import app.purecipes.shared.data.network.PurecipesApi
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

	@Provides
	fun provideAuthenticationLocalDataSource(
		store: AuthenticationStore,
		sessionTokenStore: SessionTokenStore,
	): AuthenticationDataSource.Local {
		return FirebaseAuthenticationLocalDataSource(store, sessionTokenStore)
	}

	@Provides
	fun provideAuthenticationRemoteDataSource(api: PurecipesApi): AuthenticationDataSource.Remote {
		return AuthenticationRemoteDataSource(api)
	}

	@Provides
	fun provideAuthenticationRepository(
		localDataSource: AuthenticationDataSource.Local,
		remoteDataSource: AuthenticationDataSource.Remote,
	): AuthenticationRepository {
		return AuthenticationAccessor(localDataSource, remoteDataSource)
	}
}
