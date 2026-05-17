package app.purecipes.backend.auth

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FirebaseIdTokenVerifierTest {

	@Test
	fun `resolveFirebaseProjectId prefers system property`() {
		val resolved = resolveFirebaseProjectId(
			systemProperty = { key ->
				when (key) {
					"purecipes.firebaseProjectId" -> "property-project"
					"PURECIPES_FIREBASE_PROJECT_ID" -> "uppercase-property"
					else -> null
				}
			},
			environmentVariable = { "env-project" },
			resourceProperty = { "resource-project" },
		)

		assertEquals("property-project", resolved)
	}

	@Test
	fun `resolveFirebaseProjectId falls back to environment variable`() {
		val resolved = resolveFirebaseProjectId(
			systemProperty = { null },
			environmentVariable = { key ->
				if (key == "PURECIPES_FIREBASE_PROJECT_ID") "env-project" else null
			},
			resourceProperty = { "resource-project" },
		)

		assertEquals("env-project", resolved)
	}

	@Test
	fun `resolveFirebaseProjectId ignores blank system property`() {
		val resolved = resolveFirebaseProjectId(
			systemProperty = { key ->
				if (key == "purecipes.firebaseProjectId") "   " else null
			},
			environmentVariable = { null },
			resourceProperty = { "resource-project" },
		)

		assertEquals("resource-project", resolved)
	}

	@Test
	fun `matchesConfiguredFirebaseProject accepts issuer project id`() {
		assertTrue(
			matchesConfiguredFirebaseProject(
				issuer = "https://securetoken.google.com/purecipes-50e5c",
				audiences = listOf("922845075790"),
				configuredProjectId = "purecipes-50e5c",
				configuredProjectNumber = "922845075790",
			),
		)
	}

	@Test
	fun `matchesConfiguredFirebaseProject accepts audience project number`() {
		assertTrue(
			matchesConfiguredFirebaseProject(
				issuer = null,
				audiences = listOf("922845075790"),
				configuredProjectId = "purecipes-50e5c",
				configuredProjectNumber = "922845075790",
			),
		)
	}

	@Test
	fun `matchesConfiguredFirebaseProject accepts audience project id`() {
		assertTrue(
			matchesConfiguredFirebaseProject(
				issuer = null,
				audiences = listOf("purecipes-50e5c"),
				configuredProjectId = "purecipes-50e5c",
			),
		)
	}

	@Test
	fun `matchesConfiguredFirebaseProject rejects mismatched issuer and audience`() {
		assertFalse(
			matchesConfiguredFirebaseProject(
				issuer = "https://securetoken.google.com/other-project",
				audiences = listOf(
					"922845075790-aiom7ev08u8uamcrlt9714kfmfumked7.apps.googleusercontent.com",
				),
				configuredProjectId = "purecipes-50e5c",
			),
		)
	}

	@Test
	fun `firebaseIssuerProjectId extracts project id from issuer`() {
		assertEquals(
			"purecipes-50e5c",
			firebaseIssuerProjectId("https://securetoken.google.com/purecipes-50e5c"),
		)
	}
}
