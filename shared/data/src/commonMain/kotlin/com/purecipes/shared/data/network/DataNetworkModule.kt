package com.purecipes.shared.data.network

import de.jensklingenberg.ktorfit.Ktorfit
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

@ContributesTo(AppScope::class)
interface DataNetworkModule {

	@Provides
	fun provideHttpClient(): HttpClient {
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

	@Provides
	fun providePurecipesApi(httpClient: HttpClient): PurecipesApi {
		val ktorfit = Ktorfit.Builder()
			.baseUrl(if (backendBaseUrl.endsWith("/")) backendBaseUrl else "$backendBaseUrl/")
			.httpClient(httpClient)
			.build()
		return ktorfit.createPurecipesApi()
	}
}
