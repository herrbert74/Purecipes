package com.purecipes.shared.data.util

import com.github.michaelbull.result.Err
import com.purecipes.base.kotlin.result.Failure
import com.purecipes.shared.data.getresult.handle
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.JsonConvertException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlin.test.Test
import kotlin.test.assertFailsWith

class KotlinResultTest {

	@Test
	fun `runCatchingApi maps io exception to io failure`() = runTest {
		runCatchingApi<Int> {
			throw IOException("boom")
		} shouldBe Err(Failure.IoFailure)
	}

	@Test
	fun `runCatchingApi maps 400 response exception to server error message`() = runTest {
		val client = HttpClient(
			MockEngine {
				respond(
					content = """{"message":"Invalid request","detail":"bad request"}""",
					status = HttpStatusCode.BadRequest,
					headers = headersOf(
						HttpHeaders.ContentType,
						ContentType.Application.Json.toString()
					)
				)
			}
		) {
			expectSuccess = true
		}

		try {
			runCatchingApi {
				client.get("https://example.com/error")
			} shouldBe Err(Failure.ServerError("bad request"))
		} finally {
			client.close()
		}
	}

	@Test
	fun `runCatchingApi maps json conversion exception to server error`() = runTest {
		runCatchingApi<Int> {
			throw JsonConvertException("Unexpected server response")
		} shouldBe Err(Failure.ServerError("Unexpected server response"))
	}

	@Test
	fun `http response handle maps error response to server error`() = runTest {
		val client = HttpClient(
			MockEngine {
				respond(
					content = """{"message":"Invalid request","detail":"bad request"}""",
					status = HttpStatusCode.BadRequest,
					headers = headersOf(
						HttpHeaders.ContentType,
						ContentType.Application.Json.toString()
					)
				)
			}
		)

		try {
			client.get("https://example.com/error").body<String>()
		} catch (exception: io.ktor.client.plugins.ClientRequestException) {
			exception.response.handle() shouldBe Failure.ServerError("bad request")
		} finally {
			client.close()
		}
	}

	@Test
	fun `http response handle maps not modified response`() = runTest {
		val client = HttpClient(
			MockEngine {
				respond(
					content = "",
					status = HttpStatusCode.NotModified,
					headers = headersOf(
						HttpHeaders.ContentType,
						ContentType.Text.Plain.toString()
					)
				)
			}
		)

		try {
			client.get("https://example.com/cache").body<String>()
		} catch (exception: io.ktor.client.plugins.RedirectResponseException) {
			exception.response.handle() shouldBe Failure.NotModified
		} finally {
			client.close()
		}
	}

	@Test
	fun `runCatchingApi rethrows cancellation exception`() = runTest {
		val exception = CancellationException("cancelled")

		assertFailsWith<CancellationException> {
			runCatchingApi<Int> {
				throw exception
			}
		} shouldBe exception
	}

	@Test
	fun `runCatchingApi rethrows unexpected exception`() = runTest {
		val exception = IllegalStateException("unexpected")

		assertFailsWith<IllegalStateException> {
			runCatchingApi<Int> {
				throw exception
			}
		} shouldBe exception
	}

	@Test
	fun `runCatchingUnit wraps io exception in err`() {
		val exception = IOException("boom")

		runCatchingUnit<Unit> {
			throw exception
		} shouldBe Err(exception)
	}

	@Test
	fun `runCatchingUnit rethrows cancellation exception`() {
		val exception = CancellationException("cancelled")

		assertFailsWith<CancellationException> {
			runCatchingUnit<Unit> {
				throw exception
			}
		} shouldBe exception
	}
}
