package app.purecipes.feature.search.ui

import app.purecipes.feature.search.domain.model.SearchPreferences
import app.purecipes.shared.domain.model.DietaryPreference
import app.purecipes.shared.domain.model.SearchFilters
import app.purecipes.shared.testfixtures.fake.FakeRecipeSearchFilterRepository
import app.purecipes.shared.testfixtures.fake.FakeRecipeSearchRepository
import app.purecipes.shared.testfixtures.fake.FakeSearchPreferencesRepository
import app.purecipes.shared.testfixtures.fake.FakeUserExcludedIngredientsRepository
import app.purecipes.shared.testfixtures.fake.FakeUserPantryRepository
import app.purecipes.shared.testfixtures.runViewModelTest
import com.github.michaelbull.result.Ok
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeSearchViewModelTitleSearchTest {

	@Test
	fun `title search sends recipe filter flag from preferences`() = runViewModelTest {
		val repository = FakeRecipeSearchRepository(Ok(emptyList()))
		val viewModel = RecipeSearchViewModelTestSupport.makeViewModel(
			searchRepository = repository,
			searchPreferencesRepository = FakeSearchPreferencesRepository(
				SearchPreferences(applyRecipeFiltersToTitleSearch = false),
			),
		)
		advanceUntilIdle()

		viewModel.onSearchQueryChange("chocolate")
		viewModel.searchNow()
		advanceUntilIdle()

		repository.lastQuery shouldBe "chocolate"
		repository.lastApplyRecipeFilters shouldBe false
		viewModel.searchFilterNote shouldBe null
	}

	@Test
	fun `blank search always applies recipe filters even when title-search setting is off`() = runViewModelTest {
		val repository = FakeRecipeSearchRepository(Ok(emptyList()))
		val viewModel = RecipeSearchViewModelTestSupport.makeViewModel(
			searchRepository = repository,
			filterRepository = FakeRecipeSearchFilterRepository(
				SearchFilters(dietaryPreferences = setOf(DietaryPreference.PALEO)),
			),
			searchPreferencesRepository = FakeSearchPreferencesRepository(
				SearchPreferences(applyRecipeFiltersToTitleSearch = false),
			),
		)
		advanceUntilIdle()

		repository.lastApplyRecipeFilters shouldBe true
		repository.lastFilters shouldBe SearchFilters(dietaryPreferences = setOf(DietaryPreference.PALEO))
		viewModel.searchFilterNote shouldBe "Diet and other filters still apply. Change them with the filter button."
	}

	@Test
	fun `title search skips selected recipe filters when setting is off`() = runViewModelTest {
		val repository = FakeRecipeSearchRepository(Ok(emptyList()))
		val viewModel = RecipeSearchViewModelTestSupport.makeViewModel(
			searchRepository = repository,
			filterRepository = FakeRecipeSearchFilterRepository(
				SearchFilters(dietaryPreferences = setOf(DietaryPreference.PALEO)),
			),
			searchPreferencesRepository = FakeSearchPreferencesRepository(
				SearchPreferences(applyRecipeFiltersToTitleSearch = false),
			),
		)
		advanceUntilIdle()

		viewModel.onSearchQueryChange("chocolate")
		viewModel.searchNow()
		advanceUntilIdle()

		repository.lastApplyRecipeFilters shouldBe false
		repository.lastFilters shouldBe SearchFilters()
		viewModel.searchFilterNote shouldBe
			"Diet and other filters are off for this search — change this in Settings."
	}

	@Test
	fun `title search with pantry does not show ranking note`() = runViewModelTest {
		val repository = FakeRecipeSearchRepository(Ok(emptyList()))
		val viewModel = RecipeSearchViewModelTestSupport.makeViewModel(
			searchRepository = repository,
			pantryRepository = FakeUserPantryRepository(setOf("Chicken")),
			sessionKey = "session",
		)
		advanceUntilIdle()

		viewModel.onSearchQueryChange("chocolate")
		viewModel.searchNow()
		advanceUntilIdle()

		viewModel.searchFilterNote shouldBe null
	}

	@Test
	fun `blank search still hides excluded ingredients`() = runViewModelTest {
		val repository = FakeRecipeSearchRepository(Ok(emptyList()))
		val viewModel = RecipeSearchViewModelTestSupport.makeViewModel(
			searchRepository = repository,
			excludedIngredientsRepository = FakeUserExcludedIngredientsRepository(setOf("Garlic")),
			sessionKey = "session",
		)
		advanceUntilIdle()

		viewModel.searchFilterNote shouldBe "Recipes with excluded ingredients are hidden."
	}
}
