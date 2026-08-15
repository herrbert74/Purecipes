package app.purecipes.feature.main.ui

import app.purecipes.base.kotlin.result.Failure
import app.purecipes.feature.ads.domain.PreCookInterstitialChance
import app.purecipes.feature.ads.domain.repository.AdsRepository
import app.purecipes.feature.ads.domain.usecase.DecidePreCookInterstitialUseCase
import app.purecipes.feature.ads.domain.usecase.ObserveShouldShowAdsUseCase
import app.purecipes.feature.ads.domain.usecase.ShowInterstitialAdUseCase
import app.purecipes.feature.analytics.domain.model.AnalyticsOrigin
import app.purecipes.feature.analytics.domain.model.ConsentState
import app.purecipes.feature.analytics.domain.usecase.LogBreadcrumbUseCase
import app.purecipes.feature.analytics.domain.usecase.RefreshConsentUseCase
import app.purecipes.feature.analytics.domain.usecase.SendHandledExceptionUseCase
import app.purecipes.feature.analytics.domain.usecase.SetAnalyticsUserIdUseCase
import app.purecipes.feature.analytics.domain.usecase.SetCrashCustomValueUseCase
import app.purecipes.feature.analytics.domain.usecase.SetCrashUserIdUseCase
import app.purecipes.feature.analytics.domain.usecase.SetGlobalPropertiesUseCase
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.auth.domain.usecase.ObserveAuthenticationStateUseCase
import app.purecipes.feature.auth.domain.usecase.ValidateSessionUseCase
import app.purecipes.feature.library.domain.repository.CookbookCoverRepository
import app.purecipes.feature.library.domain.usecase.AddFavoriteRecipeUseCase
import app.purecipes.feature.library.domain.usecase.AddRecipeToCookbookUseCase
import app.purecipes.feature.library.domain.usecase.CreateCookbookUseCase
import app.purecipes.feature.library.domain.usecase.DeleteCookbookUseCase
import app.purecipes.feature.library.domain.usecase.GetCookbookCoverImageUrlUseCase
import app.purecipes.feature.library.domain.usecase.GetCookbookRecipesPageUseCase
import app.purecipes.feature.library.domain.usecase.GetCookbooksPageUseCase
import app.purecipes.feature.library.domain.usecase.GetFavoriteRecipesPageUseCase
import app.purecipes.feature.library.domain.usecase.GetRecipeCookbooksUseCase
import app.purecipes.feature.library.domain.usecase.ObserveFavoriteEventsUseCase
import app.purecipes.feature.library.domain.usecase.RemoveFavoriteRecipeUseCase
import app.purecipes.feature.library.ui.LibraryViewModel
import app.purecipes.feature.measurement.domain.usecase.FilterRecipesForMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.GetMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.MarkMeasurementMismatchSeenUseCase
import app.purecipes.feature.measurement.domain.usecase.ObserveMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.ProcessRecipeDetailsForMeasurementPreferencesUseCase
import app.purecipes.feature.recipedetails.domain.usecase.GetRecipeDetailsUseCase
import app.purecipes.feature.recipedetails.ui.RecipeDetailsViewModel
import app.purecipes.feature.search.domain.readiness.SearchReadinessCoordinator
import app.purecipes.feature.search.domain.usecase.GetSearchFiltersUseCase
import app.purecipes.feature.search.domain.usecase.GetUserExcludedIngredientsUseCase
import app.purecipes.feature.search.domain.usecase.GetUserPantryUseCase
import app.purecipes.feature.search.domain.usecase.MatchIngredientInRecipesUseCase
import app.purecipes.feature.search.domain.usecase.SaveSearchFiltersUseCase
import app.purecipes.feature.search.domain.usecase.SearchRecipesUseCase
import app.purecipes.feature.search.domain.usecase.UpdateUserExcludedIngredientsUseCase
import app.purecipes.feature.search.domain.usecase.UpdateUserPantryUseCase
import app.purecipes.feature.search.ui.RecipeSearchViewModel
import app.purecipes.feature.sharing.domain.model.PurecipesLink
import app.purecipes.feature.sharing.domain.repository.CookbookShareRepository
import app.purecipes.feature.sharing.domain.repository.IncomingLinkRepository
import app.purecipes.feature.sharing.domain.repository.ShareRepository
import app.purecipes.feature.sharing.domain.repository.WebLaunchLinkRepository
import app.purecipes.feature.sharing.domain.usecase.CreateCookbookShareUseCase
import app.purecipes.feature.sharing.domain.usecase.ImportCookbookShareUseCase
import app.purecipes.feature.sharing.domain.usecase.ObserveIncomingLinksUseCase
import app.purecipes.feature.sharing.domain.usecase.PublishWebLaunchLinkUseCase
import app.purecipes.feature.sharing.domain.usecase.ShareCookbookUseCase
import app.purecipes.feature.sharing.domain.usecase.ShareRecipeUseCase
import app.purecipes.feature.subscription.domain.usecase.ObservePremiumStatusUseCase
import app.purecipes.feature.subscription.domain.usecase.SyncSubscriptionUserIdUseCase
import app.purecipes.shared.data.config.PurecipesBuildType
import app.purecipes.shared.data.config.PurecipesConfig
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import app.purecipes.shared.testfixtures.fake.FakeAuthenticationRepository
import app.purecipes.shared.testfixtures.fake.FakeConsentRepository
import app.purecipes.shared.testfixtures.fake.FakeCookbooksRepository
import app.purecipes.shared.testfixtures.fake.FakeCrashRepository
import app.purecipes.shared.testfixtures.fake.FakeFavoritesRepository
import app.purecipes.shared.testfixtures.fake.FakeIngredientMatchRepository
import app.purecipes.shared.testfixtures.fake.FakeMeasurementPreferencesRepository
import app.purecipes.shared.testfixtures.fake.FakeMonetisationDebugOverridesRepository
import app.purecipes.shared.testfixtures.fake.FakeRecipeDetailsRepository
import app.purecipes.shared.testfixtures.fake.FakeRecipeSearchFilterRepository
import app.purecipes.shared.testfixtures.fake.FakeRecipeSearchRepository
import app.purecipes.shared.testfixtures.fake.FakeSubscriptionRepository
import app.purecipes.shared.testfixtures.fake.FakeUserExcludedIngredientsRepository
import app.purecipes.shared.testfixtures.fake.FakeUserPantryRepository
import app.purecipes.shared.testfixtures.fake.fakeRecipeDetails
import app.purecipes.shared.testfixtures.fake.fakeTrackScreenViewUseCase
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import kotlinx.coroutines.flow.emptyFlow

