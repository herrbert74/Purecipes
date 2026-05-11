package app.purecipes.shared.domain.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class SearchFiltersTest {

	@Test
	fun `empty filters have isEmpty true`() {
		SearchFilters().isEmpty shouldBe true
	}

	@Test
	fun `filters with cuisines have isEmpty false`() {
		SearchFilters(cuisines = setOf(Cuisine.ITALIAN)).isEmpty shouldBe false
	}

	@Test
	fun `default filters are empty`() {
		SearchFilters.default().isEmpty shouldBe true
	}

	@Test
	fun `default filters have no cuisines selected`() {
		SearchFilters.default().cuisines shouldBe emptySet()
	}

	@Test
	fun `default filters have no cooking time ranges selected`() {
		SearchFilters.default().cookingTimeRanges shouldBe emptySet()
	}

	@Test
	fun `default filters have no dietary preferences selected`() {
		SearchFilters.default().dietaryPreferences shouldBe emptySet()
	}

	@Test
	fun `default filters have no difficulty levels selected`() {
		SearchFilters.default().difficultyLevels shouldBe emptySet()
	}
}
