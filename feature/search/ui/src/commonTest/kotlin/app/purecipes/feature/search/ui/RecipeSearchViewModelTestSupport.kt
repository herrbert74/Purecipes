package app.purecipes.feature.search.ui

import app.purecipes.feature.analytics.domain.usecase.LogBreadcrumbUseCase
import app.purecipes.feature.analytics.domain.usecase.SendHandledExceptionUseCase
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.library.domain.usecase.ObserveFavoriteEventsUseCase
import app.purecipes.feature.measurement.domain.usecase.FilterRecipesForMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.GetMeasurementPreferencesUseCase
import app.purecipes.feature.search.domain.readiness.SearchReadinessCoordinator
import app.purecipes.feature.search.domain.repository.RecipeSearchFilterRepository
import app.purecipes.feature.search.domain.repository.RecipeSearchRepository
import app.purecipes.feature.search.domain.usecase.GetSearchFiltersUseCase
import app.purecipes.feature.search.domain.usecase.GetSearchPreferencesUseCase
import app.purecipes.feature.search.domain.usecase.GetUserExcludedIngredientsUseCase
import app.purecipes.feature.search.domain.usecase.GetUserPantryUseCase
import app.purecipes.feature.search.domain.usecase.MatchIngredientInRecipesUseCase
import app.purecipes.feature.search.domain.usecase.ObserveSearchPreferencesUseCase
import app.purecipes.feature.search.domain.usecase.SaveSearchFiltersUseCase
import app.purecipes.feature.search.domain.usecase.SearchRecipesUseCase
import app.purecipes.feature.search.domain.usecase.UpdateUserExcludedIngredientsUseCase
import app.purecipes.feature.search.domain.usecase.UpdateUserPantryUseCase
import app.purecipes.feature.subscription.domain.model.SubscriptionState
import app.purecipes.feature.subscription.domain.model.SubscriptionStatus
import app.purecipes.feature.subscription.domain.usecase.ObservePremiumStatusUseCase
import app.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import app.purecipes.shared.testfixtures.fake.FakeCrashRepository
import app.purecipes.shared.testfixtures.fake.FakeFavoritesRepository
import app.purecipes.shared.testfixtures.fake.FakeIngredientMatchRepository
import app.purecipes.shared.testfixtures.fake.FakeMeasurementPreferencesRepository
import app.purecipes.shared.testfixtures.fake.FakeMonetisationDebugOverridesRepository
import app.purecipes.shared.testfixtures.fake.FakeRecipeSearchFilterRepository
import app.purecipes.shared.testfixtures.fake.FakeRecipeSearchRepository
import app.purecipes.shared.testfixtures.fake.FakeSearchPreferencesRepository
import app.purecipes.shared.testfixtures.fake.FakeSubscriptionRepository
import app.purecipes.shared.testfixtures.fake.FakeUserExcludedIngredientsRepository
import app.purecipes.shared.testfixtures.fake.FakeUserPantryRepository
import com.github.michaelbull.result.Ok

internal object RecipeSearchViewModelTestSupport {

	fun makeViewModel(
		searchRepository: RecipeSearchRepository = FakeRecipeSearchRepository(Ok(emptyList())),
		filterRepository: RecipeSearchFilterRepository = FakeRecipeSearchFilterRepository(),
		pantryRepository: FakeUserPantryRepository = FakeUserPantryRepository(),
		excludedIngredientsRepository: FakeUserExcludedIngredientsRepository =
			FakeUserExcludedIngredientsRepository(),
		ingredientMatchRepository: FakeIngredientMatchRepository = FakeIngredientMatchRepository(),
		searchReadiness: SearchReadinessCoordinator = SearchReadinessCoordinator(),
		subscriptionRepository: FakeSubscriptionRepository = FakeSubscriptionRepository(),
		analyticsRepository: FakeAnalyticsRepository = FakeAnalyticsRepository(),
		favoritesRepository: FakeFavoritesRepository = FakeFavoritesRepository(),
		searchPreferencesRepository: FakeSearchPreferencesRepository = FakeSearchPreferencesRepository(),
		sessionKey: String? = null,
	) = RecipeSearchViewModel(
		filterRecipesForMeasurementPreferences = FilterRecipesForMeasurementPreferencesUseCase(),
		getMeasurementPreferences = GetMeasurementPreferencesUseCase(FakeMeasurementPreferencesRepository()),
		searchRecipes = SearchRecipesUseCase(searchRepository),
		trackEvent = TrackEventUseCase(analyticsRepository),
		logBreadcrumb = LogBreadcrumbUseCase(FakeCrashRepository()),
		sendHandledException = SendHandledExceptionUseCase(FakeCrashRepository()),
		getSearchFilters = GetSearchFiltersUseCase(filterRepository),
		saveSearchFilters = SaveSearchFiltersUseCase(filterRepository),
		getSearchPreferences = GetSearchPreferencesUseCase(searchPreferencesRepository),
		observeSearchPreferences = ObserveSearchPreferencesUseCase(searchPreferencesRepository),
		getUserPantry = GetUserPantryUseCase(pantryRepository),
		updateUserPantry = UpdateUserPantryUseCase(pantryRepository),
		getUserExcludedIngredients = GetUserExcludedIngredientsUseCase(excludedIngredientsRepository),
		updateUserExcludedIngredients = UpdateUserExcludedIngredientsUseCase(excludedIngredientsRepository),
		matchIngredientInRecipes = MatchIngredientInRecipesUseCase(ingredientMatchRepository),
		searchReadiness = searchReadiness,
		observeFavoriteEvents = ObserveFavoriteEventsUseCase(favoritesRepository),
		observePremiumStatus = ObservePremiumStatusUseCase(
			subscriptionRepository,
			FakeMonetisationDebugOverridesRepository(),
		),
		initialShowFilterSheet = false,
		sessionKey = sessionKey,
	)

	fun premiumSubscriptionState(): SubscriptionState = SubscriptionState(
		status = SubscriptionStatus.PREMIUM,
		isActive = true,
		expirationInstant = null,
		trialActive = false,
	)
}
