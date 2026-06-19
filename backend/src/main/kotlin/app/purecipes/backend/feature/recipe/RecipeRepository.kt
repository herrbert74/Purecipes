package app.purecipes.backend.feature.recipe

import app.purecipes.backend.feature.search.IngredientVocabulary
import app.purecipes.shared.domain.ingredient.ingredientSlots
import app.purecipes.shared.domain.ingredient.slotIsOptional
import app.purecipes.shared.domain.model.CalorieRange
import app.purecipes.shared.domain.model.CookingMethod
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.DietaryPreference
import app.purecipes.shared.domain.model.DifficultyLevel
import app.purecipes.shared.domain.model.IngredientGroup
import app.purecipes.shared.domain.model.IngredientRequirement
import app.purecipes.shared.domain.model.MealType
import app.purecipes.shared.domain.model.MeasurementSystem
import app.purecipes.shared.domain.model.RecipeDetails
import app.purecipes.shared.domain.model.RecipeIngredient
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.domain.model.RecipeWriteRequest
import app.purecipes.shared.domain.model.cuisineFromRawValue
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement
import javax.sql.DataSource

class RecipeRepository(
	internal val dataSource: DataSource,
) {

	fun getRecipeDetails(recipeId: Int, userId: Long? = null): RecipeDetails? = dataSource.connection.use { conn ->
		val recipeRecord = loadRecipeRecord(conn, recipeId) ?: return@use null
		buildRecipeDetails(conn, recipeRecord, userId)
	}

	fun getRecipesCreatedByUser(userId: Long): List<RecipeDetails> = dataSource.connection.use { conn ->
		loadCreatedRecipeRecords(conn, userId).map { recipeRecord ->
			buildRecipeDetails(conn, recipeRecord, userId)
		}
	}

	fun createRecipe(userId: Long, request: RecipeWriteRequest): RecipeDetails = dataSource.connection.use { conn ->
		runInTransaction(conn) {
			val recipeId = insertRecipe(conn, userId, request)
			writeIngredients(conn, recipeId, request.ingredientGroups)
			writeSteps(conn, recipeId, request.steps)
			buildRecipeDetails(
				conn,
				loadRecipeRecord(conn, recipeId) ?: error("Recipe missing after create"),
				userId,
			)
		}
	}

	fun updateRecipe(
		userId: Long,
		recipeId: Int,
		request: RecipeWriteRequest,
	): RecipeDetails? = dataSource.connection.use { conn ->
		if (!isRecipeOwnedByUser(conn, recipeId, userId)) {
			return@use null
		}

		runInTransaction(conn) {
			updateRecipeRow(conn, recipeId, request)
			deleteRecipeChildren(conn, recipeId)
			writeIngredients(conn, recipeId, request.ingredientGroups)
			writeSteps(conn, recipeId, request.steps)
			buildRecipeDetails(
				conn,
				loadRecipeRecord(conn, recipeId) ?: error("Recipe missing after update"),
				userId,
			)
		}
	}

	private fun <T> runInTransaction(conn: java.sql.Connection, block: () -> T): T {
		val originalAutoCommit = conn.autoCommit
		conn.autoCommit = false
		var committed = false
		try {
			val result = block()
			conn.commit()
			committed = true
			return result
		} finally {
			if (!committed) {
				conn.rollback()
			}
			conn.autoCommit = originalAutoCommit
		}
	}

	private fun loadRecipeRecord(conn: java.sql.Connection, recipeId: Int): RecipeRecord? {
		return conn.prepareStatement(RecipeRepositorySql.RECIPE_SQL).use { ps ->
			ps.setInt(1, recipeId)
			ps.executeQuery().use { rs ->
				if (!rs.next()) return@use null

				RecipeRecord(
					id = rs.getInt("id"),
					title = rs.getString("title"),
					description = rs.getNullableString("description"),
					instructions = rs.getNullableString("instructions"),
					totalTime = rs.getObject("total_time") as? Int,
					yields = rs.getNullableString("yields"),
					imageUrl = rs.getNullableString("image_url"),
					cuisine = rs.getNullableString("cuisine"),
					mealType = rs.getNullableString("meal_type"),
					measurementSystem = rs.getNullableMeasurementSystem("measurement_system"),
					difficulty = rs.getNullableString("difficulty"),
					cookingMethod = rs.getNullableString("cooking_method"),
					calorieRange = rs.getNullableString("calorie_range"),
					dietaryPreferences = rs.getStringArray("dietary_preferences"),
					tags = rs.getStringArray("tags"),
				)
			}
		}
	}

	private fun loadCreatedRecipeRecords(conn: java.sql.Connection, userId: Long): List<RecipeRecord> {
		return conn.prepareStatement(RecipeRepositorySql.CREATED_RECIPES_SQL).use { ps ->
			ps.setLong(RecipeRepositorySql.FIRST_PARAMETER_INDEX, userId)
			ps.executeQuery().use(::readRecipeRecords)
		}
	}

	private fun readRecipeRecords(rs: ResultSet): List<RecipeRecord> {
		val records = mutableListOf<RecipeRecord>()
		while (rs.next()) {
			records += rs.toRecipeRecord()
		}
		return records
	}

	private fun buildRecipeDetails(
		conn: java.sql.Connection,
		recipeRecord: RecipeRecord,
		userId: Long?,
	): RecipeDetails {
		val ingredientGroups = loadIngredientGroupsForRecipe(conn, recipeRecord.id)
		val measurementSystem = recipeRecord.measurementSystem ?: detectMeasurementSystem(ingredientGroups)
		val isFavorite = isFavorite(conn, recipeRecord.id, userId)
		val steps = loadStepsForRecipe(conn, recipeRecord.id, recipeRecord.instructions)
		val mealType = recipeRecord.mealType?.let { runCatching { MealType.valueOf(it) }.getOrNull() }
		val nutrition = RecipeNutritionDetailsLoader.load(conn, recipeRecord.id)

		return RecipeDetails(
			id = recipeRecord.id,
			title = recipeRecord.title,
			description = recipeRecord.description ?: buildDescription(
				cuisine = cuisineFromRawValue(recipeRecord.cuisine),
				mealType = mealType,
				totalTime = recipeRecord.totalTime,
				yields = recipeRecord.yields,
			),
			imageUrl = recipeRecord.imageUrl,
			ingredientGroups = ingredientGroups,
			steps = steps,
			totalTime = recipeRecord.totalTime,
			yields = recipeRecord.yields,
			cuisine = cuisineFromRawValue(recipeRecord.cuisine),
			measurementSystem = measurementSystem,
			isFavorite = isFavorite,
			mealType = mealType,
			difficultyLevel = recipeRecord.difficulty?.let { runCatching { DifficultyLevel.valueOf(it) }.getOrNull() },
			cookingMethod = recipeRecord.cookingMethod?.let { runCatching { CookingMethod.valueOf(it) }.getOrNull() },
			calorieRange = recipeRecord.calorieRange?.let { runCatching { CalorieRange.valueOf(it) }.getOrNull() },
			dietaryPreferences = recipeRecord.dietaryPreferences
				.mapNotNull { runCatching { DietaryPreference.valueOf(it) }.getOrNull() }
				.toSet(),
			tags = recipeRecord.tags.toSet(),
			nutrition = nutrition,
		)
	}

	@Suppress("MagicNumber")
	internal fun searchRecipes(
		sql: String,
		like: String,
		pageSize: Int,
		offset: Int = 0
	): ArrayList<RecipeSummary> = dataSource.connection.use { conn ->
		conn.prepareStatement(sql).use { ps ->
			ps.setString(1, like)
			ps.setString(2, like)
			ps.setInt(3, pageSize)
			ps.setInt(4, offset)

			return executeQuery(ps)
		}
	}

	internal fun executeQuery(ps: PreparedStatement): ArrayList<RecipeSummary> = ps.executeQuery().use { rs ->
		val results = ArrayList<RecipeSummary>()
		while (rs.next()) {
			val recipeId = rs.getInt("id")
			results.add(
				RecipeSummary(
					id = recipeId,
					title = rs.getString("title"),
					cuisine = cuisineFromRawValue(rs.getString("cuisine")),
					imageUrl = rs.getString("image_url"),
					totalTime = rs.getObject("total_time") as? Int,
					measurementSystem = rs.getNullableMeasurementSystem("measurement_system")
						?: loadMeasurementSystemForRecipe(recipeId),
				)
			)
		}
		return results
	}

	internal fun loadIngredientGroupsForRecipe(
		conn: java.sql.Connection,
		recipeId: Int,
	): List<IngredientGroup> = loadIngredientGroups(
		ps = conn.prepareStatement(RecipeRepositorySql.INGREDIENT_GROUPS_SQL).also {
			it.setInt(1, recipeId)
		},
	)

	internal fun loadIngredientGroups(ps: PreparedStatement): List<IngredientGroup> = ps.use { statement ->
		statement.executeQuery().use { rs ->
			val groupsById = linkedMapOf<Int, IngredientGroupAccumulator>()

			while (rs.next()) {
				val groupId = rs.getInt("group_id")
				val group = groupsById.getOrPut(groupId) {
					IngredientGroupAccumulator(name = rs.getNullableString("group_name"))
				}
				readRecipeIngredient(rs)?.let(group.ingredients::add)
			}

			groupsById.values.map { accumulator ->
				IngredientGroup(
					name = accumulator.name,
					ingredients = accumulator.ingredients,
				)
			}
		}
	}

	private fun readRecipeIngredient(rs: ResultSet): RecipeIngredient? {
		val ingredientText = rs.getNullableString("ingredient") ?: return null
		val requirement = rs.getNullableString("requirement")
			?.let { value -> runCatching { IngredientRequirement.valueOf(value) }.getOrNull() }
			?: IngredientRequirement.REQUIRED
		return RecipeIngredient(
			text = ingredientText,
			requirement = requirement,
			alternativeGroupKey = rs.getObject("alternative_group_key") as? Int,
		)
	}

	private fun loadStepsForRecipe(
		conn: java.sql.Connection,
		recipeId: Int,
		instructions: String?,
	): List<String> = loadSteps(
		ps = conn.prepareStatement(RecipeRepositorySql.STEPS_SQL).also {
			it.setInt(1, recipeId)
		},
		instructions = instructions,
	)

	private fun loadSteps(ps: PreparedStatement, instructions: String?): List<String> = ps.use { statement ->
		statement.executeQuery().use { rs ->
			val steps = mutableListOf<String>()
			while (rs.next()) {
				rs.getNullableString("step")
					?.takeIf { it.isNotBlank() }
					?.let(steps::add)
			}

			steps.ifEmpty {
				instructions
					.orEmpty()
					.lineSequence()
					.map(String::trim)
					.filter(String::isNotEmpty)
					.toList()
			}
		}
	}

	private fun isFavorite(conn: java.sql.Connection, recipeId: Int, userId: Long?): Boolean {
		if (userId == null) {
			return false
		}
		return conn.prepareStatement(RecipeRepositorySql.IS_FAVORITE_SQL).use { ps ->
			ps.setLong(1, userId)
			ps.setInt(2, recipeId)
			ps.executeQuery().use { rs ->
				rs.next() && rs.getBoolean("is_favorite")
			}
		}
	}

	private fun isRecipeOwnedByUser(conn: java.sql.Connection, recipeId: Int, userId: Long): Boolean {
		return conn.prepareStatement(RecipeRepositorySql.RECIPE_OWNED_BY_USER_SQL).use { ps ->
			ps.setInt(1, recipeId)
			ps.setLong(2, userId)
			ps.executeQuery().use { rs ->
				rs.next()
			}
		}
	}

	internal fun recipeExists(conn: java.sql.Connection, recipeId: Int): Boolean {
		return conn.prepareStatement(RecipeRepositorySql.RECIPE_EXISTS_SQL).use { ps ->
			ps.setInt(1, recipeId)
			ps.executeQuery().use { rs ->
				rs.next()
			}
		}
	}

	private fun deleteRecipeChildren(conn: java.sql.Connection, recipeId: Int) {
		conn.prepareStatement(RecipeRepositorySql.DELETE_INGREDIENTS_FOR_RECIPE_SQL).use { ps ->
			ps.setInt(1, recipeId)
			ps.executeUpdate()
		}
		conn.prepareStatement(RecipeRepositorySql.DELETE_INGREDIENT_GROUPS_SQL).use { ps ->
			ps.setInt(1, recipeId)
			ps.executeUpdate()
		}
		conn.prepareStatement(RecipeRepositorySql.DELETE_INSTRUCTION_STEPS_SQL).use { ps ->
			ps.setInt(1, recipeId)
			ps.executeUpdate()
		}
	}

	private fun writeIngredients(conn: java.sql.Connection, recipeId: Int, ingredientGroups: List<IngredientGroup>) {
		ingredientGroups.forEachIndexed { groupIndex, group ->
			val ingredients = group.ingredients.mapNotNull { ingredient ->
				ingredient.text.trim().takeIf(String::isNotEmpty)?.let { text ->
					ingredient.copy(text = text)
				}
			}
			if (ingredients.isEmpty()) {
				return@forEachIndexed
			}

			val groupId = insertIngredientGroup(conn, recipeId, group.name, groupIndex)

			conn.prepareStatement(RecipeRepositorySql.CREATE_INGREDIENT_SQL).use { ps ->
				ingredients.forEachIndexed { ingredientIndex, ingredient ->
					ps.setInt(RecipeRepositorySql.FIRST_PARAMETER_INDEX, groupId)
					ps.setString(RecipeRepositorySql.SECOND_PARAMETER_INDEX, ingredient.text)
					ps.setInt(RecipeRepositorySql.THIRD_PARAMETER_INDEX, ingredientIndex)
					ps.setString(RecipeRepositorySql.FOURTH_PARAMETER_INDEX, ingredient.requirement.name)
					val alternativeGroupKey = ingredient.alternativeGroupKey
					if (alternativeGroupKey == null) {
						ps.setNull(RecipeRepositorySql.FIFTH_PARAMETER_INDEX, java.sql.Types.INTEGER)
					} else {
						ps.setInt(RecipeRepositorySql.FIFTH_PARAMETER_INDEX, alternativeGroupKey)
					}
					ps.addBatch()
				}
				ps.executeBatch()
			}
		}
	}

	private fun writeSteps(conn: java.sql.Connection, recipeId: Int, steps: List<String>) {
		val normalizedSteps = steps.map(String::trim).filter(String::isNotEmpty)
		if (normalizedSteps.isEmpty()) {
			return
		}

		conn.prepareStatement(RecipeRepositorySql.CREATE_INSTRUCTION_STEP_SQL).use { ps ->
			normalizedSteps.forEachIndexed { stepIndex, step ->
				ps.setInt(RecipeRepositorySql.FIRST_PARAMETER_INDEX, recipeId)
				ps.setString(RecipeRepositorySql.SECOND_PARAMETER_INDEX, step)
				ps.setInt(RecipeRepositorySql.THIRD_PARAMETER_INDEX, stepIndex)
				ps.addBatch()
			}
			ps.executeBatch()
		}
	}

	private fun insertRecipe(conn: java.sql.Connection, userId: Long, request: RecipeWriteRequest): Int {
		return conn.prepareStatement(RecipeRepositorySql.CREATE_RECIPE_SQL, Statement.RETURN_GENERATED_KEYS).use { ps ->
			val measurementSystem = detectMeasurementSystem(request.ingredientGroups)
			ps.setString(RecipeRepositorySql.FIRST_PARAMETER_INDEX, request.title.trim())
			ps.setString(RecipeRepositorySql.SECOND_PARAMETER_INDEX, request.description.trim())
			ps.setString(RecipeRepositorySql.THIRD_PARAMETER_INDEX, request.steps.joinToString(separator = "\n"))
			ps.setObject(RecipeRepositorySql.FOURTH_PARAMETER_INDEX, request.totalTime)
			ps.setString(RecipeRepositorySql.FIFTH_PARAMETER_INDEX, request.yields?.trim())
			ps.setString(RecipeRepositorySql.SIXTH_PARAMETER_INDEX, request.imageUrl?.trim())
			ps.setString(RecipeRepositorySql.SEVENTH_PARAMETER_INDEX, request.cuisine?.displayName)
			ps.setString(RecipeRepositorySql.EIGHTH_PARAMETER_INDEX, measurementSystem?.name)
			ps.setLong(RecipeRepositorySql.NINTH_PARAMETER_INDEX, userId)
			ps.executeUpdate()
			ps.generatedId()
		}
	}

	private fun updateRecipeRow(conn: java.sql.Connection, recipeId: Int, request: RecipeWriteRequest) {
		conn.prepareStatement(RecipeRepositorySql.UPDATE_RECIPE_SQL).use { ps ->
			val measurementSystem = detectMeasurementSystem(request.ingredientGroups)
			ps.setString(RecipeRepositorySql.FIRST_PARAMETER_INDEX, request.title.trim())
			ps.setString(RecipeRepositorySql.SECOND_PARAMETER_INDEX, request.description.trim())
			ps.setString(RecipeRepositorySql.THIRD_PARAMETER_INDEX, request.steps.joinToString(separator = "\n"))
			ps.setObject(RecipeRepositorySql.FOURTH_PARAMETER_INDEX, request.totalTime)
			ps.setString(RecipeRepositorySql.FIFTH_PARAMETER_INDEX, request.yields?.trim())
			ps.setString(RecipeRepositorySql.SIXTH_PARAMETER_INDEX, request.imageUrl?.trim())
			ps.setString(RecipeRepositorySql.SEVENTH_PARAMETER_INDEX, request.cuisine?.displayName)
			ps.setString(RecipeRepositorySql.EIGHTH_PARAMETER_INDEX, measurementSystem?.name)
			ps.setInt(RecipeRepositorySql.NINTH_PARAMETER_INDEX, recipeId)
			ps.executeUpdate()
		}
	}

	private fun insertIngredientGroup(
		conn: java.sql.Connection,
		recipeId: Int,
		groupName: String?,
		groupIndex: Int,
	): Int {
		return conn.prepareStatement(
			RecipeRepositorySql.CREATE_INGREDIENT_GROUP_SQL,
			Statement.RETURN_GENERATED_KEYS,
		).use { ps ->
			ps.setInt(RecipeRepositorySql.FIRST_PARAMETER_INDEX, recipeId)
			ps.setString(RecipeRepositorySql.SECOND_PARAMETER_INDEX, groupName?.trim())
			ps.setInt(RecipeRepositorySql.THIRD_PARAMETER_INDEX, groupIndex)
			ps.executeUpdate()
			ps.generatedId()
		}
	}

	private fun PreparedStatement.generatedId(): Int {
		return generatedKeys.use { keys ->
			keys.next()
			keys.getInt(RecipeRepositorySql.FIRST_PARAMETER_INDEX)
		}
	}

	private fun buildDescription(
		cuisine: Cuisine?,
		mealType: MealType?,
		totalTime: Int?,
		yields: String?,
	): String {
		val introParts = listOfNotNull(
			cuisine?.displayName,
			mealType?.displayName,
		)
		val intro = when {
			introParts.isEmpty() -> "Recipe"
			else -> introParts.joinToString(separator = " ", postfix = " recipe")
		}

		val details = buildList {
			totalTime?.let { add("ready in $it minutes") }
			yields?.takeIf { it.isNotBlank() }?.let(::add)
		}

		return if (details.isEmpty()) {
			"$intro."
		} else {
			"$intro, ${details.joinToString()}."
		}
	}

	internal fun loadMeasurementSystemForRecipe(recipeId: Int): MeasurementSystem? = dataSource.connection.use { conn ->
		detectMeasurementSystem(loadIngredientGroupsForRecipe(conn, recipeId))
	}

	internal fun detectMeasurementSystem(ingredientGroups: List<IngredientGroup>): MeasurementSystem? {
		var imperialHits = 0
		var metricHits = 0
		ingredientGroups.asSequence()
			.flatMap { it.ingredients.asSequence() }
			.forEach { ingredient ->
				val normalized = ingredient.text.lowercase()
				if (RecipeRepositorySql.IMPERIAL_UNIT_REGEX.containsMatchIn(normalized)) {
					imperialHits += 1
				}
				if (RecipeRepositorySql.METRIC_UNIT_REGEX.containsMatchIn(normalized)) {
					metricHits += 1
				}
			}
		return when {
			imperialHits == 0 && metricHits == 0 -> null
			imperialHits > 0 && metricHits > 0 -> MeasurementSystem.MIXED
			imperialHits > 0 -> MeasurementSystem.IMPERIAL
			else -> MeasurementSystem.METRIC
		}
	}

}

