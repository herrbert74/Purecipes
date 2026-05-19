package app.purecipes.backend.tools

import app.purecipes.backend.db.Db
import app.purecipes.backend.feature.nutrition.RecipeNutritionService

private const val ARG_RECIPE_ID = "--recipe-id"
private const val ARG_RECIPE_IDS = "--recipe-ids"
private const val ARG_ALL_RECIPES = "--all-recipes"

fun main(args: Array<String>) {
	val recipeId = readArgumentValue(args, ARG_RECIPE_ID)?.toIntOrNull()
	val recipeIds = readRecipeIdsArgument(args)
	val allRecipes = args.contains(ARG_ALL_RECIPES)
	val modeCount = listOf(recipeId != null, recipeIds.isNotEmpty(), allRecipes).count { it }
	if (modeCount != 1) {
		error("Provide exactly one of $ARG_RECIPE_ID=<id>, $ARG_RECIPE_IDS=<id,id,...>, or $ARG_ALL_RECIPES")
	}

	val service = RecipeNutritionService(Db.create().dataSource)
	val results = when {
		allRecipes -> service.calculateAndPersistAll()
		recipeId != null -> listOf(service.calculateAndPersist(recipeId))
		else -> service.calculateAndPersistRecipeIds(recipeIds)
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

private fun readRecipeIdsArgument(args: Array<String>): List<Int> =
	readArgumentValue(args, ARG_RECIPE_IDS)
		?.split(",")
		?.mapNotNull { value -> value.trim().toIntOrNull() }
		.orEmpty()

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
