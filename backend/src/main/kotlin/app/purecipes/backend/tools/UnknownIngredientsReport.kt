package app.purecipes.backend.tools

import app.purecipes.backend.db.Db
import app.purecipes.backend.feature.search.IngredientVocabulary
import app.purecipes.shared.domain.model.IngredientCatalogue

private const val ARG_OUTPUT = "--output"

private data class RecipeIngredientRow(
	val recipeId: Int,
	val recipeTitle: String,
	val ingredient: String,
)

private data class UnknownIngredientEntry(
	val recipeId: Int,
	val recipeTitle: String,
	val ingredient: String,
)

fun main(args: Array<String>) {
	val outputPath = args.toList().windowed(size = 2, step = 1)
		.firstOrNull { it.first() == ARG_OUTPUT }
		?.last()

	val allowedIngredients = loadAllowedIngredients()
	if (allowedIngredients.isEmpty()) {
		println("No allowed app ingredients found. Cannot run unknown ingredient report.")
		return
	}

	val unknownEntries = collectUnknownIngredients(allowedIngredients)

	val report = buildString {
		appendLine("Unknown ingredient report")
		appendLine("Allowed app ingredients: ${allowedIngredients.size}")
		appendLine("Recipes with unknown ingredients: ${unknownEntries.size}")
		appendLine()

		unknownEntries.forEach { (recipeKey, ingredients) ->
			appendLine("Recipe ${recipeKey.recipeId}: ${recipeKey.recipeTitle}")
			ingredients.sorted().forEach { ingredient ->
				appendLine("- $ingredient")
			}
			appendLine()
		}
	}

	print(report)

	if (outputPath != null) {
		java.io.File(outputPath).writeText(report)
		println("Saved report to $outputPath")
	}
}

private fun collectUnknownIngredients(
	allowedIngredients: Set<String>,
): Map<RecipeKey, Set<String>> {
	val allRows = Db.create().dataSource.connection.use { conn ->
		conn.prepareStatement(
			"""
			SELECT r.id, r.title, i.ingredient
			FROM recipes r
			JOIN ingredient_groups ig ON ig.recipe_id = r.id
			JOIN ingredients i ON i.ingredient_group_id = ig.id
			WHERE i.ingredient IS NOT NULL
			ORDER BY r.id, ig.order_index, i.order_index
			""".trimIndent(),
		).use { ps ->
			ps.executeQuery().use { rs ->
				buildList {
					while (rs.next()) {
						add(
							RecipeIngredientRow(
								recipeId = rs.getInt("id"),
								recipeTitle = rs.getString("title") ?: "",
								ingredient = rs.getString("ingredient") ?: "",
							),
						)
					}
				}
			}
		}
	}

	val unknownEntries = allRows.mapNotNull { row ->
		val normalizedIngredient = row.ingredient.trim()
		if (normalizedIngredient.isEmpty()) {
			return@mapNotNull null
		}
		if (IngredientVocabulary.isIgnorableIngredientLine(normalizedIngredient)) {
			return@mapNotNull null
		}

		val matchesAnyAllowed = IngredientVocabulary.matchesAnyIngredient(
			ingredientLine = normalizedIngredient,
			ingredientNames = allowedIngredients,
		) || IngredientVocabulary.isPantryIngredient(normalizedIngredient)

		if (matchesAnyAllowed) {
			null
		} else {
			UnknownIngredientEntry(
				recipeId = row.recipeId,
				recipeTitle = row.recipeTitle,
				ingredient = row.ingredient.trim(),
			)
		}
	}

	return unknownEntries
		.groupBy { RecipeKey(recipeId = it.recipeId, recipeTitle = it.recipeTitle) }
		.mapValues { (_, entries) -> entries.map { it.ingredient }.toSet() }
		.toSortedMap(compareBy<RecipeKey> { it.recipeId }.thenBy { it.recipeTitle })
}

private fun loadAllowedIngredients(): Set<String> = IngredientCatalogue.allItems

private data class RecipeKey(
	val recipeId: Int,
	val recipeTitle: String,
)