internal const val HARDWARE_BACK_TEST_RECIPE_ID = 7

internal const val HARDWARE_BACK_TEST_RECIPE_DESCRIPTION = "Sweet and savory side dish."

internal data class HardwareBackTestEnvironment(
	val mainViewModel: MainViewModel,
	val searchViewModel: RecipeSearchViewModel,
	val recipeDetailsViewModel: RecipeDetailsViewModel,
	val libraryViewModel: LibraryViewModel,
	val analyticsRepository: FakeAnalyticsRepository = FakeAnalyticsRepository(),
	val recipeId: Int = HARDWARE_BACK_TEST_RECIPE_ID,
)

internal fun hardwareBackTestEnvironment(
	analyticsRepository: FakeAnalyticsRepository = FakeAnalyticsRepository(),
): HardwareBackTestEnvironment {
	val recipeId = HARDWARE_BACK_TEST_RECIPE_ID
	return HardwareBackTestEnvironment(
		mainViewModel = mainViewModelForDeviceTest(analyticsRepository = analyticsRepository),
		searchViewModel = recipeSearchViewModelForDeviceTest(
			searchRepository = FakeRecipeSearchRepository(
				result = Ok(
					listOf(
						RecipeSummary(
							id = recipeId,
							title = "Roasted Carrots",
							cuisine = Cuisine.MEDITERRANEAN,
							imageUrl = null,
							totalTime = 35,
						),
					),
				),
			),
		),
		recipeDetailsViewModel = recipeDetailsViewModelForDeviceTest(
			recipeId = recipeId,
			recipeDetailsRepository = FakeRecipeDetailsRepository(
				fakeRecipeDetails(
					id = recipeId,
					title = "Roasted Carrots",
					description = HARDWARE_BACK_TEST_RECIPE_DESCRIPTION,
				),
			),
		),
		libraryViewModel = favoritesViewModelForDeviceTest(),
		analyticsRepository = analyticsRepository,
		recipeId = recipeId,
	)
}

