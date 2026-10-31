package app.purecipes.backend.tools

import app.purecipes.backend.feature.nutrition.GramWeightSource
import app.purecipes.backend.feature.nutrition.NutritionNameNormalizer
import app.purecipes.backend.feature.search.IngredientVocabulary
import java.math.BigDecimal
import java.sql.ResultSet
import javax.sql.DataSource

internal data class RecipeReference(
	val recipeId: Int,
	val recipeTitle: String,
) {

	fun format(): String = "Recipe $recipeId: $recipeTitle"
}

internal data class FrequencyCount(
	val label: String,
	val count: Int,
	val recipes: List<RecipeReference> = emptyList(),
)

internal data class IngredientFoodMatchReportData(
	val countableLines: Int,
	val notMeasurableCount: Int,
	val noFoodCount: Int,
	val noGramsCount: Int,
	val matchedCount: Int,
	val massGramCount: Int,
	val measureGramCount: Int,
	val densityGramCount: Int,
	val notMeasurableNames: List<FrequencyCount>,
	val unmatchedNames: List<FrequencyCount>,
	val unresolvedGramsNames: List<FrequencyCount>,
	val totalRecipes: Int,
	val calculatedNutritionCount: Int,
	val scrapedNutritionCount: Int,
	val completeEstimateCount: Int,
	val partialEstimateCount: Int,
	val missingNutritionCount: Int,
	val scrapedRecipes: List<String>,
)

