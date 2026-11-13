package app.purecipes.backend.tools

import ProcessedScrapedIngredient
import RecipeHeadingContext
import app.purecipes.backend.feature.nutrition.RecipeNutritionService
import extractScrapedAppliances
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import processRecipeScrapedIngredientGroups
import java.math.BigDecimal
import java.math.RoundingMode
import java.nio.file.Files
import java.nio.file.Path
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Statement
import java.sql.Types
import java.util.Locale
import javax.sql.DataSource
import kotlin.io.path.extension
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.io.path.name
import kotlin.io.path.readText

internal data class ScrapedJsonImportSummary(
	val totalFileCount: Int,
	val importedCount: Int,
	val skippedExistingCount: Int,
	val errorCount: Int,
	val importedRecipeIds: List<Int>,
)

private data class ParsedScrapedJsonRecipe(
	val title: String,
	val description: String?,
	val ingredientGroups: List<Pair<String?, List<ProcessedScrapedIngredient>>>,
	val instructionList: List<String>,
	val instructions: String,
	val totalTime: Int?,
	val prepTime: Int?,
	val cookTime: Int?,
	val yields: String?,
	val image: String?,
	val nutrients: Map<String, String?>,
	val language: String,
	val cuisine: String?,
	val categoryColumns: ScrapedCategoryColumns,
	val sourceUrl: String,
	val measurementSystem: String?,
	val appliances: List<String>,
)

private data class ScrapedCategoryColumns(
	val mealType: String?,
	val difficulty: String?,
	val cookingMethod: String?,
	val dietaryPreferences: List<String>,
	val tags: List<String>,
)

private const val MAX_DB_INGREDIENT_LENGTH = 255
private const val SCRAPED_NUTRITION_SCALE = 2
private const val DEFAULT_RECIPE_LANGUAGE = "en"

private val numberPattern = Regex("""-?\d+(?:\.\d+)?""")

private val scrapedServingsRegex = Regex(
	pattern = """(\d+(?:\.\d+)?)\s*(?:servings?|serves|portions?|people|items?|pieces?|pancakes?|""" +
		"""biscuits?|cookies?|rolls?|muffins?|cupcakes?|buns?|bars?|loaves|loaf|batch|pie)""",
	option = RegexOption.IGNORE_CASE,
)

private val scrapedLeadingCountRegex = Regex(
	pattern = """^(\d+(?:\.\d+)?)\s*(?:x\s*)?(?:servings?|serves|items?|pieces?)""",
	option = RegexOption.IGNORE_CASE,
)

private val imperialUnitRegex = Regex(
	pattern = "(?<!\\p{L})(cups?|ounces?|ounce|oz|" +
		"pounds?|pound|lbs?|lb|fahrenheit|°f)\\b",
	options = setOf(RegexOption.IGNORE_CASE),
)

private val metricUnitRegex = Regex(
	pattern = "(?<!\\p{L})(kilograms?|kilogram|kg|grams?|gram|g|milliliters?|milliliter|" +
		"ml|liters?|liter|l|celsius|°c)\\b",
	options = setOf(RegexOption.IGNORE_CASE),
)

private val scrapedNutrientKeyAliases = mapOf(
	"calories" to listOf("calories", "energy", "calorie"),
	"protein" to listOf("protein", "proteinContent"),
	"carbohydrates" to listOf("carbohydrates", "carbs", "carbohydrateContent"),
	"fat" to listOf("fat", "fatContent"),
	"fiber" to listOf("fiber", "fiberContent"),
	"sugar" to listOf("sugar", "sugarContent"),
	"sodium" to listOf("sodium", "sodiumContent"),
)

private val mealTypeMapping = listOf(
	"BREAKFAST" to listOf("breakfast"),
	"BRUNCH" to listOf("brunch"),
	"LUNCH" to listOf("lunch"),
	"DINNER" to listOf("dinner", "main course", "main"),
	"SNACK" to listOf("snack"),
	"DESSERT" to listOf("dessert", "sweet", "pudding", "baking", "cake"),
	"APPETIZER" to listOf("appetizer", "starter", "small plate"),
	"DRINK" to listOf("drink", "cocktail", "smoothie", "juice", "beverage"),
	"SIDE_DISH" to listOf("side", "side dish"),
)

