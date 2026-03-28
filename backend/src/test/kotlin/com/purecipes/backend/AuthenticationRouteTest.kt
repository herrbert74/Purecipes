package com.purecipes.backend

import com.purecipes.backend.auth.GoogleIdTokenVerificationResult
import com.purecipes.backend.auth.GoogleIdTokenVerifier
import com.purecipes.shared.domain.model.VerifiedGoogleUser
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthenticationRouteTest {

	@Test
	fun `blank google token yields 400`() = testApplication {
		application {
			module(
				googleIdTokenVerifier = FakeGoogleIdTokenVerifier(
					result = GoogleIdTokenVerificationResult.Invalid("Should not be called"),
				),
			)
		}

		val response = client.post("/auth/google") {
			contentType(ContentType.Application.Json)
			setBody("""{"idToken":"   "}""")
		}

		assertEquals(HttpStatusCode.BadRequest, response.status)
		assertEquals(
			"""{"message":"Invalid request","detail":"Google id token is required"}""",
			response.bodyAsText(),
		)
	}

	@Test
	fun `verified google token returns user`() = testApplication {
		application {
			module(
				googleIdTokenVerifier = FakeGoogleIdTokenVerifier(
					result = GoogleIdTokenVerificationResult.Success(
						VerifiedGoogleUser(
							id = "google-subject",
							email = "taylor@example.com",
							displayName = "Taylor Baker",
							firstName = "Taylor",
							familyName = "Baker",
							profileImageUrl = "https://example.com/avatar.png",
						),
					),
				),
			)
		}

		val response = client.post("/auth/google") {
			contentType(ContentType.Application.Json)
			setBody("""{"idToken":"verified-id-token"}""")
		}

		assertEquals(HttpStatusCode.OK, response.status)
		assertEquals(
			"""{"id":"google-subject","email":"taylor@example.com","displayName":"Taylor Baker","firstName":"Taylor","familyName":"Baker","profileImageUrl":"https://example.com/avatar.png"}""",
			response.bodyAsText(),
		)
	}

	@Test
	fun `invalid google token yields 401`() = testApplication {
		application {
			module(
				googleIdTokenVerifier = FakeGoogleIdTokenVerifier(
					result = GoogleIdTokenVerificationResult.Invalid("Google token verification failed"),
				),
			)
		}

		val response = client.post("/auth/google") {
			contentType(ContentType.Application.Json)
			setBody("""{"idToken":"bad-id-token"}""")
		}

		assertEquals(HttpStatusCode.Unauthorized, response.status)
		assertEquals(
			"""{"message":"Unauthorized","detail":"Google token verification failed"}""",
			response.bodyAsText(),
		)
	}

	private class FakeGoogleIdTokenVerifier(
		private val result: GoogleIdTokenVerificationResult,
	) : GoogleIdTokenVerifier {

		override suspend fun verify(idToken: String): GoogleIdTokenVerificationResult = result
	}
}
