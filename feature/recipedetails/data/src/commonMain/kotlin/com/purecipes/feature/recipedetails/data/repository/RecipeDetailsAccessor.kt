package com.purecipes.feature.recipedetails.data.repository

import com.purecipes.feature.recipedetails.data.datasource.RecipeDetailsDataSource
import com.purecipes.feature.recipedetails.domain.repository.RecipeDetailsRepository

class RecipeDetailsAccessor(
	private val remoteDataSource: RecipeDetailsDataSource.Remote,
) : RecipeDetailsRepository {

	override suspend fun getRecipeDetails(recipeId: Int) = remoteDataSource.getRecipeDetails(recipeId)
}
