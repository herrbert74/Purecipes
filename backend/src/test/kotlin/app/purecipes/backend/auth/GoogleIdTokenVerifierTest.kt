package app.purecipes.backend.auth

import io.kotest.matchers.shouldBe
import kotlin.test.Test

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

		resolved shouldBe "lowercase-property"
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

		resolved shouldBe "uppercase-property"
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

		resolved shouldBe "env-value"
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

		resolved shouldBe "bundled-value"
	}
}
