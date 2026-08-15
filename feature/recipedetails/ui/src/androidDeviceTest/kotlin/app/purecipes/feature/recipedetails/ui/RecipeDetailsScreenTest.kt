package app.purecipes.feature.recipedetails.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToNode
import app.purecipes.feature.analytics.domain.model.AnalyticsOrigin
import app.purecipes.feature.analytics.domain.usecase.LogBreadcrumbUseCase
import app.purecipes.feature.analytics.domain.usecase.SendHandledExceptionUseCase
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.library.domain.usecase.AddFavoriteRecipeUseCase
import app.purecipes.feature.library.domain.usecase.AddRecipeToCookbookUseCase
import app.purecipes.feature.library.domain.usecase.CreateCookbookUseCase
import app.purecipes.feature.library.domain.usecase.GetCookbooksPageUseCase
import app.purecipes.feature.library.domain.usecase.GetRecipeCookbooksUseCase
import app.purecipes.feature.library.domain.usecase.ObserveFavoriteEventsUseCase
import app.purecipes.feature.library.domain.usecase.RemoveFavoriteRecipeUseCase
import app.purecipes.feature.measurement.domain.usecase.MarkMeasurementMismatchSeenUseCase
import app.purecipes.feature.measurement.domain.usecase.ObserveMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.ProcessRecipeDetailsForMeasurementPreferencesUseCase
import app.purecipes.feature.recipedetails.domain.usecase.GetRecipeDetailsUseCase
import app.purecipes.feature.sharing.domain.repository.ShareRepository
import app.purecipes.feature.sharing.domain.usecase.ShareRecipeUseCase
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.IngredientGroup
import app.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import app.purecipes.shared.testfixtures.fake.FakeCookbooksRepository
import app.purecipes.shared.testfixtures.fake.FakeCrashRepository
import app.purecipes.shared.testfixtures.fake.FakeFavoritesRepository
import app.purecipes.shared.testfixtures.fake.FakeMeasurementPreferencesRepository
import app.purecipes.shared.testfixtures.fake.FakeRecipeDetailsRepository
import app.purecipes.shared.testfixtures.fake.fakeRecipeDetails
import app.purecipes.shared.testfixtures.fake.recipeIngredients
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
					canManageFavorites = true,
					onOpenMeasurementPreferences = {},
					onBack = {},
					onStartCooking = {},
					viewModel = recipeDetailsViewModelForTest(
						recipeId = 7,
						favoritesRepository = favoritesRepository,
						cookbooksRepository = cookbooksRepository,
						measurementRepository = measurementRepository,
						recipeDetailsRepository = FakeRecipeDetailsRepository(
							fakeRecipeDetails(
								id = 7,
								title = "Roasted Carrots",
								description = "Sweet and savory side dish.",
								imageUrl = null,
								ingredientGroups = listOf(
									IngredientGroup(
										name = "Ingredients",
										ingredients = recipeIngredients("6 carrots", "2 tbsp olive oil"),
									),
								),
								steps = listOf("Trim the carrots", "Roast until tender"),
								totalTime = 35,
								yields = "4 servings",
								cuisine = Cuisine.MEDITERRANEAN,
							),
						),
						sessionKey = "user-7",
					),
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

private fun recipeDetailsViewModelForTest(
	recipeId: Int,
	favoritesRepository: FakeFavoritesRepository = FakeFavoritesRepository(),
	cookbooksRepository: FakeCookbooksRepository = FakeCookbooksRepository(),
	measurementRepository: FakeMeasurementPreferencesRepository = FakeMeasurementPreferencesRepository(),
	recipeDetailsRepository: FakeRecipeDetailsRepository = FakeRecipeDetailsRepository(fakeRecipeDetails()),
	sessionKey: String? = null,
): RecipeDetailsViewModel = RecipeDetailsViewModel(
	addFavoriteRecipe = AddFavoriteRecipeUseCase(favoritesRepository),
	getRecipeDetails = GetRecipeDetailsUseCase(recipeDetailsRepository),
	observeMeasurementPreferences = ObserveMeasurementPreferencesUseCase(measurementRepository),
	markMeasurementMismatchSeen = MarkMeasurementMismatchSeenUseCase(measurementRepository),
	processRecipeDetailsForMeasurementPreferences = ProcessRecipeDetailsForMeasurementPreferencesUseCase(),
	removeFavoriteRecipe = RemoveFavoriteRecipeUseCase(favoritesRepository),
	observeFavoriteEvents = ObserveFavoriteEventsUseCase(favoritesRepository),
	trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
	logBreadcrumb = LogBreadcrumbUseCase(FakeCrashRepository()),
	sendHandledException = SendHandledExceptionUseCase(FakeCrashRepository()),
	getRecipeCookbooks = GetRecipeCookbooksUseCase(cookbooksRepository),
	getCookbooksPage = GetCookbooksPageUseCase(cookbooksRepository),
	createCookbook = CreateCookbookUseCase(cookbooksRepository),
	addRecipeToCookbook = AddRecipeToCookbookUseCase(cookbooksRepository),
	shareRecipe = ShareRecipeUseCase(
		object : ShareRepository {
			override fun shareText(text: String, title: String?) = Unit
		},
	),
	recipeId = recipeId,
	sessionKey = sessionKey,
	origin = AnalyticsOrigin.SEARCH.value,
)
