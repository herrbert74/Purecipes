package com.purecipes.feature.newrecipe.data.repository

import com.purecipes.feature.newrecipe.data.datasource.CreatedRecipeDataSource
import com.purecipes.feature.newrecipe.domain.model.SaveCreatedRecipeRequest
import com.purecipes.feature.newrecipe.domain.repository.CreatedRecipeRepository

class CreatedRecipeAccessor(
	private val remoteDataSource: CreatedRecipeDataSource.Remote,
) : CreatedRecipeRepository {

	override suspend fun getCreatedRecipes() = remoteDataSource.getCreatedRecipes()

	override suspend fun saveCreatedRecipe(request: SaveCreatedRecipeRequest) =
		remoteDataSource.saveCreatedRecipe(request)
}
