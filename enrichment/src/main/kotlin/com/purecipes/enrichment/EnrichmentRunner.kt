package com.purecipes.enrichment

import com.purecipes.shared.domain.model.CalorieRange
import com.purecipes.shared.domain.model.CookingMethod
import com.purecipes.shared.domain.model.Cuisine
import com.purecipes.shared.domain.model.DietaryPreference
import com.purecipes.shared.domain.model.DifficultyLevel
import com.purecipes.shared.domain.model.MealType
import java.nio.file.Files
import java.nio.file.Path
import java.sql.Connection
import java.sql.DriverManager

private const val BATCH_SIZE = 32
private const val CUISINE_THRESHOLD = 0.60f
private const val DIETARY_THRESHOLD = 0.55f
private const val LOW_CALORIE_THRESHOLD = 300.0
private const val HIGH_CALORIE_THRESHOLD = 600.0

fun main() {
	val modelPath = resolveModelPath(requiredSetting("USE_MODEL_PATH"))
	val dbUrl = requiredSetting("PURECIPES_DB_URL")
	val dbUser = requiredSetting("PURECIPES_DB_USER")
	val dbPassword = requiredSetting("PURECIPES_DB_PASSWORD")

	UseTextClassifier(modelPath).use { classifier ->
		val cuisineCentroids = classifier.buildClassCentroids(SeedExamples.cuisine)
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
					val cuisine = if (recipe.cuisine == null) {
						classifier.classifySingle(emb, cuisineCentroids, CUISINE_THRESHOLD)
					} else {
						null
					}
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
					updateRecipe(conn, recipe.id, cuisine, mealType, difficulty, cookingMethod, calorieRange, dietaryPreferences)
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
	val cuisine: String?,
	val mealType: String?,
	val difficulty: String?,
	val cookingMethod: String?,
	val calorieRange: String?,
	val dietaryPreferences: String?,
)

private fun requiredSetting(name: String): String =
	System.getenv(name)
		?: System.getProperty(name)
		?: error("Required setting $name is missing")

private fun resolveModelPath(configuredPath: String): String {
	val directCandidate = Path.of(configuredPath).toAbsolutePath().normalize()
	val parentCandidate = Path.of("..").resolve(configuredPath).toAbsolutePath().normalize()
	val candidate = sequenceOf(directCandidate, parentCandidate)
		.firstOrNull(Files::exists)
		?: error("USE_MODEL_PATH does not exist: $configuredPath")

	if (Files.isRegularFile(candidate.resolve("saved_model.pb"))) {
		return candidate.toString()
	}

	val nestedModelDir = Files.list(candidate).use { children ->
		children
			.filter { Files.isDirectory(it) && Files.isRegularFile(it.resolve("saved_model.pb")) }
			.findFirst()
			.orElse(null)
	}
	if (nestedModelDir != null) {
		return nestedModelDir.toString()
	}

	error("USE_MODEL_PATH must point to an extracted Universal Sentence Encoder SavedModel directory")
}

private fun loadRecipesToEnrich(conn: Connection): List<RecipeRow> {
	val sql = """
		SELECT
			r.id,
			COALESCE(r.title, '') || ' ' ||
			COALESCE(r.description, '') || ' ' ||
			COALESCE(string_agg(i.ingredient, ' '), '') AS recipe_text,
			MAX(n.calories) AS total_calories,
			r.cuisine,
			r.meal_type,
			r.difficulty,
			r.cooking_method,
			r.calorie_range,
			array_to_string(r.dietary_preferences, ',') AS dietary_preferences
		FROM recipes r
		LEFT JOIN ingredient_groups ig ON ig.recipe_id = r.id
		LEFT JOIN ingredients i ON i.ingredient_group_id = ig.id
		LEFT JOIN nutrition n ON n.recipe_id = r.id
		WHERE r.cuisine IS NULL
		   OR r.meal_type IS NULL
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
						cuisine = rs.getString("cuisine"),
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
	cuisine: Cuisine?,
	mealType: MealType?,
	difficulty: DifficultyLevel?,
	cookingMethod: CookingMethod?,
	calorieRange: CalorieRange?,
	dietaryPreferences: Set<DietaryPreference>?,
) {
	val setClauses = mutableListOf<String>()
	if (cuisine != null) setClauses.add("cuisine = ?")
	if (mealType != null) setClauses.add("meal_type = ?")
	if (difficulty != null) setClauses.add("difficulty = ?")
	if (cookingMethod != null) setClauses.add("cooking_method = ?")
	if (calorieRange != null) setClauses.add("calorie_range = ?")
	if (dietaryPreferences != null) setClauses.add("dietary_preferences = ?")

	if (setClauses.isEmpty()) return

	val sql = "UPDATE recipes SET ${setClauses.joinToString(", ")} WHERE id = ?"
	conn.prepareStatement(sql).use { ps ->
		var idx = 1
		if (cuisine != null) ps.setString(idx++, cuisine.name)
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
