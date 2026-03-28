package com.purecipes.feature.auth.domain.usecase

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.getError
import com.purecipes.base.kotlin.result.Failure
import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.feature.auth.domain.model.AuthProvider
import com.purecipes.feature.auth.domain.model.AuthUser
import com.purecipes.feature.auth.domain.model.AuthenticationState
import com.purecipes.feature.auth.domain.model.GoogleAuthenticationProfile
import com.purecipes.feature.auth.domain.repository.AuthenticationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthenticationUseCasesTest {

	@Test
	fun `register rejects blank first name`() = runTest {
		val useCase = RegisterWithEmailUseCase(FakeAuthenticationRepository())

		val result = useCase(
			firstName = "",
			familyName = "Baker",
			email = "user@example.com",
			password = "secret",
		)

		assertEquals("First name is required", result.getError()?.message)
	}

	@Test
	fun `register rejects blank family name`() = runTest {
		val useCase = RegisterWithEmailUseCase(FakeAuthenticationRepository())

		val result = useCase(
			firstName = "Taylor",
			familyName = "",
			email = "user@example.com",
			password = "secret",
		)

		assertEquals("Family name is required", result.getError()?.message)
	}

	@Test
	fun `google sign in rejects missing id token`() = runTest {
		val useCase = SignInWithGoogleUseCase(FakeAuthenticationRepository())

		val result = useCase(
			GoogleAuthenticationProfile(
				idToken = "",
				email = null,
				displayName = "Taylor Baker",
				profileImageUrl = null,
			),
		)

		assertEquals("Google sign-in did not return an ID token", result.getError()?.message)
	}

	private class FakeAuthenticationRepository : AuthenticationRepository {

		override val authenticationState: StateFlow<AuthenticationState> =
			MutableStateFlow(AuthenticationState.SignedOut)

		override suspend fun signInWithEmail(email: String, password: String): Outcome<AuthUser> {
			return Err(Failure.UnexpectedFailure)
		}

		override suspend fun registerWithEmail(
			firstName: String,
			familyName: String,
			email: String,
			password: String,
		): Outcome<AuthUser> {
			return Ok(
				AuthUser(
					id = email,
					email = email,
					displayName = "$firstName $familyName",
					firstName = firstName,
					familyName = familyName,
					profileImageUrl = null,
					provider = AuthProvider.EMAIL,
				),
			)
		}

		override suspend fun signInWithGoogle(profile: GoogleAuthenticationProfile): Outcome<AuthUser> {
			return Ok(
				AuthUser(
					id = profile.idToken,
					email = profile.email.orEmpty(),
					displayName = profile.displayName,
					firstName = null,
					familyName = null,
					profileImageUrl = profile.profileImageUrl,
					provider = AuthProvider.GOOGLE,
				),
			)
		}

		override suspend fun signOut() = Unit
	}
}
