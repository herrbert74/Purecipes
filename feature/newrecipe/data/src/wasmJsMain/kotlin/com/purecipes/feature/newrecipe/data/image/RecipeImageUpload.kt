package com.purecipes.feature.newrecipe.data.image

import com.purecipes.base.kotlin.result.Outcome

actual suspend fun readRecipeImageUpload(path: String): Outcome<RecipeImageUpload> {
	return recipeImageFailure("Local image file upload is not supported on Wasm yet. Use an image URL instead.")
}