private val difficultyMapping = listOf(
	"EASY" to listOf("easy", "simple", "quick", "beginner", "beginner friendly"),
	"MEDIUM" to listOf("medium", "intermediate"),
	"HARD" to listOf("hard", "advanced", "challenging", "expert"),
)

private val cookingMethodMapping = listOf(
	"AIR_FRY" to listOf("air fry", "air fryer"),
	"STIR_FRY" to listOf("stir fry", "stir-fry", "wok"),
	"SLOW_COOK" to listOf("slow cook", "slow cooker", "crockpot"),
	"PRESSURE_COOK" to listOf("pressure cook", "pressure cooker", "instant pot"),
	"MICROWAVE" to listOf("microwave"),
	"SMOKE" to listOf("smoke", "smoked"),
	"ROAST" to listOf("roast", "roasted"),
	"BAKE" to listOf("bake", "baked", "traybake", "tray bake"),
	"GRILL" to listOf("grill", "grilled", "bbq", "barbecue", "barbeque"),
	"FRY" to listOf("fry", "fried", "deep fried", "pan fried"),
	"STEAM" to listOf("steam", "steamed"),
	"BOIL" to listOf("boil", "boiled", "simmered"),
	"RAW" to listOf("raw", "no cook", "no-cook"),
)

private val dietaryPreferenceMapping = listOf(
	"VEGAN" to listOf("vegan", "plant based", "plant-based"),
	"VEGETARIAN" to listOf("vegetarian", "veggie", "meat free", "meat-free", "meatless"),
	"PESCATARIAN" to listOf("pescatarian"),
	"GLUTEN_FREE" to listOf("gluten free", "gluten-free", "glutenfree"),
	"DAIRY_FREE" to listOf("dairy free", "dairy-free", "lactose free", "lactose-free"),
	"NUT_FREE" to listOf("nut free", "nut-free", "peanut free", "peanut-free", "no nuts"),
	"EGG_FREE" to listOf("egg free", "egg-free", "eggless"),
	"SHELLFISH_FREE" to listOf("shellfish free", "shellfish-free"),
	"ALLIUM_FREE" to listOf("allium free", "allium-free"),
	"HALAL" to listOf("halal"),
	"KOSHER" to listOf("kosher"),
	"LOW_FODMAP" to listOf("low fodmap", "low-fodmap"),
	"PALEO" to listOf("paleo"),
	"KETO" to listOf("keto", "ketogenic"),
)

private val tagMapping = listOf(
	"COMFORT" to listOf("comfort", "comfort food"),
	"HEALTHY" to listOf("healthy", "lighter"),
	"SPEEDY" to listOf("speedy"),
	"DINNER_PARTY" to listOf("dinner party", "entertaining"),
	"SEASONAL" to listOf("spring", "summer", "autumn", "fall", "winter"),
	"FAKEAWAY" to listOf("fakeaway", "takeaway fakeout"),
	"FAMILY" to listOf("family", "kid friendly", "kids", "family favourites"),
	"BUDGET" to listOf("budget", "cheap", "affordable"),
	"BATCH_COOK" to listOf("batch cook", "batch cooking", "freezer friendly"),
	"MEAL_PREP" to listOf("meal prep", "make ahead", "make-ahead"),
	"ONE_POT" to listOf("one pot", "one-pot", "one pan", "one-pan"),
	"PICNIC" to listOf("picnic", "packed lunch", "lunchbox"),
	"PARTY" to listOf("party food", "party", "celebration"),
)

private object RecipeBindIndex {

	const val TITLE = 1
	const val DESCRIPTION = 2
	const val INSTRUCTIONS = 3
	const val TOTAL_TIME = 4
	const val PREP_TIME = 5
	const val COOK_TIME = 6
	const val YIELDS = 7
	const val IMAGE_URL = 8
	const val LANGUAGE = 9
	const val CUISINE = 10
	const val MEAL_TYPE = 11
	const val DIFFICULTY = 12
	const val COOKING_METHOD = 13
	const val DIETARY_PREFERENCES = 14
	const val TAGS = 15
	const val MEASUREMENT_SYSTEM = 16
	const val APPLIANCES = 17
	const val SOURCE_URL_OR_ID = 18
}

