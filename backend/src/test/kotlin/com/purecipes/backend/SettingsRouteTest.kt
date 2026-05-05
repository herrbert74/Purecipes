package com.purecipes.backend

import com.purecipes.backend.fake.FakeSessionService
import com.purecipes.backend.feature.settings.settingsRoutes
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlin.test.Test

class SettingsRouteTest {

	@Test
	fun `measurement settings get requires bearer token`() = testApplication {
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
				settingsRoutes(FakeSessionService()) { error("db should not be used") }
			}
		}

		val response = client.get("/settings/measurement")

		response.status shouldBe HttpStatusCode.Unauthorized
		response.bodyAsText() shouldBe """{"message":"Unauthorized","detail":"Missing bearer token"}"""
	}

	@Test
	fun `measurement settings put requires bearer token`() = testApplication {
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
				settingsRoutes(FakeSessionService()) { error("db should not be used") }
			}
		}

		val response = client.put("/settings/measurement") {
			contentType(ContentType.Application.Json)
			setBody(
				"""
				{
					"preferredSystem":"METRIC",
					"formatHandling":"KEEP_AS_IS"
				}
				""".trimIndent(),
			)
		}

		response.status shouldBe HttpStatusCode.Unauthorized
		response.bodyAsText() shouldBe """{"message":"Unauthorized","detail":"Missing bearer token"}"""
	}
}
