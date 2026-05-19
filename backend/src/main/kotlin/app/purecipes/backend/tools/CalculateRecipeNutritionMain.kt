package app.purecipes.backend.tools

import app.purecipes.backend.db.Db
import app.purecipes.backend.feature.nutrition.RecipeNutritionService

private const val ARG_RECIPE_ID = "--recipe-id"
private const val ARG_ALL_RECIPES = "--all-recipes"

fun main(args: Array<String>) {
	val recipeId = readArgumentValue(args, ARG_RECIPE_ID)?.toIntOrNull()
	val allRecipes = args.contains(ARG_ALL_RECIPES)
	if ((recipeId == null) == !allRecipes) {
		error("Provide exactly one of $ARG_RECIPE_ID=<id> or $ARG_ALL_RECIPES")
	}

	val service = RecipeNutritionService(Db.create().dataSource)
	val results = if (allRecipes) {
		service.calculateAndPersistAll()
	} else {
		listOf(service.calculateAndPersist(recipeId!!))
	}

	println("Recipe nutrition calculation")
	results.forEach { result ->
		val totals = result.totals
		if (totals == null) {
			println("Recipe ${result.recipeId}: no countable ingredients (${result.ingredientCount} lines)")
			return@forEach
		}
		println(
			"Recipe ${result.recipeId}: " +
				"${totals.matchedIngredientCount}/${totals.totalIngredientCount} ingredients, " +
				"complete=${totals.isComplete}, " +
				"calories=${totals.calories}",
		)
	}
}

private fun readArgumentValue(args: Array<String>, key: String): String? {
	args.forEach { arg ->
		if (arg.startsWith("$key=")) {
			return arg.removePrefix("$key=").trim()
		}
	}
	return args.toList()
		.windowed(size = 2, step = 1)
		.firstOrNull { it.first() == key }
		?.last()
}
