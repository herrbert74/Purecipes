package com.purecipes.feature.search.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.Url
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json

data class RecipeSearchClient(
	val httpClient: HttpClient,
	val api: RecipeSearchApi,
)

fun createRecipeSearchHttpClient(): HttpClient {
	return HttpClient {
		install(ContentNegotiation) {
			json(
				Json {
					ignoreUnknownKeys = true
					explicitNulls = false
				}
			)
		}
	}
}

fun createRecipeSearchClient(
	httpClient: HttpClient,
	baseUrls: List<String> = backendBaseUrls,
): RecipeSearchClient {
	require(baseUrls.isNotEmpty()) { "No backend URLs configured" }
	val parsedBaseUrls = baseUrls.map { baseUrl ->
		val normalizedBaseUrl = if (baseUrl.endsWith('/')) baseUrl else "$baseUrl/"
		Url(normalizedBaseUrl)
	}

	val api = object : RecipeSearchApi {
		override suspend fun search(query: String, limit: Int): List<RecipeSearchDto> {
			var lastError: Throwable? = null
			val attemptedUrls = mutableListOf<String>()
			for (base in parsedBaseUrls) {
				val requestUrl = "${base.protocol.name}://${base.host}:${base.port}/recipes/search"
				attemptedUrls += requestUrl
				try {
					return httpClient.get(requestUrl) {
						parameter("query", query)
						parameter("limit", limit.toString())
					}.body()
				} catch (error: Throwable) {
					if (error is CancellationException) throw error
					lastError = error
				}
			}

			val failure = IllegalStateException(
				"Unable to reach backend. Tried: ${attemptedUrls.joinToString()}"
			)
			if (lastError != null) {
				failure.addSuppressed(lastError)
			}
			throw failure
		}
	}

	return RecipeSearchClient(
		httpClient = httpClient,
		api = api,
	)
}

fun createRecipeSearchClient(): RecipeSearchClient {
	return createRecipeSearchClient(
		httpClient = createRecipeSearchHttpClient(),
		baseUrls = backendBaseUrls,
	)
}
