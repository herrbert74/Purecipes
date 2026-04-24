package com.purecipes.feature.search.domain.usecase

import com.purecipes.shared.domain.model.Cuisine
import com.purecipes.shared.domain.model.SearchFilters
import com.purecipes.shared.testfixtures.fake.FakeRecipeSearchFilterRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SaveSearchFiltersUseCaseTest {

	@Test
	fun `forwards filters to repository`() = runTest {
		val repository = FakeRecipeSearchFilterRepository()
		val useCase = SaveSearchFiltersUseCase(repository)
		val filters = SearchFilters(cuisines = setOf(Cuisine.ITALIAN))

		useCase(filters)

		assertEquals(filters, repository.savedFilters)
	}

	@Test
	fun `overwrites previously saved filters`() = runTest {
		val repository = FakeRecipeSearchFilterRepository()
		val useCase = SaveSearchFiltersUseCase(repository)
		val first = SearchFilters(cuisines = setOf(Cuisine.ITALIAN))
		val second = SearchFilters(cuisines = setOf(Cuisine.FRENCH))

		useCase(first)
		useCase(second)

		assertEquals(second, repository.savedFilters)
	}
}
