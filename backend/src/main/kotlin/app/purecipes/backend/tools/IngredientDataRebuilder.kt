package app.purecipes.backend.tools

import app.purecipes.backend.feature.nutrition.IngredientLineOverride
import app.purecipes.backend.feature.nutrition.IngredientLineOverrideKey
import app.purecipes.backend.feature.nutrition.IngredientLineOverridesLoader
import app.purecipes.backend.feature.nutrition.RecipeNutritionService
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.nio.file.Files
import java.nio.file.Path
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.sql.Types
import java.util.Locale
import javax.sql.DataSource
import kotlin.io.path.extension
import kotlin.io.path.isRegularFile
import kotlin.io.path.name
import kotlin.io.path.readText
import kotlin.io.path.writeText

private const val MAX_DB_INGREDIENT_LENGTH = 255

internal data class ScrapedIngredientItem(
	val text: String,
	val requirement: String = "REQUIRED",
	val alternativeGroupKey: Int? = null,
)

internal data class ScrapedIngredientGroup(
	val name: String?,
	val ingredients: List<ScrapedIngredientItem>,
)

internal data class ScrapedRecipeProcessingContext(
	val recipeTitle: String = "",
	val recipeInstructions: String = "",
	val lineOverridesByRawText: Map<String, IngredientLineOverride> = emptyMap(),
)

internal fun interface ScrapedIngredientGroupProcessor {

	fun processGroup(
		groupName: String?,
		storedItems: List<ScrapedIngredientItem>,
	): List<ScrapedIngredientGroup>

	fun processRecipeGroups(
		groups: List<ScrapedIngredientGroup>,
		context: ScrapedRecipeProcessingContext,
	): List<ScrapedIngredientGroup> =
		groups.flatMap { group -> processGroup(group.name, group.ingredients) }

	companion object {

		val default: ScrapedIngredientGroupProcessor by lazy {
			Class.forName("ScrapedIngredientLinesBridge")
				.getDeclaredConstructor()
				.newInstance() as ScrapedIngredientGroupProcessor
		}
	}
}

internal data class IngredientDataRebuildSummary(
	val scrapedRecipeCount: Int,
	val matchedJsonRecipeCount: Int,
	val updatedLineRecipeCount: Int,
	val syncedJsonFileCount: Int,
	val recalculatedNutritionRecipeCount: Int,
)

private data class ScrapedRecipeIdentity(
	val id: Int,
	val sourceUrl: String,
	val title: String,
	val instructions: String,
)

private data class StoredDbIngredientRow(
	val groupId: Int,
	val groupName: String?,
	val groupOrderIndex: Int,
	val ingredientText: String?,
	val lineOrderIndex: Int?,
	val requirement: String?,
	val alternativeGroupKey: Int?,
)

private data class StoredDbRecipeGroups(
	val groups: List<ScrapedIngredientGroup>,
	val hasSequentialOrderIndices: Boolean,
)

private data class JsonArchiveRecipeEntry(
	val file: Path,
	val root: JsonObject,
	val groups: List<ScrapedIngredientGroup>,
)

