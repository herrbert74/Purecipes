package com.purecipes.feature.auth.domain.usecase

import com.github.michaelbull.result.getError
import com.purecipes.feature.auth.domain.model.AuthProvider
import com.purecipes.feature.auth.domain.model.ExternalAuthenticationProfile
import com.purecipes.feature.auth.domain.model.GoogleAuthenticationProfile
import com.purecipes.shared.testfixtures.fake.FakeAuthenticationRepository
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

	@Test
	fun `external provider sign in rejects missing email`() = runTest {
		val useCase = SignInWithExternalProviderUseCase(FakeAuthenticationRepository())

		val result = useCase(
			ExternalAuthenticationProfile(
				provider = AuthProvider.APPLE,
				id = "apple-user",
				email = null,
				displayName = "Taylor Baker",
				profileImageUrl = null,
			),
		)

		assertEquals("Apple sign-in did not return an email address", result.getError()?.message)
	}

}
