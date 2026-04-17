package com.purecipes.enrichment

import com.purecipes.shared.domain.model.CalorieRange
import com.purecipes.shared.domain.model.CookingMethod
import com.purecipes.shared.domain.model.DietaryPreference
import com.purecipes.shared.domain.model.DifficultyLevel
import com.purecipes.shared.domain.model.MealType
import java.sql.Connection
import java.sql.DriverManager

private const val BATCH_SIZE = 32
private const val DIETARY_THRESHOLD = 0.55f
private const val LOW_CALORIE_THRESHOLD = 300.0
private const val HIGH_CALORIE_THRESHOLD = 600.0

fun main() {
	val modelPath = requireNotNull(System.getenv("USE_MODEL_PATH")) {
		"Environment variable USE_MODEL_PATH must be set to the Universal Sentence Encoder SavedModel path"
	}
	val dbUrl = requireNotNull(System.getenv("PURECIPES_DB_URL")) {
		"Environment variable PURECIPES_DB_URL must be set"
	}
	val dbUser = requireNotNull(System.getenv("PURECIPES_DB_USER")) {
		"Environment variable PURECIPES_DB_USER must be set"
	}
	val dbPassword = requireNotNull(System.getenv("PURECIPES_DB_PASSWORD")) {
		"Environment variable PURECIPES_DB_PASSWORD must be set"
	}

	UseTextClassifier(modelPath).use { classifier ->
		val mealTypeCentroids = classifier.buildClassCentroids(SeedExamples.mealType)
		val difficultyCentroids = classifier.buildClassCentroids(SeedExamples.difficultyLevel)
		val cookingMethodCentroids = classifier.buildClassCentroids(SeedExamples.cookingMethod)
		val dietaryPrefCentroids = classifier.buildClassCentroids(SeedExamples.dietaryPreference)

		DriverManager.getConnection(dbUrl, dbUser, dbPassword).use { conn ->
			val recipes = loadRecipesToEnrich(conn)
			println("Found ${recipes.size} recipes to enrich")

			recipes.chunked(BATCH_SIZE).forEachIndexed { batchIndex, batch ->
				println("Processing batch ${batchIndex + 1} (${batch.size} recipes)")
				val texts = batch.map { it.text }
				val embeddings = classifier.encodeTexts(texts)

				batch.forEachIndexed { i, recipe ->
					val emb = embeddings[i]
					val mealType = if (recipe.mealType == null) {
						classifier.classifySingle(emb, mealTypeCentroids)
					} else {
						null
					}
					val difficulty = if (recipe.difficulty == null) {
						classifier.classifySingle(emb, difficultyCentroids)
					} else {
						null
					}
					val cookingMethod = if (recipe.cookingMethod == null) {
						classifier.classifySingle(emb, cookingMethodCentroids)
					} else {
						null
					}
					val calorieRange = if (recipe.calorieRange == null) {
						calorieRangeFromNutrition(recipe.totalCalories)
					} else {
						null
					}
					val dietaryPreferences = if (recipe.dietaryPreferences == null) {
						classifier.classifyMultiLabel(emb, dietaryPrefCentroids, DIETARY_THRESHOLD)
					} else {
						null
					}
					updateRecipe(conn, recipe.id, mealType, difficulty, cookingMethod, calorieRange, dietaryPreferences)
				}
			}
		}
	}
	println("Enrichment complete")
}

private data class RecipeRow(
	val id: Int,
	val text: String,
	val totalCalories: Double?,
	val mealType: String?,
	val difficulty: String?,
	val cookingMethod: String?,
	val calorieRange: String?,
	val dietaryPreferences: String?,
)

private fun loadRecipesToEnrich(conn: Connection): List<RecipeRow> {
	val sql = """
		SELECT
			r.id,
			COALESCE(r.title, '') || ' ' ||
			COALESCE(r.description, '') || ' ' ||
			COALESCE(string_agg(i.name, ' '), '') AS recipe_text,
			SUM(CASE WHEN n.nutrient = 'calories' THEN n.amount ELSE NULL END) AS total_calories,
			r.meal_type,
			r.difficulty,
			r.cooking_method,
			r.calorie_range,
			array_to_string(r.dietary_preferences, ',') AS dietary_preferences
		FROM recipes r
		LEFT JOIN ingredient_groups ig ON ig.recipe_id = r.id
		LEFT JOIN ingredients i ON i.ingredient_group_id = ig.id
		LEFT JOIN nutrition n ON n.recipe_id = r.id
		WHERE r.meal_type IS NULL
		   OR r.difficulty IS NULL
		   OR r.cooking_method IS NULL
		   OR r.calorie_range IS NULL
		   OR r.dietary_preferences IS NULL
		GROUP BY r.id
		ORDER BY r.id
	""".trimIndent()

	return conn.prepareStatement(sql).use { ps ->
		ps.executeQuery().use { rs ->
			val rows = mutableListOf<RecipeRow>()
			while (rs.next()) {
				rows.add(
					RecipeRow(
						id = rs.getInt("id"),
						text = rs.getString("recipe_text") ?: "",
						totalCalories = rs.getDouble("total_calories").takeIf { !rs.wasNull() },
						mealType = rs.getString("meal_type"),
						difficulty = rs.getString("difficulty"),
						cookingMethod = rs.getString("cooking_method"),
						calorieRange = rs.getString("calorie_range"),
						dietaryPreferences = rs.getString("dietary_preferences"),
					),
				)
			}
			rows
		}
	}
}

private fun calorieRangeFromNutrition(totalCalories: Double?): CalorieRange? {
	totalCalories ?: return null
	return when {
		totalCalories < LOW_CALORIE_THRESHOLD -> CalorieRange.LOW
		totalCalories <= HIGH_CALORIE_THRESHOLD -> CalorieRange.MEDIUM
		else -> CalorieRange.HIGH
	}
}

private fun updateRecipe(
	conn: Connection,
	id: Int,
	mealType: MealType?,
	difficulty: DifficultyLevel?,
	cookingMethod: CookingMethod?,
	calorieRange: CalorieRange?,
	dietaryPreferences: Set<DietaryPreference>?,
) {
	val setClauses = mutableListOf<String>()
	if (mealType != null) setClauses.add("meal_type = ?")
	if (difficulty != null) setClauses.add("difficulty = ?")
	if (cookingMethod != null) setClauses.add("cooking_method = ?")
	if (calorieRange != null) setClauses.add("calorie_range = ?")
	if (dietaryPreferences != null) setClauses.add("dietary_preferences = ?")

	if (setClauses.isEmpty()) return

	val sql = "UPDATE recipes SET ${setClauses.joinToString(", ")} WHERE id = ?"
	conn.prepareStatement(sql).use { ps ->
		var idx = 1
		if (mealType != null) ps.setString(idx++, mealType.name)
		if (difficulty != null) ps.setString(idx++, difficulty.name)
		if (cookingMethod != null) ps.setString(idx++, cookingMethod.name)
		if (calorieRange != null) ps.setString(idx++, calorieRange.name)
		if (dietaryPreferences != null) {
			val arr = conn.createArrayOf("text", dietaryPreferences.map { it.name }.toTypedArray())
			ps.setArray(idx++, arr)
		}
		ps.setInt(idx, id)
		ps.executeUpdate()
	}
}
