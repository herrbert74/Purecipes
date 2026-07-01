package app.purecipes.backend.auth

import java.util.Base64
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class FirebaseJwtClaimsTest {

	@Test
	fun `decodeFirebaseJwtClaims reads issuer and audience from payload`() {
		val payloadJson =
			"""{"iss":"https://securetoken.google.com/purecipes-50e5c","aud":"purecipes-50e5c",""" +
				""""sub":"firebase-uid","email":"user@example.com","email_verified":true}"""
		val encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(payloadJson.toByteArray())
		val idToken = "header.$encodedPayload.signature"

		val claims = decodeFirebaseJwtClaims(idToken)

		assertNotNull(claims)
		assertEquals("https://securetoken.google.com/purecipes-50e5c", claims.issuer)
		assertEquals(listOf("purecipes-50e5c"), claims.audiences)
		assertEquals("firebase-uid", claims.subject)
		assertEquals("user@example.com", claims.email)
		assertEquals(true, claims.emailVerified)
	}

	@Test
	fun `decodeFirebaseJwtClaims reads subject from sub claim`() {
		val payloadJson = """{"sub":"uid-from-jwt"}"""
		val encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(payloadJson.toByteArray())
		val idToken = "header.$encodedPayload.signature"

		val claims = decodeFirebaseJwtClaims(idToken)

		assertNotNull(claims)
		assertEquals("uid-from-jwt", claims.subject)
	}

	@Test
	fun `decodeFirebaseJwtClaims reads firebase sign in provider`() {
		val payloadJson =
			"""{"firebase":{"sign_in_provider":"facebook.com"},"email_verified":false,"email":"user@example.com"}"""
		val encodedPayload = Base64.getUrlEncoder().withoutPadding().encodeToString(payloadJson.toByteArray())
		val idToken = "header.$encodedPayload.signature"

		val claims = decodeFirebaseJwtClaims(idToken)

		assertNotNull(claims)
		assertEquals("facebook.com", claims.signInProvider)
		assertEquals(false, claims.emailVerified)
	}

	@Test
	fun `requiresVerifiedEmail is true only for password sign in`() {
		assertTrue(
			requiresVerifiedEmail(
				FirebaseJwtClaims(
					issuer = null,
					audiences = emptyList(),
					emailVerified = false,
					signInProvider = "password",
					subject = null,
					email = null,
					name = null,
					givenName = null,
					familyName = null,
					picture = null,
				),
			),
		)
		assertFalse(
			requiresVerifiedEmail(
				FirebaseJwtClaims(
					issuer = null,
					audiences = emptyList(),
					emailVerified = false,
					signInProvider = "facebook.com",
					subject = null,
					email = null,
					name = null,
					givenName = null,
					familyName = null,
					picture = null,
				),
			),
		)
	}

	@Test
	fun `matchesConfiguredFirebaseProject accepts issuer from decoded jwt claims`() {
		val claims = FirebaseJwtClaims(
			issuer = "https://securetoken.google.com/purecipes-50e5c",
			audiences = listOf("922845075790"),
			emailVerified = true,
			signInProvider = "google.com",
			subject = "firebase-uid",
			email = "user@example.com",
			name = null,
			givenName = null,
			familyName = null,
			picture = null,
		)

		assertTrue(
			matchesConfiguredFirebaseProject(
				issuer = claims.issuer,
				audiences = claims.audiences,
				configuredProjectId = "purecipes-50e5c",
				configuredProjectNumber = "922845075790",
			),
		)
	}
}
