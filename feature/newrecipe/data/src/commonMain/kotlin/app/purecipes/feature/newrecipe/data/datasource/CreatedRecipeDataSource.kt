package app.purecipes.feature.newrecipe.data.datasource

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.newrecipe.data.image.RecipeImagePathLoader
import app.purecipes.feature.newrecipe.data.image.RecipeImageUploader
import app.purecipes.feature.newrecipe.data.repository.toRecipeWriteRequest
import app.purecipes.feature.newrecipe.domain.model.SaveCreatedRecipeRequest
import app.purecipes.shared.data.network.PurecipesApi
import app.purecipes.shared.data.util.runCatchingApi
import app.purecipes.shared.domain.model.RecipeDetails
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

interface CreatedRecipeDataSource {

	interface Remote {

		suspend fun getCreatedRecipes(): Outcome<List<RecipeDetails>>

		suspend fun saveCreatedRecipe(request: SaveCreatedRecipeRequest): Outcome<RecipeDetails>

		suspend fun deleteCreatedRecipe(recipeId: Int): Outcome<Unit>
	}
}

@Inject
@ContributesBinding(AppScope::class)
class CreatedRecipeRemoteDataSource(
	private val api: PurecipesApi,
	private val imagePathLoader: RecipeImagePathLoader,
	private val imageUploader: RecipeImageUploader,
) : CreatedRecipeDataSource.Remote {

	override suspend fun getCreatedRecipes(): Outcome<List<RecipeDetails>> {
		return runCatchingApi {
			api.getCreatedRecipes()
		}
	}

	override suspend fun saveCreatedRecipe(request: SaveCreatedRecipeRequest): Outcome<RecipeDetails> {
		val imageOutcome = resolveImageUrl(request.imageUrl)
		if (imageOutcome.component2() != null) {
			return Err(imageOutcome.component2() ?: error("Image resolution failed without an error"))
		}
		val resolvedImageUrl = imageOutcome.component1()

		return runCatchingApi {
			val recipeWriteRequest = request.toRecipeWriteRequest(imageUrl = resolvedImageUrl)
			val recipeId = request.recipeId
			if (recipeId == null) {
				api.createRecipe(recipeWriteRequest)
			} else {
				api.updateRecipe(recipeId, recipeWriteRequest)
			}
		}
	}

	override suspend fun deleteCreatedRecipe(recipeId: Int): Outcome<Unit> = runCatchingApi {
		api.deleteRecipe(recipeId)
	}

	private suspend fun resolveImageUrl(imageInput: String?): Outcome<String?> {
		val trimmedImageInput = imageInput?.trim().orEmpty()
		return when {
			trimmedImageInput.isBlank() -> Ok(null)
			trimmedImageInput.isRemoteImageUrl() -> Ok(trimmedImageInput)
			else -> {
				val imageOutcome = imagePathLoader.load(trimmedImageInput)
				if (imageOutcome.component2() == null) {
					imageUploader.upload(imageOutcome.component1() ?: error("Image loading returned no value"))
				} else {
					Err(imageOutcome.component2() ?: error("Image loading failed without an error"))
				}
			}
		}
	}
}

private fun String.isRemoteImageUrl(): Boolean {
	return startsWith("http://", ignoreCase = true) || startsWith("https://", ignoreCase = true)
}
