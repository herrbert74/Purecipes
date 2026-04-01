package com.purecipes.feature.newrecipe.data.image

import com.purecipes.base.kotlin.result.Outcome
import java.io.File

actual suspend fun readRecipeImageUpload(path: String): Outcome<RecipeImageUpload> {
	val normalizedPath = normalizeRecipeImagePath(path)
	val file = File(normalizedPath)
	if (!file.exists() || !file.isFile) {
		return recipeImageFailure("Could not read the image file from the provided local path.")
	}

	val fileName = file.name.ifBlank { recipeImageFileName(normalizedPath) }
	return recipeImageSuccess(
		RecipeImageUpload(
			bytes = file.readBytes(),
			fileName = fileName,
			contentType = recipeImageContentType(fileName),
		),
	)
}
