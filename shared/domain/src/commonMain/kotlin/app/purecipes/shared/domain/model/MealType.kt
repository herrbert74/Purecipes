package app.purecipes.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class MealType(val displayName: String) {
	BREAKFAST("Breakfast"),
	BRUNCH("Brunch"),
	LUNCH("Lunch"),
	DINNER("Dinner"),
	SNACK("Snack"),
	DESSERT("Dessert"),
	APPETIZER("Appetizer"),
	DRINK("Drink"),
	SIDE_DISH("Side Dish"),
}
