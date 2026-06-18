#!/usr/bin/env kotlin

@file:DependsOn("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")
@file:DependsOn("org.postgresql:postgresql:42.7.11")
@file:Import("../ScrapedIngredientLines.kt")

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import java.io.IOException
import java.sql.Connection
import java.sql.DriverManager

data class NormalizeConfig(
	val jsonDirs: List<File>,
	val dryRun: Boolean,
	val verbose: Boolean,
	val dbDsn: String?,
	val dbHost: String,
	val dbPort: Int,
	val dbName: String?,
	val dbUser: String?,
	val dbPassword: String?,
)

private val expectedScrapedIngredientRulesChecksum = "generic-units-v5"

private val gluedQuantitySampleRegex = Regex("""\d+(?:\.\d+)?[A-Za-z]""")

private val allowedOptions = setOf(
	"--json-dir",
	"--dry-run",
	"--verbose",
	"--db-dsn",
	"--db-host",
	"--db-port",
	"--db-name",
	"--db-user",
	"--db-password",
)

val json = Json {
	prettyPrint = true
	ignoreUnknownKeys = true
}

fun printUsageAndExit(message: String? = null): Nothing {
	if (!message.isNullOrBlank()) {
		System.err.println("Error: $message")
	}
	System.err.println(
		"""
	Usage:
		kotlin scripts/scraping/oneoff/normalize_existing_ingredients.main.kts [options]

	Options:
		--json-dir <path>           Recipe JSON root (repeatable). Updates all *.json files recursively.
		--dry-run true|false        Default: false
		--verbose true|false        Default: false. Print samples when nothing changes.

		--db-dsn <jdbc-url>
		--db-host <host>            Default: localhost
		--db-port <port>            Default: 5432
		--db-name <name>            Default: purecipes
		--db-user <user>            Default: postgres
		--db-password <password>    Default: postgres

	Provide at least one of --json-dir or database connection settings (--db-dsn or --db-name).

	Updates scraped recipe ingredient text in place:
	- inserts spaces between quantities and units (60g -> 60 g)
	- restores common cooking fractions (0.666666668653488 cup -> 2/3 cup)

	Does not recalculate nutrition or run enrichment.
	""".trimIndent()
	)
	kotlin.system.exitProcess(1)
}

private fun String.toIntOrExit(message: String): Int =
	this.toIntOrNull() ?: printUsageAndExit(message)

fun expandPath(path: String): String {
	return if (path.startsWith("~/")) {
		File(System.getProperty("user.home"), path.removePrefix("~/")).path
	} else {
		path
	}
}

fun parseOptions(scriptArgs: Array<String>): NormalizeConfig {
	val options = parseFlagArguments(scriptArgs)
	val jsonDirs = options["--json-dir"].orEmpty()
		.flatMap { value -> value.split(',') }
		.map { it.trim() }
		.filter { it.isNotBlank() }
		.map { path -> File(expandPath(path)) }
	val dryRun = (options["--dry-run"]?.firstOrNull() ?: "false").equals("true", ignoreCase = true)
	val verbose = (options["--verbose"]?.firstOrNull() ?: "false").equals("true", ignoreCase = true)
	val hasDatabase = scriptArgs.any { argument ->
		argument in setOf(
			"--db-dsn",
			"--db-name",
			"--db-host",
			"--db-port",
			"--db-user",
			"--db-password",
		)
	}
	val dbPort = options["--db-port"]?.firstOrNull()?.toIntOrExit("--db-port must be an integer") ?: 5432

	if (jsonDirs.isEmpty() && !hasDatabase) {
		printUsageAndExit("Provide at least one --json-dir or database connection settings")
	}

	return NormalizeConfig(
		jsonDirs = jsonDirs,
		dryRun = dryRun,
		verbose = verbose,
		dbDsn = if (hasDatabase) options["--db-dsn"]?.firstOrNull() else null,
		dbHost = options["--db-host"]?.firstOrNull() ?: "localhost",
		dbPort = dbPort,
		dbName = if (hasDatabase) options["--db-name"]?.firstOrNull() ?: "purecipes" else null,
		dbUser = options["--db-user"]?.firstOrNull() ?: "postgres",
		dbPassword = options["--db-password"]?.firstOrNull() ?: "postgres",
	)
}