internal fun countRecipesByKeyword(dataSource: DataSource, like: String): Int {
	val sql = """
		SELECT COUNT(*)
		FROM recipes
		WHERE LOWER(title) LIKE ? OR LOWER(cuisine) LIKE ?
	""".trimIndent()

	return dataSource.connection.use { conn ->
		conn.prepareStatement(sql).use { ps ->
			ps.setString(1, like)
			ps.setString(2, like)
			ps.executeQuery().use { rs ->
				rs.next()
				rs.getInt(1)
			}
		}
	}
}

internal fun querySearchWithFiltersRecipes(
	conn: java.sql.Connection,
	whereClause: String,
	params: List<Any>,
	executeQuery: (PreparedStatement) -> ArrayList<RecipeSummary>,
	limit: Int? = null,
	offset: Int? = null,
): List<RecipeSummary> {
	val limitAndOffsetClause = if (limit != null && offset != null) {
		"LIMIT ? OFFSET ?"
	} else {
		""
	}

	val sql = """
		SELECT r.id, r.title, r.cuisine, r.image_url, r.total_time, r.measurement_system
		FROM recipes r
		$whereClause
		GROUP BY r.id
		ORDER BY r.created_at DESC
		$limitAndOffsetClause
	""".trimIndent()

	return conn.prepareStatement(sql).use { ps ->
		params.forEachIndexed { index, param ->
			when (param) {
				is String -> ps.setString(index + 1, param)
				is Int -> ps.setInt(index + 1, param)
				else -> error("Unsupported parameter type: ${param::class}")
			}
		}
		if (limit != null && offset != null) {
			ps.setInt(params.size + 1, limit)
			ps.setInt(params.size + 2, offset)
		}
		executeQuery(ps)
	}
}

