#!/usr/bin/env kotlin

@file:DependsOn("org.postgresql:postgresql:42.7.11")
@file:Import("../ScrapedIngredientLines.kt")

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
		kotlin scripts/scraping/oneoff/backfill_ingredient_requirements.main.kts [options]

	Options:
		--dry-run true|false        Default: false
		--verbose true|false        Default: false

		--db-dsn <jdbc-url>
		--db-host <host>            Default: localhost
		--db-port <port>            Default: 5432
		--db-name <name>            Default: purecipes
		--db-user <user>            Default: postgres
		--db-password <password>    Default: postgres

	Database connection settings are required.

	Re-parses ingredient text in PostgreSQL and updates:
	- ingredients.requirement
	- ingredients.ingredient (optional markers stripped from display text)
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

	val dbName = values["--db-name"]
	val dbDsn = values["--db-dsn"]
	if (dbDsn.isNullOrBlank() && dbName.isNullOrBlank()) {
		printUsageAndExit("Provide --db-dsn or --db-name")
	}

	return BackfillConfig(
		dryRun = values["--dry-run"]?.toBooleanStrictOrNull() ?: false,
		verbose = values["--verbose"]?.toBooleanStrictOrNull() ?: false,
		dbDsn = dbDsn,
		dbHost = values["--db-host"] ?: "localhost",
		dbPort = values["--db-port"]?.toIntOrNull() ?: 5432,
		dbName = dbName ?: "purecipes",
		dbUser = values["--db-user"] ?: "postgres",
		dbPassword = values["--db-password"] ?: "postgres",
	)
}

fun openConnection(config: BackfillConfig): Connection {
	val jdbcUrl = config.dbDsn ?: "jdbc:postgresql://${config.dbHost}:${config.dbPort}/${config.dbName}"
	return DriverManager.getConnection(jdbcUrl, config.dbUser, config.dbPassword)
}

fun ensureRequirementColumn(connection: Connection) {
	connection.createStatement().use { statement ->
		statement.execute(
			"""
			ALTER TABLE ingredients
			ADD COLUMN IF NOT EXISTS requirement VARCHAR(20) NOT NULL DEFAULT 'REQUIRED'
			""".trimIndent(),
		)
	}
}

data class IngredientRow(
	val id: Int,
	val text: String,
	val requirement: String,
)

fun loadIngredients(connection: Connection): List<IngredientRow> =
	connection.prepareStatement(
		"""
		SELECT id, ingredient, requirement
		FROM ingredients
		ORDER BY id
		""".trimIndent(),
	).use { statement ->
		statement.executeQuery().use { resultSet ->
			buildList {
				while (resultSet.next()) {
					add(
						IngredientRow(
							id = resultSet.getInt("id"),
							text = resultSet.getString("ingredient").orEmpty(),
							requirement = resultSet.getString("requirement") ?: "REQUIRED",
						),
					)
				}
			}
		}
	}

fun backfillIngredients(connection: Connection, config: BackfillConfig) {
	ensureRequirementColumn(connection)
	val rows = loadIngredients(connection)
	var updatedCount = 0

	connection.prepareStatement(
		"""
		UPDATE ingredients
		SET ingredient = ?, requirement = ?
		WHERE id = ?
		""".trimIndent(),
	).use { statement ->
		rows.forEach { row ->
			val parsed = parseIngredientRequirement(row.text)
			if (parsed.text == row.text && parsed.requirement == row.requirement) {
				return@forEach
			}
			updatedCount += 1
			if (config.verbose) {
				println(
					"ingredient ${row.id}: requirement ${row.requirement} -> ${parsed.requirement}; " +
						"text '${row.text}' -> '${parsed.text}'",
				)
			}
			if (!config.dryRun) {
				statement.setString(1, parsed.text)
				statement.setString(2, parsed.requirement)
				statement.setInt(3, row.id)
				statement.addBatch()
			}
		}
		if (!config.dryRun) {
			statement.executeBatch()
		}
	}

	println("Scanned ${rows.size} ingredients; updated $updatedCount")
	if (config.dryRun) {
		println("Dry run enabled; no database rows were modified")
	}
}

fun main(args: Array<String>) {
	val config = parseArgs(args)
	openConnection(config).use { connection ->
		backfillIngredients(connection, config)
	}
}

main(args)