internal class IngredientFoodMatchReporter(
	private val dataSource: DataSource,
) {

	fun collect(): IngredientFoodMatchReportData {
		val classified = classifyIngredientRows(loadIngredientRows())
		val recipes = loadRecipeNutritionRows()
		val matched = classified.filter { line -> line.kind == IngredientFoodMatchGapKind.MATCHED }
		return IngredientFoodMatchReportData(
			countableLines = classified.size,
			notMeasurableCount = classified.count { it.kind == IngredientFoodMatchGapKind.NOT_MEASURABLE },
			noFoodCount = classified.count { it.kind == IngredientFoodMatchGapKind.NO_FOOD },
			noGramsCount = classified.count { it.kind == IngredientFoodMatchGapKind.NO_GRAMS },
			matchedCount = matched.size,
			massGramCount = matched.count { it.gramsSource == GramWeightSource.MASS },
			measureGramCount = matched.count { it.gramsSource == GramWeightSource.MEASURE },
			densityGramCount = matched.count { it.gramsSource == GramWeightSource.DENSITY },
			notMeasurableNames = frequencyCounts(
				classified,
				IngredientFoodMatchGapKind.NOT_MEASURABLE,
			),
			unmatchedNames = frequencyCounts(
				classified,
				IngredientFoodMatchGapKind.NO_FOOD,
			),
			unresolvedGramsNames = frequencyCounts(
				classified,
				IngredientFoodMatchGapKind.NO_GRAMS,
			),
			totalRecipes = recipes.size,
			calculatedNutritionCount = recipes.count { it.calculationSource == CALCULATION_SOURCE_CALCULATED },
			scrapedNutritionCount = recipes.count { it.calculationSource == CALCULATION_SOURCE_SCRAPED },
			completeEstimateCount = recipes.count { it.isComplete == true },
			partialEstimateCount = recipes.count { it.hasNutrition && it.isComplete != true },
			missingNutritionCount = recipes.count { !it.hasNutrition },
			scrapedRecipes = recipes
				.filter { it.calculationSource == CALCULATION_SOURCE_SCRAPED }
				.map { "Recipe ${it.recipeId}: ${it.title}" },
		)
	}

	fun format(
		data: IngredientFoodMatchReportData,
		includeScrapedRecipes: Boolean = false,
	): String = buildString {
		appendLine("Ingredient food-table matching report")
		appendLine("Countable ingredient lines: ${data.countableLines}")
		appendLine("Not measurable: ${data.notMeasurableCount}")
		appendLine("No food match: ${data.noFoodCount}")
		appendLine("No gram weight: ${data.noGramsCount}")
		appendLine("Matched: ${data.matchedCount}")
		appendLine("  Gram source mass: ${data.massGramCount}")
		appendLine("  Gram source measure: ${data.measureGramCount}")
		appendLine("  Gram source density fallback: ${data.densityGramCount}")
		appendLine()
		appendLine("Recipes: ${data.totalRecipes}")
		appendLine("Calculated nutrition: ${data.calculatedNutritionCount}")
		appendLine("Scraped nutrition: ${data.scrapedNutritionCount}")
		appendLine("Complete estimates: ${data.completeEstimateCount}")
		appendLine("Partial estimates: ${data.partialEstimateCount}")
		appendLine("Without nutrition row: ${data.missingNutritionCount}")
		appendFrequencySection("Not measurable parsed names", data.notMeasurableNames)
		appendFrequencySection("Unmatched parsed names", data.unmatchedNames)
		appendFrequencySection("Unresolved gram weights", data.unresolvedGramsNames)
		if (includeScrapedRecipes) {
			appendLineList("Scraped nutrition recipes", data.scrapedRecipes)
		}
	}

	private fun loadIngredientRows(): List<IngredientFoodMatchRow> =
		dataSource.connection.use { connection ->
			connection.prepareStatement(INGREDIENT_ROWS_SQL).use { statement ->
				statement.executeQuery().use { resultSet ->
					buildList {
						while (resultSet.next()) {
							add(resultSet.toIngredientFoodMatchRow())
						}
					}
				}
			}
		}

	private fun loadRecipeNutritionRows(): List<RecipeNutritionRow> =
		dataSource.connection.use { connection ->
			connection.prepareStatement(RECIPE_NUTRITION_SQL).use { statement ->
				statement.executeQuery().use { resultSet ->
					buildList {
						while (resultSet.next()) {
							add(resultSet.toRecipeNutritionRow())
						}
					}
				}
			}
		}

	private fun classifyIngredientRows(rows: List<IngredientFoodMatchRow>): List<ClassifiedIngredientLine> {
		val chosenAlternativeKeysByRecipe = chosenAlternativeRowsByRecipe(rows)
		return rows.mapNotNull { row ->
			val labelSource = row.parsedName?.trim().orEmpty().ifBlank { row.ingredientText }
			val countable = row.hasMeasurement &&
				!IngredientVocabulary.isIgnorableIngredientLine(row.ingredientText) &&
				NutritionNameNormalizer.hasMeaningfulFoodName(labelSource) &&
				countsTowardMatching(row, chosenAlternativeKeysByRecipe)
			when {
				countable -> classifyLine(row)
				else -> null
			}
		}
	}

	private fun chosenAlternativeRowsByRecipe(
		rows: List<IngredientFoodMatchRow>,
	): Map<Int, Map<Int, IngredientFoodMatchRow>> =
		rows
			.filter { row ->
				row.requirement == "ALTERNATIVE" && row.alternativeGroupKey != null
			}
			.groupBy { row -> row.recipeId }
			.mapValues { (_, recipeRows) ->
				recipeRows
					.groupBy { row -> row.alternativeGroupKey!! }
					.mapValues { (_, groupRows) ->
						groupRows.firstOrNull { row -> row.foodId != null && row.gramsResolved != null }
							?: groupRows.firstOrNull { row -> row.foodId != null }
							?: groupRows.first()
					}
			}

	private fun classifyLine(row: IngredientFoodMatchRow): ClassifiedIngredientLine {
		val parsedLabel = row.parsedName?.trim().orEmpty().ifBlank { row.ingredientText }
		val groupedParsedLabel = groupedParsedName(parsedLabel)
		val kind = when {
			!row.isMeasurable -> IngredientFoodMatchGapKind.NOT_MEASURABLE
			row.foodId == null -> IngredientFoodMatchGapKind.NO_FOOD
			row.gramsResolved == null -> IngredientFoodMatchGapKind.NO_GRAMS
			else -> IngredientFoodMatchGapKind.MATCHED
		}
		val label = when (kind) {
			IngredientFoodMatchGapKind.NO_GRAMS -> unresolvedGramsLabel(groupedParsedLabel, row.unit)
			else -> groupedParsedLabel
		}
		return ClassifiedIngredientLine(
			kind = kind,
			label = label,
			gramsSource = row.gramsSource,
			recipe = RecipeReference(
				recipeId = row.recipeId,
				recipeTitle = row.recipeTitle,
			),
		)
	}

	private companion object {

		const val CALCULATION_SOURCE_CALCULATED = "calculated"
		const val CALCULATION_SOURCE_SCRAPED = "scraped"

		const val INGREDIENT_ROWS_SQL = """
			SELECT
				r.id AS recipe_id,
				r.title AS recipe_title,
				i.ingredient AS ingredient_text,
				i.requirement,
				i.alternative_group_key,
				im.ingredient_id AS measurement_ingredient_id,
				im.is_measurable,
				im.parsed_name,
				im.unit,
				inm.food_id,
				inc.grams_resolved,
				inc.grams_source
			FROM recipes r
			JOIN ingredient_groups ig ON ig.recipe_id = r.id
			JOIN ingredients i ON i.ingredient_group_id = ig.id
			LEFT JOIN ingredient_measurements im ON im.ingredient_id = i.id
			LEFT JOIN ingredient_nutrition_matches inm ON inm.ingredient_id = i.id
			LEFT JOIN ingredient_nutrition_contributions inc ON inc.ingredient_id = i.id
			WHERE i.ingredient IS NOT NULL
			ORDER BY r.id, ig.order_index, i.order_index
		"""

		const val RECIPE_NUTRITION_SQL = """
			SELECT
				r.id AS recipe_id,
				r.title,
				n.recipe_id AS nutrition_recipe_id,
				n.calculation_source,
				n.is_complete
			FROM recipes r
			LEFT JOIN nutrition n ON n.recipe_id = r.id
			ORDER BY r.id
		"""
	}
}

private enum class IngredientFoodMatchGapKind {
	NOT_MEASURABLE,
	NO_FOOD,
	NO_GRAMS,
	MATCHED,
}