private object ScrapedNutritionBindIndex {

	const val CALORIES = 1
	const val PROTEIN = 2
	const val CARBOHYDRATES = 3
	const val FAT = 4
	const val FIBER = 5
	const val SUGAR = 6
	const val SODIUM = 7
	const val CALCULATION_SOURCE = 8
	const val SERVING_COUNT = 9
	const val RECIPE_ID = 10
}

private object IngredientBindIndex {

	const val GROUP_ID = 1
	const val INGREDIENT = 2
	const val ORDER_INDEX = 3
	const val REQUIREMENT = 4
	const val ALTERNATIVE_GROUP_KEY = 5
}

private object InstructionBindIndex {

	const val RECIPE_ID = 1
	const val STEP = 2
	const val ORDER_INDEX = 3
}

internal class ScrapedRecipeJsonImporter(
	private val dataSource: DataSource,
	private val calculateNutrition: (List<Int>) -> Unit = { recipeIds ->
		RecipeNutritionService(dataSource).calculateAndPersistRecipeIds(
			recipeIds = recipeIds,
			onProgress = createNutritionProgressCallback(),
		)
	},
) {

	private val json = Json {
		ignoreUnknownKeys = true
	}

	fun importDirectory(
		jsonArchiveDir: Path = IngredientDataRebuilder.defaultJsonArchiveDir()
			?: error("Default JSON archive directory ~/Purecipes/recipeoutput/recipes does not exist"),
		upsertExisting: Boolean = false,
		calculateImportedNutrition: Boolean = false,
	): ScrapedJsonImportSummary {
		val jsonFiles = listJsonFiles(jsonArchiveDir)
		if (jsonFiles.isEmpty()) {
			return ScrapedJsonImportSummary(
				totalFileCount = 0,
				importedCount = 0,
				skippedExistingCount = 0,
				errorCount = 0,
				importedRecipeIds = emptyList(),
			)
		}

		val progressLogger = ProgressLogger(
			stepLabel = "Importing scraped JSON recipes",
			total = jsonFiles.size,
			startMessage = "Found ${jsonFiles.size} JSON files to import...",
		)
		var importedCount = 0
		var skippedCount = 0
		var errorCount = 0
		val importedRecipeIds = mutableListOf<Int>()

		dataSource.connection.use { connection ->
			val previousAutoCommit = connection.autoCommit
			connection.autoCommit = false
			try {
				jsonFiles.forEachIndexed { index, file ->
					val parsed = parseRecipeFile(file)
					if (parsed == null) {
						errorCount += 1
					} else {
						val savedId = saveRecipe(connection, parsed, upsertExisting)
						if (savedId == null) {
							skippedCount += 1
						} else {
							importedCount += 1
							importedRecipeIds += savedId
						}
					}
					progressLogger.onItemProcessed(index + 1)
				}
			} catch (exception: SQLException) {
				connection.rollback()
				throw exception
			} finally {
				connection.autoCommit = previousAutoCommit
			}
		}

		if (calculateImportedNutrition && importedRecipeIds.isNotEmpty()) {
			calculateNutrition(importedRecipeIds)
		}

		return ScrapedJsonImportSummary(
			totalFileCount = jsonFiles.size,
			importedCount = importedCount,
			skippedExistingCount = skippedCount,
			errorCount = errorCount,
			importedRecipeIds = importedRecipeIds,
		)
	}

	private fun listJsonFiles(jsonArchiveDir: Path): List<Path> =
		if (!jsonArchiveDir.isDirectory()) {
			emptyList()
		} else {
			Files.walk(jsonArchiveDir).use { paths ->
				paths
					.filter { path -> path.isRegularFile() && path.extension.equals("json", ignoreCase = true) }
					.sorted(Comparator.comparing(Path::name))
					.toList()
			}
		}

	private fun parseRecipeFile(file: Path): ParsedScrapedJsonRecipe? {
		val root = runCatching { json.parseToJsonElement(file.readText()).jsonObject }.getOrNull() ?: return null
		val title = root["title"]?.jsonPrimitive?.contentOrNull?.trim().orEmpty()
		val sourceUrl = root["source_url"]?.jsonPrimitive?.contentOrNull?.trim().orEmpty()
		val description = root["description"]?.jsonPrimitive?.contentOrNull?.trim()?.takeIf(String::isNotBlank)
		val instructionList = root["instruction_list"]?.jsonArray
			?.mapNotNull { it.jsonPrimitive.contentOrNull?.trim() }
			?.filter(String::isNotBlank)
			.orEmpty()
		val instructionsText = root["instructions"]?.jsonPrimitive?.contentOrNull.orEmpty().ifBlank {
			instructionList.joinToString("\n").trim()
		}
		val rawGroups = root["ingredient_groups"]?.jsonArray
			?.mapNotNull(::parseStoredIngredientGroupJson)
			.orEmpty()
		val ingredientGroups = processRecipeScrapedIngredientGroups(
			groups = rawGroups,
			context = RecipeHeadingContext(
				recipeTitle = title,
				recipeInstructions = instructionsText,
			),
		).filter { (_, items) -> items.isNotEmpty() }

		val category = root["category"]?.jsonPrimitive?.contentOrNull
		val categoryColumns = mapCategoryToColumns(category)
		val jsonAppliances = root["appliances"]?.jsonArray?.mapNotNull { it.jsonPrimitive.contentOrNull }
		val appliances = if (!jsonAppliances.isNullOrEmpty()) {
			jsonAppliances
		} else {
			extractScrapedAppliances(
				instructionList = instructionList,
				instructions = instructionsText,
				title = title,
				description = description,
				cookingMethod = categoryColumns.cookingMethod,
			)
		}

		return if (title.isBlank() || sourceUrl.isBlank() || ingredientGroups.isEmpty()) {
			null
		} else {
			ParsedScrapedJsonRecipe(
				title = title,
				description = description,
				ingredientGroups = ingredientGroups,
				instructionList = instructionList,
				instructions = instructionsText,
				totalTime = root["total_time"]?.jsonPrimitive?.contentOrNull?.trim()?.toIntOrNull(),
				prepTime = root["prep_time"]?.jsonPrimitive?.contentOrNull?.trim()?.toIntOrNull(),
				cookTime = root["cook_time"]?.jsonPrimitive?.contentOrNull?.trim()?.toIntOrNull(),
				yields = root["yields"]?.jsonPrimitive?.contentOrNull,
				image = root["image"]?.jsonPrimitive?.contentOrNull,
				nutrients = parseNutrientsFromRoot(root),
				language = root["language"]?.jsonPrimitive?.contentOrNull ?: DEFAULT_RECIPE_LANGUAGE,
				cuisine = root["cuisine"]?.jsonPrimitive?.contentOrNull,
				categoryColumns = categoryColumns,
				sourceUrl = sourceUrl,
				measurementSystem = detectMeasurementSystem(ingredientGroups),
				appliances = appliances,
			)
		}
	}

	private fun saveRecipe(
		connection: Connection,
		recipe: ParsedScrapedJsonRecipe,
		upsertExisting: Boolean,
	): Int? {
		val existingId = findRecipeIdBySourceUrl(connection, recipe.sourceUrl)
		if (existingId != null && !upsertExisting) {
			return null
		}
		val recipeId = if (existingId == null) {
			insertRecipeRecord(connection, recipe)
		} else {
			updateRecipeRecord(connection, existingId, recipe)
			deleteRecipeDerivedRowsForUpsert(connection, existingId)
			existingId
		}
		saveRecipeIngredientGroups(connection, recipe, recipeId)
		saveRecipeInstructions(connection, recipe, recipeId)
		saveScrapedRecipeNutrition(connection, recipe, recipeId)
		connection.commit()
		return recipeId
	}

	private fun findRecipeIdBySourceUrl(connection: Connection, sourceUrl: String): Int? =
		connection.prepareStatement("SELECT id FROM recipes WHERE source_url = ?").use { statement ->
			statement.setString(1, sourceUrl)
			statement.executeQuery().use { resultSet ->
				if (resultSet.next()) resultSet.getInt("id") else null
			}
		}

	private fun insertRecipeRecord(connection: Connection, recipe: ParsedScrapedJsonRecipe): Int =
		connection.prepareStatement(
			"""
			INSERT INTO recipes (
				title,
				description,
				instructions,
				total_time,
				prep_time,
				cook_time,
				yields,
				image_url,
				language,
				cuisine,
				meal_type,
				difficulty,
				cooking_method,
				dietary_preferences,
				tags,
				measurement_system,
				appliances,
				source_url
			) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
			""".trimIndent(),
			Statement.RETURN_GENERATED_KEYS,
		).use { statement ->
			bindCommonRecipeColumns(statement, connection, recipe)
			statement.setString(RecipeBindIndex.SOURCE_URL_OR_ID, recipe.sourceUrl)
			statement.executeUpdate()
			statement.generatedKeys.use { keys ->
				keys.next()
				keys.getInt(1)
			}
		}

	private fun updateRecipeRecord(
		connection: Connection,
		recipeId: Int,
		recipe: ParsedScrapedJsonRecipe,
	) {
		connection.prepareStatement(
			"""
			UPDATE recipes SET
				title = ?,
				description = ?,
				instructions = ?,
				total_time = ?,
				prep_time = ?,
				cook_time = ?,
				yields = ?,
				image_url = ?,
				language = ?,
				cuisine = ?,
				meal_type = ?,
				difficulty = ?,
				cooking_method = ?,
				dietary_preferences = ?,
				tags = ?,
				measurement_system = ?,
				appliances = ?
			WHERE id = ?
			""".trimIndent(),
		).use { statement ->
			bindCommonRecipeColumns(statement, connection, recipe)
			statement.setInt(RecipeBindIndex.SOURCE_URL_OR_ID, recipeId)
			statement.executeUpdate()
		}
	}

	private fun bindCommonRecipeColumns(
		statement: PreparedStatement,
		connection: Connection,
		recipe: ParsedScrapedJsonRecipe,
	) {
		statement.setString(RecipeBindIndex.TITLE, recipe.title)
		statement.setString(RecipeBindIndex.DESCRIPTION, recipe.description)
		statement.setString(RecipeBindIndex.INSTRUCTIONS, recipe.instructions)
		statement.setObject(RecipeBindIndex.TOTAL_TIME, recipe.totalTime)
		statement.setObject(RecipeBindIndex.PREP_TIME, recipe.prepTime)
		statement.setObject(RecipeBindIndex.COOK_TIME, recipe.cookTime)
		statement.setString(RecipeBindIndex.YIELDS, recipe.yields)
		statement.setString(RecipeBindIndex.IMAGE_URL, recipe.image)
		statement.setString(RecipeBindIndex.LANGUAGE, recipe.language)
		statement.setString(RecipeBindIndex.CUISINE, recipe.cuisine)
		statement.setString(RecipeBindIndex.MEAL_TYPE, recipe.categoryColumns.mealType)
		statement.setString(RecipeBindIndex.DIFFICULTY, recipe.categoryColumns.difficulty)
		statement.setString(RecipeBindIndex.COOKING_METHOD, recipe.categoryColumns.cookingMethod)
		setTextArrayOrNull(
			statement,
			connection,
			RecipeBindIndex.DIETARY_PREFERENCES,
			recipe.categoryColumns.dietaryPreferences
		)
		setTextArrayOrNull(statement, connection, RecipeBindIndex.TAGS, recipe.categoryColumns.tags)
		statement.setString(RecipeBindIndex.MEASUREMENT_SYSTEM, recipe.measurementSystem)
		setTextArrayOrNull(statement, connection, RecipeBindIndex.APPLIANCES, recipe.appliances)
	}

	private fun deleteRecipeDerivedRowsForUpsert(connection: Connection, recipeId: Int) {
		connection.prepareStatement("DELETE FROM ingredient_groups WHERE recipe_id = ?").use { statement ->
			statement.setInt(1, recipeId)
			statement.executeUpdate()
		}
		connection.prepareStatement("DELETE FROM instruction_steps WHERE recipe_id = ?").use { statement ->
			statement.setInt(1, recipeId)
			statement.executeUpdate()
		}
	}

	private fun saveRecipeIngredientGroups(
		connection: Connection,
		recipe: ParsedScrapedJsonRecipe,
		recipeId: Int,
	) {
		val insertGroupSql = "INSERT INTO ingredient_groups (recipe_id, name, order_index) VALUES (?, ?, ?)"
		val insertIngredientSql = """
			INSERT INTO ingredients (
				ingredient_group_id,
				ingredient,
				order_index,
				requirement,
				alternative_group_key
			) VALUES (?, ?, ?, ?, ?)
		""".trimIndent()

		recipe.ingredientGroups.forEachIndexed { groupIndex, (groupName, items) ->
			val groupId =
				connection.prepareStatement(insertGroupSql, Statement.RETURN_GENERATED_KEYS).use { statement ->
					statement.setInt(1, recipeId)
					statement.setString(2, groupName)
					statement.setInt(3, groupIndex)
					statement.executeUpdate()
					statement.generatedKeys.use { keys ->
						keys.next()
						keys.getInt(1)
					}
				}

			connection.prepareStatement(insertIngredientSql).use { statement ->
				items.forEachIndexed { itemIndex, ingredient ->
					statement.setInt(IngredientBindIndex.GROUP_ID, groupId)
					statement.setString(IngredientBindIndex.INGREDIENT, ingredient.text.take(MAX_DB_INGREDIENT_LENGTH))
					statement.setInt(IngredientBindIndex.ORDER_INDEX, itemIndex)
					statement.setString(IngredientBindIndex.REQUIREMENT, ingredient.requirement)
					if (ingredient.alternativeGroupKey == null) {
						statement.setNull(IngredientBindIndex.ALTERNATIVE_GROUP_KEY, Types.INTEGER)
					} else {
						statement.setInt(IngredientBindIndex.ALTERNATIVE_GROUP_KEY, ingredient.alternativeGroupKey)
					}
					statement.addBatch()
				}
				statement.executeBatch()
			}
		}
	}

	private fun saveRecipeInstructions(
		connection: Connection,
		recipe: ParsedScrapedJsonRecipe,
		recipeId: Int,
	) {
		if (recipe.instructionList.isEmpty()) {
			return
		}
		connection.prepareStatement(
			"INSERT INTO instruction_steps (recipe_id, step, order_index) VALUES (?, ?, ?)",
		).use { statement ->
			recipe.instructionList.forEachIndexed { index, step ->
				statement.setInt(InstructionBindIndex.RECIPE_ID, recipeId)
				statement.setString(InstructionBindIndex.STEP, step)
				statement.setInt(InstructionBindIndex.ORDER_INDEX, index)
				statement.addBatch()
			}
			statement.executeBatch()
		}
	}

	private fun saveScrapedRecipeNutrition(
		connection: Connection,
		recipe: ParsedScrapedJsonRecipe,
		recipeId: Int,
	) {
		if (!recipe.nutrients.values.any { value -> !value.isNullOrBlank() && parseNumber(value) != null }) {
			return
		}
		val servingCount = parseScrapedServingCount(recipe.yields)
		val multiplier = if (servingCount != null && servingCount > 0.0) servingCount else 1.0
		val nutrients = recipe.nutrients
		connection.prepareStatement("DELETE FROM nutrition WHERE recipe_id = ?").use { deleteStatement ->
			deleteStatement.setInt(1, recipeId)
			deleteStatement.executeUpdate()
		}
		connection.prepareStatement(
			"""
			INSERT INTO nutrition (
				calories,
				protein,
				carbohydrates,
				fat,
				fiber,
				sugar,
				sodium,
				calculation_source,
				serving_count,
				recipe_id
			) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
			""".trimIndent(),
		).use { statement ->
			statement.setObject(
				ScrapedNutritionBindIndex.CALORIES,
				scaleScrapedNutrient(nutrients["calories"], multiplier)
			)
			statement.setObject(
				ScrapedNutritionBindIndex.PROTEIN,
				scaleScrapedNutrient(nutrients["protein"], multiplier)
			)
			statement.setObject(
				ScrapedNutritionBindIndex.CARBOHYDRATES,
				scaleScrapedNutrient(nutrients["carbohydrates"], multiplier),
			)
			statement.setObject(ScrapedNutritionBindIndex.FAT, scaleScrapedNutrient(nutrients["fat"], multiplier))
			statement.setObject(ScrapedNutritionBindIndex.FIBER, scaleScrapedNutrient(nutrients["fiber"], multiplier))
			statement.setObject(ScrapedNutritionBindIndex.SUGAR, scaleScrapedNutrient(nutrients["sugar"], multiplier))
			statement.setObject(ScrapedNutritionBindIndex.SODIUM, scaleScrapedNutrient(nutrients["sodium"], multiplier))
			statement.setString(ScrapedNutritionBindIndex.CALCULATION_SOURCE, "scraped")
			statement.setObject(ScrapedNutritionBindIndex.SERVING_COUNT, servingCount)
			statement.setInt(ScrapedNutritionBindIndex.RECIPE_ID, recipeId)
			statement.executeUpdate()
		}
	}

	private fun setTextArrayOrNull(
		statement: PreparedStatement,
		connection: Connection,
		index: Int,
		values: List<String>,
	) {
		if (values.isEmpty()) {
			statement.setNull(index, Types.ARRAY)
		} else {
			statement.setArray(index, connection.createArrayOf("text", values.toTypedArray()))
		}
	}
}

