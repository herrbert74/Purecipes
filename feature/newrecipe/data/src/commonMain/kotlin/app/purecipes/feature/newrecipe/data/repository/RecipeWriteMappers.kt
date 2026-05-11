package app.purecipes.feature.newrecipe.data.repository

import app.purecipes.feature.newrecipe.domain.model.SaveCreatedRecipeRequest
import app.purecipes.shared.domain.model.IngredientGroup
import app.purecipes.shared.domain.model.RecipeWriteRequest

internal fun SaveCreatedRecipeRequest.toRecipeWriteRequest(imageUrl: String? = this.imageUrl): RecipeWriteRequest {
	return RecipeWriteRequest(
		title = title,
		description = description,
		imageUrl = imageUrl,
		ingredientGroups = listOf(
			IngredientGroup(
				ingredients = ingredients,
			),
		),
		steps = steps,
		totalTime = totalTime,
		yields = yields,
		cuisine = cuisine,
	)
}
