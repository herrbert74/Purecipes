package app.purecipes.backend.auth

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FirebaseSignInProfileTest {

	@Test
	fun `resolvedFirebaseUserEmail keeps relay addresses`() {
		assertEquals(
			"hidden@privaterelay.appleid.com",
			resolvedFirebaseUserEmail(
				signInProvider = APPLE_SIGN_IN_PROVIDER,
				email = "  Hidden@privaterelay.appleid.com ",
				subject = "apple-uid",
			),
		)
	}

	@Test
	fun `resolvedFirebaseUserEmail synthesizes apple email when missing`() {
		assertEquals(
			"apple.apple-uid@appleid.purecipes.app",
			resolvedFirebaseUserEmail(
				signInProvider = APPLE_SIGN_IN_PROVIDER,
				email = "  ",
				subject = "Apple-UID",
			),
		)
	}

	@Test
	fun `resolvedFirebaseUserEmail still requires email for other providers`() {
		assertNull(
			resolvedFirebaseUserEmail(
				signInProvider = "google.com",
				email = null,
				subject = "google-uid",
			),
		)
	}

	@Test
	fun `resolvedFirebaseUserDisplayName uses apple placeholder for relay email`() {
		assertEquals(
			APPLE_FALLBACK_DISPLAY_NAME,
			resolvedFirebaseUserDisplayName(
				signInProvider = APPLE_SIGN_IN_PROVIDER,
				name = "  ",
				email = "hidden@privaterelay.appleid.com",
			),
		)
	}

	@Test
	fun `resolvedFirebaseUserDisplayName prefers supplied apple name`() {
		assertEquals(
			"Taylor Baker",
			resolvedFirebaseUserDisplayName(
				signInProvider = APPLE_SIGN_IN_PROVIDER,
				name = " Taylor Baker ",
				email = "hidden@privaterelay.appleid.com",
			),
		)
	}

	@Test
	fun `isAppleFallbackEmail matches generated apple email`() {
		assertTrue(isAppleFallbackEmail("apple.apple-uid@appleid.purecipes.app", "Apple-UID"))
		assertFalse(isAppleFallbackEmail("hidden@privaterelay.appleid.com", "apple-uid"))
	}

	@Test
	fun `mergedWithExisting keeps relay email and name on apple re-login`() {
		val incoming = SessionUserProfile(
			email = appleFallbackEmail("apple-uid"),
			displayName = APPLE_FALLBACK_DISPLAY_NAME,
			firstName = null,
			familyName = null,
			profileImageUrl = null,
		)
		val existing = SessionUserProfile(
			email = "hidden@privaterelay.appleid.com",
			displayName = "Taylor Baker",
			firstName = "Taylor",
			familyName = "Baker",
			profileImageUrl = "https://example.com/avatar.png",
		)

		assertEquals(
			existing,
			incoming.mergedWithExisting(externalUserId = "apple-uid", existing = existing),
		)
	}

	@Test
	fun `mergedWithExisting replaces generated email with later apple email`() {
		val incoming = SessionUserProfile(
			email = "hidden@privaterelay.appleid.com",
			displayName = "Taylor Baker",
			firstName = "Taylor",
			familyName = "Baker",
			profileImageUrl = null,
		)
		val existing = SessionUserProfile(
			email = appleFallbackEmail("apple-uid"),
			displayName = APPLE_FALLBACK_DISPLAY_NAME,
			firstName = null,
			familyName = null,
			profileImageUrl = null,
		)

		assertEquals(
			incoming,
			incoming.mergedWithExisting(externalUserId = "apple-uid", existing = existing),
		)
	}
}
