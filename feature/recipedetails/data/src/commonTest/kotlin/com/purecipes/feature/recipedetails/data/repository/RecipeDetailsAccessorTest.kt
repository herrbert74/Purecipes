package com.purecipes.feature.recipedetails.data.repository

import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import com.purecipes.feature.recipedetails.data.datasource.RecipeDetailsRemoteDataSource
import com.purecipes.shared.data.network.PurecipesApi
import com.purecipes.shared.domain.model.AuthenticatedSession
import com.purecipes.shared.domain.model.Cuisine
import com.purecipes.shared.domain.model.GoogleSignInRequest
import com.purecipes.shared.domain.model.IngredientGroup
import com.purecipes.shared.domain.model.RecipeDetails
import com.purecipes.shared.domain.model.RecipeSummary
import com.purecipes.shared.domain.model.RecipeWriteRequest
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RecipeDetailsAccessorTest {

	@Test
	fun `details repository returns shared domain recipe details`() = runTest {
		val expected = RecipeDetails(
			id = 42,
			title = "Tomato Pasta",
			description = "Simple dinner.",
			imageUrl = "https://example.com/pasta.jpg",
			ingredientGroups = listOf(
				IngredientGroup(
					name = "Sauce",
					ingredients = listOf("2 tomatoes", "1 garlic clove"),
				)
			),
			steps = listOf("Boil pasta", "Make sauce"),
			totalTime = 25,
			yields = "2 servings",
			cuisine = Cuisine.ITALIAN,
		)
		val accessor = RecipeDetailsAccessor(RecipeDetailsRemoteDataSource(FakePurecipesApi(expected)))

		val outcome = accessor.getRecipeDetails(42)

		assertEquals(expected, outcome.get())
		assertNull(outcome.getError())
	}

	private class FakePurecipesApi(
		private val details: RecipeDetails,
	) : PurecipesApi {

		override suspend fun search(query: String, limit: Int): List<RecipeSummary> {
			return emptyList()
		}

		override suspend fun getRecipeDetails(recipeId: Int): RecipeDetails {
			return details
		}

		override suspend fun getCreatedRecipes(): List<RecipeDetails> {
			error("Not needed in this test")
		}

		override suspend fun createRecipe(request: RecipeWriteRequest): RecipeDetails {
			error("Not needed in this test")
		}

		override suspend fun updateRecipe(recipeId: Int, request: RecipeWriteRequest): RecipeDetails {
			error("Not needed in this test")
		}

		override suspend fun signInWithGoogle(request: GoogleSignInRequest): AuthenticatedSession {
			error("Not needed in this test")
		}

		override suspend fun getCurrentSession(): AuthenticatedSession {
			error("Not needed in this test")
		}

		override suspend fun signOut() {
			error("Not needed in this test")
		}

		override suspend fun getFavorites(): List<RecipeSummary> {
			return emptyList()
		}

		override suspend fun addFavorite(recipeId: Int) {
			error("Not needed in this test")
		}

		override suspend fun removeFavorite(recipeId: Int) {
			error("Not needed in this test")
		}
	}
}
