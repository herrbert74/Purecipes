#!/usr/bin/env kotlin

@file:DependsOn("org.postgresql:postgresql:42.7.14")
@file:Import("../ScrapedDietaryPreferences.kt")

import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.Types

data class BackfillConfig(
	val dryRun: Boolean,
	val verbose: Boolean,
	val dbDsn: String?,
	val dbHost: String,
	val dbPort: Int,
	val dbName: String?,
	val dbUser: String?,
	val dbPassword: String?,
)

private val allowedOptions = setOf(
	"--dry-run",
	"--verbose",
	"--db-dsn",
	"--db-host",
	"--db-port",
	"--db-name",
	"--db-user",
	"--db-password",
)

fun printUsageAndExit(message: String? = null): Nothing {
	if (!message.isNullOrBlank()) {
		System.err.println("Error: $message")
	}
	System.err.println(
		"""
	Usage:
		kotlin scripts/scraping/oneoff/backfill_recipe_dietary_preferences.main.kts [options]

	Options:
		--dry-run true|false        Default: false
		--verbose true|false        Default: false

		--db-dsn <jdbc-url>
		--db-host <host>            Default: localhost
		--db-port <port>            Default: 5432
		--db-name <name>            Default: purecipes
		--db-user <user>            Default: postgres
		--db-password <password>    Default: postgres

	Extracts and backfills dietary_preferences for all existing recipes in PostgreSQL.
		""".trimIndent(),
	)
	kotlin.system.exitProcess(1)
}

fun parseArgs(args: Array<String>): BackfillConfig {
	val values = mutableMapOf<String, String>()
	var index = 0
	while (index < args.size) {
		val option = args[index]
		if (option !in allowedOptions) {
			printUsageAndExit("Unknown option: $option")
		}
		val value = args.getOrNull(index + 1)
		if (value.isNullOrBlank() || value.startsWith("--")) {
			printUsageAndExit("Missing value for $option")
		}
		values[option] = value
		index += 2
	}

	return BackfillConfig(
		dryRun = values["--dry-run"]?.toBooleanStrictOrNull() ?: false,
		verbose = values["--verbose"]?.toBooleanStrictOrNull() ?: false,
		dbDsn = values["--db-dsn"],
		dbHost = values["--db-host"] ?: "localhost",
		dbPort = values["--db-port"]?.toIntOrNull() ?: 5432,
		dbName = values["--db-name"] ?: "purecipes",
		dbUser = values["--db-user"] ?: "postgres",
		dbPassword = values["--db-password"] ?: "postgres",
	)
}

fun openConnection(config: BackfillConfig): Connection {
	val jdbcUrl = config.dbDsn ?: "jdbc:postgresql://${config.dbHost}:${config.dbPort}/${config.dbName}"
	return DriverManager.getConnection(jdbcUrl, config.dbUser, config.dbPassword)
}

data class RecipeRow(
	val id: Int,
	val title: String,
	val existingDietaryPreferences: List<String>,
	val tags: List<String>,
	val ingredients: List<String>,
)

fun loadRecipes(connection: Connection): List<RecipeRow> =
	connection.prepareStatement(
		"""
		SELECT r.id,
		       r.title,
		       r.dietary_preferences,
		       r.tags,
		       COALESCE(
		           array_agg(i.ingredient ORDER BY ig.order_index, i.order_index)
		               FILTER (WHERE i.ingredient IS NOT NULL),
		           ARRAY[]::text[]
		       ) AS ingredients
		FROM recipes r
		LEFT JOIN ingredient_groups ig ON ig.recipe_id = r.id
		LEFT JOIN ingredients i ON i.ingredient_group_id = ig.id
		GROUP BY r.id, r.title, r.dietary_preferences, r.tags
		ORDER BY r.id
		""".trimIndent(),
	).use { statement ->
		statement.executeQuery().use { resultSet ->
			buildList {
				while (resultSet.next()) {
					val existingPrefs = (resultSet.getArray("dietary_preferences")?.array as? Array<*>)
						?.filterIsInstance<String>()
						.orEmpty()
					val tags = (resultSet.getArray("tags")?.array as? Array<*>)
						?.filterIsInstance<String>()
						.orEmpty()
					val ingredients = (resultSet.getArray("ingredients")?.array as? Array<*>)
						?.filterIsInstance<String>()
						.orEmpty()
					add(
						RecipeRow(
							id = resultSet.getInt("id"),
							title = resultSet.getString("title").orEmpty(),
							existingDietaryPreferences = existingPrefs,
							tags = tags,
							ingredients = ingredients,
						),
					)
				}
			}
		}
	}