private fun parseFlagArguments(args: Array<String>): Map<String, List<String>> {
	val options = mutableMapOf<String, MutableList<String>>()
	var index = 0
	while (index < args.size) {
		val key = args[index]
		if (!key.startsWith("--")) {
			printUsageAndExit("Unexpected argument: $key")
		}
		val next = args.getOrNull(index + 1)
		if (next == null || next.startsWith("--")) {
			printUsageAndExit("Missing value for option $key")
		}
		if (!allowedOptions.contains(key)) {
			printUsageAndExit("Unknown option: $key")
		}
		options.getOrPut(key) { mutableListOf() }.add(next)
		index += 2
	}
	return options
}

fun buildJdbcUrl(config: NormalizeConfig): String? {
	if (!config.dbDsn.isNullOrBlank()) {
		return config.dbDsn
	}
	if (config.dbName.isNullOrBlank()) {
		return null
	}
	return "jdbc:postgresql://${config.dbHost}:${config.dbPort}/${config.dbName}"
}

fun openConnection(config: NormalizeConfig): Connection {
	val jdbcUrl = buildJdbcUrl(config) ?: printUsageAndExit("Database connection settings are incomplete")
	return if (config.dbDsn.isNullOrBlank()) {
		val user = config.dbUser ?: printUsageAndExit("Provide --db-user for postgres connection")
		val props = java.util.Properties().apply {
			setProperty("user", user)
			if (!config.dbPassword.isNullOrBlank()) {
				setProperty("password", config.dbPassword)
			}
		}
		DriverManager.getConnection(jdbcUrl, props)
	} else {
		DriverManager.getConnection(jdbcUrl)
	}
}

data class IngredientUpdate(
	val ingredientId: Int,
	val recipeId: Int,
	val before: String,
	val after: String,
)

private val dryRunPreviewLimit = 10

private fun toIngredientUpdate(
	ingredientId: Int,
	recipeId: Int,
	before: String,
): IngredientUpdate? {
	val after = normalizeIngredientText(before)
	return if (before == after) {
		null
	} else {
		IngredientUpdate(
			ingredientId = ingredientId,
			recipeId = recipeId,
			before = before,
			after = after,
		)
	}
}

fun verifyScrapedIngredientRules() {
	require(SCRAPED_INGREDIENT_RULES_CHECKSUM == expectedScrapedIngredientRulesChecksum) {
		"""
		Stale scraped-ingredient rules loaded ($SCRAPED_INGREDIENT_RULES_CHECKSUM != $expectedScrapedIngredientRulesChecksum).
		Kotlin script caching can keep an old copy of ScrapedIngredientLines.kt.
		Run: scripts/scraping/oneoff/normalize_existing_ingredients.sh (clears cache first)
		Or delete: ~/Library/Caches/main.kts.compiled.cache/
		""".trimIndent()
	}
	val samples = listOf(
		"60g butter" to "60 g butter",
		"12Rasher bacon" to "12 Rasher bacon",
		"3Sprig Curry Leaf" to "3 Sprig Curry Leaf",
	)
	samples.forEach { (input, expected) ->
		val actual = normalizeIngredientText(input)
		require(actual == expected) {
			"""
			Normalization self-test failed for '$input' (got '$actual', expected '$expected').
			Kotlin script caching can keep an old copy of ScrapedIngredientLines.kt.
			Run: scripts/scraping/oneoff/normalize_existing_ingredients.sh (clears cache first)
			Or delete: ~/Library/Caches/main.kts.compiled.cache/
			""".trimIndent()
		}
	}
}

fun countScrapedIngredients(connection: Connection): Int {
	val sql = """
		SELECT COUNT(*)
		FROM ingredients i
		JOIN ingredient_groups ig ON ig.id = i.ingredient_group_id
		JOIN recipes r ON r.id = ig.recipe_id
		WHERE r.source_url IS NOT NULL
			AND TRIM(r.source_url) <> ''
			AND i.ingredient IS NOT NULL
	""".trimIndent()
	connection.prepareStatement(sql).use { statement ->
		statement.executeQuery().use { rows ->
			rows.next()
			return rows.getInt(1)
		}
	}
}

