package app.purecipes.feature.search.ui

import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.MealType
import app.purecipes.shared.domain.model.SearchFilters
import app.purecipes.shared.ui.component.BrowseTileItem
import app.purecipes.shared.ui.component.ContainerTint
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

internal fun searchBrowseTiles(filters: SearchFilters): ImmutableList<BrowseTileItem> =
	persistentListOf(
		BrowseTileItem(
			id = browseMealTypeId(MealType.BREAKFAST),
			title = MealType.BREAKFAST.displayName,
			illustration = "🍳",
			tint = ContainerTint.Primary,
			selected = filters.mealTypes == setOf(MealType.BREAKFAST),
		),
		BrowseTileItem(
			id = browseMealTypeId(MealType.LUNCH),
			title = MealType.LUNCH.displayName,
			illustration = "🥗",
			tint = ContainerTint.Secondary,
			selected = filters.mealTypes == setOf(MealType.LUNCH),
		),
		BrowseTileItem(
			id = browseMealTypeId(MealType.DINNER),
			title = MealType.DINNER.displayName,
			illustration = "🍝",
			tint = ContainerTint.Tertiary,
			selected = filters.mealTypes == setOf(MealType.DINNER),
		),
		BrowseTileItem(
			id = browseMealTypeId(MealType.DESSERT),
			title = MealType.DESSERT.displayName,
			illustration = "🍰",
			tint = ContainerTint.Primary,
			selected = filters.mealTypes == setOf(MealType.DESSERT),
		),
		BrowseTileItem(
			id = browseCuisineId(Cuisine.ITALIAN),
			title = Cuisine.ITALIAN.displayName,
			illustration = "🇮🇹",
			tint = ContainerTint.Secondary,
			selected = filters.cuisines == setOf(Cuisine.ITALIAN),
		),
		BrowseTileItem(
			id = browseCuisineId(Cuisine.MEXICAN),
			title = Cuisine.MEXICAN.displayName,
			illustration = "🇲🇽",
			tint = ContainerTint.Tertiary,
			selected = filters.cuisines == setOf(Cuisine.MEXICAN),
		),
		BrowseTileItem(
			id = browseCuisineId(Cuisine.INDIAN),
			title = Cuisine.INDIAN.displayName,
			illustration = "🇮🇳",
			tint = ContainerTint.Primary,
			selected = filters.cuisines == setOf(Cuisine.INDIAN),
		),
		BrowseTileItem(
			id = browseCuisineId(Cuisine.JAPANESE),
			title = Cuisine.JAPANESE.displayName,
			illustration = "🇯🇵",
			tint = ContainerTint.Secondary,
			selected = filters.cuisines == setOf(Cuisine.JAPANESE),
		),
	)

internal fun SearchFilters.toggleBrowseTile(tileId: String): SearchFilters {
	val mealType = MealType.entries.firstOrNull { browseMealTypeId(it) == tileId }
	if (mealType != null) {
		return copy(
			mealTypes = if (mealTypes == setOf(mealType)) {
				emptySet()
			} else {
				setOf(mealType)
			},
		)
	}
	val cuisine = Cuisine.entries.firstOrNull { browseCuisineId(it) == tileId }
	return if (cuisine != null) {
		copy(
			cuisines = if (cuisines == setOf(cuisine)) {
				emptySet()
			} else {
				setOf(cuisine)
			},
		)
	} else {
		this
	}
}

private fun browseMealTypeId(mealType: MealType): String = "meal:${mealType.name}"

private fun browseCuisineId(cuisine: Cuisine): String = "cuisine:${cuisine.name}"