private data class IngredientFoodMatchRow(
	val recipeId: Int,
	val recipeTitle: String,
	val ingredientText: String,
	val requirement: String,
	val alternativeGroupKey: Int?,
	val hasMeasurement: Boolean,
	val isMeasurable: Boolean,
	val parsedName: String?,
	val unit: String?,
	val foodId: Int?,
	val gramsResolved: BigDecimal?,
	val gramsSource: String?,
)

private data class RecipeNutritionRow(
	val recipeId: Int,
	val title: String,
	val hasNutrition: Boolean,
	val calculationSource: String?,
	val isComplete: Boolean?,
)

private data class ClassifiedIngredientLine(
	val kind: IngredientFoodMatchGapKind,
	val label: String,
	val gramsSource: String?,
	val recipe: RecipeReference,
)

private fun countsTowardMatching(
	row: IngredientFoodMatchRow,
	chosenAlternativeKeysByRecipe: Map<Int, Map<Int, IngredientFoodMatchRow>>,
): Boolean = when (row.requirement) {
	"OPTIONAL" -> false
	"ALTERNATIVE" -> {
		val groupKey = row.alternativeGroupKey ?: return true
		chosenAlternativeKeysByRecipe[row.recipeId]?.get(groupKey) === row
	}

	else -> true
}

private fun groupedParsedName(parsedLabel: String): String =
	NutritionNameNormalizer.forLookup(parsedLabel).ifBlank { parsedLabel.trim() }

private fun unresolvedGramsLabel(parsedName: String, unit: String?): String =
	if (unit.isNullOrBlank()) {
		parsedName
	} else {
		"$parsedName ($unit)"
	}

private fun frequencyCounts(
	lines: List<ClassifiedIngredientLine>,
	kind: IngredientFoodMatchGapKind,
): List<FrequencyCount> =
	lines
		.filter { it.kind == kind && it.label.isNotBlank() }
		.groupBy { it.label }
		.map { (label, matchingLines) ->
			FrequencyCount(
				label = label,
				count = matchingLines.size,
				recipes = matchingLines.map(ClassifiedIngredientLine::recipe),
			)
		}
		.sortedWith(compareByDescending<FrequencyCount> { it.count }.thenBy { it.label })

private fun StringBuilder.appendFrequencySection(title: String, rows: List<FrequencyCount>) {
	if (rows.isEmpty()) {
		return
	}
	appendLine()
	appendLine("$title (${rows.size} unique)")
	rows.forEach { row ->
		if (row.count == 1 && row.recipes.size == 1) {
			appendLine("  ${row.count} x ${row.label} (${row.recipes.single().format()})")
		} else {
			appendLine("  ${row.count} x ${row.label}")
			row.recipes.forEach { recipe ->
				appendLine("    ${recipe.format()}")
			}
		}
	}
}

private fun StringBuilder.appendLineList(title: String, lines: List<String>) {
	if (lines.isEmpty()) {
		return
	}
	appendLine()
	appendLine("$title (${lines.size})")
	lines.forEach { line ->
		appendLine("  $line")
	}
}

private fun ResultSet.toIngredientFoodMatchRow(): IngredientFoodMatchRow {
	val ingredientText = getString("ingredient_text")?.trim().orEmpty()
	return IngredientFoodMatchRow(
		recipeId = getInt("recipe_id"),
		recipeTitle = getString("recipe_title") ?: "",
		ingredientText = ingredientText,
		requirement = getString("requirement") ?: "REQUIRED",
		alternativeGroupKey = getNullableInt("alternative_group_key"),
		hasMeasurement = getObject("measurement_ingredient_id") != null,
		isMeasurable = getNullableBoolean("is_measurable") == true,
		parsedName = getNullableTrimmedString("parsed_name"),
		unit = getNullableTrimmedString("unit"),
		foodId = getNullableInt("food_id"),
		gramsResolved = getNullableBigDecimal("grams_resolved"),
		gramsSource = getNullableTrimmedString("grams_source"),
	)
}

private fun ResultSet.toRecipeNutritionRow(): RecipeNutritionRow =
	RecipeNutritionRow(
		recipeId = getInt("recipe_id"),
		title = getString("title") ?: "",
		hasNutrition = getObject("nutrition_recipe_id") != null,
		calculationSource = getNullableTrimmedString("calculation_source"),
		isComplete = getNullableBoolean("is_complete"),
	)

private fun ResultSet.getNullableTrimmedString(columnLabel: String): String? =
	getString(columnLabel)?.trim()?.takeIf { it.isNotEmpty() }

private fun ResultSet.getNullableInt(columnLabel: String): Int? =
	getObject(columnLabel)?.let { value ->
		when (value) {
			is Number -> value.toInt()
			else -> null
		}
	}

private fun ResultSet.getNullableBigDecimal(columnLabel: String): BigDecimal? =
	getObject(columnLabel)?.let { value ->
		when (value) {
			is BigDecimal -> value
			is Number -> BigDecimal.valueOf(value.toDouble())
			else -> null
		}
	}

private fun ResultSet.getNullableBoolean(columnLabel: String): Boolean? =
	getObject(columnLabel)?.let { value ->
		when (value) {
			is Boolean -> value
			else -> null
		}
	}
