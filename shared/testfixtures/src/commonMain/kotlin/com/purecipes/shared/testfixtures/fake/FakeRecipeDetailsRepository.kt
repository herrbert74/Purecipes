package com.purecipes.shared.testfixtures.fake

import com.github.michaelbull.result.Ok
import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.feature.recipedetails.domain.repository.RecipeDetailsRepository
import com.purecipes.shared.domain.model.RecipeDetails

class FakeRecipeDetailsRepository(
	private val result: Outcome<RecipeDetails>,
) : RecipeDetailsRepository {

	constructor(recipeDetails: RecipeDetails) : this(Ok(recipeDetails))

	override suspend fun getRecipeDetails(recipeId: Int): Outcome<RecipeDetails> = result
}
