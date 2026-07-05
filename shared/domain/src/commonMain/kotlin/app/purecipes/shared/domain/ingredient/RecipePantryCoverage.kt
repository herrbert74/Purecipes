package app.purecipes.shared.domain.ingredient

import app.purecipes.shared.domain.model.IngredientGroup
import app.purecipes.shared.domain.model.RecipeIngredient

fun uncoveredIngredientSlots(
	ingredientGroups: List<IngredientGroup>,
	isSlotCovered: (List<RecipeIngredient>) -> Boolean,
): List<List<RecipeIngredient>> {
	return ingredientGroups.flatMap { group ->
		ingredientSlots(group.ingredients).filterNot { slot ->
			slotIsOptional(slot) || isSlotCovered(slot)
		}
	}
}

fun missingIngredientCount(
	ingredientGroups: List<IngredientGroup>,
	isSlotCovered: (List<RecipeIngredient>) -> Boolean,
): Int = uncoveredIngredientSlots(ingredientGroups, isSlotCovered).size

fun singleMissingIngredientLabel(
	ingredientGroups: List<IngredientGroup>,
	isSlotCovered: (List<RecipeIngredient>) -> Boolean,
): String? {
	val uncoveredSlots = uncoveredIngredientSlots(ingredientGroups, isSlotCovered)
	return if (uncoveredSlots.size == 1) {
		slotDisplayText(uncoveredSlots.first())
	} else {
		null
	}
}
