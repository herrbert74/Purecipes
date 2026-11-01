package app.purecipes.backend.tools

import app.purecipes.backend.feature.nutrition.FDC_BRANDED_SOURCE_NAME
import app.purecipes.backend.feature.nutrition.FdcFoodMatchingSupport
import app.purecipes.backend.feature.nutrition.IngredientLineOverride
import app.purecipes.backend.feature.nutrition.IngredientLineOverrideKey
import app.purecipes.backend.feature.nutrition.IngredientLineOverridesLoader
import app.purecipes.backend.feature.nutrition.IngredientLineParser
import app.purecipes.backend.feature.nutrition.NutritionNameNormalizer
import app.purecipes.backend.feature.nutrition.NutritionSeedAliases
import java.math.BigDecimal
import java.sql.ResultSet
import javax.sql.DataSource

internal class IngredientSnapshotExporter(
	private val dataSource: DataSource,
) {

	fun collect(
		existingNames: List<IngredientNameSnapshotRow> = emptyList(),
		seedPins: Boolean = false,
		verifyPinnedMatchesAgainstDb: Boolean = false,
		lineOverrides: Map<IngredientLineOverrideKey, IngredientLineOverride> =
			IngredientLineOverridesLoader.loadDefaultOverrides(),
	): IngredientSnapshotData {
		val dbRows = loadDbRows()
		val lineRows = buildLineSnapshotRows(dbRows, lineOverrides)
		val nameRows = buildNameSnapshotRows(
			dbRows = dbRows,
			lineRows = lineRows,
			existingNames = existingNames,
			seedPins = seedPins,
			verifyPinnedMatchesAgainstDb = verifyPinnedMatchesAgainstDb,
			lineOverrides = lineOverrides,
		)
		return IngredientSnapshotData(
			names = nameRows,
			lines = lineRows,
		)
	}

	private fun buildLineSnapshotRows(
		dbRows: List<DbIngredientSnapshotRow>,
		lineOverrides: Map<IngredientLineOverrideKey, IngredientLineOverride>,
	): List<IngredientLineSnapshotRow> =
		dbRows
			.groupBy(DbIngredientSnapshotRow::sourceUrl)
			.toSortedMap()
			.values
			.flatMap { recipeRows ->
				val groups = recipeRows.groupBy { row -> row.groupIndex to row.groupName.orEmpty() }
				groups.entries
					.sortedWith(
						compareBy<Map.Entry<Pair<Int, String>, List<DbIngredientSnapshotRow>>> { it.key.first }
							.thenBy { it.key.second },
					)
					.flatMapIndexed { normalizedGroupIndex, (_, groupRows) ->
						groupRows
							.sortedWith(
								compareBy<DbIngredientSnapshotRow> { it.lineIndex }
									.thenBy { it.rawText },
							)
							.mapIndexed { normalizedLineIndex, row ->
								toLineSnapshotRow(
									row = row.copy(
										groupIndex = normalizedGroupIndex,
										lineIndex = normalizedLineIndex,
									),
									override = lineOverrides[
										IngredientLineOverrideKey(row.sourceUrl, row.rawText.trim()),
									],
								)
							}
					}
			}

	private fun loadDbRows(): List<DbIngredientSnapshotRow> =
		dataSource.connection.use { connection ->
			connection.prepareStatement(SNAPSHOT_ROWS_SQL).use { statement ->
				statement.executeQuery().use { resultSet ->
					buildList {
						while (resultSet.next()) {
							add(resultSet.toDbRow())
						}
					}
				}
			}
		}

	private fun toLineSnapshotRow(
		row: DbIngredientSnapshotRow,
		override: IngredientLineOverride? = null,
	): IngredientLineSnapshotRow {
		val parsedFallback = if (row.parsedName == null) {
			IngredientLineParser.parse(row.rawText)
		} else {
			null
		}
		val parsedName = override?.lookupName?.ifBlank { null }
			?: row.parsedName
			?: parsedFallback?.parsedName.orEmpty()
		val kind = override?.kind?.ifBlank { null }
			?: IngredientSnapshotClassifier.classifyLineKind(row.rawText, parsedName)
		val isIngredient = kind == IngredientSnapshotClassifier.KIND_INGREDIENT
		val quantity = if (isIngredient) {
			override?.quantity?.ifBlank { null }
				?: formatQuantity(row.quantity ?: parsedFallback?.quantity)
		} else {
			""
		}
		val unit = if (isIngredient) {
			override?.unit?.ifBlank { null }
				?: (row.unit ?: parsedFallback?.unit).orEmpty()
		} else {
			""
		}
		val lookupName = if (isIngredient) {
			resolveLookupName(parsedName)
		} else {
			""
		}
		val groupName = override?.groupName?.ifBlank { null } ?: row.groupName.orEmpty()
		return IngredientLineSnapshotRow(
			sourceUrl = sanitizeTsvField(row.sourceUrl),
			groupIndex = row.groupIndex,
			lineIndex = row.lineIndex,
			rawText = sanitizeTsvField(row.rawText),
			kind = kind,
			groupName = sanitizeTsvField(groupName),
			quantity = quantity,
			unit = sanitizeTsvField(unit),
			lookupName = sanitizeTsvField(lookupName),
			requirement = sanitizeTsvField(row.requirement),
			alternativeGroupKey = row.alternativeGroupKey?.toString().orEmpty(),
		)
	}

	private fun buildNameSnapshotRows(
		dbRows: List<DbIngredientSnapshotRow>,
		lineRows: List<IngredientLineSnapshotRow>,
		existingNames: List<IngredientNameSnapshotRow>,
		seedPins: Boolean,
		verifyPinnedMatchesAgainstDb: Boolean,
		lineOverrides: Map<IngredientLineOverrideKey, IngredientLineOverride>,
	): List<IngredientNameSnapshotRow> {
		val pinnedByLookupName = existingNames
			.filter { row -> row.status == STATUS_PINNED }
			.associateBy { row -> row.lookupName }
		val pinnedPantryByLookupName = pinnedByLookupName.values
			.filter { row -> row.pantryItem.isNotEmpty() }
			.associate { row -> row.lookupName to row.pantryItem }
		val ingredientLinesByLookup = lineRows
			.filter { row -> row.kind == IngredientSnapshotClassifier.KIND_INGREDIENT && row.lookupName.isNotEmpty() }
			.groupBy { row -> row.lookupName }
		val foodCandidatesByLookup = buildFoodCandidatesByLookup(dbRows, lineOverrides)
		val allLookupNames = (ingredientLinesByLookup.keys + pinnedByLookupName.keys).toSortedSet()

		return allLookupNames.map { lookupName ->
			val linesForName = ingredientLinesByLookup[lookupName].orEmpty()
			val bestFood = pickBestFoodCandidate(foodCandidatesByLookup[lookupName].orEmpty())
			val resolvedPantry = PantryItemResolver.resolve(
				lookupName = lookupName,
				pinnedPantryByLookupName = pinnedPantryByLookupName,
			)
			val pinned = pinnedByLookupName[lookupName]
			if (pinned != null) {
				resolvePinnedNameRow(
					pinned = pinned,
					bestFood = bestFood,
					resolvedPantry = resolvedPantry,
					lineCount = linesForName.size,
					verifyAgainstDb = verifyPinnedMatchesAgainstDb,
				)
			} else {
				val autoRow = IngredientNameSnapshotRow(
					lookupName = lookupName,
					status = STATUS_AUTO,
					pantryItem = sanitizeTsvField(resolvedPantry),
					fdcSource = sanitizeTsvField(bestFood?.fdcSource.orEmpty()),
					fdcId = sanitizeTsvField(bestFood?.fdcId.orEmpty()),
					fdcDescription = sanitizeTsvField(bestFood?.fdcDescription.orEmpty()),
					matchSource = sanitizeTsvField(bestFood?.matchSource.orEmpty()),
					lineCount = linesForName.size,
				)
				if (seedPins && ConfirmedIngredientMatchSeeder.isConfirmedMatch(autoRow)) {
					autoRow.copy(
						status = STATUS_PINNED,
						matchSource = STATUS_PINNED,
					)
				} else {
					autoRow
				}
			}
		}
	}

	private fun resolvePinnedNameRow(
		pinned: IngredientNameSnapshotRow,
		bestFood: FoodCandidate?,
		resolvedPantry: String,
		lineCount: Int,
		verifyAgainstDb: Boolean,
	): IngredientNameSnapshotRow {
		val pantryItem = pinned.pantryItem.ifBlank { sanitizeTsvField(resolvedPantry) }
		return if (verifyAgainstDb && bestFood != null && pinned.fdcId.isNotEmpty()) {
			IngredientNameSnapshotRow(
				lookupName = pinned.lookupName,
				status = STATUS_PINNED,
				pantryItem = pantryItem,
				fdcSource = sanitizeTsvField(bestFood.fdcSource),
				fdcId = sanitizeTsvField(bestFood.fdcId),
				fdcDescription = sanitizeTsvField(bestFood.fdcDescription),
				matchSource = sanitizeTsvField(bestFood.matchSource),
				lineCount = lineCount,
			)
		} else {
			pinned.copy(
				pantryItem = pantryItem,
				fdcDescription = pinned.fdcDescription.ifBlank {
					sanitizeTsvField(bestFood?.fdcDescription.orEmpty())
				},
				matchSource = pinned.matchSource.ifBlank { STATUS_PINNED },
				lineCount = lineCount,
			)
		}
	}

	private fun buildFoodCandidatesByLookup(
		dbRows: List<DbIngredientSnapshotRow>,
		lineOverrides: Map<IngredientLineOverrideKey, IngredientLineOverride>,
	): Map<String, List<FoodCandidate>> =
		dbRows.mapNotNull { row ->
			val override = lineOverrides[IngredientLineOverrideKey(row.sourceUrl, row.rawText.trim())]
			val parsedName = override?.lookupName?.ifBlank { null }
				?: row.parsedName
				?: IngredientLineParser.parse(row.rawText).parsedName
			val kind = override?.kind?.ifBlank { null }
				?: IngredientSnapshotClassifier.classifyLineKind(row.rawText, parsedName)
			val lookupName = resolveLookupName(parsedName)
			val fdcSource = row.fdcSource
			val fdcId = row.fdcId
			val isIngredientLine = kind == IngredientSnapshotClassifier.KIND_INGREDIENT && lookupName.isNotEmpty()
			val hasFoodIdentity = !fdcSource.isNullOrBlank() && !fdcId.isNullOrBlank()
			if (!isIngredientLine || !hasFoodIdentity) {
				null
			} else {
				lookupName to FoodCandidate(
					fdcSource = fdcSource.orEmpty(),
					fdcId = fdcId.orEmpty(),
					fdcDescription = row.fdcDescription.orEmpty(),
					matchSource = row.matchSource.orEmpty(),
				)
			}
		}.groupBy({ it.first }, { it.second })

	private fun pickBestFoodCandidate(candidates: List<FoodCandidate>): FoodCandidate? =
		candidates
			.groupingBy { it }
			.eachCount()
			.entries
			.minWithOrNull(
				compareByDescending<Map.Entry<FoodCandidate, Int>> { it.value }
					.thenBy { FdcFoodMatchingSupport.sourcePriority(it.key.fdcSource) }
					.thenBy { it.key.fdcId },
			)?.key

	companion object {

		const val STATUS_AUTO = "auto"
		const val STATUS_PINNED = "pinned"

		private const val TSV_SEPARATOR = "\t"
		private const val NAME_COLUMN_COUNT = 8
		private const val LINE_COLUMN_COUNT = 11

		private val NAMES_HEADER = listOf(
			"lookup_name",
			"status",
			"pantry_item",
			"fdc_source",
			"fdc_id",
			"fdc_description",
			"match_source",
			"line_count",
		).joinToString(TSV_SEPARATOR)

		private val LINES_HEADER = listOf(
			"source_url",
			"group_index",
			"line_index",
			"raw_text",
			"kind",
			"group_name",
			"quantity",
			"unit",
			"lookup_name",
			"requirement",
			"alternative_group_key",
		).joinToString(TSV_SEPARATOR)

		private const val SNAPSHOT_ROWS_SQL = """
			SELECT
				r.source_url,
				ig.order_index AS group_index,
				ig.name AS group_name,
				i.order_index AS line_index,
				i.ingredient AS raw_text,
				i.requirement,
				i.alternative_group_key,
				im.quantity,
				im.unit,
				im.parsed_name,
				inm.match_source,
				nf.source_name AS fdc_source,
				nf.source_id AS fdc_id,
				nf.display_name AS fdc_description
			FROM recipes r
			JOIN ingredient_groups ig ON ig.recipe_id = r.id
			JOIN ingredients i ON i.ingredient_group_id = ig.id
			LEFT JOIN ingredient_measurements im ON im.ingredient_id = i.id
			LEFT JOIN ingredient_nutrition_matches inm ON inm.ingredient_id = i.id
			LEFT JOIN nutrition_foods nf ON nf.id = inm.food_id
			WHERE r.source_url IS NOT NULL
				AND TRIM(r.source_url) <> ''
				AND i.ingredient IS NOT NULL
			ORDER BY r.source_url, ig.order_index, i.order_index
		"""

		fun formatNamesTsv(rows: List<IngredientNameSnapshotRow>): String = buildString {
			appendLine(NAMES_HEADER)
			rows.forEach { row ->
				appendLine(
					listOf(
						row.lookupName,
						row.status,
						row.pantryItem,
						row.fdcSource,
						row.fdcId,
						row.fdcDescription,
						row.matchSource,
						row.lineCount.toString(),
					).joinToString(TSV_SEPARATOR),
				)
			}
		}

		fun parseNamesTsv(content: String): List<IngredientNameSnapshotRow> =
			content.lineSequence()
				.drop(1)
				.filter { line -> line.isNotBlank() }
				.map { line ->
					val parts = line.split(TSV_SEPARATOR, limit = NAME_COLUMN_COUNT)
					require(parts.size == NAME_COLUMN_COUNT) {
						"Invalid names.tsv row (expected $NAME_COLUMN_COUNT columns): $line"
					}
					IngredientNameSnapshotRow(
						lookupName = parts[0],
						status = parts[1],
						pantryItem = parts[2],
						fdcSource = parts[3],
						fdcId = parts[4],
						fdcDescription = parts[5],
						matchSource = parts[6],
						lineCount = parts[7].toIntOrNull() ?: 0,
					)
				}
				.toList()

		fun formatLinesTsv(rows: List<IngredientLineSnapshotRow>): String = buildString {
			appendLine(LINES_HEADER)
			rows.forEach { row ->
				appendLine(
					listOf(
						row.sourceUrl,
						row.groupIndex.toString(),
						row.lineIndex.toString(),
						row.rawText,
						row.kind,
						row.groupName,
						row.quantity,
						row.unit,
						row.lookupName,
						row.requirement,
						row.alternativeGroupKey,
					).joinToString(TSV_SEPARATOR),
				)
			}
		}

		fun parseLinesTsv(content: String): List<IngredientLineSnapshotRow> =
			content.lineSequence()
				.drop(1)
				.filter { line -> line.isNotBlank() }
				.map { line ->
					val parts = line.split(TSV_SEPARATOR, limit = LINE_COLUMN_COUNT)
					require(parts.size == LINE_COLUMN_COUNT) {
						"Invalid lines.tsv row (expected $LINE_COLUMN_COUNT columns): $line"
					}
					IngredientLineSnapshotRow(
						sourceUrl = parts[0],
						groupIndex = parts[1].toInt(),
						lineIndex = parts[2].toInt(),
						rawText = parts[3],
						kind = parts[4],
						groupName = parts[5],
						quantity = parts[6],
						unit = parts[7],
						lookupName = parts[8],
						requirement = parts[9],
						alternativeGroupKey = parts[10],
					)
				}
				.toList()

		fun diff(
			expected: IngredientSnapshotData,
			actual: IngredientSnapshotData,
		): IngredientSnapshotDiff {
			val expectedNames = expected.names.associateBy(IngredientNameSnapshotRow::lookupName)
			val actualNames = actual.names.associateBy(IngredientNameSnapshotRow::lookupName)
			val addedNames = actual.names.filter { row -> row.lookupName !in expectedNames }
			val removedNames = expected.names.filter { row -> row.lookupName !in actualNames }
			val changedNames = actual.names.mapNotNull { after ->
				val before = expectedNames[after.lookupName] ?: return@mapNotNull null
				if (before != after) {
					IngredientNameChange(before = before, after = after)
				} else {
					null
				}
			}

			val expectedLines = expected.lines.associateBy(IngredientLineSnapshotRow::key)
			val actualLines = actual.lines.associateBy(IngredientLineSnapshotRow::key)
			val addedLines = actual.lines.filter { row -> row.key() !in expectedLines }
			val removedLines = expected.lines.filter { row -> row.key() !in actualLines }
			val changedLines = actual.lines.mapNotNull { after ->
				val before = expectedLines[after.key()] ?: return@mapNotNull null
				if (before != after) {
					IngredientLineChange(before = before, after = after)
				} else {
					null
				}
			}

			return IngredientSnapshotDiff(
				addedNames = addedNames,
				removedNames = removedNames,
				changedNames = changedNames,
				addedLines = addedLines,
				removedLines = removedLines,
				changedLines = changedLines,
			)
		}
	}
}

