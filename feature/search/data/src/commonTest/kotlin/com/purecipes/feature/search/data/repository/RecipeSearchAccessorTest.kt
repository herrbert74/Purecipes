package com.purecipes.feature.search.data.repository

import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import com.purecipes.feature.search.data.datasource.RecipeSearchRemoteDataSource
import com.purecipes.shared.data.network.PurecipesApi
import com.purecipes.shared.domain.model.AuthenticatedSession
import com.purecipes.shared.domain.model.GoogleSignInRequest
import com.purecipes.shared.domain.model.RecipeDetails
import com.purecipes.shared.domain.model.RecipeSummary
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RecipeSearchAccessorTest {

	@Test
	fun `blank queries return empty list without calling api`() = runTest {
		val api = FakePurecipesApi(
			searchResult = listOf(
				RecipeSummary(
					id = 1,
					title = "Should not be used",
					cuisine = "Test",
					imageUrl = null,
					totalTime = 10,
				)
			),
		)
		val accessor = RecipeSearchAccessor(RecipeSearchRemoteDataSource(api))

		val outcome = accessor.search("   ")

		assertEquals(emptyList(), outcome.get())
		assertNull(outcome.getError())
		assertEquals(0, api.searchCalls)
	}

	private class FakePurecipesApi(
		private val searchResult: List<RecipeSummary>,
	) : PurecipesApi {

		var searchCalls: Int = 0

		override suspend fun search(query: String, limit: Int): List<RecipeSummary> {
			searchCalls += 1
			return searchResult
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
			error("Not needed in this test")
		}

		override suspend fun addFavorite(recipeId: Int) {
			error("Not needed in this test")
		}

		override suspend fun removeFavorite(recipeId: Int) {
			error("Not needed in this test")
		}
	}
}
