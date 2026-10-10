package app.purecipes.backend.feature.search

import app.purecipes.shared.domain.model.MealType

internal const val HOME_BREAKFAST_START_HOUR = 5

internal const val HOME_BREAKFAST_END_HOUR = 10

internal const val HOME_LUNCH_START_HOUR = 11

internal const val HOME_LUNCH_END_HOUR = 15

internal fun mealTypeForLocalHour(localHour: Int): MealType = when (localHour) {
	in HOME_BREAKFAST_START_HOUR..HOME_BREAKFAST_END_HOUR -> MealType.BREAKFAST
	in HOME_LUNCH_START_HOUR..HOME_LUNCH_END_HOUR -> MealType.LUNCH
	else -> MealType.DINNER
}
