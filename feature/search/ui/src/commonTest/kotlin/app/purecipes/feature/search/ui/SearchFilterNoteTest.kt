package app.purecipes.feature.search.ui

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class SearchFilterNoteTest {

	@Test
	fun `title search with pantry does not add ranking note`() {
		formatSearchFilterNote(
			isTitleSearch = true,
			hasPantry = true,
			hasExclusions = false,
			hasRecipeFilters = false,
			applyRecipeFiltersToTitleSearch = true,
		) shouldBe null
	}

	@Test
	fun `title search with pantry and exclusions hides excluded recipes`() {
		formatSearchFilterNote(
			isTitleSearch = true,
			hasPantry = true,
			hasExclusions = true,
			hasRecipeFilters = false,
			applyRecipeFiltersToTitleSearch = true,
		) shouldBe "Recipes with excluded ingredients are hidden."
	}

	@Test
	fun `title search notes recipe filters can be changed in settings`() {
		formatSearchFilterNote(
			isTitleSearch = true,
			hasPantry = false,
			hasExclusions = false,
			hasRecipeFilters = true,
			applyRecipeFiltersToTitleSearch = true,
		) shouldBe "Diet and other filters still apply — change this in Settings."
	}

	@Test
	fun `title search notes when recipe filters are off`() {
		formatSearchFilterNote(
			isTitleSearch = true,
			hasPantry = false,
			hasExclusions = false,
			hasRecipeFilters = true,
			applyRecipeFiltersToTitleSearch = false,
		) shouldBe "Diet and other filters are off for this search — change this in Settings."
	}

	@Test
	fun `browse search explains pantry filter without settings`() {
		formatSearchFilterNote(
			isTitleSearch = false,
			hasPantry = true,
			hasExclusions = true,
			hasRecipeFilters = false,
			applyRecipeFiltersToTitleSearch = false,
		) shouldBe "Showing recipes you can make with your pantry. Recipes with excluded ingredients are hidden."
	}

	@Test
	fun `returns null when no filters apply`() {
		formatSearchFilterNote(
			isTitleSearch = true,
			hasPantry = false,
			hasExclusions = false,
			hasRecipeFilters = false,
			applyRecipeFiltersToTitleSearch = true,
		) shouldBe null
	}

	@Test
	fun `browse search mentions recipe filters`() {
		formatSearchFilterNote(
			isTitleSearch = false,
			hasPantry = false,
			hasExclusions = false,
			hasRecipeFilters = true,
			applyRecipeFiltersToTitleSearch = true,
		) shouldBe "Diet and other filters still apply. Change them with the filter button."
	}
}
