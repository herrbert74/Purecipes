#!/usr/bin/env kotlin

@file:DependsOn("org.postgresql:postgresql:42.7.14")

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

private val defaultDbPort = 5432

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

fun printUsageAndExit(message: String? = null): Nothing {
	if (!message.isNullOrBlank()) {
		System.err.println("Error: $message")
	}
	System.err.println(
		"""
	Usage:
		kotlin scripts/scraping/oneoff/backfill_recipe_measurement_system.main.kts [options]

	Options:
		--dry-run true|false        Default: false
		--verbose true|false        Default: false

		--db-dsn <jdbc-url>
		--db-host <host>            Default: localhost
		--db-port <port>            Default: 5432
		--db-name <name>            Default: purecipes
		--db-user <user>            Default: postgres
		--db-password <password>    Default: postgres

	Recomputes measurement_system for all existing recipes in PostgreSQL.
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
		dbPort = values["--db-port"]?.toIntOrNull() ?: defaultDbPort,
		dbName = values["--db-name"] ?: "purecipes",
		dbUser = values["--db-user"] ?: "postgres",
		dbPassword = values["--db-password"] ?: "postgres",
	)
}

fun openConnection(config: BackfillConfig): Connection {
	val jdbcUrl = config.dbDsn ?: "jdbc:postgresql://${config.dbHost}:${config.dbPort}/${config.dbName}"
	return DriverManager.getConnection(jdbcUrl, config.dbUser, config.dbPassword)
}

data class RecipeMeasurementRow(
	val id: Int,
	val title: String,
	val currentSystem: String?,
	val ingredientLines: List<String>,
)

fun loadRecipes(connection: Connection): List<RecipeMeasurementRow> {
	val recipes = mutableMapOf<Int, Pair<String, String?>>()
	val ingredientsByRecipe = mutableMapOf<Int, MutableList<String>>()

	connection.prepareStatement(
		"SELECT id, title, measurement_system FROM recipes ORDER BY id",
	).use { statement ->
		statement.executeQuery().use { rs ->
			while (rs.next()) {
				recipes[rs.getInt("id")] = rs.getString("title") to rs.getString("measurement_system")
			}
		}
	}

	connection.prepareStatement(
		"""
		SELECT ig.recipe_id, i.ingredient
		FROM ingredients i
		JOIN ingredient_groups ig ON ig.id = i.ingredient_group_id
		ORDER BY ig.recipe_id, ig.order_index, i.order_index
		""".trimIndent(),
	).use { statement ->
		statement.executeQuery().use { rs ->
			while (rs.next()) {
				val recipeId = rs.getInt("recipe_id")
				val line = rs.getString("ingredient").orEmpty()
				if (line.isNotBlank()) {
					ingredientsByRecipe.getOrPut(recipeId) { mutableListOf() }.add(line)
				}
			}
		}
	}

	return recipes.map { (id, titleAndSystem) ->
		RecipeMeasurementRow(
			id = id,
			title = titleAndSystem.first,
			currentSystem = titleAndSystem.second,
			ingredientLines = ingredientsByRecipe[id].orEmpty(),
		)
	}
}

fun detectMeasurementSystem(ingredientLines: List<String>): String? {
	var imperialHits = 0
	var metricHits = 0
	for (line in ingredientLines) {
		val normalized = line.lowercase()
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

fun backfillMeasurementSystems(connection: Connection, config: BackfillConfig) {
	val recipes = loadRecipes(connection)
	println("Loaded ${recipes.size} recipes from database.")

	val updateStmt = connection.prepareStatement(
		"UPDATE recipes SET measurement_system = ? WHERE id = ?",
	)

	val beforeCounts = mutableMapOf<String, Int>()
	val afterCounts = mutableMapOf<String, Int>()
	var updatedRows = 0

	for (recipe in recipes) {
		val beforeKey = recipe.currentSystem ?: "NULL"
		beforeCounts[beforeKey] = (beforeCounts[beforeKey] ?: 0) + 1

		val detected = detectMeasurementSystem(recipe.ingredientLines)
		val afterKey = detected ?: "NULL"
		afterCounts[afterKey] = (afterCounts[afterKey] ?: 0) + 1

		if (config.verbose && detected != recipe.currentSystem) {
			println("[${recipe.id}] ${recipe.title}: $beforeKey -> $afterKey")
		}

		if (!config.dryRun && detected != recipe.currentSystem) {
			updateMeasurementSystem(updateStmt, recipe.id, detected)
			updatedRows += 1
		}
	}

	updateStmt.close()
	printSummary(recipes.size, updatedRows, beforeCounts, afterCounts, config.dryRun)
}

private fun updateMeasurementSystem(
	updateStmt: PreparedStatement,
	recipeId: Int,
	detected: String?,
) {
	if (detected == null) {
		updateStmt.setNull(1, Types.VARCHAR)
	} else {
		updateStmt.setString(1, detected)
	}
	updateStmt.setInt(2, recipeId)
	updateStmt.executeUpdate()
}

private fun printSummary(
	totalRecipes: Int,
	updatedRows: Int,
	beforeCounts: Map<String, Int>,
	afterCounts: Map<String, Int>,
	dryRun: Boolean,
) {
	println("\n--- Measurement System Backfill Summary ---")
	println("Total recipes: $totalRecipes")
	println("Updated rows: $updatedRows")
	println("\nBreakdown (before -> after):")
	val keys = (beforeCounts.keys + afterCounts.keys).sorted()
	for (key in keys) {
		println("  $key: ${beforeCounts[key] ?: 0} -> ${afterCounts[key] ?: 0}")
	}
	if (dryRun) {
		println("\n(Dry run completed: no database rows were modified)")
	} else {
		println("\nDatabase updated successfully.")
	}
}

val config = parseArgs(args)
openConnection(config).use { connection ->
	backfillMeasurementSystems(connection, config)
}
