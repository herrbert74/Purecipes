package app.purecipes.backend.tools

import app.purecipes.backend.db.Db
import app.purecipes.backend.feature.search.IngredientVocabulary
import java.net.URI

private const val ARG_OUTPUT = "--output"
private const val DEFAULT_INGREDIENTS_SOURCE =
	"feature/search/ui/src/commonMain/kotlin/app/purecipes/feature/search/ui/filter/IngredientFilterSection.kt"
private const val MISSING_ONE = 1
private const val MISSING_TWO = 2
private const val MISSING_INVISIBLE_THRESHOLD = 3
private const val TSV_SEPARATOR = "	"

private data class VisibilityRecipeIngredientRow(
	val recipeId: Int,
	val recipeTitle: String,
	val sourceUrl: String?,
	val ingredient: String?,
)

private data class RecipeCoverage(
	val recipeId: Int,
	val recipeTitle: String,
	val sourceDomain: String,
	val unknownIngredients: Set<String>,
)

private data class SourceBreakdown(
	val sourceDomain: String,
	val total: Int,
	val visible: Int,
	val missingOne: Int,
	val missingTwo: Int,
	val invisible: Int,
)

fun main(args: Array<String>) {
	val outputPath = args.toList().windowed(size = 2, step = 1)
		.firstOrNull { it.first() == ARG_OUTPUT }
		?.last()

	val allowedIngredients = loadAllowedIngredients()
	if (allowedIngredients.isEmpty()) {
		println("No allowed app ingredients found. Cannot run recipe visibility analysis report.")
		return
	}

	val recipeCoverage = collectRecipeCoverage(allowedIngredients)

	val totalRecipes = recipeCoverage.size
	val visible = recipeCoverage.count { it.unknownIngredients.isEmpty() }
	val missingOne = recipeCoverage.count { it.unknownIngredients.size == MISSING_ONE }
	val missingTwo = recipeCoverage.count { it.unknownIngredients.size == MISSING_TWO }
	val invisible = recipeCoverage.count { it.unknownIngredients.size >= MISSING_INVISIBLE_THRESHOLD }
	val visibleWithOneMissingAllowed = visible + missingOne
	val visibleWithTwoMissingAllowed = visible + missingOne + missingTwo

	val sourceBreakdowns = recipeCoverage
		.groupBy { it.sourceDomain }
		.map { (sourceDomain, recipes) ->
			SourceBreakdown(
				sourceDomain = sourceDomain,
				total = recipes.size,
				visible = recipes.count { it.unknownIngredients.isEmpty() },
				missingOne = recipes.count { it.unknownIngredients.size == MISSING_ONE },
				missingTwo = recipes.count { it.unknownIngredients.size == MISSING_TWO },
				invisible = recipes.count { it.unknownIngredients.size >= MISSING_INVISIBLE_THRESHOLD },
			)
		}
		.sortedWith(
			compareByDescending<SourceBreakdown> { it.total }
				.thenByDescending { it.visible }
				.thenBy { it.sourceDomain },
		)

	val report = buildString {
		appendLine("Recipe visibility analysis")
		appendLine("Allowed app ingredients: ${allowedIngredients.size}")
		appendLine("Total recipes: $totalRecipes")
		appendLine("Visible (0 missing): $visible")
		appendLine("Almost visible (1 missing): $missingOne")
		appendLine("Nearly visible (2 missing): $missingTwo")
		appendLine("Invisible (3+ missing): $invisible")
		appendLine("Visible if allowing <=1 missing: $visibleWithOneMissingAllowed")
		appendLine("Visible if allowing <=2 missing: $visibleWithTwoMissingAllowed")
		appendLine()
		appendLine("Visible/invisible by source domain")
		appendLine(
			listOf("source_domain", "total", "visible", "missing_1", "missing_2", "invisible_3_plus")
				.joinToString(TSV_SEPARATOR),
		)
		sourceBreakdowns.forEach { row ->
			val line = listOf(
				row.sourceDomain,
				row.total.toString(),
				row.visible.toString(),
				row.missingOne.toString(),
				row.missingTwo.toString(),
				row.invisible.toString(),
			).joinToString(TSV_SEPARATOR)
			appendLine(
				line,
			)
		}
	}

	println(report)

	if (outputPath != null) {
		java.io.File(outputPath).writeText(report)
		println("Saved report to $outputPath")
	}
}

