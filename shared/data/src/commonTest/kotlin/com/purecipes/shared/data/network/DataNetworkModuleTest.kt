package com.purecipes.shared.data.network

import com.diamondedge.logging.FixedLogLevel
import com.diamondedge.logging.KmLogging
import com.diamondedge.logging.PrintLogger
import com.purecipes.base.kotlin.result.Failure
import com.purecipes.shared.data.getresult.handle
import com.purecipes.shared.domain.model.RecipeDetails
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

class DataNetworkModuleTest {

	@BeforeTest
	fun setup() {
		KmLogging.setLoggers(PrintLogger(FixedLogLevel(true)))
	}

	@Test
	fun `configured http client throws response exception for details 404`() = runTest {
		val client = HttpClient(
			MockEngine {
				respond(
					content = """{"message":"Recipe not found","detail":"No recipe found for id: 999999"}""",
					status = HttpStatusCode.NotFound,
					headers = headersOf(
						HttpHeaders.ContentType,
						ContentType.Application.Json.toString(),
					),
				)
			}
		) {
			configurePurecipesHttpClient()
		}

		try {
			val exception = assertFailsWith<ClientRequestException> {
				client.get("https://example.com/recipes/999999").body<RecipeDetails>()
			}

			exception.response.handle() shouldBe Failure.ServerError("No recipe found for id: 999999")
		} finally {
			client.close()
		}
	}
}
