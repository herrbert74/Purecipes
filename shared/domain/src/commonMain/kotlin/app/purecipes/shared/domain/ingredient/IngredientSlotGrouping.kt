package app.purecipes.shared.domain.ingredient

import app.purecipes.shared.domain.model.IngredientRequirement
import app.purecipes.shared.domain.model.RecipeIngredient

fun ingredientSlots(ingredients: List<RecipeIngredient>): List<List<RecipeIngredient>> {
	val slots = mutableListOf<List<RecipeIngredient>>()
	var index = 0
	while (index < ingredients.size) {
		val ingredient = ingredients[index]
		if (ingredient.requirement == IngredientRequirement.ALTERNATIVE && ingredient.alternativeGroupKey != null) {
			val groupKey = ingredient.alternativeGroupKey
			val slot = mutableListOf<RecipeIngredient>()
			while (
				index < ingredients.size &&
				ingredients[index].requirement == IngredientRequirement.ALTERNATIVE &&
				ingredients[index].alternativeGroupKey == groupKey
			) {
				slot += ingredients[index]
				index++
			}
			slots += slot
		} else {
			slots += listOf(ingredient)
			index++
		}
	}
	return slots
}

fun slotDisplayText(slot: List<RecipeIngredient>): String =
	if (slot.size > 1 && slot.all { ingredient -> ingredient.requirement == IngredientRequirement.ALTERNATIVE }) {
		slot.joinToString(separator = " or ") { ingredient -> ingredient.text }
	} else {
		slot.first().text
	}

fun slotIsOptional(slot: List<RecipeIngredient>): Boolean =
	slot.all { ingredient -> ingredient.requirement == IngredientRequirement.OPTIONAL }

fun nutritionIngredientTexts(ingredients: List<RecipeIngredient>): List<String> {
	val seenAlternativeKeys = mutableSetOf<Int>()
	return ingredients.mapNotNull { ingredient ->
		when (ingredient.requirement) {
			IngredientRequirement.OPTIONAL -> null
			IngredientRequirement.ALTERNATIVE -> {
				val groupKey = ingredient.alternativeGroupKey
				if (groupKey != null && !seenAlternativeKeys.add(groupKey)) {
					null
				} else {
					ingredient.text
				}
			}
			IngredientRequirement.REQUIRED -> ingredient.text
		}
	}
}