private fun collectRecipeCoverage(
	allowedIngredients: Set<String>,
): List<RecipeCoverage> {
	val rows = Db.create().dataSource.connection.use { conn ->
		conn.prepareStatement(
			"""
			SELECT r.id, r.title, r.source_url, i.ingredient
			FROM recipes r
			LEFT JOIN ingredient_groups ig ON ig.recipe_id = r.id
			LEFT JOIN ingredients i ON i.ingredient_group_id = ig.id
			ORDER BY r.id, ig.order_index, i.order_index
			""".trimIndent(),
		).use { ps ->
			ps.executeQuery().use { rs ->
				buildList {
					while (rs.next()) {
						add(
							VisibilityRecipeIngredientRow(
								recipeId = rs.getInt("id"),
								recipeTitle = rs.getString("title") ?: "",
								sourceUrl = rs.getString("source_url"),
								ingredient = rs.getString("ingredient"),
							),
						)
					}
				}
			}
		}
	}

	return rows
		.groupBy { Triple(it.recipeId, it.recipeTitle, normalizeSourceDomain(it.sourceUrl)) }
		.map { (key, recipeRows) ->
			val unknownIngredients = recipeRows.mapNotNull { row ->
				val normalizedIngredient = row.ingredient?.trim().orEmpty()
				if (normalizedIngredient.isEmpty()) {
					return@mapNotNull null
				}
				if (IngredientVocabulary.isIgnorableIngredientLine(normalizedIngredient)) {
					return@mapNotNull null
				}

				val matchesAnyAllowed = IngredientVocabulary.matchesAnyIngredient(
					ingredientLine = normalizedIngredient,
					ingredientNames = allowedIngredients,
				) || IngredientVocabulary.isPantryIngredient(normalizedIngredient)

				if (matchesAnyAllowed) {
					null
				} else {
					normalizedIngredient
				}
			}.toSet()

			RecipeCoverage(
				recipeId = key.first,
				recipeTitle = key.second,
				sourceDomain = key.third,
				unknownIngredients = unknownIngredients,
			)
		}
		.sortedBy { it.recipeId }
}

private fun normalizeSourceDomain(sourceUrl: String?): String {
	val raw = sourceUrl?.trim().orEmpty()
	if (raw.isEmpty()) {
		return "(none)"
	}

	val host = runCatching {
		val withScheme = if (raw.startsWith("http://") || raw.startsWith("https://")) {
			raw
		} else {
			"https://$raw"
		}
		URI(withScheme).host
	}.getOrNull()

	val normalizedHost = host
		?.lowercase()
		?.removePrefix("www.")
		?.takeIf { it.isNotBlank() }

	return normalizedHost ?: "(invalid-url)"
}

private fun loadAllowedIngredients(): Set<String> {
	val sourceFile = resolveIngredientSourceFile()

	if (!sourceFile.exists()) {
		return emptySet()
	}

	val quotedStringPattern = Regex(""""([^"]+)"""")

	return sourceFile.readLines()
		.asSequence()
		.filter { line ->
			line.contains("items = listOf(") || line.trimStart().firstOrNull() == '"'
		}
		.flatMap { line ->
			quotedStringPattern.findAll(line).map { match -> match.groupValues[1] }
		}
		.map(String::trim)
		.filter(String::isNotEmpty)
		.toSet()
}

private fun resolveIngredientSourceFile(): java.io.File {
	val explicitPath = System.getenv("PURECIPES_INGREDIENT_SOURCE_FILE")
	if (!explicitPath.isNullOrBlank()) {
		return java.io.File(explicitPath)
	}

	val currentDirectory = java.nio.file.Path.of(System.getProperty("user.dir"))
	val repoRoot = generateSequence(currentDirectory) { it.parent }
		.firstOrNull { candidate ->
			candidate.resolve("settings.gradle.kts").toFile().exists()
		}

	if (repoRoot != null) {
		return repoRoot.resolve(DEFAULT_INGREDIENTS_SOURCE).toFile()
	}

	return currentDirectory.resolve(DEFAULT_INGREDIENTS_SOURCE).toFile()
}
