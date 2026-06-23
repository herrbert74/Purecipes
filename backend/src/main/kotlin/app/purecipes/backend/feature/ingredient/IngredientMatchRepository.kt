package app.purecipes.backend.feature.ingredient

import app.purecipes.backend.feature.search.IngredientVocabulary
import app.purecipes.shared.domain.ingredient.ClassifiedIngredientMatches
import app.purecipes.shared.domain.ingredient.IngredientLookup
import app.purecipes.shared.domain.model.IngredientCatalogue
import app.purecipes.shared.domain.model.IngredientMatchCount
import app.purecipes.shared.domain.model.IngredientMatchResponse
import app.purecipes.shared.domain.model.LikelyIngredientMatch
import javax.sql.DataSource

class IngredientMatchRepository(
	private val dataSource: DataSource,
) {

	fun matchIngredient(name: String): IngredientMatchResponse {
		val query = name.trim()
		if (query.isBlank()) {
			return IngredientMatchResponse(query = query)
		}

		val recipeIngredients = loadRecipeIngredientLines()
		val vocabulary = buildIngredientVocabulary(recipeIngredients)
		val classified = IngredientLookup.classifyIngredientMatches(query, vocabulary)
		val exactRecipeIds = recipeIdsMatchingQuery(query, recipeIngredients)

		val exactMatches = buildExactMatches(
			query = query,
			classified = classified,
			exactRecipeIds = exactRecipeIds,
			recipeIngredients = recipeIngredients,
		)

		val likelyMatches = classified.likelyMatches.mapNotNull { match ->
			val recipeIds = recipeIdsMatchingIngredient(match.ingredient, recipeIngredients) - exactRecipeIds
			if (recipeIds.isEmpty()) {
				null
			} else {
				LikelyIngredientMatch(
					ingredient = match.ingredient,
					recipeCount = recipeIds.size,
					confidence = match.confidence,
				)
			}
		}

		return IngredientMatchResponse(
			query = query,
			exactMatches = exactMatches,
			likelyMatches = likelyMatches,
		)
	}

	private fun loadRecipeIngredientLines(): Map<Int, List<String>> =
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

	private fun buildExactMatches(
		query: String,
		classified: ClassifiedIngredientMatches,
		exactRecipeIds: Set<Int>,
		recipeIngredients: Map<Int, List<String>>,
	): List<IngredientMatchCount> {
		if (exactRecipeIds.isEmpty()) {
			return classified.exactMatches.mapNotNull { match ->
				val recipeCount = recipeIdsMatchingIngredient(match.ingredient, recipeIngredients).size
				if (recipeCount == 0) {
					null
				} else {
					IngredientMatchCount(
						ingredient = match.ingredient,
						recipeCount = recipeCount,
					)
				}
			}
		}

		val catalogueName = IngredientLookup.resolveCatalogueIngredient(query, IngredientCatalogue.allItems)
		val bestClassifiedMatch = classified.exactMatches
			.maxByOrNull { match ->
				recipeIdsMatchingIngredient(match.ingredient, recipeIngredients).size
			}
			?.ingredient
		val displayName = catalogueName ?: bestClassifiedMatch ?: titleCaseQuery(query)

		return listOf(
			IngredientMatchCount(
				ingredient = displayName,
				recipeCount = exactRecipeIds.size,
			),
		)
	}

	private fun titleCaseQuery(query: String): String =
		query.trim().replaceFirstChar { character ->
			if (character.isLowerCase()) {
				character.titlecase()
			} else {
				character.toString()
			}
		}

	private fun recipeIdsMatchingIngredient(
		ingredient: String,
		recipeIngredients: Map<Int, List<String>>,
	): Set<Int> =
		recipeIngredients.filter { (_, ingredientLines) ->
			ingredientLines.any { ingredientLine ->
				IngredientVocabulary.matchesAnyIngredient(ingredientLine, listOf(ingredient))
			}
		}.keys

	private fun recipeIdsMatchingQuery(
		query: String,
		recipeIngredients: Map<Int, List<String>>,
	): Set<Int> =
		recipeIngredients.filter { (_, ingredientLines) ->
			ingredientLines.any { ingredientLine ->
				IngredientVocabulary.matchesAnyIngredient(ingredientLine, listOf(query))
			}
		}.keys
}
