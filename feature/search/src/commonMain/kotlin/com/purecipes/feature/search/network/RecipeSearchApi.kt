package com.purecipes.feature.search.network

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

interface RecipeSearchApi {

	@GET("recipes/search")
	suspend fun search(
		@Query("query") query: String,
		@Query("limit") limit: Int = 25,
	): List<RecipeSearchDto>
}
