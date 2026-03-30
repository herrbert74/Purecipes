package com.purecipes.feature.favorites.data.repository

import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import com.purecipes.feature.favorites.data.datasource.FavoritesRemoteDataSource
import com.purecipes.shared.data.network.PurecipesApi
import com.purecipes.shared.domain.model.AuthenticatedSession
import com.purecipes.shared.domain.model.GoogleSignInRequest
import com.purecipes.shared.domain.model.RecipeDetails
import com.purecipes.shared.domain.model.RecipeSummary
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FavoritesAccessorTest {

	@Test
	fun `favorites repository returns favorite recipes`() = runTest {
		val expected = listOf(
			RecipeSummary(
				id = 42,
				title = "Tomato Pasta",
				cuisine = "Italian",
				imageUrl = "https://example.com/pasta.jpg",
				totalTime = 25,
				isFavorite = true,
			),
		)
		val accessor = FavoritesAccessor(FavoritesRemoteDataSource(FakePurecipesApi(expected)))

		val outcome = accessor.getFavoriteRecipes()

		assertEquals(expected, outcome.get())
		assertNull(outcome.getError())
	}

	private class FakePurecipesApi(
		private val favorites: List<RecipeSummary>,
	) : PurecipesApi {

		override suspend fun search(query: String, limit: Int): List<RecipeSummary> {
			return emptyList()
		}

		override suspend fun getRecipeDetails(recipeId: Int): RecipeDetails {
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
			return favorites
		}

		override suspend fun addFavorite(recipeId: Int) = Unit

		override suspend fun removeFavorite(recipeId: Int) = Unit
	}
}
