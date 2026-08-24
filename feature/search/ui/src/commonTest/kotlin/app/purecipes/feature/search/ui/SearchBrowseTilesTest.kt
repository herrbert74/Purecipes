package app.purecipes.feature.search.ui

import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.MealType
import app.purecipes.shared.domain.model.SearchFilters
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class SearchBrowseTilesTest {

	@Test
	fun toggleBrowseTileSelectsAndClearsMealType() {
		val empty = SearchFilters.default()
		val selected = empty.toggleBrowseTile("meal:BREAKFAST")
		selected.mealTypes shouldBe setOf(MealType.BREAKFAST)

		val cleared = selected.toggleBrowseTile("meal:BREAKFAST")
		cleared.mealTypes shouldBe emptySet()
	}

	@Test
	fun toggleBrowseTileSelectsCuisineWithoutClearingMealType() {
		val filters = SearchFilters(mealTypes = setOf(MealType.DINNER))
		val updated = filters.toggleBrowseTile("cuisine:ITALIAN")

		updated.mealTypes shouldBe setOf(MealType.DINNER)
		updated.cuisines shouldBe setOf(Cuisine.ITALIAN)
	}

	@Test
	fun searchBrowseTilesMarkSelectedFilters() {
		val tiles = searchBrowseTiles(
			SearchFilters(
				mealTypes = setOf(MealType.LUNCH),
				cuisines = setOf(Cuisine.MEXICAN),
			),
		)

		tiles.single { it.id == "meal:LUNCH" }.selected shouldBe true
		tiles.single { it.id == "cuisine:MEXICAN" }.selected shouldBe true
		tiles.single { it.id == "meal:BREAKFAST" }.selected shouldBe false
	}
}