private data class DbIngredientSnapshotRow(
	val sourceUrl: String,
	val groupIndex: Int,
	val groupName: String?,
	val lineIndex: Int,
	val rawText: String,
	val requirement: String,
	val alternativeGroupKey: Int?,
	val quantity: BigDecimal?,
	val unit: String?,
	val parsedName: String?,
	val matchSource: String?,
	val fdcSource: String?,
	val fdcId: String?,
	val fdcDescription: String?,
)

private data class FoodCandidate(
	val fdcSource: String,
	val fdcId: String,
	val fdcDescription: String,
	val matchSource: String,
)

internal object ConfirmedIngredientMatchSeeder {

	private val confirmedNonBrandedMatchSources = setOf("alias", "name", "pinned")

	private val curatedDescriptionsByLookupKey: Map<String, Set<String>> by lazy {
		val map = mutableMapOf<String, MutableSet<String>>()
		NutritionSeedAliases.aliases.forEach { seedAlias ->
			val sanitizedDescriptions = seedAlias.preferredDescriptions
				.map(::sanitizeTsvField)
				.filter(String::isNotEmpty)
			val keys = listOf(
				NutritionNameNormalizer.normalize(seedAlias.alias),
				NutritionNameNormalizer.forLookup(seedAlias.alias),
			).filter(String::isNotEmpty)
			keys.forEach { key ->
				map.getOrPut(key) { linkedSetOf() }.addAll(sanitizedDescriptions)
			}
		}
		map
	}