private fun isGluedQuantityIngredient(ingredient: String): Boolean =
	gluedQuantitySampleRegex.containsMatchIn(ingredient)

private fun collectGluedQuantitySamples(rows: java.sql.ResultSet, limit: Int): List<String> {
	val samples = mutableListOf<String>()
	while (rows.next() && samples.size < limit) {
		val ingredient = rows.getString(1).orEmpty()
		if (isGluedQuantityIngredient(ingredient)) {
			samples += ingredient
		}
	}
	return samples
}

fun loadGluedQuantitySamples(connection: Connection, limit: Int): List<String> {
	val sql = """
		SELECT i.ingredient
		FROM ingredients i
		JOIN ingredient_groups ig ON ig.id = i.ingredient_group_id
		JOIN recipes r ON r.id = ig.recipe_id
		WHERE r.source_url IS NOT NULL
			AND TRIM(r.source_url) <> ''
			AND i.ingredient IS NOT NULL
	""".trimIndent()
	val statement = connection.prepareStatement(sql)
	statement.use { preparedStatement ->
		val resultSet = preparedStatement.executeQuery()
		resultSet.use { rows -> return collectGluedQuantitySamples(rows, limit) }
	}
}

private fun printDatabaseVerboseDiagnostics(config: NormalizeConfig) {
	openConnection(config).use { connection ->
		val total = countScrapedIngredients(connection)
		val gluedSamples = loadGluedQuantitySamples(connection, limit = 10)
		println("Database verbose: scanned $total scraped ingredient rows")
		if (gluedSamples.isEmpty()) {
			println("Database verbose: no glued quantity+letter patterns found")
		} else {
			println("Database verbose: examples still matching quantity+letter without space:")
			gluedSamples.forEach { sample ->
				println("  $sample -> ${normalizeIngredientText(sample)}")
			}
		}
	}
}

fun loadScrapedIngredientUpdates(connection: Connection): List<IngredientUpdate> {
	val sql = """
		SELECT i.id, r.id, i.ingredient
		FROM ingredients i
		JOIN ingredient_groups ig ON ig.id = i.ingredient_group_id
		JOIN recipes r ON r.id = ig.recipe_id
		WHERE r.source_url IS NOT NULL
			AND TRIM(r.source_url) <> ''
			AND i.ingredient IS NOT NULL
	""".trimIndent()

	val statement = connection.prepareStatement(sql)
	statement.use { preparedStatement ->
		val resultSet = preparedStatement.executeQuery()
		resultSet.use { rows ->
			val updates = mutableListOf<IngredientUpdate>()
			while (rows.next()) {
				toIngredientUpdate(
					ingredientId = rows.getInt(1),
					recipeId = rows.getInt(2),
					before = rows.getString(3).orEmpty(),
				)?.let(updates::add)
			}
			return updates
		}
	}
}

private fun printDatabaseDryRunPreview(updates: List<IngredientUpdate>, recipeCount: Int) {
	println("Database dry run: would update ${updates.size} ingredients across $recipeCount recipes")
	updates.take(dryRunPreviewLimit).forEach { update ->
		println("  [recipe ${update.recipeId}] ${update.before} -> ${update.after}")
	}
	if (updates.size > dryRunPreviewLimit) {
		println("  ... and ${updates.size - dryRunPreviewLimit} more")
	}
}

private fun persistDatabaseUpdates(connection: Connection, updates: List<IngredientUpdate>) {
	val statement = connection.prepareStatement("UPDATE ingredients SET ingredient = ? WHERE id = ?")
	statement.use { preparedStatement ->
		updates.forEach { update ->
			preparedStatement.setString(1, update.after)
			preparedStatement.setInt(2, update.ingredientId)
			preparedStatement.addBatch()
		}
		preparedStatement.executeBatch()
	}
}

fun applyDatabaseUpdates(config: NormalizeConfig) {
	openConnection(config).use { connection ->
		connection.autoCommit = false
		val updates = loadScrapedIngredientUpdates(connection)
		if (updates.isEmpty()) {
			println("Database: no scraped ingredient rows need updating")
			if (config.verbose) {
				printDatabaseVerboseDiagnostics(config)
			}
			return
		}

		val recipeCount = updates.map { it.recipeId }.distinct().size
		if (config.dryRun) {
			printDatabaseDryRunPreview(updates, recipeCount)
			return
		}

		persistDatabaseUpdates(connection, updates)
		connection.commit()
		println("Database: updated ${updates.size} ingredients across $recipeCount recipes")
	}
}

