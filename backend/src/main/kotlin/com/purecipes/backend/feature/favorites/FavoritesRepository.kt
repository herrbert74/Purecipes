package com.purecipes.backend.feature.favorites

import com.purecipes.backend.feature.recipe.RecipeRepository
import com.purecipes.backend.feature.recipe.RecipeRepositorySql
import com.purecipes.backend.feature.recipe.getNullableMeasurementSystem
import com.purecipes.shared.domain.model.Cuisine
import com.purecipes.shared.domain.model.RecipeSummary
import com.purecipes.shared.domain.model.SearchResultsPage
import javax.sql.DataSource

class FavoritesRepository(
	private val dataSource: DataSource,
) {
	private val recipeRepository = RecipeRepository(dataSource)

	fun getFavoriteRecipesPage(userId: Long, pageNumber: Int, pageSize: Int): SearchResultsPage {
		val normalizedPageNumber = pageNumber.coerceAtLeast(1)
		val normalizedPageSize = pageSize.coerceIn(1, RecipeRepositorySql.SEARCH_WITH_FILTERS_MAX_LIMIT)
		val offset = (normalizedPageNumber - 1) * normalizedPageSize
		return dataSource.connection.use { conn ->
			val totalMatches = conn.prepareStatement(RecipeRepositorySql.FAVORITES_COUNT_SQL).use { ps ->
				ps.setLong(RecipeRepositorySql.FIRST_PARAMETER_INDEX, userId)
				ps.executeQuery().use { rs ->
					if (rs.next()) rs.getInt(1) else 0
				}
			}
			val items = conn.prepareStatement(RecipeRepositorySql.FAVORITES_PAGE_SQL).use { ps ->
				ps.setLong(RecipeRepositorySql.FIRST_PARAMETER_INDEX, userId)
				ps.setInt(RecipeRepositorySql.SECOND_PARAMETER_INDEX, normalizedPageSize)
				ps.setInt(RecipeRepositorySql.THIRD_PARAMETER_INDEX, offset)
				ps.executeQuery().use(::readFavoriteRecipes)
			}
			SearchResultsPage(items, normalizedPageNumber, normalizedPageSize, totalMatches)
		}
	}

	fun addFavorite(userId: Long, recipeId: Int): Boolean = dataSource.connection.use { conn ->
		if (!recipeRepository.recipeExists(conn, recipeId)) {
			return@use false
		}
		conn.prepareStatement(RecipeRepositorySql.ADD_FAVORITE_SQL).use { ps ->
			ps.setLong(1, userId)
			ps.setInt(2, recipeId)
			ps.executeUpdate()
		}
		true
	}

	fun removeFavorite(userId: Long, recipeId: Int): Boolean = dataSource.connection.use { conn ->
		if (!recipeRepository.recipeExists(conn, recipeId)) {
			return@use false
		}
		conn.prepareStatement(RecipeRepositorySql.REMOVE_COOKBOOK_RECIPES_FOR_USER_RECIPE_SQL).use { ps ->
			ps.setLong(1, userId)
			ps.setInt(2, recipeId)
			ps.executeUpdate()
		}
		conn.prepareStatement(RecipeRepositorySql.REMOVE_FAVORITE_SQL).use { ps ->
			ps.setLong(1, userId)
			ps.setInt(2, recipeId)
			ps.executeUpdate()
		}
		true
	}

	private fun readFavoriteRecipes(rs: java.sql.ResultSet): List<RecipeSummary> {
		val results = ArrayList<RecipeSummary>()
		while (rs.next()) {
			val recipeId = rs.getInt("id")
			results.add(
				RecipeSummary(
					id = recipeId,
					title = rs.getString("title"),
					cuisine = Cuisine.fromRawValue(rs.getString("cuisine")),
					imageUrl = rs.getString("image_url"),
					totalTime = rs.getObject("total_time") as? Int,
					measurementSystem = rs.getNullableMeasurementSystem("measurement_system")
						?: recipeRepository.loadMeasurementSystemForRecipe(recipeId),
					isFavorite = true,
				),
			)
		}
		return results
	}
}