	fun isConfirmedMatch(row: IngredientNameSnapshotRow): Boolean {
		if (row.fdcSource.isBlank() || row.fdcId.isBlank()) {
			return false
		}
		val isNonBrandedConfirmed = row.fdcSource != FDC_BRANDED_SOURCE_NAME &&
			row.matchSource in confirmedNonBrandedMatchSources
		return isNonBrandedConfirmed || isCuratedSeedAliasMatch(row.lookupName, row.fdcDescription)
	}

	fun isCuratedSeedAliasMatch(lookupName: String, fdcDescription: String): Boolean {
		val sanitizedDescription = sanitizeTsvField(fdcDescription)
		if (sanitizedDescription.isEmpty()) {
			return false
		}
		val keys = listOf(
			lookupName.trim(),
			NutritionNameNormalizer.normalize(lookupName),
			NutritionNameNormalizer.forLookup(lookupName),
		).filter(String::isNotEmpty)
		return keys.any { key ->
			curatedDescriptionsByLookupKey[key]?.contains(sanitizedDescription) == true
		}
	}
}

private fun resolveLookupName(parsedName: String): String =
	NutritionNameNormalizer.forLookup(parsedName).ifBlank {
		NutritionNameNormalizer.normalize(parsedName)
	}

private fun formatQuantity(quantity: BigDecimal?): String =
	quantity?.stripTrailingZeros()?.toPlainString().orEmpty()