private fun parseStoredIngredientGroupJson(
	groupEl: JsonElement,
): Pair<String?, List<ProcessedScrapedIngredient>>? =
	when (groupEl) {
		is JsonObject -> {
			val name = groupEl["name"]?.jsonPrimitive?.contentOrNull?.trim().orEmpty().ifBlank { null }
			val items = groupEl["ingredients"]?.jsonArray
				?.mapNotNull(::parseStoredIngredientJson)
				.orEmpty()
			if (items.isEmpty()) null else (name to items)
		}

		is JsonArray -> {
			val name = groupEl.getOrNull(0)?.jsonPrimitive?.contentOrNull?.trim().orEmpty().ifBlank { null }
			val items = groupEl.getOrNull(1)?.jsonArray
				?.mapNotNull(::parseStoredIngredientJson)
				.orEmpty()
			if (items.isEmpty()) null else (name to items)
		}

		else -> null
	}

private fun parseStoredIngredientJson(ingredientEl: JsonElement): ProcessedScrapedIngredient? =
	when (ingredientEl) {
		is JsonPrimitive -> ingredientEl.contentOrNull?.trim()?.takeIf(String::isNotBlank)?.let { text ->
			ProcessedScrapedIngredient(text = text)
		}

		is JsonObject -> {
			val text = ingredientEl["text"]?.jsonPrimitive?.contentOrNull?.trim().orEmpty()
			if (text.isBlank()) {
				null
			} else {
				val requirement = ingredientEl["requirement"]?.jsonPrimitive?.contentOrNull
					?.trim()
					?.uppercase(Locale.ROOT)
					?.takeIf(String::isNotBlank)
					?: "REQUIRED"
				val alternativeGroupKey = ingredientEl["alternativeGroupKey"]?.jsonPrimitive?.contentOrNull
					?.toIntOrNull()
				ProcessedScrapedIngredient(
					text = text,
					requirement = requirement,
					alternativeGroupKey = alternativeGroupKey,
				)
			}
		}

		else -> null
	}