internal class IngredientDataRebuilder(
	private val dataSource: DataSource,
	private val processor: ScrapedIngredientGroupProcessor = ScrapedIngredientGroupProcessor.default,
	private val recalculateNutrition: (List<Int>) -> Unit = { recipeIds ->
		RecipeNutritionService(dataSource).calculateAndPersistRecipeIds(recipeIds)
	},
) {

	private val json = Json {
		prettyPrint = true
		ignoreUnknownKeys = true
	}

	fun rebuild(
		jsonArchiveDir: Path? = defaultJsonArchiveDir(),
		useJsonAsInput: Boolean = false,
		syncJsonArchive: Boolean = true,
		recalculateAllNutrition: Boolean = true,
		lineOverrides: Map<IngredientLineOverrideKey, IngredientLineOverride> =
			IngredientLineOverridesLoader.loadDefaultOverrides(),
	): IngredientDataRebuildSummary {
		val jsonEntriesBySourceUrl = loadJsonEntriesBySourceUrl(jsonArchiveDir)
		val scrapedRecipes = loadScrapedRecipes()
		var matchedJsonCount = 0
		var syncedJsonCount = 0
		val updatedRecipeIds = mutableListOf<Int>()

		scrapedRecipes.forEach { recipe ->
			val dbState = loadStoredDbGroups(recipe.id)
			val jsonEntry = jsonEntriesBySourceUrl[recipe.sourceUrl]
			if (jsonEntry != null) {
				matchedJsonCount += 1
			}
			val sourceGroups = if (useJsonAsInput && jsonEntry != null) {
				jsonEntry.groups
			} else {
				dbState.groups.ifEmpty { jsonEntry?.groups.orEmpty() }
			}
			val recipeOverrides = lineOverrides
				.filterKeys { key -> key.sourceUrl == recipe.sourceUrl }
				.mapKeys { (key, _) -> key.rawText }
			val reprocessedGroups = reprocessGroups(
				sourceGroups = sourceGroups,
				context = ScrapedRecipeProcessingContext(
					recipeTitle = recipe.title,
					recipeInstructions = recipe.instructions,
					lineOverridesByRawText = recipeOverrides,
				),
			)
			val shouldUpdateDb = reprocessedGroups.isNotEmpty() && (
				reprocessedGroups != dbState.groups || !dbState.hasSequentialOrderIndices
				)
			if (shouldUpdateDb) {
				replaceRecipeIngredientGroups(recipe.id, reprocessedGroups)
				updatedRecipeIds += recipe.id
			}
			if (syncJsonArchive && jsonEntry != null && reprocessedGroups.isNotEmpty()) {
				if (syncJsonRecipeGroups(jsonEntry, reprocessedGroups)) {
					syncedJsonCount += 1
				}
			}
		}

		val nutritionRecipeIds = if (recalculateAllNutrition) {
			scrapedRecipes.map(ScrapedRecipeIdentity::id)
		} else {
			updatedRecipeIds
		}
		recalculateNutrition(nutritionRecipeIds)

		return IngredientDataRebuildSummary(
			scrapedRecipeCount = scrapedRecipes.size,
			matchedJsonRecipeCount = matchedJsonCount,
			updatedLineRecipeCount = updatedRecipeIds.size,
			syncedJsonFileCount = syncedJsonCount,
			recalculatedNutritionRecipeCount = nutritionRecipeIds.size,
		)
	}

	fun restoreFromLinesSnapshot(lines: List<IngredientLineSnapshotRow>): Int {
		val recipesByUrl = loadScrapedRecipes().associateBy(ScrapedRecipeIdentity::sourceUrl)
		val linesByUrl = lines.groupBy(IngredientLineSnapshotRow::sourceUrl)
		var restoredCount = 0

		linesByUrl.forEach { (sourceUrl, recipeLines) ->
			val recipe = recipesByUrl[sourceUrl] ?: return@forEach
			val groups = recipeLines
				.sortedWith(
					compareBy<IngredientLineSnapshotRow>(
						IngredientLineSnapshotRow::groupIndex,
						IngredientLineSnapshotRow::lineIndex,
					),
				)
				.groupBy(IngredientLineSnapshotRow::groupIndex)
				.values
				.map { groupRows ->
					val groupName = groupRows.first().groupName.trim().ifBlank { null }
					ScrapedIngredientGroup(
						name = groupName,
						ingredients = groupRows.map { row ->
							ScrapedIngredientItem(
								text = row.rawText,
								requirement = row.requirement.ifBlank { "REQUIRED" },
								alternativeGroupKey = row.alternativeGroupKey.toIntOrNull(),
							)
						},
					)
				}
			if (groups.isNotEmpty()) {
				replaceRecipeIngredientGroups(recipe.id, groups)
				restoredCount += 1
			}
		}
		return restoredCount
	}

	internal fun reprocessGroups(
		sourceGroups: List<ScrapedIngredientGroup>,
		context: ScrapedRecipeProcessingContext = ScrapedRecipeProcessingContext(),
	): List<ScrapedIngredientGroup> =
		processor.processRecipeGroups(sourceGroups, context)
			.map { group ->
				group.copy(
					ingredients = group.ingredients.map { item ->
						item.copy(text = item.text.take(MAX_DB_INGREDIENT_LENGTH))
					},
				)
			}
			.filter { group -> group.ingredients.isNotEmpty() }

	private fun loadJsonEntriesBySourceUrl(jsonArchiveDir: Path?): Map<String, JsonArchiveRecipeEntry> {
		if (jsonArchiveDir == null || !Files.isDirectory(jsonArchiveDir)) {
			return emptyMap()
		}
		val jsonFiles = Files.walk(jsonArchiveDir).use { stream ->
			stream
				.filter { path -> path.isRegularFile() && path.extension == "json" }
				.sorted(Comparator.comparing(Path::name))
				.toList()
		}
		return buildMap {
			jsonFiles.forEach { file ->
				parseJsonRecipeEntry(file)?.let { (sourceUrl, entry) ->
					put(sourceUrl, entry)
				}
			}
		}
	}

	private fun parseJsonRecipeEntry(file: Path): Pair<String, JsonArchiveRecipeEntry>? {
		val root = runCatching { json.parseToJsonElement(file.readText()).jsonObject }.getOrNull()
		val sourceUrl = root?.get("source_url")?.jsonPrimitive?.contentOrNull?.trim().orEmpty()
		val groups = root?.get("ingredient_groups")?.jsonArray
			?.mapNotNull(::parseStoredGroupElement)
			?.filter { group -> group.ingredients.isNotEmpty() }
			.orEmpty()
		return if (root == null || sourceUrl.isBlank() || groups.isEmpty()) {
			null
		} else {
			sourceUrl to JsonArchiveRecipeEntry(file = file, root = root, groups = groups)
		}
	}

	private fun syncJsonRecipeGroups(
		entry: JsonArchiveRecipeEntry,
		reprocessedGroups: List<ScrapedIngredientGroup>,
	): Boolean {
		if (entry.groups == reprocessedGroups) {
			return false
		}
		val updatedGroupsJson = buildJsonArray {
			reprocessedGroups.forEach { group ->
				add(
					buildJsonObject {
						put("name", JsonPrimitive(group.name.orEmpty()))
						put(
							"ingredients",
							buildJsonArray {
								group.ingredients.forEach { item ->
									add(
										buildJsonObject {
											put("text", JsonPrimitive(item.text))
											put("requirement", JsonPrimitive(item.requirement))
											item.alternativeGroupKey?.let { key ->
												put("alternativeGroupKey", JsonPrimitive(key))
											}
										},
									)
								}
							},
						)
					},
				)
			}
		}
		val updatedRoot = buildJsonObject {
			entry.root.forEach { (key, value) ->
				put(key, if (key == "ingredient_groups") updatedGroupsJson else value)
			}
		}
		entry.file.writeText(json.encodeToString(JsonObject.serializer(), updatedRoot) + "\n")
		return true
	}

	private fun parseStoredGroupElement(element: JsonElement): ScrapedIngredientGroup? =
		when (element) {
			is JsonObject -> {
				val name = element["name"]?.jsonPrimitive?.contentOrNull?.trim().orEmpty().ifBlank { null }
				val items = element["ingredients"]?.jsonArray
					?.mapNotNull(::parseStoredIngredientElement)
					.orEmpty()
				if (items.isEmpty()) null else ScrapedIngredientGroup(name = name, ingredients = items)
			}

			is JsonArray -> {
				val name = element.getOrNull(0)?.jsonPrimitive?.contentOrNull?.trim().orEmpty().ifBlank { null }
				val items = element.getOrNull(1)?.jsonArray
					?.mapNotNull(::parseStoredIngredientElement)
					.orEmpty()
				if (items.isEmpty()) null else ScrapedIngredientGroup(name = name, ingredients = items)
			}

			else -> null
		}

	private fun parseStoredIngredientElement(element: JsonElement): ScrapedIngredientItem? =
		when (element) {
			is JsonPrimitive -> element.contentOrNull?.trim()?.takeIf(String::isNotBlank)?.let { text ->
				ScrapedIngredientItem(text = text)
			}

			is JsonObject -> {
				val text = element["text"]?.jsonPrimitive?.contentOrNull?.trim().orEmpty()
				if (text.isBlank()) {
					null
				} else {
					val requirement = element["requirement"]?.jsonPrimitive?.contentOrNull
						?.trim()
						?.uppercase(Locale.ROOT)
						?.takeIf(String::isNotBlank)
						?: "REQUIRED"
					val alternativeGroupKey = element["alternativeGroupKey"]?.jsonPrimitive?.contentOrNull
						?.toIntOrNull()
					ScrapedIngredientItem(
						text = text,
						requirement = requirement,
						alternativeGroupKey = alternativeGroupKey,
					)
				}
			}

			else -> null
		}

	private fun loadScrapedRecipes(): List<ScrapedRecipeIdentity> =
		dataSource.connection.use { connection ->
			connection.prepareStatement(
				"""
				SELECT id, source_url, title, COALESCE(instructions, '') AS instructions
				FROM recipes
				WHERE source_url IS NOT NULL
					AND TRIM(source_url) <> ''
					AND created_by_user_id IS NULL
				ORDER BY id
				""".trimIndent(),
			).use { statement ->
				statement.executeQuery().use { resultSet ->
					buildList {
						while (resultSet.next()) {
							add(
								ScrapedRecipeIdentity(
									id = resultSet.getInt("id"),
									sourceUrl = resultSet.getString("source_url").trim(),
									title = resultSet.getString("title")?.trim().orEmpty(),
									instructions = resultSet.getString("instructions").orEmpty(),
								),
							)
						}
					}
				}
			}
		}

	private fun loadStoredDbGroups(recipeId: Int): StoredDbRecipeGroups {
		val rows = dataSource.connection.use { connection ->
			connection.prepareStatement(
				"""
				SELECT
					ig.id AS group_id,
					ig.name AS group_name,
					ig.order_index AS group_order_index,
					i.ingredient AS ingredient_text,
					i.order_index AS line_order_index,
					i.requirement AS requirement,
					i.alternative_group_key AS alternative_group_key
				FROM ingredient_groups ig
				LEFT JOIN ingredients i ON i.ingredient_group_id = ig.id
				WHERE ig.recipe_id = ?
				ORDER BY ig.order_index, ig.id, i.order_index, i.id
				""".trimIndent(),
			).use { statement ->
				statement.setInt(1, recipeId)
				statement.executeQuery().use(::readStoredDbRows)
			}
		}

		val byGroupId = rows.groupBy(StoredDbIngredientRow::groupId)
		var sequentialIndices = true
		val groups = byGroupId.values.mapIndexedNotNull { expectedGroupIndex, groupRows ->
			val first = groupRows.first()
			if (first.groupOrderIndex != expectedGroupIndex) {
				sequentialIndices = false
			}
			val items = groupRows.mapNotNull { row ->
				val text = row.ingredientText?.trim()?.takeIf(String::isNotBlank)
				if (text == null) {
					null
				} else {
					ScrapedIngredientItem(
						text = text,
						requirement = row.requirement ?: "REQUIRED",
						alternativeGroupKey = row.alternativeGroupKey,
					)
				}
			}
			val lineIndices = groupRows.mapNotNull(StoredDbIngredientRow::lineOrderIndex)
			if (lineIndices != lineIndices.indices.toList()) {
				sequentialIndices = false
			}
			if (items.isEmpty()) {
				null
			} else {
				ScrapedIngredientGroup(
					name = first.groupName?.trim()?.ifBlank { null },
					ingredients = items,
				)
			}
		}
		return StoredDbRecipeGroups(
			groups = groups,
			hasSequentialOrderIndices = sequentialIndices,
		)
	}

	private fun readStoredDbRows(resultSet: ResultSet): List<StoredDbIngredientRow> =
		buildList {
			while (resultSet.next()) {
				val lineIndexRaw = resultSet.getInt("line_order_index")
				val lineOrderIndex = if (resultSet.wasNull()) null else lineIndexRaw
				val altKeyRaw = resultSet.getInt("alternative_group_key")
				val altKey = if (resultSet.wasNull()) null else altKeyRaw
				add(
					StoredDbIngredientRow(
						groupId = resultSet.getInt("group_id"),
						groupName = resultSet.getString("group_name"),
						groupOrderIndex = resultSet.getInt("group_order_index"),
						ingredientText = resultSet.getString("ingredient_text"),
						lineOrderIndex = lineOrderIndex,
						requirement = resultSet.getString("requirement"),
						alternativeGroupKey = altKey,
					),
				)
			}
		}

	private fun replaceRecipeIngredientGroups(
		recipeId: Int,
		groups: List<ScrapedIngredientGroup>,
	) {
		dataSource.connection.use { connection ->
			val previousAutoCommit = connection.autoCommit
			connection.autoCommit = false
			try {
				deleteIngredientGroups(connection, recipeId)
				insertIngredientGroups(connection, recipeId, groups)
				connection.commit()
			} catch (exception: SQLException) {
				connection.rollback()
				throw exception
			} finally {
				connection.autoCommit = previousAutoCommit
			}
		}
	}

	private fun deleteIngredientGroups(connection: Connection, recipeId: Int) {
		connection.prepareStatement("DELETE FROM ingredient_groups WHERE recipe_id = ?").use { statement ->
			statement.setInt(1, recipeId)
			statement.executeUpdate()
		}
	}

	private fun insertIngredientGroups(
		connection: Connection,
		recipeId: Int,
		groups: List<ScrapedIngredientGroup>,
	) {
		val insertGroupSql =
			"INSERT INTO ingredient_groups (recipe_id, name, order_index) VALUES (?, ?, ?)"
		val insertIngredientSql =
			"""
			INSERT INTO ingredients (
				ingredient_group_id,
				ingredient,
				order_index,
				requirement,
				alternative_group_key
			) VALUES (?, ?, ?, ?, ?)
			""".trimIndent()

		groups.forEachIndexed { groupIndex, group ->
			val groupId = connection.prepareStatement(
				insertGroupSql,
				Statement.RETURN_GENERATED_KEYS,
			).use { statement ->
				var paramIndex = 1
				statement.setInt(paramIndex++, recipeId)
				statement.setString(paramIndex++, group.name)
				statement.setInt(paramIndex, groupIndex)
				statement.executeUpdate()
				statement.generatedKeys.use { keys ->
					check(keys.next())
					keys.getInt(1)
				}
			}
			connection.prepareStatement(insertIngredientSql).use { statement ->
				group.ingredients.forEachIndexed { lineIndex, item ->
					var paramIndex = 1
					statement.setInt(paramIndex++, groupId)
					statement.setString(paramIndex++, item.text.take(MAX_DB_INGREDIENT_LENGTH))
					statement.setInt(paramIndex++, lineIndex)
					statement.setString(paramIndex++, item.requirement)
					if (item.alternativeGroupKey == null) {
						statement.setNull(paramIndex, Types.INTEGER)
					} else {
						statement.setInt(paramIndex, item.alternativeGroupKey)
					}
					statement.addBatch()
				}
				statement.executeBatch()
			}
		}
	}

	companion object {

		fun defaultJsonArchiveDir(): Path? {
			val userHome = System.getProperty("user.home")?.takeIf(String::isNotBlank) ?: return null
			val candidate = Path.of(userHome, "Purecipes", "recipeoutput", "recipes")
			return candidate.takeIf(Files::isDirectory)
		}
	}
}
