package app.purecipes.backend

import app.purecipes.backend.fake.FakeSessionService
import app.purecipes.backend.feature.featurerequests.featureRequestRoutes
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlin.test.Test

private const val UNAUTHORIZED_BODY = """{"message":"Unauthorized","detail":"Missing bearer token"}"""

class FeatureRequestRouteTest {

	@Test
	fun `feature requests list requires bearer token`() = testApplication {
		installFeatureRequestRoutes()

		assertUnauthorized(client.get("/feature-requests"))
	}

	@Test
	fun `create feature request requires bearer token`() = testApplication {
		installFeatureRequestRoutes()

		assertUnauthorized(client.post("/feature-requests"))
	}

	@Test
	fun `feature request detail requires bearer token`() = testApplication {
		installFeatureRequestRoutes()

		assertUnauthorized(client.get("/feature-requests/42"))
	}

	@Test
	fun `vote toggle requires bearer token`() = testApplication {
		installFeatureRequestRoutes()

		assertUnauthorized(client.post("/feature-requests/42/vote"))
	}

	@Test
	fun `comments list requires bearer token`() = testApplication {
		installFeatureRequestRoutes()

		assertUnauthorized(client.get("/feature-requests/42/comments"))
	}

	@Test
	fun `add comment requires bearer token`() = testApplication {
		installFeatureRequestRoutes()

		assertUnauthorized(client.post("/feature-requests/42/comments"))
	}

	private fun ApplicationTestBuilder.installFeatureRequestRoutes() {
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
				featureRequestRoutes(FakeSessionService()) { error("db should not be used") }
			}
		}
	}

	private suspend fun assertUnauthorized(response: HttpResponse) {
		response.status shouldBe HttpStatusCode.Unauthorized
		response.bodyAsText() shouldBe UNAUTHORIZED_BODY
	}
}
