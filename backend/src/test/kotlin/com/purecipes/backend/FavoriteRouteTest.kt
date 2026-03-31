package com.purecipes.backend

import com.purecipes.backend.auth.SessionService
import com.purecipes.backend.routes.favoriteRoutes
import com.purecipes.shared.domain.model.AuthenticatedBackendUser
import com.purecipes.shared.domain.model.AuthenticatedSession
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class FavoriteRouteTest {

	@Test
	fun `favorites list requires bearer token`() = testApplication {
		application {
			install(ContentNegotiation) {
				json(
					Json {
						ignoreUnknownKeys = true
						explicitNulls = false
					},
				)
			}
			routing {
				favoriteRoutes(FakeSessionService()) { error("db should not be used") }
			}
		}

		val response = client.get("/favorites")

		assertEquals(HttpStatusCode.Unauthorized, response.status)
		assertEquals(
			"""{"message":"Unauthorized","detail":"Missing bearer token"}""",
			response.bodyAsText(),
		)
	}

	@Test
	fun `add favorite requires bearer token`() = testApplication {
		application {
			install(ContentNegotiation) {
				json(
					Json {
						ignoreUnknownKeys = true
						explicitNulls = false
					},
				)
			}
			routing {
				favoriteRoutes(FakeSessionService()) { error("db should not be used") }
			}
		}

		val response = client.post("/favorites/42")

		assertEquals(HttpStatusCode.Unauthorized, response.status)
		assertEquals(
			"""{"message":"Unauthorized","detail":"Missing bearer token"}""",
			response.bodyAsText(),
		)
	}

	@Test
	fun `remove favorite requires bearer token`() = testApplication {
		application {
			install(ContentNegotiation) {
				json(
					Json {
						ignoreUnknownKeys = true
						explicitNulls = false
					},
				)
			}
			routing {
				favoriteRoutes(FakeSessionService()) { error("db should not be used") }
			}
		}

		val response = client.delete("/favorites/42")

		assertEquals(HttpStatusCode.Unauthorized, response.status)
		assertEquals(
			"""{"message":"Unauthorized","detail":"Missing bearer token"}""",
			response.bodyAsText(),
		)
	}

	private class FakeSessionService : SessionService {

		override fun ensureSchema() = Unit

		override fun createSession(
			provider: String,
			externalUserId: String,
			email: String,
			displayName: String,
			firstName: String?,
			familyName: String?,
			profileImageUrl: String?,
		): AuthenticatedSession {
			return AuthenticatedSession(
				accessToken = "session-token",
				expiresAtEpochSeconds = 4_102_444_800,
				user = AuthenticatedBackendUser(
					id = "1",
					email = email,
					displayName = displayName,
					firstName = firstName,
					familyName = familyName,
					profileImageUrl = profileImageUrl,
					provider = provider,
				),
			)
		}

		override fun getSession(accessToken: String): AuthenticatedSession? = null

		override fun revokeSession(accessToken: String): Boolean = false
	}
}
