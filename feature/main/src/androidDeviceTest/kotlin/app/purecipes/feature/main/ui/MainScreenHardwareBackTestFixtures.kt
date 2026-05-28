package app.purecipes.feature.main.ui

import app.purecipes.feature.analytics.domain.model.ConsentState
import app.purecipes.feature.analytics.domain.usecase.RefreshConsentUseCase
import app.purecipes.feature.analytics.domain.usecase.SetAnalyticsUserIdUseCase
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.auth.domain.usecase.ObserveAuthenticationStateUseCase
import app.purecipes.feature.favorites.domain.usecase.AddFavoriteRecipeUseCase
import app.purecipes.feature.favorites.domain.usecase.AddRecipeToCookbookUseCase
import app.purecipes.feature.favorites.domain.usecase.CreateCookbookUseCase
import app.purecipes.feature.favorites.domain.usecase.GetCookbooksPageUseCase
import app.purecipes.feature.favorites.domain.usecase.GetRecipeCookbooksUseCase
import app.purecipes.feature.favorites.domain.usecase.RemoveFavoriteRecipeUseCase
import app.purecipes.feature.measurement.domain.usecase.FilterRecipesForMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.GetMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.MarkMeasurementMismatchSeenUseCase
import app.purecipes.feature.measurement.domain.usecase.ProcessRecipeDetailsForMeasurementPreferencesUseCase
import app.purecipes.feature.recipedetails.domain.usecase.GetRecipeDetailsUseCase
import app.purecipes.feature.recipedetails.ui.RecipeDetailsViewModel
import app.purecipes.feature.search.domain.usecase.GetSearchFiltersUseCase
import app.purecipes.feature.search.domain.usecase.GetUserPantryUseCase
import app.purecipes.feature.search.domain.usecase.SaveSearchFiltersUseCase
import app.purecipes.feature.search.domain.usecase.SearchRecipesUseCase
import app.purecipes.feature.search.domain.usecase.UpdateUserPantryUseCase
import app.purecipes.feature.search.ui.RecipeSearchViewModel
import app.purecipes.feature.sharing.domain.model.PurecipesLink
import app.purecipes.feature.sharing.domain.repository.IncomingLinkRepository
import app.purecipes.feature.sharing.domain.repository.ShareRepository
import app.purecipes.feature.sharing.domain.repository.WebLaunchLinkRepository
import app.purecipes.feature.sharing.domain.usecase.ObserveIncomingLinksUseCase
import app.purecipes.feature.sharing.domain.usecase.PublishWebLaunchLinkUseCase
import app.purecipes.feature.sharing.domain.usecase.ShareRecipeUseCase
import app.purecipes.shared.data.config.PurecipesBuildType
import app.purecipes.shared.data.config.PurecipesConfig
import app.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import app.purecipes.shared.testfixtures.fake.FakeAuthenticationRepository
import app.purecipes.shared.testfixtures.fake.FakeConsentRepository
import app.purecipes.shared.testfixtures.fake.FakeCookbooksRepository
import app.purecipes.shared.testfixtures.fake.FakeFavoritesRepository
import app.purecipes.shared.testfixtures.fake.FakeMeasurementPreferencesRepository
import app.purecipes.shared.testfixtures.fake.FakeRecipeDetailsRepository
import app.purecipes.shared.testfixtures.fake.FakeRecipeSearchFilterRepository
import app.purecipes.shared.testfixtures.fake.FakeRecipeSearchRepository
import app.purecipes.shared.testfixtures.fake.FakeUserPantryRepository
import kotlinx.coroutines.flow.emptyFlow

internal const val HARDWARE_BACK_TEST_RECIPE_ID = 7

internal fun mainViewModelForDeviceTest(): MainViewModel = MainViewModel(
	observeAuthenticationState = ObserveAuthenticationStateUseCase(FakeAuthenticationRepository()),
	refreshConsent = RefreshConsentUseCase(FakeConsentRepository(ConsentState.NOT_REQUIRED)),
	setAnalyticsUserId = SetAnalyticsUserIdUseCase(FakeAnalyticsRepository()),
	observeIncomingLinks = ObserveIncomingLinksUseCase(emptyIncomingLinkRepositoryForDeviceTest()),
	publishWebLaunchLink = PublishWebLaunchLinkUseCase(
		object : WebLaunchLinkRepository {
			override fun readLaunchUrl(): String? = null
		},
		emptyIncomingLinkRepositoryForDeviceTest(),
	),
	purecipesConfig = object : PurecipesConfig {
		override fun buildType(): PurecipesBuildType = PurecipesBuildType.DEBUG
	},
	onDeliverPendingIncomingLink = {},
)

internal fun recipeSearchViewModelForDeviceTest(
	searchRepository: FakeRecipeSearchRepository = FakeRecipeSearchRepository(),
): RecipeSearchViewModel = RecipeSearchViewModel(
	filterRecipesForMeasurementPreferences = FilterRecipesForMeasurementPreferencesUseCase(),
	getMeasurementPreferences = GetMeasurementPreferencesUseCase(FakeMeasurementPreferencesRepository()),
	searchRecipes = SearchRecipesUseCase(searchRepository),
	trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
	getSearchFilters = GetSearchFiltersUseCase(FakeRecipeSearchFilterRepository()),
	saveSearchFilters = SaveSearchFiltersUseCase(FakeRecipeSearchFilterRepository()),
	getUserPantry = GetUserPantryUseCase(FakeUserPantryRepository()),
	updateUserPantry = UpdateUserPantryUseCase(FakeUserPantryRepository()),
	initialShowFilterSheet = false,
	sessionKey = null,
)

internal fun recipeDetailsViewModelForDeviceTest(
	recipeId: Int,
	recipeDetailsRepository: FakeRecipeDetailsRepository,
): RecipeDetailsViewModel = RecipeDetailsViewModel(
	addFavoriteRecipe = AddFavoriteRecipeUseCase(FakeFavoritesRepository()),
	getRecipeDetails = GetRecipeDetailsUseCase(recipeDetailsRepository),
	getMeasurementPreferences = GetMeasurementPreferencesUseCase(FakeMeasurementPreferencesRepository()),
	markMeasurementMismatchSeen = MarkMeasurementMismatchSeenUseCase(FakeMeasurementPreferencesRepository()),
	processRecipeDetailsForMeasurementPreferences = ProcessRecipeDetailsForMeasurementPreferencesUseCase(),
	removeFavoriteRecipe = RemoveFavoriteRecipeUseCase(FakeFavoritesRepository()),
	trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
	getRecipeCookbooks = GetRecipeCookbooksUseCase(FakeCookbooksRepository()),
	getCookbooksPage = GetCookbooksPageUseCase(FakeCookbooksRepository()),
	createCookbook = CreateCookbookUseCase(FakeCookbooksRepository()),
	addRecipeToCookbook = AddRecipeToCookbookUseCase(FakeCookbooksRepository()),
	shareRecipe = ShareRecipeUseCase(
		object : ShareRepository {
			override fun shareText(text: String, title: String?) = Unit
		},
	),
	recipeId = recipeId,
	sessionKey = null,
)

private fun emptyIncomingLinkRepositoryForDeviceTest(): IncomingLinkRepository = object : IncomingLinkRepository {
	override fun observeLinks() = emptyFlow<PurecipesLink>()

	override fun deliver(url: String) = Unit
}
