package com.purecipes.backend

import com.purecipes.backend.auth.GoogleIdTokenVerificationResult
import com.purecipes.backend.auth.GoogleIdTokenVerifier
import com.purecipes.backend.fake.FakeSessionService
import com.purecipes.shared.domain.model.VerifiedGoogleUser
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
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
		val sessionService = FakeSessionService()
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
				sessionService = sessionService,
			)
		}

		val response = client.post("/auth/google") {
			contentType(ContentType.Application.Json)
			setBody("""{"idToken":"verified-id-token"}""")
		}

		assertEquals(HttpStatusCode.OK, response.status)
		val expectedGoogleAuthResponse = listOf(
			"""{"accessToken":"session-token-1","expiresAtEpochSeconds":4102444800,"user":{""",
			""""id":"1","email":"taylor@example.com","displayName":"Taylor Baker",""",
			""""firstName":"Taylor","familyName":"Baker",""",
			""""profileImageUrl":"https://example.com/avatar.png","provider":"GOOGLE"}}""",
		).joinToString(separator = "")
		assertEquals(
			expectedGoogleAuthResponse,
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

	@Test
	fun `current session requires bearer token`() = testApplication {
		application {
			module(
				googleIdTokenVerifier = FakeGoogleIdTokenVerifier(
					result = GoogleIdTokenVerificationResult.Invalid("Not used"),
				),
				sessionService = FakeSessionService(),
			)
		}

		val response = client.get("/auth/session")

		assertEquals(HttpStatusCode.Unauthorized, response.status)
		assertEquals(
			"""{"message":"Unauthorized","detail":"Missing bearer token"}""",
			response.bodyAsText(),
		)
	}

	@Test
	fun `current session returns authenticated session`() = testApplication {
		val sessionService = FakeSessionService()
		val session = sessionService.createSession(
			provider = "GOOGLE",
			externalUserId = "google-subject",
			email = "taylor@example.com",
			displayName = "Taylor Baker",
			firstName = "Taylor",
			familyName = "Baker",
			profileImageUrl = "https://example.com/avatar.png",
		)
		application {
			module(
				googleIdTokenVerifier = FakeGoogleIdTokenVerifier(
					result = GoogleIdTokenVerificationResult.Invalid("Not used"),
				),
				sessionService = sessionService,
			)
		}

		val response = client.get("/auth/session") {
			header(HttpHeaders.Authorization, "Bearer ${session.accessToken}")
		}

		assertEquals(HttpStatusCode.OK, response.status)
		val expectedSessionResponse =
			listOf(
				"""{"accessToken":"${session.accessToken}",""",
				""""expiresAtEpochSeconds":${session.expiresAtEpochSeconds},"user":{""",
				""""id":"1","email":"taylor@example.com","displayName":"Taylor Baker",""",
				""""firstName":"Taylor","familyName":"Baker",""",
				""""profileImageUrl":"https://example.com/avatar.png","provider":"GOOGLE"}}""",
			).joinToString(separator = "")
		assertEquals(
			expectedSessionResponse,
			response.bodyAsText(),
		)
	}

	@Test
	fun `sign out revokes session`() = testApplication {
		val sessionService = FakeSessionService()
		val session = sessionService.createSession(
			provider = "GOOGLE",
			externalUserId = "google-subject",
			email = "taylor@example.com",
			displayName = "Taylor Baker",
			firstName = "Taylor",
			familyName = "Baker",
			profileImageUrl = null,
		)
		application {
			module(
				googleIdTokenVerifier = FakeGoogleIdTokenVerifier(
					result = GoogleIdTokenVerificationResult.Invalid("Not used"),
				),
				sessionService = sessionService,
			)
		}

		val signOutResponse = client.post("/auth/sign-out") {
			header(HttpHeaders.Authorization, "Bearer ${session.accessToken}")
		}

		assertEquals(HttpStatusCode.NoContent, signOutResponse.status)

		val sessionResponse = client.get("/auth/session") {
			header(HttpHeaders.Authorization, "Bearer ${session.accessToken}")
		}

		assertEquals(HttpStatusCode.Unauthorized, sessionResponse.status)
	}

	private class FakeGoogleIdTokenVerifier(
		private val result: GoogleIdTokenVerificationResult,
	) : GoogleIdTokenVerifier {

		override suspend fun verify(idToken: String): GoogleIdTokenVerificationResult = result
	}
}
