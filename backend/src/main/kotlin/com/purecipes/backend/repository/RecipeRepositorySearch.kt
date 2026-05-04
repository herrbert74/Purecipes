package com.purecipes.backend.repository

import com.purecipes.shared.domain.model.CookingTimeRange
import com.purecipes.shared.domain.model.Cuisine
import com.purecipes.shared.domain.model.RecipeSummary
import com.purecipes.shared.domain.model.SearchRequest
import com.purecipes.shared.domain.model.SearchResultsPage
import java.sql.Connection

fun RecipeRepository.searchByKeyword(
	keyword: String,
	pageNumber: Int = 1,
	pageSize: Int = 20,
): List<RecipeSummary> {
	val trimmed = keyword.trim()
	if (trimmed.isEmpty()) return emptyList()
	val normalizedPageNumber = pageNumber.coerceAtLeast(1)
	val normalizedPageSize = pageSize.coerceIn(1, RecipeRepositorySql.SEARCH_WITH_FILTERS_MAX_LIMIT)
	val offset = (normalizedPageNumber - 1) * normalizedPageSize

	val like = "%${trimmed.lowercase()}%"
	val sql = """
		SELECT id, title, cuisine, image_url, total_time, measurement_system
		FROM recipes
		WHERE LOWER(title) LIKE ? OR LOWER(cuisine) LIKE ?
		ORDER BY created_at DESC
		LIMIT ? OFFSET ?
	""".trimIndent()

	return searchRecipes(sql, like, normalizedPageSize, offset)
}

fun RecipeRepository.searchByKeywordPaginated(
	keyword: String,
	pageNumber: Int = 1,
	pageSize: Int = 20,
): SearchResultsPage {
	val trimmed = keyword.trim()
	if (trimmed.isEmpty()) {
		val normalizedPageSize = pageSize.coerceIn(1, RecipeRepositorySql.SEARCH_WITH_FILTERS_MAX_LIMIT)
		return SearchResultsPage(
			items = emptyList(),
			pageNumber = 1,
			pageSize = normalizedPageSize,
			totalMatches = 0,
		)
	}

	val normalizedPageNumber = pageNumber.coerceAtLeast(1)
	val normalizedPageSize = pageSize.coerceIn(1, RecipeRepositorySql.SEARCH_WITH_FILTERS_MAX_LIMIT)
	val like = "%${trimmed.lowercase()}%"
	val items = searchByKeyword(trimmed, normalizedPageNumber, normalizedPageSize)
	val totalMatches = countRecipesByKeyword(dataSource, like)
	return SearchResultsPage(
		items = items,
		pageNumber = normalizedPageNumber,
		pageSize = normalizedPageSize,
		totalMatches = totalMatches,
	)
}

fun RecipeRepository.searchWithFilters(request: SearchRequest, userId: Long? = null): SearchResultsPage {
	val normalizedPageNumber = request.pageNumber.coerceAtLeast(1)
	val normalizedPageSize = request.pageSize.coerceIn(1, RecipeRepositorySql.SEARCH_WITH_FILTERS_MAX_LIMIT)
	val offset = (normalizedPageNumber - 1) * normalizedPageSize
	val filters = request.filters
	val conditions = mutableListOf<String>()
	val params = mutableListOf<Any>()

	if (request.query.isNotBlank()) {
		val like = "%${request.query.trim().lowercase()}%"
		conditions.add("(LOWER(r.title) LIKE ? OR LOWER(r.cuisine) LIKE ?)")
		params.add(like)
		params.add(like)
	}

	if (filters.cuisines.isNotEmpty() && filters.cuisines.size < Cuisine.entries.size) {
		val placeholders = filters.cuisines.joinToString(",") { "?" }
		conditions.add("r.cuisine IN ($placeholders)")
		filters.cuisines.forEach { params.add(it.displayName) }
	}

	if (filters.cookingTimeRanges.isNotEmpty() && filters.cookingTimeRanges.size < CookingTimeRange.entries.size) {
		val timeParts = filters.cookingTimeRanges.map { range ->
			when (range) {
				CookingTimeRange.UNDER_15 -> "r.total_time <= 15"
				CookingTimeRange.UNDER_30 -> "r.total_time <= 30"
				CookingTimeRange.UNDER_60 -> "r.total_time <= 60"
				CookingTimeRange.OVER_60 -> "r.total_time > 60"
			}
		}
		conditions.add("(${timeParts.joinToString(" OR ")})")
	}

	addEnrichmentFilterConditions(filters, conditions, params)
	val whereClause = if (conditions.isEmpty()) "" else "WHERE ${conditions.joinToString(" AND ")}"

	val (items, totalMatches) = dataSource.connection.use { conn ->
		val availableIngredients = if (userId != null) {
			loadAvailableIngredientsForUser(conn, userId)
		} else {
			emptyList()
		}
		if (availableIngredients.isEmpty()) {
			val total = countSearchWithFiltersRecipes(conn, whereClause, params)
			val page = querySearchWithFiltersRecipes(
				conn = conn,
				whereClause = whereClause,
				params = params,
				limit = normalizedPageSize,
				offset = offset,
				executeQuery = this::executeQuery,
			)
			page to total
		} else {
			val filtered = querySearchWithFiltersRecipes(
				conn = conn,
				whereClause = whereClause,
				params = params,
				executeQuery = this::executeQuery,
			).filter { summary ->
				isRecipeCoveredByAvailableIngredients(
					recipeId = summary.id,
					availableIngredients = availableIngredients,
					loadIngredientGroups = { recipeId -> loadIngredientGroupsForRecipe(conn, recipeId) },
				)
			}
			filtered.drop(offset).take(normalizedPageSize) to filtered.size
		}
	}

	return SearchResultsPage(
		items = items,
		pageNumber = normalizedPageNumber,
		pageSize = normalizedPageSize,
		totalMatches = totalMatches,
	)
}

private fun loadAvailableIngredientsForUser(conn: Connection, userId: Long): List<String> {
	return conn.prepareStatement(RecipeRepositorySql.GET_USER_PANTRY_SQL).use { ps ->
		ps.setLong(RecipeRepositorySql.FIRST_PARAMETER_INDEX, userId)
		ps.executeQuery().use { rs ->
			buildList {
				while (rs.next()) {
					val ingredient = rs.getString("ingredient")?.trim().orEmpty()
					if (ingredient.isNotEmpty()) {
						add(ingredient)
					}
				}
			}.distinct()
		}
	}
}
