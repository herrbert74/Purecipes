package app.purecipes.shared.testfixtures.fake

import app.purecipes.shared.domain.model.IngredientRequirement
import app.purecipes.shared.domain.model.RecipeIngredient

fun recipeIngredients(vararg texts: String): List<RecipeIngredient> =
	texts.map { text -> RecipeIngredient(text = text) }

fun optionalRecipeIngredient(text: String): RecipeIngredient =
	RecipeIngredient(text = text, requirement = IngredientRequirement.OPTIONAL)