internal fun mainViewModelForDeviceTest(
	analyticsRepository: FakeAnalyticsRepository = FakeAnalyticsRepository(),
): MainViewModel {
	val subscriptionRepository = FakeSubscriptionRepository()
	return MainViewModel(
		observeAuthenticationState = ObserveAuthenticationStateUseCase(FakeAuthenticationRepository()),
		validateSession = ValidateSessionUseCase(FakeAuthenticationRepository()),
		refreshConsent = RefreshConsentUseCase(FakeConsentRepository(ConsentState.NOT_REQUIRED)),
		setAnalyticsUserId = SetAnalyticsUserIdUseCase(analyticsRepository),
		setCrashUserId = SetCrashUserIdUseCase(FakeCrashRepository()),
		setCrashCustomValue = SetCrashCustomValueUseCase(FakeCrashRepository()),
		setGlobalProperties = SetGlobalPropertiesUseCase(analyticsRepository),
		trackScreenView = fakeTrackScreenViewUseCase(analyticsRepository),
		trackEvent = TrackEventUseCase(analyticsRepository),
		syncSubscriptionUserId = SyncSubscriptionUserIdUseCase(subscriptionRepository),
		observePremiumStatus = ObservePremiumStatusUseCase(
			subscriptionRepository,
			FakeMonetisationDebugOverridesRepository(),
		),
		observeIncomingLinks = ObserveIncomingLinksUseCase(emptyIncomingLinkRepositoryForDeviceTest()),
		publishWebLaunchLink = PublishWebLaunchLinkUseCase(
			object : WebLaunchLinkRepository {
				override fun readLaunchUrl(): String? = null
			},
			emptyIncomingLinkRepositoryForDeviceTest(),
		),
		decidePreCookInterstitial = DecidePreCookInterstitialUseCase(
			observeShouldShowAds = ObserveShouldShowAdsUseCase(
				observePremiumStatus = ObservePremiumStatusUseCase(
					subscriptionRepository,
					FakeMonetisationDebugOverridesRepository(),
				),
				monetisationDebugOverrides = FakeMonetisationDebugOverridesRepository(),
			),
			preCookInterstitialChance = PreCookInterstitialChance { false },
		),
		showInterstitialAd = ShowInterstitialAdUseCase(
			object : AdsRepository {
				override fun initialize() = Unit

				override fun showInterstitial(
					onDismissed: () -> Unit,
					onImpression: (() -> Unit)?,
					onClicked: (() -> Unit)?,
				) {
					onDismissed()
				}
			},
		),
		purecipesConfig = object : PurecipesConfig {
			override fun buildType(): PurecipesBuildType = PurecipesBuildType.DEBUG

			override fun versionName(): String = "0.0.0-test"

			override fun versionCode(): Long = 0L
		},
		searchReadiness = SearchReadinessCoordinator(),
		onDeliverPendingIncomingLink = {},
	).also { it.initializeTabBackStacksForTest() }
}

internal fun recipeSearchViewModelForDeviceTest(
	searchRepository: FakeRecipeSearchRepository = FakeRecipeSearchRepository(),
): RecipeSearchViewModel = RecipeSearchViewModel(
	filterRecipesForMeasurementPreferences = FilterRecipesForMeasurementPreferencesUseCase(),
	getMeasurementPreferences = GetMeasurementPreferencesUseCase(FakeMeasurementPreferencesRepository()),
	searchRecipes = SearchRecipesUseCase(searchRepository),
	trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
	logBreadcrumb = LogBreadcrumbUseCase(FakeCrashRepository()),
	sendHandledException = SendHandledExceptionUseCase(FakeCrashRepository()),
	getSearchFilters = GetSearchFiltersUseCase(FakeRecipeSearchFilterRepository()),
	saveSearchFilters = SaveSearchFiltersUseCase(FakeRecipeSearchFilterRepository()),
	getUserPantry = GetUserPantryUseCase(FakeUserPantryRepository()),
	updateUserPantry = UpdateUserPantryUseCase(FakeUserPantryRepository()),
	getUserExcludedIngredients = GetUserExcludedIngredientsUseCase(FakeUserExcludedIngredientsRepository()),
	updateUserExcludedIngredients = UpdateUserExcludedIngredientsUseCase(FakeUserExcludedIngredientsRepository()),
	matchIngredientInRecipes = MatchIngredientInRecipesUseCase(FakeIngredientMatchRepository()),
	searchReadiness = SearchReadinessCoordinator(),
	observePremiumStatus = ObservePremiumStatusUseCase(
		FakeSubscriptionRepository(),
		FakeMonetisationDebugOverridesRepository(),
	),
	initialShowFilterSheet = false,
	sessionKey = null,
)

