package app.purecipes.feature.search.ui

import app.purecipes.base.kotlin.result.Failure
import app.purecipes.feature.analytics.domain.usecase.LogBreadcrumbUseCase
import app.purecipes.feature.analytics.domain.usecase.SendHandledExceptionUseCase
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.favorites.domain.model.FavoriteEvent
import app.purecipes.feature.favorites.domain.usecase.ObserveFavoriteEventsUseCase
import app.purecipes.feature.measurement.domain.usecase.FilterRecipesForMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.GetMeasurementPreferencesUseCase
import app.purecipes.feature.search.domain.readiness.SearchReadinessCoordinator
import app.purecipes.feature.search.domain.usecase.GetSearchFiltersUseCase
import app.purecipes.feature.search.domain.usecase.GetUserExcludedIngredientsUseCase
import app.purecipes.feature.search.domain.usecase.GetUserPantryUseCase
import app.purecipes.feature.search.domain.usecase.MatchIngredientInRecipesUseCase
import app.purecipes.feature.search.domain.usecase.SaveSearchFiltersUseCase
import app.purecipes.feature.search.domain.usecase.SearchRecipesUseCase
import app.purecipes.feature.search.domain.usecase.UpdateUserExcludedIngredientsUseCase
import app.purecipes.feature.search.domain.usecase.UpdateUserPantryUseCase
import app.purecipes.feature.subscription.domain.usecase.ObservePremiumStatusUseCase
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.IngredientMatchCount
import app.purecipes.shared.domain.model.IngredientMatchResponse
import app.purecipes.shared.domain.model.NearMissRecipe
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.domain.model.SearchFilters
import app.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import app.purecipes.shared.testfixtures.fake.FakeCrashRepository
import app.purecipes.shared.testfixtures.fake.FakeFavoritesRepository
import app.purecipes.shared.testfixtures.fake.FakeIngredientMatchRepository
import app.purecipes.shared.testfixtures.fake.FakeMeasurementPreferencesRepository
import app.purecipes.shared.testfixtures.fake.FakeMonetisationDebugOverridesRepository
import app.purecipes.shared.testfixtures.fake.FakeRecipeSearchFilterRepository
import app.purecipes.shared.testfixtures.fake.FakeRecipeSearchRepository
import app.purecipes.shared.testfixtures.fake.FakeSubscriptionRepository
import app.purecipes.shared.testfixtures.fake.FakeUserExcludedIngredientsRepository
import app.purecipes.shared.testfixtures.fake.FakeUserPantryRepository
import app.purecipes.shared.testfixtures.runViewModelTest
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeSearchViewModelTest {

	@Test
	fun `search loads recipes on init`() = runViewModelTest {
		val repository = FakeRecipeSearchRepository(
			result = Ok(
				listOf(
					RecipeSummary(
						id = 7,
						title = "Tomato Pasta",
						cuisine = Cuisine.ITALIAN,
						imageUrl = null,
						totalTime = 20,
					),
				),
			),
		)
		val viewModel = RecipeSearchViewModelTestSupport.makeViewModel(searchRepository = repository)

		advanceUntilIdle()

		repository.queries shouldBe listOf("")
		viewModel.recipes.size shouldBe 1
		viewModel.recipes.single().title shouldBe "Tomato Pasta"
		viewModel.isSearching shouldBe false
		viewModel.isSearchBarActive shouldBe false
		viewModel.errorMessage shouldBe null
	}

	@Test
	fun `favorite added event marks matching search result as favorite`() = runViewModelTest {
		val favoritesRepository = FakeFavoritesRepository()
		val repository = FakeRecipeSearchRepository(
			result = Ok(
				listOf(
					RecipeSummary(
						id = 7,
						title = "Tomato Pasta",
						cuisine = Cuisine.ITALIAN,
						imageUrl = null,
						totalTime = 20,
					),
				),
			),
		)
		val viewModel = RecipeSearchViewModelTestSupport.makeViewModel(
			searchRepository = repository,
			favoritesRepository = favoritesRepository,
			sessionKey = "session",
		)
		advanceUntilIdle()
		viewModel.recipes.single().isFavorite shouldBe false

		favoritesRepository.emitFavoriteEvent(FavoriteEvent.Added(7))
		advanceUntilIdle()

		viewModel.recipes.single().isFavorite shouldBe true
	}

	@Test
	fun `favorite removed event clears favorite on matching search result`() = runViewModelTest {
		val favoritesRepository = FakeFavoritesRepository()
		val repository = FakeRecipeSearchRepository(
			result = Ok(
				listOf(
					RecipeSummary(
						id = 7,
						title = "Tomato Pasta",
						cuisine = Cuisine.ITALIAN,
						imageUrl = null,
						totalTime = 20,
						isFavorite = true,
					),
				),
			),
			nearMissRecipes = listOf(
				NearMissRecipe(
					recipe = RecipeSummary(
						id = 9,
						title = "Almost Stew",
						cuisine = Cuisine.ITALIAN,
						imageUrl = null,
						totalTime = 30,
						isFavorite = true,
					),
					missingIngredient = "Basil",
				),
			),
		)
		val viewModel = RecipeSearchViewModelTestSupport.makeViewModel(
			searchRepository = repository,
			favoritesRepository = favoritesRepository,
			sessionKey = "session",
		)
		advanceUntilIdle()

		favoritesRepository.emitFavoriteEvent(FavoriteEvent.Removed(7))
		advanceUntilIdle()
		favoritesRepository.emitFavoriteEvent(FavoriteEvent.Removed(9))
		advanceUntilIdle()

		viewModel.recipes.single().isFavorite shouldBe false
		viewModel.nearMissRecipes.single().recipe.isFavorite shouldBe false
	}

	@Test
	fun `reports readiness once the first page finishes loading`() = runViewModelTest {
		val readiness = SearchReadinessCoordinator()
		val viewModel = RecipeSearchViewModelTestSupport.makeViewModel(searchReadiness = readiness)

		readiness.isReady.value shouldBe false
		advanceUntilIdle()

		readiness.isReady.value shouldBe true
		viewModel.isSearching shouldBe false
	}

	@Test
	fun `reports readiness even when the first page fails`() = runViewModelTest {
		val readiness = SearchReadinessCoordinator()
		RecipeSearchViewModelTestSupport.makeViewModel(
			searchRepository = FakeRecipeSearchRepository(result = Err(Failure.ServerError("Search failed"))),
			searchReadiness = readiness,
		)

		advanceUntilIdle()

		readiness.isReady.value shouldBe true
	}

	@Test
	fun `search now keeps search bar expanded when already expanded`() = runViewModelTest {
		val repository = FakeRecipeSearchRepository(result = Ok(emptyList()))
		val viewModel = RecipeSearchViewModelTestSupport.makeViewModel(searchRepository = repository)
		advanceUntilIdle()

		viewModel.onSearchBarExpandedChange(true)
		viewModel.searchNow()
		advanceUntilIdle()

		viewModel.isSearchBarActive shouldBe true
	}

	@Test
	fun `search exposes repository error message`() = runViewModelTest {
		val repository = FakeRecipeSearchRepository(
			result = Err(Failure.ServerError("Search failed")),
		)
		val viewModel = RecipeSearchViewModelTestSupport.makeViewModel(searchRepository = repository)

		advanceUntilIdle()

		viewModel.recipes.isEmpty() shouldBe true
		viewModel.errorMessage shouldBe "Search failed"
	}

	@Test
	fun `search populates near miss recipes when main results are empty`() = runViewModelTest {
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
		val repository = FakeRecipeSearchRepository(
			result = Ok(emptyList()),
			totalMatches = 0,
			nearMissRecipes = listOf(nearMiss),
		)
		val viewModel = RecipeSearchViewModelTestSupport.makeViewModel(searchRepository = repository)

		advanceUntilIdle()

		viewModel.totalMatches shouldBe 0
		viewModel.recipes.isEmpty() shouldBe true
		viewModel.nearMissRecipes.single() shouldBe nearMiss
	}

	@Test
	fun `search populates near miss recipes when main results are sparse`() = runViewModelTest {
		val mainResult = RecipeSummary(
			id = 1,
			title = "Chicken Tomato Stew",
			cuisine = Cuisine.ITALIAN,
			imageUrl = null,
			totalTime = 30,
		)
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
		val repository = FakeRecipeSearchRepository(
			result = Ok(listOf(mainResult)),
			totalMatches = 1,
			nearMissRecipes = listOf(nearMiss),
		)
		val viewModel = RecipeSearchViewModelTestSupport.makeViewModel(searchRepository = repository)

		advanceUntilIdle()

		viewModel.totalMatches shouldBe 1
		viewModel.recipes.single() shouldBe mainResult
		viewModel.nearMissRecipes.single() shouldBe nearMiss
	}

	@Test
	fun `search stores total matches from paged response`() = runViewModelTest {
		val repository = FakeRecipeSearchRepository(
			result = Ok(
				listOf(
					RecipeSummary(
						id = 11,
						title = "Page Result",
						cuisine = Cuisine.FRENCH,
						imageUrl = null,
						totalTime = 25,
					),
				),
			),
			totalMatches = 37,
		)
		val viewModel = RecipeSearchViewModelTestSupport.makeViewModel(searchRepository = repository)

		advanceUntilIdle()

		viewModel.totalMatches shouldBe 37
		viewModel.recipes.size shouldBe 1
	}

	@Test
	fun `search now sends updated query and first page request`() = runViewModelTest {
		val repository = FakeRecipeSearchRepository(result = Ok(emptyList()))
		val viewModel = RecipeSearchViewModelTestSupport.makeViewModel(searchRepository = repository)
		advanceUntilIdle()

		viewModel.onSearchQueryChange("chicken")
		viewModel.searchNow()
		advanceUntilIdle()

		repository.lastQuery shouldBe "chicken"
		repository.lastPageNumber shouldBe 1
		repository.lastPageSize shouldBe 20
	}

	@Test
	fun `init uses default filters when saved filters are empty`() = runViewModelTest {
		val filterRepository = FakeRecipeSearchFilterRepository(savedFilters = SearchFilters())
		val viewModel = RecipeSearchViewModelTestSupport.makeViewModel(filterRepository = filterRepository)

		advanceUntilIdle()

		viewModel.activeFilters.isEmpty shouldBe true
		viewModel.activeFilters shouldBe SearchFilters.default()
	}

	@Test
	fun `init uses saved filters when they are not empty`() = runViewModelTest {
		val saved = SearchFilters(cuisines = setOf(Cuisine.ITALIAN))
		val filterRepository = FakeRecipeSearchFilterRepository(savedFilters = saved)
		val viewModel = RecipeSearchViewModelTestSupport.makeViewModel(filterRepository = filterRepository)

		advanceUntilIdle()

		viewModel.activeFilters shouldBe saved
	}

	@Test
	fun `onFiltersChange updates active filters without saving`() = runViewModelTest {
		val filterRepository = FakeRecipeSearchFilterRepository(savedFilters = SearchFilters())
		val viewModel = RecipeSearchViewModelTestSupport.makeViewModel(filterRepository = filterRepository)
		advanceUntilIdle()
		val savedAfterInit = filterRepository.savedFilters

		val newFilters = SearchFilters(cuisines = setOf(Cuisine.CHINESE))
		viewModel.onFiltersChange(newFilters)
		advanceUntilIdle()

		viewModel.activeFilters shouldBe newFilters
		filterRepository.savedFilters shouldBe savedAfterInit
	}

	@Test
	fun `onFiltersChange does not trigger a new search`() = runViewModelTest {
		val searchRepository = FakeRecipeSearchRepository(result = Ok(emptyList()))
		val viewModel = RecipeSearchViewModelTestSupport.makeViewModel(searchRepository = searchRepository)
		advanceUntilIdle()
		val searchCountAfterInit = searchRepository.queries.size

		viewModel.onFiltersChange(SearchFilters(cuisines = setOf(Cuisine.FRENCH)))
		advanceUntilIdle()

		searchRepository.queries.size shouldBe searchCountAfterInit
	}

	@Test
	fun `filter sheet is hidden by default`() = runViewModelTest {
		val viewModel = RecipeSearchViewModelTestSupport.makeViewModel()

		viewModel.isFilterSheetVisible shouldBe false
	}

	@Test
	fun `onFilterButtonClick shows the filter sheet`() = runViewModelTest {
		val viewModel = RecipeSearchViewModelTestSupport.makeViewModel()

		viewModel.onFilterButtonClick()

		viewModel.isFilterSheetVisible shouldBe true
	}

	@Test
	fun `navigating to paywall from filters reopens sheet when search becomes visible`() = runViewModelTest {
		val viewModel = RecipeSearchViewModelTestSupport.makeViewModel()
		advanceUntilIdle()

		viewModel.onFilterButtonClick()
		viewModel.onNavigateToPaywall()

		viewModel.isFilterSheetVisible shouldBe false

		viewModel.onSearchContentVisible()

		viewModel.isFilterSheetVisible shouldBe true
	}

	@Test
	fun `filter sheet dismiss during paywall navigation does not apply filters`() = runViewModelTest {
		val searchRepository = FakeRecipeSearchRepository(result = Ok(emptyList()))
		val viewModel = RecipeSearchViewModelTestSupport.makeViewModel(searchRepository = searchRepository)
		advanceUntilIdle()
		val searchCountAfterInit = searchRepository.queries.size

		viewModel.onFilterButtonClick()
		viewModel.onFiltersChange(SearchFilters(cuisines = setOf(Cuisine.ITALIAN)))
		viewModel.onNavigateToPaywall()
		viewModel.onFilterSheetDismiss()
		advanceUntilIdle()

		searchRepository.queries.size shouldBe searchCountAfterInit
		viewModel.onSearchContentVisible()
		viewModel.isFilterSheetVisible shouldBe true
		viewModel.activeFilters shouldBe SearchFilters(cuisines = setOf(Cuisine.ITALIAN))
	}

	@Test
	fun `init shows filter sheet when initialShowFilterSheet is true`() = runViewModelTest {
		val viewModel = RecipeSearchViewModel(
			filterRecipesForMeasurementPreferences = FilterRecipesForMeasurementPreferencesUseCase(),
			getMeasurementPreferences = GetMeasurementPreferencesUseCase(FakeMeasurementPreferencesRepository()),
			searchRecipes = SearchRecipesUseCase(FakeRecipeSearchRepository(Ok(emptyList()))),
			trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
			logBreadcrumb = LogBreadcrumbUseCase(FakeCrashRepository()),
			sendHandledException = SendHandledExceptionUseCase(FakeCrashRepository()),
			getSearchFilters = GetSearchFiltersUseCase(FakeRecipeSearchFilterRepository()),
			saveSearchFilters = SaveSearchFiltersUseCase(FakeRecipeSearchFilterRepository()),
			getUserPantry = GetUserPantryUseCase(FakeUserPantryRepository()),
			updateUserPantry = UpdateUserPantryUseCase(FakeUserPantryRepository()),
			getUserExcludedIngredients = GetUserExcludedIngredientsUseCase(FakeUserExcludedIngredientsRepository()),
			updateUserExcludedIngredients = UpdateUserExcludedIngredientsUseCase(
				FakeUserExcludedIngredientsRepository(),
			),
			matchIngredientInRecipes = MatchIngredientInRecipesUseCase(FakeIngredientMatchRepository()),
			searchReadiness = SearchReadinessCoordinator(),
			observeFavoriteEvents = ObserveFavoriteEventsUseCase(FakeFavoritesRepository()),
			observePremiumStatus = ObservePremiumStatusUseCase(
				FakeSubscriptionRepository(),
				FakeMonetisationDebugOverridesRepository(),
			),
			initialShowFilterSheet = true,
			sessionKey = null,
		)

		advanceUntilIdle()

		viewModel.isFilterSheetVisible shouldBe true
	}

	@Test
	fun `onFilterSheetDismiss hides the filter sheet`() = runViewModelTest {
		val viewModel = RecipeSearchViewModelTestSupport.makeViewModel()
		viewModel.onFilterButtonClick()

		viewModel.onFilterSheetDismiss()

		viewModel.isFilterSheetVisible shouldBe false
	}

	@Test
	fun `onFilterSheetDismiss saves filters and triggers search when filters changed`() = runViewModelTest {
		val filterRepository = FakeRecipeSearchFilterRepository(savedFilters = SearchFilters())
		val searchRepository = FakeRecipeSearchRepository(result = Ok(emptyList()))
		val viewModel = RecipeSearchViewModelTestSupport.makeViewModel(
			searchRepository = searchRepository,
			filterRepository = filterRepository,
		)
		advanceUntilIdle()
		val searchCountAfterInit = searchRepository.queries.size

		val newFilters = SearchFilters(cuisines = setOf(Cuisine.ITALIAN))
		viewModel.onFiltersChange(newFilters)
		viewModel.onFilterSheetDismiss()
		advanceUntilIdle()

		filterRepository.savedFilters shouldBe newFilters
		searchRepository.queries.size shouldBe searchCountAfterInit + 1
	}

	@Test
	fun `onFilterSheetDismiss updates pantry and triggers search when pantry changed`() = runViewModelTest {
		val pantryRepository = FakeUserPantryRepository(setOf("Chicken"))
		val searchRepository = FakeRecipeSearchRepository(result = Ok(emptyList()))
		val viewModel = RecipeSearchViewModelTestSupport.makeViewModel(
			searchRepository = searchRepository,
			pantryRepository = pantryRepository,
		)
		advanceUntilIdle()
		val searchCountAfterInit = searchRepository.queries.size

		viewModel.onIngredientSelectionChange(setOf("Chicken", "Tomato"), emptySet())
		viewModel.onFilterSheetDismiss()
		advanceUntilIdle()

		pantryRepository.getPantry() shouldBe setOf("Chicken", "Tomato")
		searchRepository.queries.size shouldBe searchCountAfterInit + 1
	}

	@Test
	fun `onFilterSheetDismiss reverts pantry and shows error when pantry update fails`() = runViewModelTest {
		val pantryRepository = FakeUserPantryRepository(
			pantry = setOf("Chicken"),
			updateFailure = Failure.ServerError("Something went wrong. Please try again."),
		)
		val searchRepository = FakeRecipeSearchRepository(result = Ok(emptyList()))
		val viewModel = RecipeSearchViewModelTestSupport.makeViewModel(
			searchRepository = searchRepository,
			pantryRepository = pantryRepository,
			sessionKey = "user-1",
		)
		advanceUntilIdle()
		val searchCountAfterInit = searchRepository.queries.size

		viewModel.onIngredientSelectionChange(setOf("Chicken Thighs"), emptySet())
		viewModel.onFilterSheetDismiss()
		advanceUntilIdle()

		viewModel.pantryIngredients shouldBe setOf("Chicken")
		viewModel.errorMessage shouldBe "Something went wrong. Please try again."
		pantryRepository.getPantry() shouldBe setOf("Chicken")
		searchRepository.queries.size shouldBe searchCountAfterInit + 1
	}

	@Test
	fun `onFilterSheetDismiss updates excluded ingredients and triggers search when excluded changed`() =
		runViewModelTest {
			val excludedRepository = FakeUserExcludedIngredientsRepository(setOf("Garlic"))
			val searchRepository = FakeRecipeSearchRepository(result = Ok(emptyList()))
			val viewModel = RecipeSearchViewModelTestSupport.makeViewModel(
				searchRepository = searchRepository,
				excludedIngredientsRepository = excludedRepository,
			)
			advanceUntilIdle()
			val searchCountAfterInit = searchRepository.queries.size

			viewModel.onIngredientSelectionChange(emptySet(), setOf("Garlic", "Peanut"))
			viewModel.onFilterSheetDismiss()
			advanceUntilIdle()

			excludedRepository.getExcludedIngredients() shouldBe setOf("Garlic", "Peanut")
			searchRepository.queries.size shouldBe searchCountAfterInit + 1
		}

	@Test
	fun `onIngredientSelectionChange keeps pantry and excluded ingredients mutually exclusive`() = runViewModelTest {
		val viewModel = RecipeSearchViewModelTestSupport.makeViewModel()
		advanceUntilIdle()

		viewModel.onIngredientSelectionChange(setOf("Chicken"), emptySet())
		viewModel.pantryIngredients shouldBe setOf("Chicken")
		viewModel.excludedIngredients shouldBe emptySet()

		viewModel.onIngredientSelectionChange(emptySet(), setOf("Chicken"))
		viewModel.pantryIngredients shouldBe emptySet()
		viewModel.excludedIngredients shouldBe setOf("Chicken")
	}

	@Test
	fun `onFilterSheetDismiss does not save or search when filters are unchanged`() = runViewModelTest {
		val saved = SearchFilters(cuisines = setOf(Cuisine.ITALIAN))
		val filterRepository = FakeRecipeSearchFilterRepository(savedFilters = saved)
		val searchRepository = FakeRecipeSearchRepository(result = Ok(emptyList()))
		val viewModel = RecipeSearchViewModelTestSupport.makeViewModel(
			searchRepository = searchRepository,
			filterRepository = filterRepository,
		)
		advanceUntilIdle()
		val searchCountAfterInit = searchRepository.queries.size
		val savedAfterInit = filterRepository.savedFilters

		viewModel.onFilterButtonClick()
		viewModel.onFilterSheetDismiss()
		advanceUntilIdle()

		filterRepository.savedFilters shouldBe savedAfterInit
		searchRepository.queries.size shouldBe searchCountAfterInit
	}

	@Test
	fun `onSessionKeyChanged reloads filters pantry excluded ingredients and searches`() = runViewModelTest {
		val filterRepository = FakeRecipeSearchFilterRepository(savedFilters = SearchFilters())
		val pantryRepository = FakeUserPantryRepository(setOf("Chicken"))
		val excludedRepository = FakeUserExcludedIngredientsRepository(setOf("Garlic"))
		val searchRepository = FakeRecipeSearchRepository(result = Ok(emptyList()))
		val viewModel = RecipeSearchViewModelTestSupport.makeViewModel(
			searchRepository = searchRepository,
			filterRepository = filterRepository,
			pantryRepository = pantryRepository,
			excludedIngredientsRepository = excludedRepository,
		)
		advanceUntilIdle()
		val searchCountAfterInit = searchRepository.queries.size
		viewModel.pantryIngredients shouldBe emptySet()
		viewModel.excludedIngredients shouldBe emptySet()

		filterRepository.savedFilters = SearchFilters(cuisines = setOf(Cuisine.ITALIAN))

		viewModel.onSessionKeyChanged("user-1")
		advanceUntilIdle()

		viewModel.activeFilters shouldBe SearchFilters(cuisines = setOf(Cuisine.ITALIAN))
		viewModel.pantryIngredients shouldBe setOf("Chicken")
		viewModel.excludedIngredients shouldBe setOf("Garlic")
		searchRepository.queries.size shouldBe searchCountAfterInit + 1
	}

	@Test
	fun `onAddIngredient adds custom ingredient to pantry`() = runViewModelTest {
		val viewModel = RecipeSearchViewModelTestSupport.makeViewModel()
		advanceUntilIdle()

		viewModel.onAddIngredient("gochujang")

		viewModel.customPantryIngredients shouldBe setOf("gochujang")
		viewModel.pantryIngredients shouldBe setOf("gochujang")
		viewModel.excludedIngredients shouldBe emptySet()
	}

	@Test
	fun `onCustomIngredientToggle switches between pantry and excluded`() = runViewModelTest {
		val viewModel = RecipeSearchViewModelTestSupport.makeViewModel()
		advanceUntilIdle()

		viewModel.onAddIngredient("gochujang")
		viewModel.onCustomIngredientToggle("gochujang")

		viewModel.customPantryIngredients shouldBe setOf("gochujang")
		viewModel.pantryIngredients shouldBe emptySet()
		viewModel.excludedIngredients shouldBe setOf("gochujang")

		viewModel.onCustomIngredientToggle("gochujang")

		viewModel.pantryIngredients shouldBe setOf("gochujang")
		viewModel.excludedIngredients shouldBe emptySet()
	}

	@Test
	fun `onRemoveCustomIngredient removes custom ingredient from pantry and list`() = runViewModelTest {
		val viewModel = RecipeSearchViewModelTestSupport.makeViewModel()
		advanceUntilIdle()

		viewModel.onAddIngredient("gochujang")
		viewModel.onRemoveCustomIngredient("gochujang")

		viewModel.customPantryIngredients shouldBe emptySet()
		viewModel.pantryIngredients shouldBe emptySet()
		viewModel.excludedIngredients shouldBe emptySet()
	}

	@Test
	fun `onAddIngredientQueryChange loads ingredient match preview after debounce`() = runViewModelTest {
		val ingredientMatchRepository = FakeIngredientMatchRepository(
			response = Ok(
				IngredientMatchResponse(
					query = "tarragon",
					exactMatches = listOf(IngredientMatchCount(ingredient = "Tarragon", recipeCount = 2)),
				),
			),
		)
		val viewModel =
			RecipeSearchViewModelTestSupport.makeViewModel(ingredientMatchRepository = ingredientMatchRepository)

		viewModel.onAddIngredientQueryChange("tarragon")
		advanceTimeBy(299)
		viewModel.ingredientMatchPreview shouldBe null
		advanceTimeBy(1)
		advanceUntilIdle()

		ingredientMatchRepository.matchedNames shouldBe listOf("tarragon")
		viewModel.ingredientMatchPreview?.exactMatches?.single()?.ingredient shouldBe "Tarragon"
		viewModel.isIngredientMatchLoading shouldBe false
	}

	@Test
	fun `onSessionKeyChanged does nothing when session key is unchanged`() = runViewModelTest {
		val searchRepository = FakeRecipeSearchRepository(result = Ok(emptyList()))
		val viewModel = RecipeSearchViewModelTestSupport.makeViewModel(searchRepository = searchRepository)
		advanceUntilIdle()
		val searchCountAfterInit = searchRepository.queries.size

		viewModel.onSessionKeyChanged(null)
		advanceUntilIdle()

		searchRepository.queries.size shouldBe searchCountAfterInit
	}

	@Test
	fun `search sends key ingredients to repository`() = runViewModelTest {
		val searchRepository = FakeRecipeSearchRepository(result = Ok(emptyList()))
		val viewModel = RecipeSearchViewModelTestSupport.makeViewModel(
			searchRepository = searchRepository,
			subscriptionRepository = FakeSubscriptionRepository(
				initialState = RecipeSearchViewModelTestSupport.premiumSubscriptionState(),
			),
		)
		advanceUntilIdle()

		viewModel.onKeyIngredientsChange(setOf("Tomato", "Chicken"))
		viewModel.searchNow()
		advanceUntilIdle()

		searchRepository.lastKeyIngredients shouldBe setOf("Tomato", "Chicken")
	}

	@Test
	fun `free user search strips key ingredients`() = runViewModelTest {
		val searchRepository = FakeRecipeSearchRepository(result = Ok(emptyList()))
		val viewModel = RecipeSearchViewModelTestSupport.makeViewModel(searchRepository = searchRepository)
		advanceUntilIdle()

		viewModel.onKeyIngredientsChange(setOf("Tomato", "Chicken"))
		viewModel.searchNow()
		advanceUntilIdle()

		viewModel.keyIngredients shouldBe emptySet()
		searchRepository.lastKeyIngredients shouldBe emptySet()
	}

	@Test
	fun `filter sheet dismiss with key ingredients change re-runs search without saving filters`() = runViewModelTest {
		val searchRepository = FakeRecipeSearchRepository(result = Ok(emptyList()))
		val filterRepository = FakeRecipeSearchFilterRepository()
		val viewModel = RecipeSearchViewModelTestSupport.makeViewModel(
			searchRepository = searchRepository,
			filterRepository = filterRepository,
			subscriptionRepository = FakeSubscriptionRepository(
				initialState = RecipeSearchViewModelTestSupport.premiumSubscriptionState(),
			),
		)
		advanceUntilIdle()
		val searchCountAfterInit = searchRepository.queries.size
		val savedFiltersBeforeDismiss = filterRepository.savedFilters

		viewModel.onFilterButtonClick()
		viewModel.onKeyIngredientsChange(setOf("Tomato"))
		viewModel.onFilterSheetDismiss()
		advanceUntilIdle()

		searchRepository.queries.size shouldBe searchCountAfterInit + 1
		searchRepository.lastKeyIngredients shouldBe setOf("Tomato")
		filterRepository.savedFilters shouldBe savedFiltersBeforeDismiss
	}

	@Test
	fun `onKeyIngredientsChange clears all key ingredients`() = runViewModelTest {
		val viewModel = RecipeSearchViewModelTestSupport.makeViewModel(
			subscriptionRepository = FakeSubscriptionRepository(
				initialState = RecipeSearchViewModelTestSupport.premiumSubscriptionState(),
			),
		)
		advanceUntilIdle()

		viewModel.onKeyIngredientsChange(setOf("Tomato", "Chicken"))
		viewModel.onKeyIngredientsChange(emptySet())

		viewModel.keyIngredients shouldBe emptySet()
	}
}
