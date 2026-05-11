package app.purecipes.shared.data.network

import app.purecipes.base.kotlin.result.Failure
import app.purecipes.shared.data.getresult.handle
import app.purecipes.shared.data.session.SessionTokenStore
import app.purecipes.shared.domain.model.AuthenticatedSession
import app.purecipes.shared.domain.model.RecipeDetails
import com.diamondedge.logging.FixedLogLevel
import com.diamondedge.logging.KmLogging
import com.diamondedge.logging.PrintLogger
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

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
			configurePurecipesHttpClient(FakeSessionTokenStore())
		}

		try {
			val exception = shouldThrow<ClientRequestException> {
				client.get("https://example.com/recipes/999999").body<RecipeDetails>()
			}

			exception.response.handle() shouldBe Failure.ServerError("No recipe found for id: 999999")
		} finally {
			client.close()
		}
	}

	@Test
	fun `configured http client attaches bearer token when available`() = runTest {
		var authorizationHeader: String? = null
		val client = HttpClient(
			MockEngine { request ->
				authorizationHeader = request.headers[HttpHeaders.Authorization]
				respond(
					content = "{}",
					status = HttpStatusCode.OK,
					headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
				)
			}
		) {
			configurePurecipesHttpClient(FakeSessionTokenStore(accessToken = "session-token"))
		}

		try {
			client.get("https://example.com/recipes/search") {
				header(HttpHeaders.Accept, ContentType.Application.Json)
			}
			authorizationHeader shouldBe "Bearer session-token"
		} finally {
			client.close()
		}
	}

	private class FakeSessionTokenStore(
		private val accessToken: String? = null,
	) : SessionTokenStore {

		override fun currentSession() = null

		override fun currentAccessToken(): String? = accessToken

		override fun saveSession(session: AuthenticatedSession) = Unit

		override fun clearSession() = Unit
	}
}
