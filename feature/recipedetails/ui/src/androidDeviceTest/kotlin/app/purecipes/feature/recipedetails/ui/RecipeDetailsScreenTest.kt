package app.purecipes.feature.recipedetails.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToNode
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.favorites.domain.usecase.AddFavoriteRecipeUseCase
import app.purecipes.feature.favorites.domain.usecase.AddRecipeToCookbookUseCase
import app.purecipes.feature.favorites.domain.usecase.CreateCookbookUseCase
import app.purecipes.feature.favorites.domain.usecase.GetCookbooksPageUseCase
import app.purecipes.feature.favorites.domain.usecase.GetRecipeCookbooksUseCase
import app.purecipes.feature.favorites.domain.usecase.RemoveFavoriteRecipeUseCase
import app.purecipes.feature.measurement.domain.usecase.GetMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.MarkMeasurementMismatchSeenUseCase
import app.purecipes.feature.measurement.domain.usecase.ProcessRecipeDetailsForMeasurementPreferencesUseCase
import app.purecipes.feature.recipedetails.domain.usecase.GetRecipeDetailsUseCase
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.IngredientGroup
import app.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import app.purecipes.shared.testfixtures.fake.FakeCookbooksRepository
import app.purecipes.shared.testfixtures.fake.FakeFavoritesRepository
import app.purecipes.shared.testfixtures.fake.FakeMeasurementPreferencesRepository
import app.purecipes.shared.testfixtures.fake.FakeRecipeDetailsRepository
import app.purecipes.shared.testfixtures.fake.fakeRecipeDetails
import app.purecipes.shared.ui.theme.PurecipesTheme
import org.junit.Rule
import org.junit.Test

class RecipeDetailsScreenTest {

	@get:Rule
	val composeRule = createComposeRule()

	@Test
	fun recipeDetailsScreenShowsTitleIngredientsAndSteps() {
		val favoritesRepository = FakeFavoritesRepository()
		val cookbooksRepository = FakeCookbooksRepository()
		val measurementRepository = FakeMeasurementPreferencesRepository()
		composeRule.setContent {
			PurecipesTheme {
				RecipeDetailsScreen(
					recipeId = 7,
					addFavoriteRecipe = AddFavoriteRecipeUseCase(favoritesRepository),
					addRecipeToCookbook = AddRecipeToCookbookUseCase(cookbooksRepository),
					canManageFavorites = true,
					createCookbook = CreateCookbookUseCase(cookbooksRepository),
					getCookbooksPage = GetCookbooksPageUseCase(cookbooksRepository),
					getRecipeCookbooks = GetRecipeCookbooksUseCase(cookbooksRepository),
					getRecipeDetails = GetRecipeDetailsUseCase(
						FakeRecipeDetailsRepository(
							fakeRecipeDetails(
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
					getMeasurementPreferences = GetMeasurementPreferencesUseCase(measurementRepository),
					markMeasurementMismatchSeen = MarkMeasurementMismatchSeenUseCase(measurementRepository),
					onOpenMeasurementPreferences = {},
					processRecipeDetailsForMeasurementPreferences =
						ProcessRecipeDetailsForMeasurementPreferencesUseCase(),
					trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
					onBack = {},
					onFavoriteChange = {},
					onStartCooking = {},
					removeFavoriteRecipe = RemoveFavoriteRecipeUseCase(favoritesRepository),
					sessionKey = "user-7",
				)
			}
		}

		composeRule.onNodeWithText("Roasted Carrots").assertIsDisplayed()
		composeRule.onNodeWithText("Sweet and savory side dish.").assertIsDisplayed()
		composeRule.onNodeWithText("Start cooking").assertIsDisplayed()
		composeRule.onNodeWithTag(RECIPE_DETAILS_CONTENT_TAG)
			.performScrollToNode(hasText("- 6 carrots"))
		composeRule.onNodeWithText("- 6 carrots").assertIsDisplayed()
		composeRule.onNodeWithTag(RECIPE_DETAILS_CONTENT_TAG)
			.performScrollToNode(hasText("Roast until tender"))
		composeRule.onNodeWithText("Roast until tender").assertIsDisplayed()
	}
}
