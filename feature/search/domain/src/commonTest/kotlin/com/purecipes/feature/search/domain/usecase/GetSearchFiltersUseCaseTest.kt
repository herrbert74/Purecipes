package com.purecipes.feature.search.domain.usecase

import com.purecipes.shared.domain.model.Cuisine
import com.purecipes.shared.domain.model.SearchFilters
import com.purecipes.shared.testfixtures.fake.FakeRecipeSearchFilterRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class GetSearchFiltersUseCaseTest {

	@Test
	fun `returns filters from repository`() = runTest {
		val expected = SearchFilters(cuisines = setOf(Cuisine.ITALIAN))
		val repository = FakeRecipeSearchFilterRepository(expected)
		val useCase = GetSearchFiltersUseCase(repository)

		val result = useCase()

		result shouldBe expected
	}

	@Test
	fun `returns empty filters when repository has none`() = runTest {
		val repository = FakeRecipeSearchFilterRepository(SearchFilters())
		val useCase = GetSearchFiltersUseCase(repository)

		val result = useCase()

		result shouldBe SearchFilters()
	}
}
