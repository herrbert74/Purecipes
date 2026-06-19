#!/usr/bin/env kotlin

@file:DependsOn("org.postgresql:postgresql:42.7.11")
@file:Import("../ScrapedIngredientLines.kt")

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

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
		kotlin scripts/scraping/oneoff/backfill_ingredient_alternatives.main.kts [options]

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

	Splits existing ingredient rows that contain " or " alternatives into structured
	ALTERNATIVE rows with alternative_group_key.
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

fun ensureAlternativeGroupKeyColumn(connection: Connection) {
	connection.createStatement().use { statement ->
		statement.execute(
			"""
			ALTER TABLE ingredients
			ADD COLUMN IF NOT EXISTS alternative_group_key INTEGER
			""".trimIndent(),
		)
	}
}

data class IngredientRow(
	val id: Int,
	val groupId: Int,
	val orderIndex: Int,
	val text: String,
	val requirement: String,
	val alternativeGroupKey: Int?,
)

data class ReplacementIngredient(
	val text: String,
	val requirement: String,
	val alternativeGroupKey: Int?,
)

fun loadIngredientsByGroup(connection: Connection): Map<Int, List<IngredientRow>> =
	connection.prepareStatement(
		"""
		SELECT id, ingredient_group_id, order_index, ingredient, requirement, alternative_group_key
		FROM ingredients
		ORDER BY ingredient_group_id ASC, order_index ASC, id ASC
		""".trimIndent(),
	).use { statement ->
		statement.executeQuery().use { resultSet ->
			buildList {
				while (resultSet.next()) {
					add(
						IngredientRow(
							id = resultSet.getInt("id"),
							groupId = resultSet.getInt("ingredient_group_id"),
							orderIndex = resultSet.getInt("order_index"),
							text = resultSet.getString("ingredient").orEmpty(),
							requirement = resultSet.getString("requirement") ?: "REQUIRED",
							alternativeGroupKey = resultSet.getObject("alternative_group_key") as? Int,
						),
					)
				}
			}
		}
	}.groupBy { row -> row.groupId }

fun replacementIngredientsForRow(
	row: IngredientRow,
	nextAlternativeGroupKey: () -> Int,
): List<ReplacementIngredient> {
	val preserved = ReplacementIngredient(
		text = row.text,
		requirement = row.requirement,
		alternativeGroupKey = row.alternativeGroupKey,
	)
	val sanitized = if (row.requirement == "ALTERNATIVE" && row.alternativeGroupKey != null) {
		null
	} else {
		sanitizeIngredientLine(row.text)
	}
	return when {
		row.requirement == "ALTERNATIVE" && row.alternativeGroupKey != null -> listOf(preserved)
		sanitized == null -> listOf(preserved)
		else -> {
			val expanded = expandProcessedAlternatives(sanitized)
			val alternativeCount = expanded.count { item -> item.requirement == "ALTERNATIVE" }
			if (alternativeCount <= 1) {
				listOf(
					ReplacementIngredient(
						text = sanitized.text,
						requirement = sanitized.requirement,
						alternativeGroupKey = row.alternativeGroupKey,
					),
				)
			} else {
				val groupKey = nextAlternativeGroupKey()
				expanded.map { item ->
					ReplacementIngredient(
						text = item.text,
						requirement = item.requirement,
						alternativeGroupKey = if (item.requirement == "ALTERNATIVE") groupKey else null,
					)
				}
			}
		}
	}
}

fun rebuildIngredientGroup(
	connection: Connection,
	groupId: Int,
	replacements: List<ReplacementIngredient>,
) {
	connection.prepareStatement(
		"DELETE FROM ingredients WHERE ingredient_group_id = ?",
	).use { statement ->
		statement.setInt(1, groupId)
		statement.executeUpdate()
	}
	connection.prepareStatement(
		"""
		INSERT INTO ingredients (
			ingredient_group_id,
			ingredient,
			order_index,
			requirement,
			alternative_group_key
		) VALUES (?, ?, ?, ?, ?)
		""".trimIndent(),
	).use { statement ->
		replacements.forEachIndexed { replacementIndex, replacement ->
			statement.setInt(1, groupId)
			statement.setString(2, replacement.text)
			statement.setInt(3, replacementIndex)
			statement.setString(4, replacement.requirement)
			if (replacement.alternativeGroupKey == null) {
				statement.setNull(5, java.sql.Types.INTEGER)
			} else {
				statement.setInt(5, replacement.alternativeGroupKey)
			}
			statement.addBatch()
		}
		statement.executeBatch()
	}
}

fun backfillAlternatives(connection: Connection, config: BackfillConfig) {
	ensureAlternativeGroupKeyColumn(connection)
	val rowsByGroup = loadIngredientsByGroup(connection)
	var nextAlternativeGroupKey = 1
	var rebuiltGroupCount = 0
	var insertedRowCount = 0

	connection.autoCommit = false
	try {
		rowsByGroup.forEach { (groupId, rows) ->
			val replacements = rows.flatMap { row ->
				replacementIngredientsForRow(row) { nextAlternativeGroupKey++ }
			}
			val unchanged = replacements.size == rows.size &&
				replacements.zip(rows).all { (replacement, row) ->
					replacement.text == row.text &&
						replacement.requirement == row.requirement &&
						replacement.alternativeGroupKey == row.alternativeGroupKey
				}
			if (unchanged) {
				return@forEach
			}

			rebuiltGroupCount += 1
			insertedRowCount += replacements.size
			if (config.verbose) {
				println("group $groupId: ${rows.size} rows -> ${replacements.size} rows")
			}
			if (!config.dryRun) {
				rebuildIngredientGroup(connection, groupId, replacements)
			}
		}
		if (!config.dryRun) {
			connection.commit()
		} else {
			connection.rollback()
		}
	} catch (error: SQLException) {
		connection.rollback()
		throw error
	} finally {
		connection.autoCommit = true
	}

	println("Rebuilt $rebuiltGroupCount ingredient groups with $insertedRowCount rows")
	if (config.dryRun) {
		println("Dry run enabled; no database rows were modified")
	}
}

fun main(args: Array<String>) {
	val config = parseArgs(args)
	openConnection(config).use { connection ->
		backfillAlternatives(connection, config)
	}
}

main(args)