internal fun countSearchWithFiltersRecipes(
	conn: java.sql.Connection,
	whereClause: String,
	params: List<Any>,
): Int {
	val sql = """
		SELECT COUNT(*)
		FROM recipes r
		$whereClause
	""".trimIndent()

	return conn.prepareStatement(sql).use { ps ->
		params.forEachIndexed { index, param ->
			when (param) {
				is String -> ps.setString(index + 1, param)
				is Int -> ps.setInt(index + 1, param)
				else -> error("Unsupported parameter type: ${param::class}")
			}
		}
		ps.executeQuery().use { rs ->
			rs.next()
			rs.getInt(1)
		}
	}
}

internal fun isRecipeCoveredByAvailableIngredients(
	recipeId: Int,
	availableIngredients: List<String>,
	loadIngredientGroups: (Int) -> List<IngredientGroup>,
): Boolean {
	return loadIngredientGroups(recipeId).all { group ->
		ingredientSlots(group.ingredients).all { slot ->
			isIngredientSlotCoveredByPantry(slot, availableIngredients)
		}
	}
}

internal fun recipeContainsExcludedIngredient(
	recipeId: Int,
	excludedIngredients: List<String>,
	loadIngredientGroups: (Int) -> List<IngredientGroup>,
): Boolean {
	if (excludedIngredients.isEmpty()) {
		return false
	}

	return loadIngredientGroups(recipeId).any { group ->
		ingredientSlots(group.ingredients).any { slot ->
			isIngredientSlotExcluded(slot, excludedIngredients)
		}
	}
}

