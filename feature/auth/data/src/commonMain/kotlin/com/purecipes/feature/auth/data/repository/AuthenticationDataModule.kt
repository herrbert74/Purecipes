package com.purecipes.feature.auth.data.repository

import com.purecipes.feature.auth.data.datasource.AuthenticationDataSource
import com.purecipes.feature.auth.data.datasource.AuthenticationRemoteDataSource
import com.purecipes.feature.auth.data.datasource.AuthenticationStore
import com.purecipes.feature.auth.data.datasource.AuthenticationStoreHolder
import com.purecipes.feature.auth.data.datasource.InMemoryAuthenticationLocalDataSource
import com.purecipes.feature.auth.data.datasource.toAuthenticationState
import com.purecipes.feature.auth.domain.repository.AuthenticationRepository
import com.purecipes.feature.auth.domain.usecase.ObserveAuthenticationStateUseCase
import com.purecipes.feature.auth.domain.usecase.RegisterWithEmailUseCase
import com.purecipes.feature.auth.domain.usecase.SignInWithEmailUseCase
import com.purecipes.feature.auth.domain.usecase.SignInWithExternalProviderUseCase
import com.purecipes.feature.auth.domain.usecase.SignInWithGoogleUseCase
import com.purecipes.feature.auth.domain.usecase.SignOutUseCase
import com.purecipes.shared.data.network.PurecipesApi
import com.purecipes.shared.data.session.SessionTokenStore
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
		return InMemoryAuthenticationLocalDataSource(store, sessionTokenStore)
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

	@Provides
	fun provideObserveAuthenticationStateUseCase(repository: AuthenticationRepository): ObserveAuthenticationStateUseCase {
		return ObserveAuthenticationStateUseCase(repository)
	}

	@Provides
	fun provideSignInWithEmailUseCase(repository: AuthenticationRepository): SignInWithEmailUseCase {
		return SignInWithEmailUseCase(repository)
	}

	@Provides
	fun provideRegisterWithEmailUseCase(repository: AuthenticationRepository): RegisterWithEmailUseCase {
		return RegisterWithEmailUseCase(repository)
	}

	@Provides
	fun provideSignInWithExternalProviderUseCase(repository: AuthenticationRepository): SignInWithExternalProviderUseCase {
		return SignInWithExternalProviderUseCase(repository)
	}

	@Provides
	fun provideSignInWithGoogleUseCase(repository: AuthenticationRepository): SignInWithGoogleUseCase {
		return SignInWithGoogleUseCase(repository)
	}

	@Provides
	fun provideSignOutUseCase(repository: AuthenticationRepository): SignOutUseCase {
		return SignOutUseCase(repository)
	}
}
