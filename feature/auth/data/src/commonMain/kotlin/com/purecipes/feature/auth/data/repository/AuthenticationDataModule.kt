package com.purecipes.feature.auth.data.repository

import com.purecipes.feature.auth.data.datasource.AuthenticationDataSource
import com.purecipes.feature.auth.data.datasource.AuthenticationStore
import com.purecipes.feature.auth.data.datasource.AuthenticationStoreHolder
import com.purecipes.feature.auth.data.datasource.InMemoryAuthenticationLocalDataSource
import com.purecipes.feature.auth.domain.repository.AuthenticationRepository
import com.purecipes.feature.auth.domain.usecase.ObserveAuthenticationStateUseCase
import com.purecipes.feature.auth.domain.usecase.RegisterWithEmailUseCase
import com.purecipes.feature.auth.domain.usecase.SignInWithEmailUseCase
import com.purecipes.feature.auth.domain.usecase.SignInWithGoogleUseCase
import com.purecipes.feature.auth.domain.usecase.SignOutUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface AuthenticationDataModule {

	@Provides
	fun provideAuthenticationStore(): AuthenticationStore {
		return AuthenticationStoreHolder.store
	}

	@Provides
	fun provideAuthenticationLocalDataSource(store: AuthenticationStore): AuthenticationDataSource.Local {
		return InMemoryAuthenticationLocalDataSource(store)
	}

	@Provides
	fun provideAuthenticationRepository(localDataSource: AuthenticationDataSource.Local): AuthenticationRepository {
		return AuthenticationAccessor(localDataSource)
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
	fun provideSignInWithGoogleUseCase(repository: AuthenticationRepository): SignInWithGoogleUseCase {
		return SignInWithGoogleUseCase(repository)
	}

	@Provides
	fun provideSignOutUseCase(repository: AuthenticationRepository): SignOutUseCase {
		return SignOutUseCase(repository)
	}
}
