package com.purecipes.feature.newrecipe.data.image

import com.purecipes.base.kotlin.result.Outcome
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.posix.memcpy

actual suspend fun readRecipeImageUpload(path: String): Outcome<RecipeImageUpload> {
	val normalizedPath = normalizeRecipeImagePath(path)
	val data = NSFileManager.defaultManager.contentsAtPath(normalizedPath)
		?: return recipeImageFailure("Could not read the image file from the provided local path.")
	val fileName = recipeImageFileName(normalizedPath)
	return recipeImageSuccess(
		RecipeImageUpload(
			bytes = data.toByteArray(),
			fileName = fileName,
			contentType = recipeImageContentType(fileName),
		),
	)
}

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
	val byteArray = ByteArray(length.toInt())
	byteArray.usePinned {
		memcpy(it.addressOf(0), bytes, length)
	}
	return byteArray
}