private fun sanitizeTsvField(value: String): String =
	value
		.replace('\t', ' ')
		.replace('\r', ' ')
		.replace('\n', ' ')
		.replace('"', '″')
		.trim()

private fun ResultSet.toDbRow(): DbIngredientSnapshotRow =
	DbIngredientSnapshotRow(
		sourceUrl = getString("source_url")?.trim().orEmpty(),
		groupIndex = getInt("group_index"),
		groupName = getString("group_name")?.trim()?.takeIf(String::isNotEmpty),
		lineIndex = getInt("line_index"),
		rawText = getString("raw_text")?.trim().orEmpty(),
		requirement = getString("requirement")?.trim().orEmpty().ifBlank { "REQUIRED" },
		alternativeGroupKey = getObject("alternative_group_key")?.let { (it as? Number)?.toInt() },
		quantity = getBigDecimal("quantity"),
		unit = getString("unit")?.trim()?.takeIf(String::isNotEmpty),
		parsedName = getString("parsed_name")?.trim()?.takeIf(String::isNotEmpty),
		matchSource = getString("match_source")?.trim()?.takeIf(String::isNotEmpty),
		fdcSource = getString("fdc_source")?.trim()?.takeIf(String::isNotEmpty),
		fdcId = getString("fdc_id")?.trim()?.takeIf(String::isNotEmpty),
		fdcDescription = getString("fdc_description")?.trim()?.takeIf(String::isNotEmpty),
	)
