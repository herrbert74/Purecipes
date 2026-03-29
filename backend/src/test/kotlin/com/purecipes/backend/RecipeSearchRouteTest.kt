package com.purecipes.backend

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.get
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class RecipeSearchRouteTest {

	@Test
	fun `missing query yields 400`() = testApplication {
		application { module() }

		val response = client.get("/recipes/search")
		assertEquals(HttpStatusCode.BadRequest, response.status)
		assertEquals(
			"""{"message":"Invalid request","detail":"Missing query parameter: query"}""",
			response.bodyAsText()
		)
	}

	@Test
	fun `health endpoint returns ok`() = testApplication {
		application { module() }

		val response = client.get("/health")
		assertEquals(HttpStatusCode.OK, response.status)
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
		assertEquals(HttpStatusCode.InternalServerError, response.status)
		assertEquals(
			"""{"message":"Unexpected error","detail":"kaboom"}""",
			response.bodyAsText()
		)
	}

	@Test
	fun `invalid recipe id yields 400`() = testApplication {
		application { module() }

		val response = client.get("/recipes/not-a-number")
		assertEquals(HttpStatusCode.BadRequest, response.status)
		assertEquals(
			"""{"message":"Invalid request","detail":"Recipe id must be a number"}""",
			response.bodyAsText()
		)
	}

}
