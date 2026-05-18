package app.purecipes.feature.auth.data.repository

import app.purecipes.feature.auth.data.datasource.AuthenticationDataSource
import app.purecipes.feature.auth.data.datasource.AuthenticationRemoteDataSource
import app.purecipes.feature.auth.data.datasource.AuthenticationStore
import app.purecipes.feature.auth.data.datasource.AuthenticationStoreHolder
import app.purecipes.feature.auth.data.datasource.FirebaseAuthenticationLocalDataSource
import app.purecipes.feature.auth.domain.model.toAuthenticationState
import app.purecipes.feature.auth.domain.repository.AuthenticationRepository
import app.purecipes.feature.auth.domain.usecase.DeleteAccountUseCase
import app.purecipes.feature.auth.domain.usecase.ObserveAuthenticationStateUseCase
import app.purecipes.feature.auth.domain.usecase.RegisterWithEmailUseCase
import app.purecipes.feature.auth.domain.usecase.ResendEmailVerificationUseCase
import app.purecipes.feature.auth.domain.usecase.SendPasswordResetEmailUseCase
import app.purecipes.feature.auth.domain.usecase.SignInWithEmailUseCase
import app.purecipes.feature.auth.domain.usecase.SignInWithExternalProviderUseCase
import app.purecipes.feature.auth.domain.usecase.SignInWithGoogleUseCase
import app.purecipes.feature.auth.domain.usecase.SignOutUseCase
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

	@Provides
	fun provideObserveAuthenticationStateUseCase(
		repository: AuthenticationRepository
	): ObserveAuthenticationStateUseCase {
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
	fun provideResendEmailVerificationUseCase(
		repository: AuthenticationRepository,
	): ResendEmailVerificationUseCase {
		return ResendEmailVerificationUseCase(repository)
	}

	@Provides
	fun provideSendPasswordResetEmailUseCase(
		repository: AuthenticationRepository,
	): SendPasswordResetEmailUseCase {
		return SendPasswordResetEmailUseCase(repository)
	}

	@Provides
	fun provideSignInWithExternalProviderUseCase(
		repository: AuthenticationRepository
	): SignInWithExternalProviderUseCase {
		return SignInWithExternalProviderUseCase(repository)
	}

	@Provides
	fun provideSignInWithGoogleUseCase(repository: AuthenticationRepository): SignInWithGoogleUseCase {
		return SignInWithGoogleUseCase(repository)
	}

	@Provides
	fun provideDeleteAccountUseCase(repository: AuthenticationRepository): DeleteAccountUseCase {
		return DeleteAccountUseCase(repository)
	}

	@Provides
	fun provideSignOutUseCase(repository: AuthenticationRepository): SignOutUseCase {
		return SignOutUseCase(repository)
	}
}