internal fun recipeDetailsViewModelForDeviceTest(
	recipeId: Int,
	recipeDetailsRepository: FakeRecipeDetailsRepository,
	sessionKey: String? = null,
): RecipeDetailsViewModel = RecipeDetailsViewModel(
	addFavoriteRecipe = AddFavoriteRecipeUseCase(FakeFavoritesRepository()),
	getRecipeDetails = GetRecipeDetailsUseCase(recipeDetailsRepository),
	observeMeasurementPreferences = ObserveMeasurementPreferencesUseCase(FakeMeasurementPreferencesRepository()),
	markMeasurementMismatchSeen = MarkMeasurementMismatchSeenUseCase(FakeMeasurementPreferencesRepository()),
	processRecipeDetailsForMeasurementPreferences = ProcessRecipeDetailsForMeasurementPreferencesUseCase(),
	removeFavoriteRecipe = RemoveFavoriteRecipeUseCase(FakeFavoritesRepository()),
	trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
	logBreadcrumb = LogBreadcrumbUseCase(FakeCrashRepository()),
	sendHandledException = SendHandledExceptionUseCase(FakeCrashRepository()),
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
	sessionKey = sessionKey,
	origin = AnalyticsOrigin.SEARCH.value,
)

internal fun favoritesViewModelForDeviceTest(): LibraryViewModel = LibraryViewModel(
	getFavoriteRecipesPage = GetFavoriteRecipesPageUseCase(FakeFavoritesRepository()),
	getCookbooksPage = GetCookbooksPageUseCase(FakeCookbooksRepository()),
	createCookbook = CreateCookbookUseCase(FakeCookbooksRepository()),
	deleteCookbookUseCase = DeleteCookbookUseCase(FakeCookbooksRepository()),
	getCookbookRecipesPage = GetCookbookRecipesPageUseCase(FakeCookbooksRepository()),
	getCookbookCoverImageUrl = GetCookbookCoverImageUrlUseCase(
		object : CookbookCoverRepository {
			override fun getCookbookCoverImageUrl(
				cookbookId: Int,
				candidateImageUrls: List<String>,
				nowMillis: Long,
				random: kotlin.random.Random,
			): String? = candidateImageUrls.firstOrNull()
		},
	),
	importCookbookShare = ImportCookbookShareUseCase(
		object : CookbookShareRepository {
			override suspend fun createShare(cookbookId: Int) = Err(Failure.ServerError("unused"))

			override suspend fun importShare(token: String) = Err(Failure.ServerError("unused"))
		},
	),
	shareCookbook = ShareCookbookUseCase(
		createCookbookShareUseCase = CreateCookbookShareUseCase(
			object : CookbookShareRepository {
				override suspend fun createShare(cookbookId: Int) = Err(Failure.ServerError("unused"))

				override suspend fun importShare(token: String) = Err(Failure.ServerError("unused"))
			},
		),
		shareRepository = object : ShareRepository {
			override fun shareText(text: String, title: String?) = Unit
		},
	),
	observeFavoriteEvents = ObserveFavoriteEventsUseCase(FakeFavoritesRepository()),
	trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
	sessionKey = "hardware-back-test",
)

private fun emptyIncomingLinkRepositoryForDeviceTest(): IncomingLinkRepository = object : IncomingLinkRepository {
	override fun observeLinks() = emptyFlow<PurecipesLink>()

	override fun deliver(url: String) = Unit
}
