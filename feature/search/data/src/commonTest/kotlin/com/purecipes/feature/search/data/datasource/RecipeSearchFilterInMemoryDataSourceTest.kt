package com.purecipes.feature.search.data.datasource

import com.purecipes.shared.domain.model.Cuisine
import com.purecipes.shared.domain.model.SearchFilters
import kotlin.test.Test
import kotlin.test.assertEquals

class RecipeSearchFilterInMemoryDataSourceTest {

	@Test
	fun `returns empty filters by default`() {
		val dataSource = RecipeSearchFilterInMemoryDataSource()

		assertEquals(SearchFilters(), dataSource.getFilters())
	}

	@Test
	fun `returns saved filters`() {
		val dataSource = RecipeSearchFilterInMemoryDataSource()
		val filters = SearchFilters(cuisines = setOf(Cuisine.ITALIAN))

		dataSource.saveFilters(filters)

		assertEquals(filters, dataSource.getFilters())
	}

	@Test
	fun `overwrites previously saved filters`() {
		val dataSource = RecipeSearchFilterInMemoryDataSource()
		val first = SearchFilters(cuisines = setOf(Cuisine.ITALIAN))
		val second = SearchFilters(cuisines = setOf(Cuisine.FRENCH))

		dataSource.saveFilters(first)
		dataSource.saveFilters(second)

		assertEquals(second, dataSource.getFilters())
	}
}
