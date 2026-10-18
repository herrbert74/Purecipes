package app.purecipes.feature.recipedetails.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.purecipes.feature.analytics.domain.model.AnalyticsOrigin
import app.purecipes.feature.analytics.domain.usecase.LogBreadcrumbUseCase
import app.purecipes.feature.analytics.domain.usecase.SendHandledExceptionUseCase
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.library.domain.usecase.AddFavoriteRecipeUseCase
import app.purecipes.feature.library.domain.usecase.AddRecipeToCookbookUseCase
import app.purecipes.feature.library.domain.usecase.CreateCookbookUseCase
import app.purecipes.feature.library.domain.usecase.GetCookbooksPageUseCase
import app.purecipes.feature.library.domain.usecase.GetRecipeCookbooksUseCase
import app.purecipes.feature.library.domain.usecase.ObserveCookbookMembershipEventsUseCase
import app.purecipes.feature.library.domain.usecase.ObserveFavoriteEventsUseCase
import app.purecipes.feature.library.domain.usecase.RemoveFavoriteRecipeUseCase
import app.purecipes.feature.measurement.domain.usecase.MarkMeasurementMismatchSeenUseCase
import app.purecipes.feature.measurement.domain.usecase.ObserveMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.ProcessRecipeDetailsForMeasurementPreferencesUseCase
import app.purecipes.feature.recipedetails.domain.usecase.GetRecipeDetailsUseCase
import app.purecipes.feature.recipedetails.domain.usecase.RecordRecipeActivityUseCase
import app.purecipes.feature.sharing.domain.repository.ShareRepository
import app.purecipes.feature.sharing.domain.usecase.ShareRecipeUseCase
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.DifficultyLevel
import app.purecipes.shared.domain.model.IngredientGroup
import app.purecipes.shared.domain.model.NutritionSummary
import app.purecipes.shared.domain.model.RecipeNutrition
import app.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import app.purecipes.shared.testfixtures.fake.FakeCookbooksRepository
import app.purecipes.shared.testfixtures.fake.FakeCrashRepository
import app.purecipes.shared.testfixtures.fake.FakeFavoritesRepository
import app.purecipes.shared.testfixtures.fake.FakeMeasurementPreferencesRepository
import app.purecipes.shared.testfixtures.fake.FakeRecipeActivityRepository
import app.purecipes.shared.testfixtures.fake.FakeRecipeDetailsRepository
import app.purecipes.shared.testfixtures.fake.fakeRecipeDetails
import app.purecipes.shared.testfixtures.fake.recipeIngredients
import app.purecipes.shared.testfixtures.uitest.UiTestEssential
import app.purecipes.shared.testfixtures.uitest.UiTestRecipeDetails
import app.purecipes.shared.ui.component.NUTRITION_FACTS_BUTTON_TAG
import app.purecipes.shared.ui.component.RecipeDetailsSection
import app.purecipes.shared.ui.theme.PurecipesTheme
import dejavu.runRecompositionTrackingUiTest
import dejavu.setTrackedContent
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
@UiTestRecipeDetails
class RecipeDetailsScreenTest {

	@Test
	@UiTestEssential
	fun recipeDetailsScreenShowsTitleIngredientsAndSteps() = runRecompositionTrackingUiTest {
		val favoritesRepository = FakeFavoritesRepository()
		val cookbooksRepository = FakeCookbooksRepository()
		val measurementRepository = FakeMeasurementPreferencesRepository()
		setTrackedContent {
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
							).copy(
								difficultyLevel = DifficultyLevel.EASY,
								nutrition = RecipeNutrition(
									recipeTotals = NutritionSummary(calories = 180.0, protein = 3.0),
								),
							),
						),
						sessionKey = "user-7",
					),
				)
			}
		}

		onNodeWithText("Roasted Carrots").assertIsDisplayed()
		onNodeWithText("Sweet and savory side dish.").assertIsDisplayed()
		onNodeWithTag(RECIPE_DETAILS_CONTENT_TAG)
			.performScrollToNode(hasText("35 min"))
		onNodeWithText("35 min").assertIsDisplayed()
		onNodeWithTag(RECIPE_DETAILS_CONTENT_TAG)
			.performScrollToNode(hasText("Start cooking"))
		onNodeWithText("Start cooking").assertIsDisplayed()
		onNodeWithTag(RECIPE_DETAILS_CONTENT_TAG)
			.performScrollToNode(hasTestTag(RecipeDetailsSection.Ingredients.testTag))
		onNodeWithTag(RecipeDetailsSection.Ingredients.testTag).assertIsDisplayed()
		onNodeWithTag(RECIPE_DETAILS_CONTENT_TAG)
			.performScrollToNode(hasText("- 6 carrots"))
		onNodeWithText("- 6 carrots").assertIsDisplayed()
		onNodeWithTag(RECIPE_DETAILS_CONTENT_TAG)
			.performScrollToNode(hasTestTag(RecipeDetailsSection.Method.testTag))
		onNodeWithTag(RecipeDetailsSection.Method.testTag).performClick()
		onNodeWithTag(RECIPE_DETAILS_CONTENT_TAG)
			.performScrollToNode(hasText("Trim the carrots"))
		onNodeWithText("Trim the carrots").assertIsDisplayed()
		onNodeWithTag(RECIPE_DETAILS_CONTENT_TAG)
			.performScrollToNode(hasText("Roast until tender"))
		onNodeWithText("Roast until tender").assertIsDisplayed()
		onNodeWithTag(RECIPE_DETAILS_CONTENT_TAG)
			.performScrollToNode(hasTestTag(RecipeDetailsSection.Nutrition.testTag))
		onNodeWithTag(RecipeDetailsSection.Nutrition.testTag).performClick()
		onNodeWithTag(RECIPE_DETAILS_CONTENT_TAG)
			.performScrollToNode(hasText("Nutrition estimate"))
		onNodeWithText("Nutrition estimate").assertIsDisplayed()
		onNodeWithTag(RECIPE_DETAILS_CONTENT_TAG)
			.performScrollToNode(hasTestTag(NUTRITION_FACTS_BUTTON_TAG))
		onNodeWithTag(NUTRITION_FACTS_BUTTON_TAG).assertIsDisplayed()
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
	observeCookbookMembershipEvents = ObserveCookbookMembershipEventsUseCase(cookbooksRepository),
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
	recordRecipeActivity = RecordRecipeActivityUseCase(FakeRecipeActivityRepository()),
	recipeId = recipeId,
	sessionKey = sessionKey,
	origin = AnalyticsOrigin.SEARCH.value,
)
