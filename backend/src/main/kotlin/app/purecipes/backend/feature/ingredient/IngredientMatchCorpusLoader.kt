package app.purecipes.backend.feature.ingredient

import app.purecipes.backend.feature.search.IngredientVocabulary
import app.purecipes.shared.domain.model.IngredientCatalogue
import javax.sql.DataSource

internal object IngredientMatchCorpusLoader {

	fun load(dataSource: DataSource): IngredientMatchCorpus {
		val recipeIngredients = loadRecipeIngredientLines(dataSource)
		return IngredientMatchCorpus(
			recipeIngredients = recipeIngredients,
			vocabulary = buildIngredientVocabulary(recipeIngredients),
		)
	}

	private fun loadRecipeIngredientLines(dataSource: DataSource): Map<Int, List<String>> =
		dataSource.connection.use { connection ->
			connection.prepareStatement(IngredientMatchRepositorySql.RECIPE_INGREDIENT_LINES_SQL).use { statement ->
				statement.executeQuery().use { resultSet ->
					buildMap {
						while (resultSet.next()) {
							val recipeId = resultSet.getInt("recipe_id")
							val ingredient = resultSet.getString("ingredient").orEmpty().trim()
							if (ingredient.isNotEmpty()) {
								getOrPut(recipeId) { mutableListOf() }.add(ingredient)
							}
						}
					}.mapValues { (_, ingredients) -> ingredients.toList() }
				}
			}
		}

	private fun buildIngredientVocabulary(recipeIngredients: Map<Int, List<String>>): Set<String> {
		val vocabulary = LinkedHashSet(IngredientCatalogue.allItems)
		recipeIngredients.values.flatten().forEach { ingredientLine ->
			if (IngredientVocabulary.isIgnorableIngredientLine(ingredientLine)) {
				return@forEach
			}

			val trimmedLine = ingredientLine.trim()
			IngredientCatalogue.allItems.forEach { catalogueItem ->
				if (IngredientVocabulary.matchesAnyIngredient(trimmedLine, listOf(catalogueItem))) {
					vocabulary.add(catalogueItem)
				}
			}
			IngredientLineTokenExtractor.extractTokens(trimmedLine).forEach { token ->
				vocabulary.add(token)
			}
		}
		return vocabulary
	}
}
