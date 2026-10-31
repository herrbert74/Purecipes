#!/usr/bin/env kotlin

@file:DependsOn("org.postgresql:postgresql:42.7.14")

import java.math.BigDecimal
import java.math.RoundingMode
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

private val scrapedServingsRegex = Regex(
	pattern = """(\d+(?:\.\d+)?)\s*(?:servings?|serves|portions?|people|items?|pieces?|pancakes?|""" +
		"""biscuits?|cookies?|rolls?|muffins?|cupcakes?|buns?|bars?|loaves|loaf|batch|pie)""",
	option = RegexOption.IGNORE_CASE,
)

private val scrapedLeadingCountRegex = Regex(
	pattern = """^(\d+(?:\.\d+)?)\s*(?:x\s*)?(?:servings?|serves|items?|pieces?)""",
	option = RegexOption.IGNORE_CASE,
)

private val alreadyTotalRecipeIds = setOf(790, 827, 1252)

fun parseScrapedServingCount(yields: String?): Double? {
	val trimmed = yields?.trim().orEmpty()
	if (trimmed.isEmpty()) return null
	val match = scrapedServingsRegex.find(trimmed) ?: scrapedLeadingCountRegex.find(trimmed)
	val count = match?.groupValues?.getOrNull(1)?.toDoubleOrNull()
	return if (count == null || count <= 0.0) null else count
}

fun scaleNutrient(value: BigDecimal?, multiplier: BigDecimal): BigDecimal? {
	if (value == null) return null
	return value.multiply(multiplier).setScale(2, RoundingMode.HALF_UP)
}

fun parseArgs(args: Array<String>): BackfillConfig {
	val values = mutableMapOf<String, String>()
	var index = 0
	while (index < args.size) {
		val option = args[index++]
		if (!option.startsWith("--")) continue
		val next = args.getOrNull(index)
		if (next != null && !next.startsWith("--")) {
			values[option] = next
			index++
		} else {
			values[option] = "true"
		}
	}

	return BackfillConfig(
		dryRun = values["--dry-run"]?.toBooleanStrictOrNull() ?: false,
		verbose = values["--verbose"]?.toBooleanStrictOrNull() ?: false,
		dbDsn = values["--db-dsn"],
		dbHost = values["--db-host"] ?: "localhost",
		dbPort = values["--db-port"]?.toIntOrNull() ?: 5432,
		dbName = values["--db-name"] ?: "purecipes",
		dbUser = values["--db-user"] ?: "postgres",
		dbPassword = values["--db-password"] ?: System.getenv("PGPASSWORD") ?: "postgres",
	)
}

fun connect(config: BackfillConfig): Connection {
	val url = config.dbDsn ?: "jdbc:postgresql://${config.dbHost}:${config.dbPort}/${config.dbName}"
	return DriverManager.getConnection(url, config.dbUser, config.dbPassword)
}

val config = parseArgs(args)
val connection = connect(config)

val selectSql = """
	SELECT
		r.id,
		r.title,
		r.yields,
		n.calories,
		n.protein,
		n.carbohydrates,
		n.fat,
		n.fiber,
		n.sugar,
		n.sodium,
		n.serving_count
	FROM recipes r
	JOIN nutrition n ON r.id = n.recipe_id
	WHERE n.calculation_source = 'scraped'
	ORDER BY r.id ASC
""".trimIndent()

val updateSql = """
	UPDATE nutrition SET
		calories = ?,
		protein = ?,
		carbohydrates = ?,
		fat = ?,
		fiber = ?,
		sugar = ?,
		sodium = ?,
		serving_count = ?,
		updated_at = CURRENT_TIMESTAMP
	WHERE recipe_id = ?
""".trimIndent()

var updatedCount = 0
var unchangedCount = 0

connection.prepareStatement(selectSql).use { selectStmt ->
	selectStmt.executeQuery().use { rs ->
		connection.prepareStatement(updateSql).use { updateStmt ->
			while (rs.next()) {
				val recipeId = rs.getInt("id")
				val title = rs.getString("title")
				val yields = rs.getString("yields")
				val servingCount = parseScrapedServingCount(yields)
				val existingServingCount = rs.getBigDecimal("serving_count")

				if (existingServingCount != null && existingServingCount.toDouble() == servingCount) {
					unchangedCount++
					continue
				}

				val calories = rs.getBigDecimal("calories")
				val protein = rs.getBigDecimal("protein")
				val carbohydrates = rs.getBigDecimal("carbohydrates")
				val fat = rs.getBigDecimal("fat")
				val fiber = rs.getBigDecimal("fiber")
				val sugar = rs.getBigDecimal("sugar")
				val sodium = rs.getBigDecimal("sodium")

				val isAlreadyTotal = recipeId in alreadyTotalRecipeIds
				val multiplier = if (!isAlreadyTotal && servingCount != null && servingCount > 1.0) {
					BigDecimal.valueOf(servingCount)
				} else {
					BigDecimal.ONE
				}

				val newCalories = scaleNutrient(calories, multiplier)
				val newProtein = scaleNutrient(protein, multiplier)
				val newCarbohydrates = scaleNutrient(carbohydrates, multiplier)
				val newFat = scaleNutrient(fat, multiplier)
				val newFiber = scaleNutrient(fiber, multiplier)
				val newSugar = scaleNutrient(sugar, multiplier)
				val newSodium = scaleNutrient(sodium, multiplier)
				val newServingCount = servingCount?.let(BigDecimal::valueOf)

				if (config.verbose) {
					println("Recipe $recipeId ($title): yields='$yields'")
					println("  servings=$servingCount, multiplier=$multiplier, calories: $calories -> $newCalories")
				}

				if (!config.dryRun) {
					updateStmt.setBigDecimal(1, newCalories)
					updateStmt.setBigDecimal(2, newProtein)
					updateStmt.setBigDecimal(3, newCarbohydrates)
					updateStmt.setBigDecimal(4, newFat)
					updateStmt.setBigDecimal(5, newFiber)
					updateStmt.setBigDecimal(6, newSugar)
					updateStmt.setBigDecimal(7, newSodium)
					updateStmt.setBigDecimal(8, newServingCount)
					updateStmt.setInt(9, recipeId)
					updateStmt.addBatch()
				}
				updatedCount++
			}
			if (!config.dryRun && updatedCount > 0) {
				updateStmt.executeBatch()
			}
		}
	}
}

println("Backfill complete. updated=$updatedCount, unchanged=$unchangedCount, dryRun=${config.dryRun}")
connection.close()