private fun normalizeJsonIngredientElement(ingredientElement: JsonPrimitive): Pair<JsonPrimitive, Int> {
	val raw = ingredientElement.contentOrNull ?: return ingredientElement to 0
	val normalized = normalizeIngredientText(raw)
	val changedCount = if (normalized == raw) 0 else 1
	return JsonPrimitive(normalized) to changedCount
}

private fun normalizeJsonIngredientsArray(ingredients: JsonArray): Pair<JsonArray, Int> {
	var changedCount = 0
	val normalizedIngredients = buildJsonArray {
		ingredients.forEach { ingredientElement ->
			when (ingredientElement) {
				is JsonPrimitive -> {
					val (normalizedElement, ingredientChanges) = normalizeJsonIngredientElement(ingredientElement)
					changedCount += ingredientChanges
					add(normalizedElement)
				}

				else -> add(ingredientElement)
			}
		}
	}
	return normalizedIngredients to changedCount
}

private fun normalizeJsonGroupObject(groupElement: JsonObject): Pair<JsonObject, Int> {
	val ingredients = groupElement["ingredients"]?.jsonArray ?: return groupElement to 0
	val (normalizedIngredients, changedCount) = normalizeJsonIngredientsArray(ingredients)
	val normalizedGroup = buildJsonObject {
		groupElement.forEach { (key, value) ->
			put(key, if (key == "ingredients") normalizedIngredients else value)
		}
	}
	return normalizedGroup to changedCount
}

fun normalizeJsonIngredientGroups(groups: JsonArray): Pair<JsonArray, Int> {
	var changedCount = 0
	val normalizedGroups = buildJsonArray {
		groups.forEach { groupElement ->
			when (groupElement) {
				is JsonObject -> {
					val (normalizedGroup, groupChanges) = normalizeJsonGroupObject(groupElement)
					changedCount += groupChanges
					add(normalizedGroup)
				}

				else -> add(groupElement)
			}
		}
	}
	return normalizedGroups to changedCount
}

private fun readRecipeJsonRoot(file: File): JsonObject? {
	return try {
		json.parseToJsonElement(file.readText()).jsonObject
	} catch (_: IOException) {
		println("JSON ERROR ${file.path}: could not read file")
		null
	} catch (_: Exception) {
		println("JSON ERROR ${file.path}: invalid JSON")
		null
	}
}

fun normalizeRecipeJsonFile(file: File): Int {
	val root = readRecipeJsonRoot(file) ?: return 0
	var changedCount = 0
	var normalizedRoot = root

	val ingredientGroups = root["ingredient_groups"]?.jsonArray
	if (ingredientGroups != null) {
		val (normalizedGroups, groupChanges) = normalizeJsonIngredientGroups(ingredientGroups)
		changedCount += groupChanges
		if (groupChanges > 0) {
			normalizedRoot = buildJsonObject {
				normalizedRoot.forEach { (key, value) ->
					put(key, if (key == "ingredient_groups") normalizedGroups else value)
				}
			}
		}
	}

	val flatIngredients = root["ingredients"]?.jsonArray
	if (flatIngredients != null) {
		val (normalizedIngredients, flatChanges) = normalizeJsonIngredientsArray(flatIngredients)
		changedCount += flatChanges
		if (flatChanges > 0) {
			normalizedRoot = buildJsonObject {
				normalizedRoot.forEach { (key, value) ->
					put(key, if (key == "ingredients") normalizedIngredients else value)
				}
			}
		}
	}

	if (changedCount > 0) {
		file.writeText(json.encodeToString(JsonObject.serializer(), normalizedRoot))
	}
	return changedCount
}

data class JsonUpdateStats(
	var scannedFiles: Int = 0,
	var updatedFiles: Int = 0,
	var updatedIngredients: Int = 0,
)

