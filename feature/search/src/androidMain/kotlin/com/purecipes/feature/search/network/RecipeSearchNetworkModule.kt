package com.purecipes.feature.search.network

import com.purecipes.feature.search.repository.RecipeSearchRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import io.ktor.client.HttpClient

@ContributesTo(AppScope::class)
interface RecipeSearchNetworkModule {

	@Provides
	fun provideRecipeSearchHttpClient(): HttpClient = createRecipeSearchHttpClient()

	@Provides
	fun provideRecipeSearchClient(httpClient: HttpClient): RecipeSearchClient {
		return createRecipeSearchClient(
			httpClient = httpClient,
			baseUrls = backendBaseUrls,
		)
	}

	@Provides
	fun provideRecipeSearchRepository(client: RecipeSearchClient): RecipeSearchRepository {
		return RecipeSearchRepository(client.api)
	}
}
