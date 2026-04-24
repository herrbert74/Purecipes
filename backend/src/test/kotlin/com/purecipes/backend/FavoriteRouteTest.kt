package com.purecipes.backend

import com.purecipes.backend.fake.FakeSessionService
import com.purecipes.backend.routes.favoriteRoutes
import io.kotest.matchers.shouldBe
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

		response.status shouldBe HttpStatusCode.Unauthorized
		response.bodyAsText() shouldBe """{"message":"Unauthorized","detail":"Missing bearer token"}"""
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

		response.status shouldBe HttpStatusCode.Unauthorized
		response.bodyAsText() shouldBe """{"message":"Unauthorized","detail":"Missing bearer token"}"""
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

		response.status shouldBe HttpStatusCode.Unauthorized
		response.bodyAsText() shouldBe """{"message":"Unauthorized","detail":"Missing bearer token"}"""
	}
}
