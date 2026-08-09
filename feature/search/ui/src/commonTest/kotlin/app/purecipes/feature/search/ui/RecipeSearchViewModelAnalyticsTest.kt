package app.purecipes.feature.search.ui

import app.purecipes.feature.analytics.domain.model.AnalyticsEvent
import app.purecipes.feature.analytics.domain.model.AnalyticsOrigin
import app.purecipes.feature.analytics.domain.model.AnalyticsPremiumFeature
import app.purecipes.feature.analytics.domain.model.SearchPerformedContext
import app.purecipes.shared.domain.model.CalorieRange
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.MealType
import app.purecipes.shared.domain.model.NearMissRecipe
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.domain.model.SearchFilters
import app.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import app.purecipes.shared.testfixtures.fake.FakeRecipeSearchFilterRepository
import app.purecipes.shared.testfixtures.fake.FakeRecipeSearchRepository
import app.purecipes.shared.testfixtures.fake.FakeSubscriptionRepository
import app.purecipes.shared.testfixtures.fake.FakeUserExcludedIngredientsRepository
import app.purecipes.shared.testfixtures.fake.FakeUserPantryRepository
import app.purecipes.shared.testfixtures.runViewModelTest
import com.github.michaelbull.result.Ok
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeSearchViewModelAnalyticsTest {

	@Test
	fun `search tracks empty near miss analytics on first page`() = runViewModelTest {
		val nearMiss = NearMissRecipe(
			recipe = RecipeSummary(
				id = 9,
				title = "Almost Stew",
				cuisine = Cuisine.ITALIAN,
				imageUrl = null,
				totalTime = 30,
			),
			missingIngredient = "Basil",
		)
		val analyticsRepository = FakeAnalyticsRepository()
		val repository = FakeRecipeSearchRepository(
			result = Ok(emptyList()),
			totalMatches = 0,
			nearMissRecipes = listOf(nearMiss),
		)
		RecipeSearchViewModelTestSupport.makeViewModel(
			searchRepository = repository,
			analyticsRepository = analyticsRepository,
		)

		advanceUntilIdle()

		analyticsRepository.trackedEvents.single() shouldBe AnalyticsEvent.SearchPerformed.from(
			SearchPerformedContext(
				query = "",
				resultCount = 0,
				filters = SearchFilters(),
				pantryCount = 0,
				excludedCount = 0,
				keyIngredientCount = 0,
				nearMissCount = 1,
				isPremiumUser = false,
			),
		)
	}

	@Test
	fun `search tracks filter pantry and near miss analytics on first page`() = runViewModelTest {
		val nearMiss = NearMissRecipe(
			recipe = RecipeSummary(
				id = 9,
				title = "Almost Stew",
				cuisine = Cuisine.ITALIAN,
				imageUrl = null,
				totalTime = 30,
			),
			missingIngredient = "Basil",
		)
		val analyticsRepository = FakeAnalyticsRepository()
		val pantryRepository = FakeUserPantryRepository(setOf("Tomato", "Onion"))
		val excludedIngredientsRepository = FakeUserExcludedIngredientsRepository(setOf("Peanut"))
		val filters = SearchFilters(
			cuisines = setOf(Cuisine.ITALIAN),
			mealTypes = setOf(MealType.DINNER),
			calorieRanges = setOf(CalorieRange.LOW),
		)
		val repository = FakeRecipeSearchRepository(
			result = Ok(emptyList()),
			totalMatches = 0,
			nearMissRecipes = listOf(nearMiss),
		)
		val viewModel = RecipeSearchViewModelTestSupport.makeViewModel(
			searchRepository = repository,
			filterRepository = FakeRecipeSearchFilterRepository(),
			pantryRepository = pantryRepository,
			excludedIngredientsRepository = excludedIngredientsRepository,
			subscriptionRepository = FakeSubscriptionRepository(
				initialState = RecipeSearchViewModelTestSupport.premiumSubscriptionState(),
			),
			analyticsRepository = analyticsRepository,
			sessionKey = "user-1",
		)

		advanceUntilIdle()

		viewModel.onFiltersChange(filters)
		viewModel.onKeyIngredientsChange(setOf("Chicken"))
		viewModel.searchNow()
		advanceUntilIdle()

		analyticsRepository.trackedEvents.last() shouldBe AnalyticsEvent.SearchPerformed.from(
			SearchPerformedContext(
				query = "",
				resultCount = 0,
				filters = filters,
				pantryCount = 2,
				excludedCount = 1,
				keyIngredientCount = 1,
				nearMissCount = 1,
				isPremiumUser = true,
			),
		)
	}

	@Test
	fun `premium feature blocked tracks analytics and closes filter sheet`() = runViewModelTest {
		val analyticsRepository = FakeAnalyticsRepository()
		val viewModel = RecipeSearchViewModelTestSupport.makeViewModel(
			analyticsRepository = analyticsRepository,
		)
		advanceUntilIdle()
		viewModel.onFilterButtonClick()
		analyticsRepository.trackedEvents.clear()

		viewModel.onPremiumFeatureBlocked(AnalyticsPremiumFeature.KEY_INGREDIENTS)

		viewModel.isFilterSheetVisible shouldBe false
		analyticsRepository.trackedEvents.single() shouldBe AnalyticsEvent.PremiumFeatureBlocked(
			feature = AnalyticsPremiumFeature.KEY_INGREDIENTS,
			origin = AnalyticsOrigin.SEARCH,
		)
	}
}
