package com.purecipes.shared.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SearchFiltersTest {

	@Test
	fun `empty filters have isEmpty true`() {
		assertTrue(SearchFilters().isEmpty)
	}

	@Test
	fun `filters with include ingredients have isEmpty false`() {
		assertFalse(SearchFilters(includeIngredients = setOf("Chicken")).isEmpty)
	}

	@Test
	fun `filters with exclude ingredients have isEmpty false`() {
		assertFalse(SearchFilters(excludeIngredients = setOf("Pork")).isEmpty)
	}

	@Test
	fun `filters with cuisines have isEmpty false`() {
		assertFalse(SearchFilters(cuisines = setOf(Cuisine.ITALIAN)).isEmpty)
	}

	@Test
	fun `default filters are not empty`() {
		assertFalse(SearchFilters.default().isEmpty)
	}

	@Test
	fun `default filters include common ingredients`() {
		assertTrue(SearchFilters.default().includeIngredients.isNotEmpty())
	}

	@Test
	fun `default filters have all cuisines selected`() {
		assertEquals(Cuisine.entries.toSet(), SearchFilters.default().cuisines)
	}

	@Test
	fun `default filters have all cooking time ranges selected`() {
		assertEquals(CookingTimeRange.entries.toSet(), SearchFilters.default().cookingTimeRanges)
	}

	@Test
	fun `default filters have all dietary preferences selected`() {
		assertEquals(DietaryPreference.entries.toSet(), SearchFilters.default().dietaryPreferences)
	}

	@Test
	fun `default filters have all difficulty levels selected`() {
		assertEquals(DifficultyLevel.entries.toSet(), SearchFilters.default().difficultyLevels)
	}
}
