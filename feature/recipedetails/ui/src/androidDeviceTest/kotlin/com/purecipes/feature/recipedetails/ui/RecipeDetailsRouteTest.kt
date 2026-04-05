package com.purecipes.feature.recipedetails.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import com.github.michaelbull.result.Ok
import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.feature.analytics.domain.model.AnalyticsEvent
import com.purecipes.feature.analytics.domain.repository.AnalyticsRepository
import com.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import com.purecipes.feature.favorites.domain.repository.FavoritesRepository
import com.purecipes.feature.favorites.domain.usecase.AddFavoriteRecipeUseCase
import com.purecipes.feature.favorites.domain.usecase.RemoveFavoriteRecipeUseCase
import com.purecipes.feature.recipedetails.domain.repository.RecipeDetailsRepository
import com.purecipes.feature.recipedetails.domain.usecase.GetRecipeDetailsUseCase
import com.purecipes.shared.domain.model.Cuisine
import com.purecipes.shared.domain.model.IngredientGroup
import com.purecipes.shared.domain.model.RecipeDetails
import com.purecipes.shared.domain.model.RecipeSummary
import org.junit.Rule
import org.junit.Test

class RecipeDetailsRouteTest {

	@get:Rule
	val composeRule = createComposeRule()

	@Test
	fun recipeDetailsRouteShowsTitleIngredientsAndSteps() {
		val favoritesRepository = FakeFavoritesRepository()
		composeRule.setContent {
			RecipeDetailsRoute(
				recipeId = 7,
				addFavoriteRecipe = AddFavoriteRecipeUseCase(favoritesRepository),
				canManageFavorites = true,
				getRecipeDetails = GetRecipeDetailsUseCase(
					FakeRecipeDetailsRepository(
						RecipeDetails(
							id = 7,
							title = "Roasted Carrots",
							description = "Sweet and savory side dish.",
							imageUrl = null,
							ingredientGroups = listOf(
								IngredientGroup(
									name = "Ingredients",
									ingredients = listOf("6 carrots", "2 tbsp olive oil"),
								),
							),
							steps = listOf("Trim the carrots", "Roast until tender"),
							totalTime = 35,
							yields = "4 servings",
							cuisine = Cuisine.MEDITERRANEAN,
						),
					),
				),
				onBack = {},
				onFavoriteChange = {},
				onStartCooking = {},
				removeFavoriteRecipe = RemoveFavoriteRecipeUseCase(favoritesRepository),
				sessionKey = "user-7",
				trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
			)
		}

		composeRule.onNodeWithText("Roasted Carrots").assertIsDisplayed()
		composeRule.onNodeWithText("Sweet and savory side dish.").assertIsDisplayed()
		composeRule.onNodeWithText("Start cooking").assertIsDisplayed()
		composeRule.onNodeWithText("- 6 carrots").performScrollTo().assertIsDisplayed()
		composeRule.onNodeWithText("Roast until tender").performScrollTo().assertIsDisplayed()
	}

	private class FakeRecipeDetailsRepository(
		private val recipeDetails: RecipeDetails,
	) : RecipeDetailsRepository {

		override suspend fun getRecipeDetails(recipeId: Int): Outcome<RecipeDetails> {
			return Ok(recipeDetails)
		}
	}

	private class FakeFavoritesRepository : FavoritesRepository {

		override suspend fun addFavorite(recipeId: Int): Outcome<Unit> = Ok(Unit)

		override suspend fun getFavoriteRecipes(): Outcome<List<RecipeSummary>> = Ok(emptyList())

		override suspend fun removeFavorite(recipeId: Int): Outcome<Unit> = Ok(Unit)
	}

	private class FakeAnalyticsRepository : AnalyticsRepository {

		override fun trackEvent(event: AnalyticsEvent) = Unit

		override fun setUserId(userId: String?) = Unit
	}
}
