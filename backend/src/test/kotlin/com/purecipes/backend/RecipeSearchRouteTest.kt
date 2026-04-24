package com.purecipes.backend

import io.kotest.matchers.shouldBe
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.get
import io.ktor.server.testing.testApplication
import kotlin.test.Test

class RecipeSearchRouteTest {

	@Test
	fun `missing query yields 400`() = testApplication {
		application { module() }

		val response = client.get("/recipes/search")
		response.status shouldBe HttpStatusCode.BadRequest
		response.bodyAsText() shouldBe """{"message":"Invalid request","detail":"Missing query parameter: query"}"""
	}

	@Test
	fun `health endpoint returns ok`() = testApplication {
		application { module() }

		val response = client.get("/health")
		response.status shouldBe HttpStatusCode.OK
		response.bodyAsText()
	}

	@Test
	fun `unhandled exception yields message and detail`() = testApplication {
		application {
			module(extraRoutes = {
				get("/boom") {
					error("kaboom")
				}
			})
		}

		val response = client.get("/boom")
		response.status shouldBe HttpStatusCode.InternalServerError
		response.bodyAsText() shouldBe """{"message":"Unexpected error","detail":"kaboom"}"""
	}

	@Test
	fun `invalid recipe id yields 400`() = testApplication {
		application { module() }

		val response = client.get("/recipes/not-a-number")
		response.status shouldBe HttpStatusCode.BadRequest
		response.bodyAsText() shouldBe """{"message":"Invalid request","detail":"Recipe id must be a number"}"""
	}

	@Test
	fun `invalid favorite recipe id yields 400 on add`() = testApplication {
		application { module() }

		val response = client.post("/favorites/not-a-number")
		response.status shouldBe HttpStatusCode.BadRequest
		response.bodyAsText() shouldBe """{"message":"Invalid request","detail":"Recipe id must be a number"}"""
	}

	@Test
	fun `invalid favorite recipe id yields 400 on delete`() = testApplication {
		application { module() }

		val response = client.delete("/favorites/not-a-number")
		response.status shouldBe HttpStatusCode.BadRequest
		response.bodyAsText() shouldBe """{"message":"Invalid request","detail":"Recipe id must be a number"}"""
	}

}
