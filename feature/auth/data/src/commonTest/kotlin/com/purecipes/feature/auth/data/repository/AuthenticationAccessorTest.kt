package com.purecipes.feature.auth.data.repository

import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import com.purecipes.feature.auth.data.datasource.AuthenticationStore
import com.purecipes.feature.auth.data.datasource.InMemoryAuthenticationLocalDataSource
import com.purecipes.feature.auth.domain.model.AuthProvider
import com.purecipes.feature.auth.domain.model.AuthenticationState
import com.purecipes.feature.auth.domain.model.GoogleAuthenticationProfile
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class AuthenticationAccessorTest {

	@Test
	fun `register signs the user in`() = runTest {
		val accessor = AuthenticationAccessor(InMemoryAuthenticationLocalDataSource(AuthenticationStore()))

		val user = accessor.registerWithEmail(
			firstName = "Taylor",
			familyName = "Baker",
			email = "taylor@example.com",
			password = "secret",
		).get()

		assertEquals("Taylor Baker", user?.displayName)
		assertEquals(AuthProvider.EMAIL, user?.provider)
		assertIs<AuthenticationState.SignedIn>(accessor.authenticationState.value)
	}

	@Test
	fun `duplicate registration returns an error`() = runTest {
		val accessor = AuthenticationAccessor(InMemoryAuthenticationLocalDataSource(AuthenticationStore()))

		accessor.registerWithEmail(
			firstName = "Taylor",
			familyName = "Baker",
			email = "taylor@example.com",
			password = "secret",
		)
		val result = accessor.registerWithEmail(
			firstName = "Taylor",
			familyName = "Baker",
			email = "taylor@example.com",
			password = "secret",
		)

		assertEquals("An account already exists for this email", result.getError()?.message)
	}

	@Test
	fun `google sign in keeps remote profile image`() = runTest {
		val accessor = AuthenticationAccessor(InMemoryAuthenticationLocalDataSource(AuthenticationStore()))

		val user = accessor.signInWithGoogle(
			GoogleAuthenticationProfile(
				email = "taylor@example.com",
				displayName = "Taylor Baker",
				profileImageUrl = "https://example.com/avatar.png",
			),
		).get()

		assertEquals(AuthProvider.GOOGLE, user?.provider)
		assertEquals("https://example.com/avatar.png", user?.profileImageUrl)
		assertNull(user?.familyName?.takeIf { it.isBlank() })
	}
}