private fun countJsonFileChanges(file: File): Int {
	val root = readRecipeJsonRoot(file) ?: return 0
	val groupChanges = root["ingredient_groups"]?.jsonArray
		?.let { groups -> normalizeJsonIngredientGroups(groups).second }
		?: 0
	val flatChanges = root["ingredients"]?.jsonArray
		?.let { ingredients -> normalizeJsonIngredientsArray(ingredients).second }
		?: 0
	return groupChanges + flatChanges
}

private fun collectJsonGluedSamples(rootDir: File, limit: Int): List<String> {
	val samples = mutableListOf<String>()
	if (!rootDir.exists()) {
		return samples
	}
	rootDir.walkTopDown()
		.filter { file -> file.isFile && file.extension == "json" }
		.forEach { file ->
			if (samples.size >= limit) {
				return@forEach
			}
			val root = readRecipeJsonRoot(file) ?: return@forEach
			val ingredientTexts = buildList {
				root["ingredient_groups"]?.jsonArray?.forEach { groupElement ->
					if (groupElement is JsonObject) {
						groupElement["ingredients"]?.jsonArray?.forEach { ingredientElement ->
							ingredientElement.jsonPrimitive.contentOrNull?.let(::add)
						}
					}
				}
				root["ingredients"]?.jsonArray?.forEach { ingredientElement ->
					ingredientElement.jsonPrimitive.contentOrNull?.let(::add)
				}
			}
			ingredientTexts.forEach { ingredient ->
				if (samples.size < limit && gluedQuantitySampleRegex.containsMatchIn(ingredient)) {
					samples += ingredient
				}
			}
		}
	return samples
}

private fun printJsonVerboseDiagnostics(config: NormalizeConfig, stats: JsonUpdateStats) {
	println("JSON verbose: scanned ${stats.scannedFiles} files")
	if (stats.scannedFiles == 0) {
		println("JSON verbose: no JSON files found under ${config.jsonDirs.joinToString { it.path }}")
		return
	}
	val gluedSamples = config.jsonDirs.flatMap { rootDir -> collectJsonGluedSamples(rootDir, limit = 10) }
		.distinct()
		.take(10)
	if (gluedSamples.isEmpty()) {
		println("JSON verbose: no glued quantity+letter patterns found")
	} else {
		println("JSON verbose: examples still matching quantity+letter without space:")
		gluedSamples.forEach { sample ->
			println("  $sample -> ${normalizeIngredientText(sample)}")
		}
	}
}

private fun processJsonFile(file: File, config: NormalizeConfig, stats: JsonUpdateStats) {
	stats.scannedFiles += 1
	val changedCount = if (config.dryRun) {
		countJsonFileChanges(file)
	} else {
		normalizeRecipeJsonFile(file)
	}
	if (changedCount > 0) {
		stats.updatedFiles += 1
		stats.updatedIngredients += changedCount
	}
}

private fun processJsonDirectory(rootDir: File, config: NormalizeConfig, stats: JsonUpdateStats) {
	if (!rootDir.exists()) {
		println("JSON: skipping missing directory ${rootDir.path}")
		return
	}

	rootDir.walkTopDown()
		.filter { file -> file.isFile && file.extension == "json" }
		.forEach { file -> processJsonFile(file, config, stats) }
}

fun applyJsonUpdates(config: NormalizeConfig) {
	if (config.jsonDirs.isEmpty()) {
		return
	}

	val stats = JsonUpdateStats()
	config.jsonDirs.forEach { rootDir ->
		processJsonDirectory(rootDir, config, stats)
	}

	val action = if (config.dryRun) "would update" else "updated"
	println(
		"JSON: $action ${stats.updatedIngredients} ingredients in ${stats.updatedFiles} files " +
			"(scanned ${stats.scannedFiles} files under ${config.jsonDirs.joinToString { it.path }})",
	)
	if (config.verbose && stats.updatedIngredients == 0) {
		printJsonVerboseDiagnostics(config, stats)
	}
}

fun main() {
	verifyScrapedIngredientRules()
	val config = parseOptions(args)
	if (config.dryRun) {
		println("Dry run enabled; no files or database rows will be modified")
	}

	if (config.dbName != null || !config.dbDsn.isNullOrBlank()) {
		applyDatabaseUpdates(config)
	}

	applyJsonUpdates(config)
}

main()
