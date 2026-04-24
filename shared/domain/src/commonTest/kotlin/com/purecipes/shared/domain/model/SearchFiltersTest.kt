package com.purecipes.shared.domain.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class SearchFiltersTest {

	@Test
	fun `empty filters have isEmpty true`() {
		SearchFilters().isEmpty shouldBe true
	}

	@Test
	fun `filters with include ingredients have isEmpty false`() {
		SearchFilters(includeIngredients = setOf("Chicken")).isEmpty shouldBe false
	}

	@Test
	fun `filters with exclude ingredients have isEmpty false`() {
		SearchFilters(excludeIngredients = setOf("Pork")).isEmpty shouldBe false
	}

	@Test
	fun `filters with cuisines have isEmpty false`() {
		SearchFilters(cuisines = setOf(Cuisine.ITALIAN)).isEmpty shouldBe false
	}

	@Test
	fun `default filters are not empty`() {
		SearchFilters.default().isEmpty shouldBe false
	}

	@Test
	fun `default filters include common ingredients`() {
		SearchFilters.default().includeIngredients.isNotEmpty() shouldBe true
	}

	@Test
	fun `default filters have all cuisines selected`() {
		SearchFilters.default().cuisines shouldBe Cuisine.entries.toSet()
	}

	@Test
	fun `default filters have all cooking time ranges selected`() {
		SearchFilters.default().cookingTimeRanges shouldBe CookingTimeRange.entries.toSet()
	}

	@Test
	fun `default filters have all dietary preferences selected`() {
		SearchFilters.default().dietaryPreferences shouldBe DietaryPreference.entries.toSet()
	}

	@Test
	fun `default filters have all difficulty levels selected`() {
		SearchFilters.default().difficultyLevels shouldBe DifficultyLevel.entries.toSet()
	}
}
