package app.purecipes.backend.tools

import app.purecipes.backend.db.Db
import app.purecipes.backend.feature.nutrition.IngredientNutritionIssue
import app.purecipes.backend.feature.nutrition.IngredientNutritionIssueKind
import app.purecipes.backend.feature.nutrition.RecipeNutritionPersistResult
import app.purecipes.backend.feature.nutrition.RecipeNutritionService

private const val ARG_RECIPE_ID = "--recipe-id"
private const val ARG_RECIPE_IDS = "--recipe-ids"
private const val ARG_ALL_RECIPES = "--all-recipes"
private const val ARG_VERBOSE = "--verbose"
private const val ARG_REPORT_UNMATCHED = "--report-unmatched"

fun main(args: Array<String>) {
	val recipeId = readArgumentValue(args, ARG_RECIPE_ID)?.toIntOrNull()
	val recipeIds = readRecipeIdsArgument(args)
	val allRecipes = args.contains(ARG_ALL_RECIPES)
	val verbose = args.contains(ARG_VERBOSE)
	val reportUnmatched = args.contains(ARG_REPORT_UNMATCHED)
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

	printRecipeNutritionResults(
		results = results,
		verbose = verbose || results.size == 1,
		reportUnmatched = reportUnmatched || allRecipes,
	)
}

private fun printRecipeNutritionResults(
	results: List<RecipeNutritionPersistResult>,
	verbose: Boolean,
	reportUnmatched: Boolean,
) {
	println("Recipe nutrition calculation")
	results.forEach { result -> printRecipeResult(result, verbose) }
	printBackfillSummary(results)
	if (reportUnmatched) {
		printUnmatchedIngredientReport(results)
	}
}

private fun printRecipeResult(result: RecipeNutritionPersistResult, verbose: Boolean) {
	val totals = result.totals
	if (totals == null) {
		println("Recipe ${result.recipeId}: no countable ingredients (${result.ingredientCount} lines)")
		if (verbose) {
			printRecipeIssues(result)
		}
		return
	}

	println(
		"Recipe ${result.recipeId}: " +
			"${totals.matchedIngredientCount}/${totals.totalIngredientCount} ingredients, " +
			"complete=${totals.isComplete}, " +
			"calories=${totals.calories}",
	)
	if (verbose && result.issues.isNotEmpty()) {
		printRecipeIssues(result)
	}
}

private fun printRecipeIssues(result: RecipeNutritionPersistResult) {
	result.issues.forEach { issue ->
		println("  [${issue.kind}] ${issue.rawText} -> ${issue.parsedName}")
	}
}

private fun printBackfillSummary(results: List<RecipeNutritionPersistResult>) {
	if (results.size <= 1) {
		return
	}

	val withTotals = results.count { result -> result.totals != null }
	val complete = results.count { result -> result.totals?.isComplete == true }
	val partial = results.count { result ->
		val totals = result.totals
		totals != null && !totals.isComplete
	}
	val withoutTotals = results.size - withTotals
	val issueCount = results.sumOf { result -> result.issues.size }

	println()
	println("Backfill summary")
	println("Recipes processed: ${results.size}")
	println("With calculated totals: $withTotals")
	println("Complete estimates: $complete")
	println("Partial estimates: $partial")
	println("Without totals: $withoutTotals")
	println("Ingredient issues: $issueCount")
}

private fun printUnmatchedIngredientReport(results: List<RecipeNutritionPersistResult>) {
	val unmatchedCounts = results
		.flatMap(RecipeNutritionPersistResult::issues)
		.filter { issue -> issue.kind == IngredientNutritionIssueKind.NO_FOOD_MATCH }
		.groupingBy(IngredientNutritionIssue::parsedName)
		.eachCount()
		.toList()
		.sortedWith(compareByDescending<Pair<String, Int>> { it.second }.thenBy { it.first })

	if (unmatchedCounts.isEmpty()) {
		return
	}

	println()
	println("Unmatched parsed ingredient names (${unmatchedCounts.size} unique)")
	unmatchedCounts.forEach { (parsedName, count) ->
		println("  $count x $parsedName")
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
