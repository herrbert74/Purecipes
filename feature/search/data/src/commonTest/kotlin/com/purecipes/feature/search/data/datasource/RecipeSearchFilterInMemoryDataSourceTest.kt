package com.purecipes.feature.search.data.datasource

import com.purecipes.shared.domain.model.Cuisine
import com.purecipes.shared.domain.model.SearchFilters
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class RecipeSearchFilterInMemoryDataSourceTest {

	@Test
	fun `returns empty filters by default`() {
		val dataSource = RecipeSearchFilterInMemoryDataSource()

		dataSource.getFilters() shouldBe SearchFilters()
	}

	@Test
	fun `returns saved filters`() {
		val dataSource = RecipeSearchFilterInMemoryDataSource()
		val filters = SearchFilters(cuisines = setOf(Cuisine.ITALIAN))

		dataSource.saveFilters(filters)

		dataSource.getFilters() shouldBe filters
	}

	@Test
	fun `overwrites previously saved filters`() {
		val dataSource = RecipeSearchFilterInMemoryDataSource()
		val first = SearchFilters(cuisines = setOf(Cuisine.ITALIAN))
		val second = SearchFilters(cuisines = setOf(Cuisine.FRENCH))

		dataSource.saveFilters(first)
		dataSource.saveFilters(second)

		dataSource.getFilters() shouldBe second
	}
}