fun backfillDietaryPreferences(connection: Connection, config: BackfillConfig) {
	val recipes = loadRecipes(connection)
	println("Loaded ${recipes.size} recipes from database.")

	val updateStmt = connection.prepareStatement(
		"UPDATE recipes SET dietary_preferences = ? WHERE id = ?",
	)

	val beforeCounts = mutableMapOf<String, Int>()
	val afterCounts = mutableMapOf<String, Int>()
	var recipesWithPrefsBefore = 0
	var recipesWithPrefsAfter = 0
	var updatedRows = 0

	for (recipe in recipes) {
		if (recipe.existingDietaryPreferences.isNotEmpty()) {
			recipesWithPrefsBefore++
			recordPreferenceCounts(beforeCounts, recipe.existingDietaryPreferences)
		}

		val extracted = extractScrapedDietaryPreferences(
			title = recipe.title,
			ingredients = recipe.ingredients,
			explicitLabels = recipe.existingDietaryPreferences + recipe.tags,
		)

		if (extracted.isNotEmpty()) {
			recipesWithPrefsAfter++
			recordPreferenceCounts(afterCounts, extracted)
		}

		if (config.verbose && extracted != recipe.existingDietaryPreferences) {
			println("[${recipe.id}] ${recipe.title}: ${recipe.existingDietaryPreferences} -> $extracted")
		}

		if (!config.dryRun && extracted != recipe.existingDietaryPreferences) {
			updateRecipeDietaryPreferences(updateStmt, recipe.id, extracted)
			updatedRows++
		}
	}

	updateStmt.close()

	printBackfillSummary(
		totalRecipes = recipes.size,
		recipesWithPrefsBefore = recipesWithPrefsBefore,
		recipesWithPrefsAfter = recipesWithPrefsAfter,
		updatedRows = updatedRows,
		beforeCounts = beforeCounts,
		afterCounts = afterCounts,
		dryRun = config.dryRun,
	)
}

private fun recordPreferenceCounts(counts: MutableMap<String, Int>, preferences: Set<String>) {
	for (pref in preferences) {
		counts[pref] = (counts[pref] ?: 0) + 1
	}
}

private fun updateRecipeDietaryPreferences(
	updateStmt: PreparedStatement,
	recipeId: Int,
	extracted: Set<String>,
) {
	if (extracted.isEmpty()) {
		updateStmt.setNull(1, Types.ARRAY)
	} else {
		updateStmt.setArray(1, updateStmt.connection.createArrayOf("text", extracted.toTypedArray()))
	}
	updateStmt.setInt(2, recipeId)
	updateStmt.executeUpdate()
}

private fun printBackfillSummary(
	totalRecipes: Int,
	recipesWithPrefsBefore: Int,
	recipesWithPrefsAfter: Int,
	updatedRows: Int,
	beforeCounts: Map<String, Int>,
	afterCounts: Map<String, Int>,
	dryRun: Boolean,
) {
	println("\n--- Backfill Summary ---")
	println("Total recipes: $totalRecipes")
	println("Recipes with dietary preferences (before): $recipesWithPrefsBefore")
	println("Recipes with dietary preferences (after): $recipesWithPrefsAfter")
	println("Updated rows: $updatedRows")
	println("\nBreakdown by dietary preference (before -> after):")
	val allPrefs = (beforeCounts.keys + afterCounts.keys).sorted()
	for (pref in allPrefs) {
		println("  $pref: ${beforeCounts[pref] ?: 0} -> ${afterCounts[pref] ?: 0}")
	}
	if (dryRun) {
		println("\n(Dry run completed: no database rows were modified)")
	} else {
		println("\nDatabase updated successfully.")
	}
}

val config = parseArgs(args)
openConnection(config).use { connection ->
	backfillDietaryPreferences(connection, config)
}
