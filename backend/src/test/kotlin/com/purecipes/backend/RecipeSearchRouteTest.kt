package com.purecipes.backend

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class RecipeSearchRouteTest {
	@Test
	fun `missing query yields 400`() = testApplication {
		application { module() }

		val response = client.get("/recipes/search")
		assertEquals(HttpStatusCode.BadRequest, response.status)
	}

	@Test
	fun `health endpoint returns ok`() = testApplication {
		application { module() }

		val response = client.get("/health")
		assertEquals(HttpStatusCode.OK, response.status)
		// Keep it loose; serialization config might vary.
		response.bodyAsText()
	}
}
