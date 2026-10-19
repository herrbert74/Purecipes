#!/usr/bin/env kotlin

@file:DependsOn("org.postgresql:postgresql:42.7.14")
@file:Import("../ScrapedIngredientLines.kt")

// Cache-bust when ScrapedIngredientLines.kt changes (invalidates kotlin-main-kts jars).
// backfill-merge-incomplete-v4

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
		kotlin scripts/scraping/oneoff/backfill_merge_incomplete_alternatives.main.kts [options]

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

	Finds ALTERNATIVE ingredient clusters that were over-split on " or " (for example
	"2 tablespoons mini" + "2 tablespoons regular chocolate chips"), joins them,
	re-runs current ScrapedIngredientLines rules, and replaces those rows in place.
	Recipe ids are preserved. Recalculate nutrition afterward for affected recipes.
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

data class IngredientRow(
	val id: Int,
	val recipeId: Int,
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
		SELECT
			i.id,
			ig.recipe_id,
			i.ingredient_group_id,
			i.order_index,
			i.ingredient,
			i.requirement,
			i.alternative_group_key
		FROM ingredients i
		JOIN ingredient_groups ig ON ig.id = i.ingredient_group_id
		ORDER BY i.ingredient_group_id ASC, i.order_index ASC, i.id ASC
		""".trimIndent(),
	).use { statement ->
		statement.executeQuery().use { resultSet ->
			buildList {
				while (resultSet.next()) {
					add(
						IngredientRow(
							id = resultSet.getInt("id"),
							recipeId = resultSet.getInt("recipe_id"),
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

fun mergedReplacementIfCollapsed(joinedText: String): ReplacementIngredient? {
	val sanitized = sanitizeIngredientLine(joinedText) ?: return null
	val expanded = expandProcessedAlternatives(sanitized)
	val alternativeCount = expanded.count { item -> item.requirement == "ALTERNATIVE" }
	return if (alternativeCount <= 1 && expanded.size == 1) {
		ReplacementIngredient(
			text = expanded.first().text,
			requirement = expanded.first().requirement,
			alternativeGroupKey = null,
		)
	} else {
		null
	}
}

fun toReplacement(row: IngredientRow): ReplacementIngredient =
	ReplacementIngredient(
		text = row.text,
		requirement = row.requirement,
		alternativeGroupKey = row.alternativeGroupKey,
	)

fun alternativeClusterAt(rows: List<IngredientRow>, index: Int): List<IngredientRow> {
	val row = rows[index]
	val groupKey = row.alternativeGroupKey
	val isAltClusterStart = row.requirement == "ALTERNATIVE" &&
		groupKey != null &&
		(index == 0 || rows[index - 1].alternativeGroupKey != groupKey)
	return if (!isAltClusterStart) {
		emptyList()
	} else {
		rows.drop(index).takeWhile { candidate ->
			candidate.requirement == "ALTERNATIVE" && candidate.alternativeGroupKey == groupKey
		}
	}
}

fun replacementsForGroup(
	rows: List<IngredientRow>,
): Pair<List<ReplacementIngredient>, Boolean> {
	val replacements = mutableListOf<ReplacementIngredient>()
	var index = 0
	var changed = false
	while (index < rows.size) {
		val cluster = alternativeClusterAt(rows, index)
		if (cluster.size < 2) {
			replacements += toReplacement(rows[index])
			index += 1
		} else {
			val joinedText = cluster.joinToString(separator = " or ") { member -> member.text }
			val merged = mergedReplacementIfCollapsed(joinedText)
			if (merged == null) {
				replacements += cluster.map(::toReplacement)
			} else {
				changed = true
				replacements += merged
			}
			index += cluster.size
		}
	}
	return replacements to changed
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

fun backfillMergeIncompleteAlternatives(connection: Connection, config: BackfillConfig) {
	val rowsByGroup = loadIngredientsByGroup(connection)
	var rebuiltGroupCount = 0
	var removedRowCount = 0
	var insertedRowCount = 0
	val affectedRecipeIds = linkedSetOf<Int>()

	connection.autoCommit = false
	try {
		rowsByGroup.forEach { (groupId, rows) ->
			val (replacements, changed) = replacementsForGroup(rows)
			if (!changed) {
				return@forEach
			}

			rebuiltGroupCount += 1
			removedRowCount += rows.size
			insertedRowCount += replacements.size
			affectedRecipeIds += rows.first().recipeId
			if (config.verbose) {
				println("group $groupId (recipe ${rows.first().recipeId}): ${rows.size} -> ${replacements.size}")
				rows.filter { row -> row.alternativeGroupKey != null }.forEach { row ->
					println("  - [${row.alternativeGroupKey}] ${row.text}")
				}
				replacements.forEach { replacement ->
					println("  + [${replacement.alternativeGroupKey}] ${replacement.text}")
				}
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

	println(
		"Rebuilt $rebuiltGroupCount ingredient groups " +
			"($removedRowCount rows removed, $insertedRowCount rows inserted)",
	)
	if (affectedRecipeIds.isNotEmpty()) {
		println("Affected recipe ids (${affectedRecipeIds.size}): ${affectedRecipeIds.joinToString(",")}")
		println(
			"Recalculate nutrition, for example: " +
				"./gradlew calculateRecipeNutrition -Pnutrition.recipeIds=${affectedRecipeIds.joinToString(",")}",
		)
	}
	if (config.dryRun) {
		println("Dry run enabled; no database rows were modified")
	}
}

fun main(args: Array<String>) {
	val config = parseArgs(args)
	openConnection(config).use { connection ->
		backfillMergeIncompleteAlternatives(connection, config)
	}
}

main(args)
