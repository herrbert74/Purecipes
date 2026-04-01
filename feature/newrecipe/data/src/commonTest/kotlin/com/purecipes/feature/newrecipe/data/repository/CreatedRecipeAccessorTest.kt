package com.purecipes.feature.newrecipe.data.repository

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import com.purecipes.base.kotlin.result.Failure
import com.purecipes.feature.newrecipe.data.datasource.CreatedRecipeRemoteDataSource
import com.purecipes.feature.newrecipe.data.image.RecipeImagePathLoader
import com.purecipes.feature.newrecipe.data.image.RecipeImageUpload
import com.purecipes.feature.newrecipe.data.image.RecipeImageUploader
import com.purecipes.feature.newrecipe.domain.model.SaveCreatedRecipeRequest
import com.purecipes.shared.data.network.PurecipesApi
import com.purecipes.shared.domain.model.AuthenticatedSession
import com.purecipes.shared.domain.model.GoogleSignInRequest
import com.purecipes.shared.domain.model.IngredientGroup
import com.purecipes.shared.domain.model.RecipeDetails
import com.purecipes.shared.domain.model.RecipeSummary
import com.purecipes.shared.domain.model.RecipeWriteRequest
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CreatedRecipeAccessorTest {

	@Test
	fun `saving a recipe uploads it`() = runTest {
		val api = FakePurecipesApi()
		val accessor = CreatedRecipeAccessor(
			remoteDataSource = CreatedRecipeRemoteDataSource(
				api = api,
				imagePathLoader = FakeRecipeImagePathLoader(),
				imageUploader = FakeRecipeImageUploader(),
			),
		)

		val savedRecipe = accessor.saveCreatedRecipe(
			SaveCreatedRecipeRequest(
				title = "Tomato Pasta",
				description = "Quick weeknight dinner.",
				ingredients = listOf("200 g pasta", "2 tomatoes"),
				steps = listOf("Boil the pasta", "Finish with the tomatoes"),
				totalTime = 20,
				yields = "2 servings",
				cuisine = "Italian",
			),
		).get()

		val storedRecipes = accessor.getCreatedRecipes().get()

		assertNotNull(savedRecipe)
		assertEquals(1, savedRecipe.id)
		assertEquals(listOf(savedRecipe), storedRecipes)
	}

	@Test
	fun `saving with an existing id updates the uploaded recipe`() = runTest {
		val api = FakePurecipesApi()
		val accessor = CreatedRecipeAccessor(
			remoteDataSource = CreatedRecipeRemoteDataSource(
				api = api,
				imagePathLoader = FakeRecipeImagePathLoader(),
				imageUploader = FakeRecipeImageUploader(),
			),
		)
		val firstSave = accessor.saveCreatedRecipe(
			SaveCreatedRecipeRequest(
				title = "Tomato Pasta",
				description = "Quick weeknight dinner.",
				steps = listOf("Boil the pasta"),
			),
		).get()

		val updatedRecipe = accessor.saveCreatedRecipe(
			SaveCreatedRecipeRequest(
				recipeId = firstSave?.id,
				title = "Creamy Tomato Pasta",
				description = "Updated version.",
				steps = listOf("Boil the pasta", "Stir in cream"),
			),
		).get()

		val storedRecipes = accessor.getCreatedRecipes().get().orEmpty()

		assertEquals(firstSave?.id, updatedRecipe?.id)
		assertEquals(1, storedRecipes.size)
		assertEquals("Creamy Tomato Pasta", storedRecipes.single().title)
	}

	@Test
	fun `saving with local image path uploads image before recipe`() = runTest {
		val api = FakePurecipesApi()
		val imagePathLoader = FakeRecipeImagePathLoader()
		val imageUploader = FakeRecipeImageUploader()
		val accessor = CreatedRecipeAccessor(
			remoteDataSource = CreatedRecipeRemoteDataSource(
				api = api,
				imagePathLoader = imagePathLoader,
				imageUploader = imageUploader,
			),
		)

		val savedRecipe = accessor.saveCreatedRecipe(
			SaveCreatedRecipeRequest(
				title = "Tomato Pasta",
				description = "Quick weeknight dinner.",
				imageUrl = "/tmp/tomato.jpg",
				steps = listOf("Boil the pasta"),
			),
		).get()

		assertEquals("/tmp/tomato.jpg", imagePathLoader.loadedPath)
		assertEquals("https://cdn.purecipes.test/recipe-image.jpg", savedRecipe?.imageUrl)
	}

	@Test
	fun `saving fails when local image path cannot be read`() = runTest {
		val api = FakePurecipesApi()
		val accessor = CreatedRecipeAccessor(
			remoteDataSource = CreatedRecipeRemoteDataSource(
				api = api,
				imagePathLoader = FakeRecipeImagePathLoader(
					result = Err(Failure.ServerError("Could not read the image file from the provided local path.")),
				),
				imageUploader = FakeRecipeImageUploader(),
			),
		)

		val outcome = accessor.saveCreatedRecipe(
			SaveCreatedRecipeRequest(
				title = "Tomato Pasta",
				description = "Quick weeknight dinner.",
				imageUrl = "/tmp/missing.jpg",
				steps = listOf("Boil the pasta"),
			),
		)

		assertEquals("Could not read the image file from the provided local path.", outcome.getError()?.message)
	}

	private class FakePurecipesApi : PurecipesApi {

		private val recipes = mutableListOf<RecipeDetails>()

		override suspend fun search(query: String, limit: Int): List<RecipeSummary> = emptyList()

		override suspend fun getRecipeDetails(recipeId: Int): RecipeDetails {
			return recipes.first { it.id == recipeId }
		}

		override suspend fun getCreatedRecipes(): List<RecipeDetails> = recipes.toList()

		override suspend fun createRecipe(request: RecipeWriteRequest): RecipeDetails {
			val recipe = request.toRecipeDetails(id = 1)
			recipes.clear()
			recipes += recipe
			return recipe
		}

		override suspend fun updateRecipe(recipeId: Int, request: RecipeWriteRequest): RecipeDetails {
			val recipe = request.toRecipeDetails(id = recipeId)
			recipes.removeAll { it.id == recipeId }
			recipes += recipe
			return recipe
		}

		override suspend fun signInWithGoogle(request: GoogleSignInRequest): AuthenticatedSession {
			error("Not needed in test")
		}

		override suspend fun getCurrentSession(): AuthenticatedSession {
			error("Not needed in test")
		}

		override suspend fun signOut() {
			error("Not needed in test")
		}

		override suspend fun getFavorites(): List<RecipeSummary> = emptyList()

		override suspend fun addFavorite(recipeId: Int) = Unit

		override suspend fun removeFavorite(recipeId: Int) = Unit
	}

	private class FakeRecipeImagePathLoader(
		private val result: com.purecipes.base.kotlin.result.Outcome<RecipeImageUpload> = Ok(
			RecipeImageUpload(
				bytes = byteArrayOf(1, 2, 3),
				fileName = "recipe-image.jpg",
				contentType = "image/jpeg",
			),
		),
	) : RecipeImagePathLoader {

		var loadedPath: String? = null
			private set

		override suspend fun load(path: String): com.purecipes.base.kotlin.result.Outcome<RecipeImageUpload> {
			loadedPath = path
			return result
		}
	}

	private class FakeRecipeImageUploader(
		private val uploadedUrl: String = "https://cdn.purecipes.test/recipe-image.jpg",
	) : RecipeImageUploader {

		override suspend fun upload(image: RecipeImageUpload) = Ok(uploadedUrl)
	}
}

private fun RecipeWriteRequest.toRecipeDetails(id: Int): RecipeDetails {
	return RecipeDetails(
		id = id,
		title = title,
		description = description,
		imageUrl = imageUrl,
		ingredientGroups = ingredientGroups.ifEmpty { listOf(IngredientGroup()) },
		steps = steps,
		totalTime = totalTime,
		yields = yields,
		cuisine = cuisine,
	)
}
