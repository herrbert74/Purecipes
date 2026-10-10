package app.purecipes.backend.feature.recipe

import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.domain.model.cuisineFromRawValue
import java.sql.PreparedStatement
import java.sql.ResultSet

internal fun RecipeRepository.readRecipeSummary(rs: ResultSet): RecipeSummary {
	val recipeId = rs.getInt("id")
	return RecipeSummary(
		id = recipeId,
		title = rs.getString("title"),
		cuisine = cuisineFromRawValue(rs.getString("cuisine")),
		imageUrl = rs.getString("image_url"),
		totalTime = rs.getObject("total_time") as? Int,
		measurementSystem = rs.getNullableMeasurementSystem("measurement_system")
			?: loadMeasurementSystemForRecipe(recipeId),
		isPrivate = rs.getBoolean("is_private"),
	)
}

internal fun RecipeRepository.readSearchRecipeCandidates(ps: PreparedStatement): List<SearchRecipeCandidate> =
	ps.executeQuery().use { rs ->
		buildList {
			while (rs.next()) {
				val createdAt = rs.getTimestamp("created_at")
				add(
					SearchRecipeCandidate(
						summary = readRecipeSummary(rs),
						completenessScore = rs.getInt("completeness_score"),
						createdAtMillis = createdAt?.time ?: 0L,
					),
				)
			}
		}
	}
