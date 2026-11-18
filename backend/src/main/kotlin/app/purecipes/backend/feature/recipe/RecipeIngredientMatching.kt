package app.purecipes.backend.feature.recipe

import app.purecipes.backend.feature.search.IngredientVocabulary
import app.purecipes.shared.domain.ingredient.CompiledIngredientMatcher
import app.purecipes.shared.domain.ingredient.IngredientNameMatching
import app.purecipes.shared.domain.ingredient.ingredientSlots
import app.purecipes.shared.domain.ingredient.singleMissingIngredientLabel
import app.purecipes.shared.domain.ingredient.slotIsOptional
import app.purecipes.shared.domain.model.IngredientGroup
import app.purecipes.shared.domain.model.IngredientRequirement
import app.purecipes.shared.domain.model.RecipeIngredient

internal fun isRecipeCoveredByAvailableIngredients(
	recipeId: Int,
	availableMatcher: CompiledIngredientMatcher,
	loadIngredientGroups: (Int) -> List<IngredientGroup>,
): Boolean {
	return loadIngredientGroups(recipeId).all { group ->
		ingredientSlots(group.ingredients).all { slot ->
			isIngredientSlotCoveredByPantry(slot, availableMatcher)
		}
	}
}

internal fun pantryCoverageForRecipe(
	recipeId: Int,
	availableIngredients: List<String>,
	loadIngredientGroups: (Int) -> List<IngredientGroup>,
): PantryCoverage = pantryCoverageForRecipe(
	recipeId = recipeId,
	availableMatcher = IngredientNameMatching.compileMatcher(availableIngredients),
	loadIngredientGroups = loadIngredientGroups,
)

internal fun pantryCoverageForRecipe(
	recipeId: Int,
	availableMatcher: CompiledIngredientMatcher,
	loadIngredientGroups: (Int) -> List<IngredientGroup>,
): PantryCoverage {
	val requiredSlots = loadIngredientGroups(recipeId)
		.flatMap { group -> ingredientSlots(group.ingredients) }
		.filterNot { slot -> slotIsOptional(slot) }
	val coveredSlots = requiredSlots.count { slot ->
		isIngredientSlotCoveredByPantry(slot, availableMatcher)
	}
	return PantryCoverage(
		coveredSlots = coveredSlots,
		totalRequiredSlots = requiredSlots.size,
	)
}

internal fun singleMissingPantryIngredientLabel(
	recipeId: Int,
	availableIngredients: List<String>,
	loadIngredientGroups: (Int) -> List<IngredientGroup>,
): String? = singleMissingPantryIngredientLabel(
	recipeId = recipeId,
	availableMatcher = IngredientNameMatching.compileMatcher(availableIngredients),
	loadIngredientGroups = loadIngredientGroups,
)

internal fun singleMissingPantryIngredientLabel(
	recipeId: Int,
	availableMatcher: CompiledIngredientMatcher,
	loadIngredientGroups: (Int) -> List<IngredientGroup>,
): String? {
	return singleMissingIngredientLabel(loadIngredientGroups(recipeId)) { slot ->
		isIngredientSlotCoveredByPantry(slot, availableMatcher)
	}
}

internal fun recipeContainsExcludedIngredient(
	recipeId: Int,
	excludedMatcher: CompiledIngredientMatcher,
	loadIngredientGroups: (Int) -> List<IngredientGroup>,
): Boolean {
	if (excludedMatcher.isEmpty) {
		return false
	}

	return loadIngredientGroups(recipeId).any { group ->
		ingredientSlots(group.ingredients).any { slot ->
			isIngredientSlotExcluded(slot, excludedMatcher)
		}
	}
}

internal fun recipeContainsAllKeyIngredients(
	recipeId: Int,
	keyIngredients: List<String>,
	loadIngredientGroups: (Int) -> List<IngredientGroup>,
): Boolean = recipeContainsAllKeyIngredientMatchers(
	recipeId = recipeId,
	keyMatchers = keyIngredients.map { keyIngredient ->
		IngredientNameMatching.compileMatcher(listOf(keyIngredient))
	},
	loadIngredientGroups = loadIngredientGroups,
)

internal fun recipeContainsAllKeyIngredientMatchers(
	recipeId: Int,
	keyMatchers: List<CompiledIngredientMatcher>,
	loadIngredientGroups: (Int) -> List<IngredientGroup>,
): Boolean {
	if (keyMatchers.isEmpty()) {
		return true
	}

	val groups = loadIngredientGroups(recipeId)
	return keyMatchers.all { keyMatcher ->
		groups.any { group ->
			ingredientSlots(group.ingredients).any { slot ->
				isIngredientSlotMatchingKeyIngredient(slot, keyMatcher)
			}
		}
	}
}

internal fun isIngredientSlotCoveredByPantry(
	slot: List<RecipeIngredient>,
	availableMatcher: CompiledIngredientMatcher,
): Boolean {
	if (slotIsOptional(slot)) {
		return true
	}
	val requiredMembers = slot.filter { ingredient -> ingredient.requirement != IngredientRequirement.OPTIONAL }
	return if (requiredMembers.isEmpty()) {
		true
	} else if (requiredMembers.any { ingredient -> ingredient.requirement == IngredientRequirement.ALTERNATIVE }) {
		requiredMembers.any { ingredient ->
			IngredientVocabulary.isCoveredByAvailableIngredients(
				ingredientLine = ingredient.text,
				availableMatcher = availableMatcher,
			)
		}
	} else {
		requiredMembers.all { ingredient ->
			IngredientVocabulary.isCoveredByAvailableIngredients(
				ingredientLine = ingredient.text,
				availableMatcher = availableMatcher,
			)
		}
	}
}

internal fun isIngredientSlotExcluded(
	slot: List<RecipeIngredient>,
	excludedMatcher: CompiledIngredientMatcher,
): Boolean {
	if (excludedMatcher.isEmpty) {
		return false
	}
	val alternativeMembers = slot.filter { ingredient ->
		ingredient.requirement == IngredientRequirement.ALTERNATIVE
	}
	return if (alternativeMembers.size > 1) {
		alternativeMembers.all { ingredient ->
			IngredientVocabulary.matchesAnyIngredient(
				ingredientLine = ingredient.text,
				ingredientMatcher = excludedMatcher,
			)
		}
	} else {
		slot.any { ingredient ->
			IngredientVocabulary.matchesAnyIngredient(
				ingredientLine = ingredient.text,
				ingredientMatcher = excludedMatcher,
			)
		}
	}
}

internal fun isIngredientSlotMatchingKeyIngredient(
	slot: List<RecipeIngredient>,
	keyMatcher: CompiledIngredientMatcher,
): Boolean {
	if (keyMatcher.isEmpty || slotIsOptional(slot)) {
		return false
	}
	val alternativeMembers = slot.filter { ingredient ->
		ingredient.requirement == IngredientRequirement.ALTERNATIVE
	}
	return if (alternativeMembers.size > 1) {
		alternativeMembers.any { ingredient ->
			IngredientVocabulary.matchesAnyIngredient(
				ingredientLine = ingredient.text,
				ingredientMatcher = keyMatcher,
			)
		}
	} else {
		slot.any { ingredient ->
			ingredient.requirement != IngredientRequirement.OPTIONAL &&
				IngredientVocabulary.matchesAnyIngredient(
					ingredientLine = ingredient.text,
					ingredientMatcher = keyMatcher,
				)
		}
	}
}
