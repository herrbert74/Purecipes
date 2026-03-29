package com.purecipes.backend.auth

import kotlin.test.Test
import kotlin.test.assertEquals

class GoogleIdTokenVerifierTest {

	@Test
	fun `resolve google client id prefers lowercase system property`() {
		val resolved = resolveGoogleWebClientId(
			systemProperty = {
				when (it) {
					"purecipes.googleWebClientId" -> "lowercase-property"
					"PURECIPES_GOOGLE_WEB_CLIENT_ID" -> "uppercase-property"
					else -> null
				}
			},
			environmentVariable = { "env-value" },
		)

		assertEquals("lowercase-property", resolved)
	}

	@Test
	fun `resolve google client id falls back to uppercase system property`() {
		val resolved = resolveGoogleWebClientId(
			systemProperty = {
				when (it) {
					"PURECIPES_GOOGLE_WEB_CLIENT_ID" -> "uppercase-property"
					else -> null
				}
			},
			environmentVariable = { "env-value" },
		)

		assertEquals("uppercase-property", resolved)
	}

	@Test
	fun `resolve google client id falls back to environment variable`() {
		val resolved = resolveGoogleWebClientId(
			systemProperty = { null },
			environmentVariable = {
				if (it == "PURECIPES_GOOGLE_WEB_CLIENT_ID") "env-value" else null
			},
			resourceProperty = { null },
		)

		assertEquals("env-value", resolved)
	}

	@Test
	fun `resolve google client id falls back to bundled resource`() {
		val resolved = resolveGoogleWebClientId(
			systemProperty = { null },
			environmentVariable = { null },
			resourceProperty = {
				if (it == "purecipes.googleWebClientId") "bundled-value" else null
			},
		)

		assertEquals("bundled-value", resolved)
	}
}