#!/usr/bin/env kotlin

@file:DependsOn("org.postgresql:postgresql:42.7.14")
@file:Import("../ScrapedAppliances.kt")

import java.sql.Connection
import java.sql.DriverManager

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
		kotlin scripts/scraping/oneoff/backfill_recipe_appliances.main.kts [options]

	Options:
		--dry-run true|false        Default: false
		--verbose true|false        Default: false

		--db-dsn <jdbc-url>
		--db-host <host>            Default: localhost
		--db-port <port>            Default: 5432
		--db-name <name>            Default: purecipes
		--db-user <user>            Default: postgres
		--db-password <password>    Default: postgres

	Extracts and backfills appliances for all existing recipes in PostgreSQL.
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

	val dbName = values["--db-name"] ?: "purecipes"
	val dbDsn = values["--db-dsn"]

	return BackfillConfig(
		dryRun = values["--dry-run"]?.toBooleanStrictOrNull() ?: false,
		verbose = values["--verbose"]?.toBooleanStrictOrNull() ?: false,
		dbDsn = dbDsn,
		dbHost = values["--db-host"] ?: "localhost",
		dbPort = values["--db-port"]?.toIntOrNull() ?: 5432,
		dbName = dbName,
		dbUser = values["--db-user"] ?: "postgres",
		dbPassword = values["--db-password"] ?: "postgres",
	)
}

fun openConnection(config: BackfillConfig): Connection {
	val jdbcUrl = config.dbDsn ?: "jdbc:postgresql://${config.dbHost}:${config.dbPort}/${config.dbName}"
	return DriverManager.getConnection(jdbcUrl, config.dbUser, config.dbPassword)
}

fun ensureApplianceColumn(connection: Connection) {
	connection.createStatement().use { statement ->
		statement.execute(
			"""
			ALTER TABLE recipes
			ADD COLUMN IF NOT EXISTS appliances TEXT[]
			""".trimIndent(),
		)
	}
}

data class RecipeRow(
	val id: Int,
	val title: String,
	val description: String?,
	val instructions: String?,
	val cookingMethod: String?,
	val stepsText: String?,
)

fun loadRecipes(connection: Connection): List<RecipeRow> =
	connection.prepareStatement(
		"""
		SELECT r.id, r.title, r.description, r.instructions, r.cooking_method,
		       COALESCE(string_agg(s.step, E'\n' ORDER BY s.order_index), '') AS steps_text
		FROM recipes r
		LEFT JOIN instruction_steps s ON s.recipe_id = r.id
		GROUP BY r.id, r.title, r.description, r.instructions, r.cooking_method
		ORDER BY r.id
		""".trimIndent(),
	).use { statement ->
		statement.executeQuery().use { resultSet ->
			buildList {
				while (resultSet.next()) {
					add(
						RecipeRow(
							id = resultSet.getInt("id"),
							title = resultSet.getString("title").orEmpty(),
							description = resultSet.getString("description"),
							instructions = resultSet.getString("instructions"),
							cookingMethod = resultSet.getString("cooking_method"),
							stepsText = resultSet.getString("steps_text"),
						),
					)
				}
			}
		}
	}

fun backfillAppliances(connection: Connection, config: BackfillConfig) {
	ensureApplianceColumn(connection)
	val recipes = loadRecipes(connection)
	println("Loaded ${recipes.size} recipes from database.")

	val updateStmt = connection.prepareStatement(
		"UPDATE recipes SET appliances = ? WHERE id = ?",
	)

	val applianceCounts = mutableMapOf<String, Int>()
	var recipesWithAppliancesCount = 0

	for (recipe in recipes) {
		val fullInstructions = buildString {
			if (!recipe.instructions.isNullOrBlank()) {
				append(recipe.instructions).append('\n')
			}
			if (!recipe.stepsText.isNullOrBlank()) {
				append(recipe.stepsText).append('\n')
			}
		}.trim()

		val appliances = extractScrapedAppliances(
			instructions = fullInstructions,
			title = recipe.title,
			description = recipe.description,
			cookingMethod = recipe.cookingMethod,
		)

		if (appliances.isNotEmpty()) {
			recipesWithAppliancesCount++
			for (appliance in appliances) {
				applianceCounts[appliance] = (applianceCounts[appliance] ?: 0) + 1
			}
		}

		if (config.verbose) {
			println("[${recipe.id}] ${recipe.title} -> ${appliances.joinToString()}")
		}

		if (!config.dryRun) {
			if (appliances.isEmpty()) {
				updateStmt.setNull(1, java.sql.Types.ARRAY)
			} else {
				val array = connection.createArrayOf("text", appliances.toTypedArray())
				updateStmt.setArray(1, array)
			}
			updateStmt.setInt(2, recipe.id)
			updateStmt.executeUpdate()
		}
	}

	updateStmt.close()

	println("\n--- Backfill Summary ---")
	println("Total recipes: ${recipes.size}")
	println("Recipes with appliances: $recipesWithAppliancesCount")
	println("Recipes without appliances: ${recipes.size - recipesWithAppliancesCount}")
	println("Breakdown by appliance:")
	applianceCounts.entries.sortedByDescending { it.value }.forEach { (appliance, count) ->
		println("  $appliance: $count")
	}
	if (config.dryRun) {
		println("\n(Dry run completed: no database rows were modified)")
	} else {
		println("\nDatabase updated successfully.")
	}
}

val config = parseArgs(args)
openConnection(config).use { connection ->
	backfillAppliances(connection, config)
}