internal fun isIngredientSlotCoveredByPantry(
	slot: List<RecipeIngredient>,
	availableIngredients: List<String>,
): Boolean {
	if (slotIsOptional(slot)) {
		return true
	}
	val requiredMembers = slot.filter { ingredient -> ingredient.requirement != IngredientRequirement.OPTIONAL }
	if (requiredMembers.isEmpty()) {
		return true
	}
	return if (requiredMembers.any { ingredient -> ingredient.requirement == IngredientRequirement.ALTERNATIVE }) {
		requiredMembers.any { ingredient ->
			IngredientVocabulary.isCoveredByAvailableIngredients(
				ingredientLine = ingredient.text,
				availableIngredients = availableIngredients,
			)
		}
	} else {
		requiredMembers.all { ingredient ->
			IngredientVocabulary.isCoveredByAvailableIngredients(
				ingredientLine = ingredient.text,
				availableIngredients = availableIngredients,
			)
		}
	}
}

internal fun isIngredientSlotExcluded(
	slot: List<RecipeIngredient>,
	excludedIngredients: List<String>,
): Boolean {
	if (excludedIngredients.isEmpty()) {
		return false
	}
	val alternativeMembers = slot.filter { ingredient ->
		ingredient.requirement == IngredientRequirement.ALTERNATIVE
	}
	return if (alternativeMembers.size > 1) {
		alternativeMembers.all { ingredient ->
			IngredientVocabulary.matchesAnyIngredient(
				ingredientLine = ingredient.text,
				ingredientNames = excludedIngredients,
			)
		}
	} else {
		slot.any { ingredient ->
			IngredientVocabulary.matchesAnyIngredient(
				ingredientLine = ingredient.text,
				ingredientNames = excludedIngredients,
			)
		}
	}
}