private fun parseNutrientsFromRoot(root: JsonObject): Map<String, String?> {
	val nutrientsObject = root["nutrients"]?.jsonObject ?: JsonObject(emptyMap())
	val raw = nutrientsObject.mapNotNull { (key, value) ->
		val textValue = when (value) {
			is JsonPrimitive -> when {
				value.isString -> value.content.trim().takeIf(String::isNotBlank)
				else -> value.content.toDoubleOrNull()?.toString()
			}

			else -> null
		}
		textValue?.let { key to it }
	}.toMap()
	val rawByLowerKey = raw.mapKeys { (key, _) -> key.lowercase(Locale.ROOT) }
	return scrapedNutrientKeyAliases.mapNotNull { (canonical, aliases) ->
		val value = aliases.firstNotNullOfOrNull { alias ->
			raw[alias]?.takeIf(String::isNotBlank)
				?: rawByLowerKey[alias.lowercase(Locale.ROOT)]?.takeIf(String::isNotBlank)
		}
		value?.let { canonical to it }
	}.toMap()
}

private fun mapCategoryToColumns(category: String?): ScrapedCategoryColumns {
	val rawLabels = category.orEmpty()
		.split(',', ';', '|', '/', '>')
		.asSequence()
		.map(String::trim)
		.filter(String::isNotBlank)
		.distinctBy(String::lowercase)
		.toList()
	val tokens = rawLabels.map(::normalizeCategoryLabel)

	val mealType = firstMappedValue(tokens, mealTypeMapping)
	val difficulty = firstMappedValue(tokens, difficultyMapping)
	val cookingMethod = firstMappedValue(tokens, cookingMethodMapping)
	val dietaryPreferences = allMappedValues(tokens, dietaryPreferenceMapping)
	val tagKeywordGroups = tagMapping.map { (_, keywords) -> keywords }
	val tags = rawLabels.filter { label ->
		val token = normalizeCategoryLabel(label)
		tagKeywordGroups.any { keywords -> matchesKeywords(token, keywords) }
	}
	return ScrapedCategoryColumns(
		mealType = mealType,
		difficulty = difficulty,
		cookingMethod = cookingMethod,
		dietaryPreferences = dietaryPreferences,
		tags = tags,
	)
}

private fun normalizeCategoryLabel(label: String): String = label.lowercase()
	.replace("&", " and ")
	.replace(Regex("""[^a-z0-9+ -]"""), " ")
	.replace(Regex("""\s+"""), " ")
	.trim()

private fun matchesKeywords(token: String, keywords: List<String>): Boolean =
	keywords.any { keyword -> token.contains(keyword) }

private fun firstMappedValue(
	tokens: List<String>,
	mapping: List<Pair<String, List<String>>>,
): String? =
	mapping.firstOrNull { (_, keywords) ->
		tokens.any { token -> matchesKeywords(token, keywords) }
	}?.first

private fun allMappedValues(
	tokens: List<String>,
	mapping: List<Pair<String, List<String>>>,
): List<String> =
	mapping.mapNotNull { (value, keywords) ->
		if (tokens.any { token -> matchesKeywords(token, keywords) }) value else null
	}.distinct()

private fun detectMeasurementSystem(
	ingredientGroups: List<Pair<String?, List<ProcessedScrapedIngredient>>>,
): String? {
	var imperialHits = 0
	var metricHits = 0
	ingredientGroups.asSequence()
		.flatMap { (_, ingredients) -> ingredients.asSequence() }
		.forEach { ingredient ->
			val normalized = ingredient.text.lowercase()
			if (imperialUnitRegex.containsMatchIn(normalized)) {
				imperialHits += 1
			}
			if (metricUnitRegex.containsMatchIn(normalized)) {
				metricHits += 1
			}
		}
	return when {
		imperialHits == 0 && metricHits == 0 -> null
		imperialHits > 0 && metricHits > 0 -> "MIXED"
		imperialHits > 0 -> "IMPERIAL"
		else -> "METRIC"
	}
}

private fun parseNumber(value: String?): Double? =
	value?.takeIf(String::isNotBlank)?.let { numberPattern.find(it)?.value?.toDoubleOrNull() }

private fun parseScrapedServingCount(yields: String?): Double? {
	val trimmed = yields?.trim().orEmpty()
	if (trimmed.isEmpty()) {
		return null
	}
	val match = scrapedServingsRegex.find(trimmed) ?: scrapedLeadingCountRegex.find(trimmed)
	val count = match?.groupValues?.getOrNull(1)?.toDoubleOrNull()
	return if (count == null || count <= 0.0) null else count
}

private fun scaleScrapedNutrient(value: String?, multiplier: Double): Double? {
	val parsed = parseNumber(value) ?: return null
	return BigDecimal.valueOf(parsed * multiplier)
		.setScale(SCRAPED_NUTRITION_SCALE, RoundingMode.HALF_UP)
		.toDouble()
}
