#!/usr/bin/env kotlin
@file:DependsOn("org.jetbrains.kotlinx:kotlinx-serialization-json:1.10.0")
@file:DependsOn("org.postgresql:postgresql:42.7.10")

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.sql.Connection
import java.sql.DriverManager
import java.util.Locale

val knownWebsites = mapOf(
	"allrecipes" to "https://www.allrecipes.com",
	"foodnetwork" to "https://www.foodnetwork.com",
	"bonappetit" to "https://www.bonappetit.com",
	"epicurious" to "https://www.epicurious.com",
	"seriouseats" to "https://www.seriouseats.com"
)

val json = Json {
	prettyPrint = true
	ignoreUnknownKeys = true
}

data class RecipeData(
	val title: String,
	val ingredientGroups: List<Pair<String?, List<String>>>,
	val instructionList: List<String>,
	val instructions: String,
	val totalTime: Int?,
	val prepTime: Int?,
	val cookTime: Int?,
	val yields: String?,
	val image: String?,
	val nutrients: Map<String, String?>,
	val language: String?,
	val cuisine: String?,
	val category: String?,
	val links: Map<String, String?>,
	val sourceUrl: String
) {

	fun toJsonObject(): JsonObject {
		val ingredientGroupsArray = kotlinx.serialization.json.buildJsonArray {
			ingredientGroups.forEach { (groupName, items) ->
				add(
					kotlinx.serialization.json.buildJsonObject {
						put("name", JsonPrimitive(groupName ?: ""))
						put(
							"ingredients",
							kotlinx.serialization.json.buildJsonArray {
								items.forEach { add(JsonPrimitive(it)) }
							}
						)
					}
				)
			}
		}

		val instructionListArray = kotlinx.serialization.json.buildJsonArray {
			instructionList.forEach { add(JsonPrimitive(it)) }
		}
		val nutrientsObject = kotlinx.serialization.json.buildJsonObject {
			nutrients.forEach { (k, v) -> put(k, if (v == null) JsonPrimitive("") else JsonPrimitive(v)) }
		}
		val linksObject = kotlinx.serialization.json.buildJsonObject {
			links.forEach { (k, v) -> put(k, if (v == null) JsonPrimitive("") else JsonPrimitive(v)) }
		}

		return kotlinx.serialization.json.buildJsonObject {
			put("title", JsonPrimitive(title))
			put("ingredient_groups", ingredientGroupsArray)
			put("instruction_list", instructionListArray)
			put("instructions", JsonPrimitive(instructions))
			if (totalTime == null) put("total_time", JsonPrimitive("")) else put("total_time", JsonPrimitive(totalTime))
			if (prepTime == null) put("prep_time", JsonPrimitive("")) else put("prep_time", JsonPrimitive(prepTime))
			if (cookTime == null) put("cook_time", JsonPrimitive("")) else put("cook_time", JsonPrimitive(cookTime))
			put("yields", JsonPrimitive(yields ?: ""))
			put("image", JsonPrimitive(image ?: ""))
			put("nutrients", nutrientsObject)
			put("language", JsonPrimitive(language ?: "en"))
			put("cuisine", JsonPrimitive(cuisine ?: ""))
			put("category", JsonPrimitive(category ?: ""))
			put("links", linksObject)
			put("source_url", JsonPrimitive(sourceUrl))
		}
	}
}

data class Config(
	val website: String,
	val outputDir: String,
	val mode: String,
	val urlsFile: String?,
	val simpleScraperEndpoint: String,
	val simpleScraperApiKey: String?,
	val simpleScraperTimeoutSeconds: Long,
	val recipeUrlPattern: Regex,
	val maxUrls: Int,
	val sleepMillis: Long,
	val dbDsn: String?,
	val dbHost: String,
	val dbPort: Int,
	val dbName: String?,
	val dbUser: String?,
	val dbPassword: String?,
	val precheckDb: Boolean,
	val importFile: String?
)

fun printUsageAndExit(message: String? = null): Nothing {
	if (!message.isNullOrBlank()) {
		System.err.println("Error: $message")
	}
	System.err.println(
		"""
        Usage:
          kotlin scripts/recipe_site_scraper.main.kts -- <website> <output_dir> [options]

        Options:
          --mode postgres|ndjson                    Default: postgres
          --urls-file <path>                        Optional pre-discovered URL list
          --simplescraper-endpoint <url>            Default: https://simplescraper.io/extracturls
          --simplescraper-api-key <key>             Or set SIMPLESCRAPER_API_KEY
          --simplescraper-timeout <seconds>         Default: 30
          --recipe-url-pattern <regex>              Default: /recipe/
          --max-urls <n>                            Default: 50 (<=0 means no limit)
          --sleep-seconds <decimal>                 Default: 0.4

          --db-dsn <jdbc-url>
          --db-host <host>                          Default: localhost
          --db-port <port>                          Default: 5432
          --db-name <name>                          Default: purecipes
          --db-user <user>                          Default: postgres
          --db-password <password>                  Default: postgres
          --precheck-db true|false                  Default: true (postgres mode)

          --import-file <path>                      NDJSON target in --mode ndjson

        Notes:
          - Uses Python recipe-scrapers per URL, invoked from Kotlin script.
          - Requires python3 + recipe-scrapers installed.
        """.trimIndent()
	)
	kotlin.system.exitProcess(1)
}

// allowed flag names for command‑line parsing
private val allowedOptions = setOf(
	"--mode",
	"--urls-file",
	"--simplescraper-endpoint",
	"--simplescraper-api-key",
	"--simplescraper-timeout",
	"--recipe-url-pattern",
	"--max-urls",
	"--sleep-seconds",
	"--db-dsn",
	"--db-host",
	"--db-port",
	"--db-name",
	"--db-user",
	"--db-password",
	"--precheck-db",
	"--import-file"
)

fun parseOptions(scriptArgs: Array<String>): Config {
	if (scriptArgs.size < 2) printUsageAndExit("Missing required arguments: <website> <output_dir>")

	val website = scriptArgs[0]
	val outputDir = scriptArgs[1]
	val options = parseFlagArguments(scriptArgs.drop(2).toTypedArray())

	val mode = (options["--mode"] ?: "postgres").also { m ->
		if (m !in setOf("postgres", "ndjson")) printUsageAndExit("--mode must be postgres or ndjson")
	}

	val regex = try {
		Regex(options["--recipe-url-pattern"] ?: "/recipe/", RegexOption.IGNORE_CASE)
	} catch (_: Exception) {
		printUsageAndExit("Invalid --recipe-url-pattern regex")
	}

	val maxUrls = options["--max-urls"]?.toIntOrExit("--max-urls must be an integer") ?: 50
	val sleepMillis = options["--sleep-seconds"]?.toDoubleOrExit("--sleep-seconds must be a number")
		?.let { (it * 1000).toLong() } ?: 400
	val timeout = options["--simplescraper-timeout"]?.toLongOrExit("--simplescraper-timeout must be an integer") ?: 30L

	return Config(
		website = website,
		outputDir = outputDir,
		mode = mode,
		urlsFile = options["--urls-file"]?.let(::expandPath),
		simpleScraperEndpoint = options["--simplescraper-endpoint"] ?: "https://simplescraper.io/extracturls",
		simpleScraperApiKey = options["--simplescraper-api-key"] ?: System.getenv("SIMPLESCRAPER_API_KEY"),
		simpleScraperTimeoutSeconds = timeout,
		recipeUrlPattern = regex,
		maxUrls = maxUrls,
		sleepMillis = sleepMillis,
		dbDsn = options["--db-dsn"],
		dbHost = options["--db-host"] ?: "localhost",
		dbPort = (options["--db-port"] ?: "5432").toIntOrExit("--db-port must be an integer"),
		dbName = options["--db-name"] ?: "purecipes",
		dbUser = options["--db-user"] ?: "postgres",
		dbPassword = options["--db-password"] ?: "postgres",
		precheckDb = (options["--precheck-db"] ?: "true").equals("true", ignoreCase = true),
		importFile = options["--import-file"]?.let(::expandPath)
	)
}

// helper extensions to reduce boilerplate and complexity
private fun String.toIntOrExit(msg: String): Int = this.toIntOrNull() ?: printUsageAndExit(msg)
private fun String.toDoubleOrExit(msg: String): Double = this.toDoubleOrNull() ?: printUsageAndExit(msg)
private fun String.toLongOrExit(msg: String): Long = this.toLongOrNull() ?: printUsageAndExit(msg)

private fun parseFlagArguments(args: Array<String>): Map<String, String> {
	val options = mutableMapOf<String, String>()
	var i = 0
	while (i < args.size) {
		val key = args[i]
		if (!key.startsWith("--")) {
			printUsageAndExit("Unexpected argument: $key")
		}
		val next = args.getOrNull(i + 1)
		if (next == null || next.startsWith("--")) {
			printUsageAndExit("Missing value for option $key")
		}
		if (!allowedOptions.contains(key)) {
			printUsageAndExit("Unknown option: $key")
		}
		options[key] = next
		i += 2
	}
	return options
}

fun expandPath(path: String): String {
	return if (path.startsWith("~/")) {
		File(System.getProperty("user.home"), path.removePrefix("~/")).path
	} else {
		path
	}
}

fun normalizeWebsite(input: String): String {
	val trimmed = input.trim()
	val deFlagged = trimmed.removePrefix("--")
	val lower = deFlagged.lowercase(Locale.getDefault())
	val result = when {
		knownWebsites[lower] != null -> knownWebsites[lower]!!
		trimmed.startsWith("http://") || trimmed.startsWith("https://") -> trimmed.trimEnd('/')
		else -> knownWebsites[trimmed.lowercase(Locale.getDefault())] ?: "https://${trimmed.trimEnd('/')}"
	}
	return result
}

fun slugify(input: String): String {
	return input
		.lowercase(Locale.getDefault())
		.replace(Regex("[^a-z0-9]+"), "-")
		.trim('-')
		.ifBlank { "recipe" }
}

fun extractUrls(config: Config, websiteUrl: String): List<String> {
	if (!config.urlsFile.isNullOrBlank()) {
		return File(config.urlsFile).readLines()
			.map { it.trim() }
			.filter { it.isNotBlank() }
	}

	val query = buildString {
		append("url=")
		append(URLEncoder.encode(websiteUrl, StandardCharsets.UTF_8))
		append("&format=txt")
		if (!config.simpleScraperApiKey.isNullOrBlank()) {
			append("&api_key=")
			append(URLEncoder.encode(config.simpleScraperApiKey, StandardCharsets.UTF_8))
		}
	}

	val uri = URI("${config.simpleScraperEndpoint}?$query")
	val request = HttpRequest.newBuilder()
		.uri(uri)
		.timeout(java.time.Duration.ofSeconds(config.simpleScraperTimeoutSeconds))
		.GET()
		.build()

	val client = HttpClient.newHttpClient()
	val response = client.send(request, HttpResponse.BodyHandlers.ofString())
	if (response.statusCode() !in 200..299) {
		error("SimpleScraper failed with status ${response.statusCode()}: ${response.body().take(300)}")
	}

	return response.body()
		.lineSequence()
		.map { it.trim() }
		.filter { it.startsWith("http") }
		.toList()
}

fun filterRecipeUrls(urls: List<String>, websiteUrl: String, pattern: Regex): List<String> {
	val host = URI(websiteUrl).host?.removePrefix("www.") ?: ""
	return urls.asSequence()
		.distinct()
		.mapNotNull { url ->
			val candidate = try {
				URI(url)
			} catch (_: Exception) {
				return@mapNotNull null
			}
			val candidateHost = candidate.host?.removePrefix("www.") ?: return@mapNotNull null
			if (host.isNotBlank() && !candidateHost.contains(host)) return@mapNotNull null
			val path = candidate.path ?: ""
			if (candidateHost.contains("allrecipes.com") && path.contains("/recipes/")) return@mapNotNull null
			if (!pattern.containsMatchIn(path)) return@mapNotNull null
			url
		}
		.toList()
}

fun scrapeRecipeWithPython(url: String): RecipeData? {
	val py = """
import json
import sys
from recipe_scrapers import scrape_me

url = sys.argv[1]
try:
    try:
        s = scrape_me(url, wild_mode=True)
    except TypeError:
        s = scrape_me(url)

    def safe(name, default=None):
        fn = getattr(s, name, None)
        if not callable(fn):
            return default
        try:
            value = fn()
            if value in (None, ""):
                return default
            return value
        except Exception:
            return default

    title = (safe("title", "") or "").strip()

    ingredient_groups = safe("ingredient_groups")
    ingredients = safe("ingredients", []) or []
    if ingredient_groups is None:
        ingredient_groups = [(None, ingredients)]
    else:
        # Normalize to list of (name, [ingredients]) and filter blanks
        normalized = []
        try:
            for g in ingredient_groups:
                if isinstance(g, (list, tuple)) and len(g) == 2:
                    name, items = g
                elif isinstance(g, dict):
                    name = g.get("name")
                    items = g.get("ingredients")
                else:
                    name, items = None, g

                if items is None:
                    items = []
                items = [str(x).strip() for x in items if str(x).strip()]
                if not items:
                    continue
                normalized.append((name, items))
        except Exception:
            normalized = [(None, [str(x).strip() for x in ingredients if str(x).strip()])]
        ingredient_groups = normalized

    instruction_list = safe("instruction_list")
    if instruction_list is None:
        raw_instructions = (safe("instructions", "") or "").strip()
        instruction_list = [x.strip() for x in raw_instructions.split("\n") if x.strip()]
    else:
        instruction_list = [str(x).strip() for x in instruction_list if str(x).strip()]

    instructions = (safe("instructions", "") or "").strip()
    if not instructions:
        instructions = "\n".join(instruction_list).strip()

    cuisine = safe("cuisine")
    category = safe("category")

    if not title or not ingredient_groups:
        print("", end="")
        raise SystemExit(0)

    payload = {
        "title": title,
        "ingredient_groups": [
            {"name": (n or ""), "ingredients": lst}
            for (n, lst) in ingredient_groups
        ],
        "instruction_list": instruction_list,
        "instructions": instructions,
        "total_time": safe("total_time"),
        "prep_time": safe("prep_time"),
        "cook_time": safe("cook_time"),
        "yields": safe("yields"),
        "image": safe("image"),
        "nutrients": safe("nutrients", {}) or {},
        "language": safe("language", "en"),
        "cuisine": cuisine,
        "category": category,
        "links": {"canonical": safe("canonical_url") or url},
        "source_url": url
    }
    print(json.dumps(payload, ensure_ascii=False))
except Exception as exc:
    print(f"__SCRAPE_ERROR__:{type(exc).__name__}:{exc}")
    raise SystemExit(2)
""".trimIndent()

	val command = listOf("python3", "-c", py, url)
	val process = ProcessBuilder(command)
		.redirectErrorStream(true)
		.start()

	val output = process.inputStream.bufferedReader().readText().trim()
	val code = process.waitFor()
	if (code != 0) {
		if (output.contains("No module named 'recipe_scrapers'")) {
			error("Missing Python dependency: recipe-scrapers. Install with: python3 -m pip install recipe-scrapers")
		}
		println("Python scrape failed for $url (exit=$code): ${output.take(300)}")
	} else if (output.startsWith("__SCRAPE_ERROR__:")) {
		println("Python scrape failed for $url: ${output.removePrefix("__SCRAPE_ERROR__:").take(300)}")
	} else if (output.isBlank()) {
		println("Python returned blank output for $url")
	} else {
		return parseRecipe(output)
	}
	return null
}

fun extractRecipeLinksFromCollectionPage(pageUrl: String, timeoutSeconds: Long): List<String> {
	val base = try {
		URI(pageUrl)
	} catch (_: Exception) {
		return emptyList()
	}

	val request = HttpRequest.newBuilder()
		.uri(base)
		.timeout(java.time.Duration.ofSeconds(timeoutSeconds))
		.GET()
		.build()

	val response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString())
	if (response.statusCode() !in 200..299) {
		return emptyList()
	}

	val hrefRegex = Regex("""href\s*=\s*"([^\"]+)"""", RegexOption.IGNORE_CASE)
	val seen = mutableSetOf<String>()
	val links = mutableListOf<String>()

	hrefRegex.findAll(response.body()).forEach { match ->
		val raw = match.groupValues[1].trim()
		if (raw.isBlank()) return@forEach
		if (raw.startsWith("#") || raw.startsWith("javascript:")) return@forEach

		val resolved = try {
			base.resolve(raw)
		} catch (_: Exception) {
			return@forEach
		}

		val host = resolved.host?.removePrefix("www.") ?: return@forEach
		val baseHost = base.host?.removePrefix("www.") ?: return@forEach
		if (!host.contains(baseHost)) return@forEach

		val path = resolved.path?.lowercase(Locale.getDefault()) ?: return@forEach
		if (!path.contains("/recipe/")) return@forEach

		val normalized = resolved.toString().substringBefore('#')
		if (seen.add(normalized)) {
			links += normalized
		}
	}

	return links
}

fun parseInt(value: String?): Int? = value?.trim()?.toIntOrNull()

fun parseRecipe(rawJson: String): RecipeData? {
	val root = runCatching { json.parseToJsonElement(rawJson).jsonObject }
		.getOrNull() ?: return null

	val title = root["title"]?.jsonPrimitive?.contentOrNull?.trim().orEmpty()

	val ingredientGroups = root["ingredient_groups"]?.jsonArray
		?.mapNotNull { groupEl ->
			when (groupEl) {
				is JsonObject -> {
					val name = groupEl["name"]?.jsonPrimitive?.contentOrNull?.trim().orEmpty().ifBlank { null }
					val items = groupEl["ingredients"]?.jsonArray
						?.mapNotNull { it.jsonPrimitive.contentOrNull?.trim() }
						?.filter { it.isNotBlank() }
						?: emptyList()
					if (items.isEmpty()) null else (name to items)
				}

				is kotlinx.serialization.json.JsonArray -> {
					val name = groupEl.getOrNull(0)?.jsonPrimitive?.contentOrNull?.trim().orEmpty().ifBlank { null }
					val items = groupEl.getOrNull(1)?.jsonArray
						?.mapNotNull { it.jsonPrimitive.contentOrNull?.trim() }
						?.filter { it.isNotBlank() }
						?: emptyList()
					if (items.isEmpty()) null else (name to items)
				}

				else -> null
			}
		}
		?: emptyList()

	val instructionList = root["instruction_list"]?.jsonArray
		?.mapNotNull { it.jsonPrimitive.contentOrNull?.trim() }
		?.filter { it.isNotBlank() }
		?: emptyList()

	val nutrientsObject = root["nutrients"]?.jsonObject ?: JsonObject(emptyMap())
	val nutrients = nutrientsObject.mapValues { (_, v) -> v.jsonPrimitive.contentOrNull }

	val linksObject = root["links"]?.jsonObject ?: JsonObject(emptyMap())
	val links = linksObject.mapValues { (_, v) -> v.jsonPrimitive.contentOrNull }

	val instructionsText = root["instructions"]?.jsonPrimitive?.contentOrNull.orEmpty().ifBlank {
		instructionList.joinToString("\n").trim()
	}

	if (title.isBlank() || ingredientGroups.isEmpty()) return null
	return RecipeData(
		title = title,
		ingredientGroups = ingredientGroups,
		instructionList = instructionList,
		instructions = instructionsText,
		totalTime = parseInt(root["total_time"]?.jsonPrimitive?.contentOrNull),
		prepTime = parseInt(root["prep_time"]?.jsonPrimitive?.contentOrNull),
		cookTime = parseInt(root["cook_time"]?.jsonPrimitive?.contentOrNull),
		yields = root["yields"]?.jsonPrimitive?.contentOrNull,
		image = root["image"]?.jsonPrimitive?.contentOrNull,
		nutrients = nutrients,
		language = root["language"]?.jsonPrimitive?.contentOrNull ?: "en",
		cuisine = root["cuisine"]?.jsonPrimitive?.contentOrNull,
		category = root["category"]?.jsonPrimitive?.contentOrNull,
		links = links,
		sourceUrl = root["source_url"]?.jsonPrimitive?.contentOrNull.orEmpty()
	)
}

fun ensureSchema(connection: Connection) {
	val sql = """
    CREATE TABLE IF NOT EXISTS recipes (
        id SERIAL PRIMARY KEY,
        title VARCHAR(255) NOT NULL,
        instructions TEXT,
        total_time INTEGER,
        prep_time INTEGER,
        cook_time INTEGER,
        yields VARCHAR(100),
        image_url TEXT,
        language VARCHAR(10) DEFAULT 'en',
        cuisine VARCHAR(100),
        category VARCHAR(100),
        source_url TEXT UNIQUE,
        scraped_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

    CREATE TABLE IF NOT EXISTS ingredient_groups (
        id SERIAL PRIMARY KEY,
        recipe_id INTEGER REFERENCES recipes(id) ON DELETE CASCADE,
        name TEXT,
        order_index INTEGER
    );

    CREATE TABLE IF NOT EXISTS ingredients (
        id SERIAL PRIMARY KEY,
        ingredient_group_id INTEGER REFERENCES ingredient_groups(id) ON DELETE CASCADE,
        ingredient TEXT NOT NULL,
        order_index INTEGER
    );

    CREATE TABLE IF NOT EXISTS instruction_steps (
        id SERIAL PRIMARY KEY,
        recipe_id INTEGER REFERENCES recipes(id) ON DELETE CASCADE,
        step TEXT NOT NULL,
        order_index INTEGER
    );

    CREATE TABLE IF NOT EXISTS nutrition (
        id SERIAL PRIMARY KEY,
        recipe_id INTEGER UNIQUE REFERENCES recipes(id) ON DELETE CASCADE,
        calories DECIMAL(10,2),
        protein DECIMAL(10,2),
        carbohydrates DECIMAL(10,2),
        fat DECIMAL(10,2),
        fiber DECIMAL(10,2),
        sugar DECIMAL(10,2),
        sodium DECIMAL(10,2)
    );
    """.trimIndent()

	connection.createStatement().use { it.execute(sql) }
	connection.commit()
}

fun isDuplicate(connection: Connection, sourceUrl: String): Boolean {
	connection.prepareStatement("SELECT 1 FROM recipes WHERE source_url = ?").use { ps ->
		ps.setString(1, sourceUrl)
		ps.executeQuery().use { rs ->
			return rs.next()
		}
	}
}

fun parseNumber(value: String?): Double? {
	if (value.isNullOrBlank()) return null
	val match = Regex("""-?\d+(?:\.\d+)?""").find(value) ?: return null
	return match.value.toDoubleOrNull()
}

fun detectMeasurementSystem(ingredientGroups: List<Pair<String?, List<String>>>): String? {
	var imperialHits = 0
	var metricHits = 0
	ingredientGroups.asSequence()
		.flatMap { (_, ingredients) -> ingredients.asSequence() }
		.forEach { ingredient ->
			val normalized = ingredient.lowercase()
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

fun saveRecipe(connection: Connection, recipe: RecipeData): Boolean {
	if (isDuplicate(connection, recipe.sourceUrl)) return false
	val measurementSystem = detectMeasurementSystem(recipe.ingredientGroups)

	val recipeId = connection.prepareStatement(
		"""
		INSERT INTO recipes (
			title,
			instructions,
			total_time,
			prep_time,
			cook_time,
			yields,
			image_url,
			language,
			cuisine,
			category,
			source_url,
			measurement_system
		)
		VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        RETURNING id
        """.trimIndent()
	).use { ps ->
		ps.setString(1, recipe.title)
		ps.setString(2, recipe.instructions)
		ps.setObject(3, recipe.totalTime)
		ps.setObject(4, recipe.prepTime)
		ps.setObject(5, recipe.cookTime)
		ps.setString(6, recipe.yields)
		ps.setString(7, recipe.image)
		ps.setString(8, recipe.language ?: "en")
		ps.setString(9, recipe.cuisine)
		ps.setString(10, recipe.category)
		ps.setString(11, recipe.sourceUrl)
		ps.setString(12, measurementSystem)
		ps.executeQuery().use { rs ->
			rs.next()
			rs.getInt(1)
		}
	}

	val insertGroupSql = "INSERT INTO ingredient_groups (recipe_id, name, order_index) VALUES (?, ?, ?) RETURNING id"
	val insertIngredientSql = "INSERT INTO ingredients (ingredient_group_id, ingredient, order_index) VALUES (?, ?, ?)"

	recipe.ingredientGroups.forEachIndexed { groupIndex, (groupName, items) ->
		val groupId = connection.prepareStatement(insertGroupSql).use { ps ->
			ps.setInt(1, recipeId)
			ps.setString(2, groupName)
			ps.setInt(3, groupIndex)
			ps.executeQuery().use { rs ->
				rs.next()
				rs.getInt(1)
			}
		}

		connection.prepareStatement(insertIngredientSql).use { ps ->
			items.forEachIndexed { itemIndex, ingredient ->
				ps.setInt(1, groupId)
				ps.setString(2, ingredient)
				ps.setInt(3, itemIndex)
				ps.addBatch()
			}
			ps.executeBatch()
		}
	}

	if (recipe.instructionList.isNotEmpty()) {
		connection.prepareStatement(
			"INSERT INTO instruction_steps (recipe_id, step, order_index) VALUES (?, ?, ?)"
		).use { ps ->
			recipe.instructionList.forEachIndexed { index, step ->
				ps.setInt(1, recipeId)
				ps.setString(2, step)
				ps.setInt(3, index)
				ps.addBatch()
			}
			ps.executeBatch()
		}
	}

	val nutrients = recipe.nutrients
	connection.prepareStatement(
		"""
        INSERT INTO nutrition (recipe_id, calories, protein, carbohydrates, fat, fiber, sugar, sodium)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()
	).use { ps ->
		ps.setInt(1, recipeId)
		ps.setObject(2, parseNumber(nutrients["calories"]))
		ps.setObject(3, parseNumber(nutrients["protein"]))
		ps.setObject(4, parseNumber(nutrients["carbohydrates"] ?: nutrients["carbs"]))
		ps.setObject(5, parseNumber(nutrients["fat"]))
		ps.setObject(6, parseNumber(nutrients["fiber"]))
		ps.setObject(7, parseNumber(nutrients["sugar"]))
		ps.setObject(8, parseNumber(nutrients["sodium"]))
		ps.executeUpdate()
	}

	connection.commit()
	return true
}

fun buildJdbcUrl(config: Config): String {
	if (!config.dbDsn.isNullOrBlank()) {
		return config.dbDsn
	}
	val dbName = config.dbName ?: printUsageAndExit("For postgres mode provide --db-dsn or --db-name")
	return "jdbc:postgresql://${config.dbHost}:${config.dbPort}/$dbName"
}

fun openConnection(config: Config): Connection {
	val jdbcUrl = buildJdbcUrl(config)
	return if (config.dbDsn.isNullOrBlank()) {
		val user = config.dbUser
			?: printUsageAndExit("For postgres mode provide --db-user (or include credentials in --db-dsn)")
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

val imperialUnitRegex = Regex(
	pattern =
		"(?<!\\p{L})(cups?|tbsp|tablespoons?|tsp|teaspoons?|ounces?|ounce|oz|" +
			"pounds?|pound|lbs?|lb|fahrenheit|°f)\\b",
	options = setOf(RegexOption.IGNORE_CASE),
)

val metricUnitRegex = Regex(
	pattern =
		"(?<!\\p{L})(kilograms?|kilogram|kg|grams?|gram|g|milliliters?|milliliter|" +
			"ml|liters?|liter|l|celsius|°c)\\b",
	options = setOf(RegexOption.IGNORE_CASE),
)

fun precheckExistingUrls(config: Config, urls: List<String>): Set<String> {
	if (urls.isEmpty()) return emptySet()

	openConnection(config).use { connection ->
		connection.autoCommit = false
		ensureSchema(connection)
		return collectExistingUrls(connection, urls)
	}
}

/**
 * Returns the subset of [urls] that already exist in the database represented by
 * [connection].
 */
private fun collectExistingUrls(connection: Connection, urls: List<String>): Set<String> {
	connection.prepareStatement("SELECT 1 FROM recipes WHERE source_url = ?").use { ps ->
		return urls.mapNotNull { url ->
			ps.setString(1, url)
			ps.executeQuery().use { rs ->
				if (rs.next()) url else null
			}
		}.toSet()
	}
}

fun runPostgresImport(config: Config, recipes: List<RecipeData>) {
	openConnection(config).use { connection ->
		connection.autoCommit = false
		ensureSchema(connection)

		var imported = 0
		var duplicates = 0
		for (recipe in recipes) {
			val inserted = saveRecipe(connection, recipe)
			if (inserted) imported++ else duplicates++
		}
		println("Database import complete. Imported=$imported Duplicates=$duplicates")
	}
}

fun writeNdjson(targetFile: File, recipes: List<RecipeData>) {
	targetFile.parentFile?.mkdirs()
	targetFile.bufferedWriter().use { writer ->
		recipes.forEach { recipe ->
			writer.write(json.encodeToString(JsonObject.serializer(), recipe.toJsonObject()))
			writer.newLine()
		}
	}
}

val config = parseOptions(args)
val websiteUrl = normalizeWebsite(config.website)
val outputRoot = File(expandPath(config.outputDir)).apply { mkdirs() }
val recipesDir = File(outputRoot, "recipes").apply { mkdirs() }

println("Discovering URLs from $websiteUrl")
val discovered = extractUrls(config, websiteUrl)
println("Discovered ${discovered.size} candidate URLs")

val filtered = filterRecipeUrls(discovered, websiteUrl, config.recipeUrlPattern)
val selectedAfterDbPrecheck = if (config.mode == "postgres" && config.precheckDb) {
	val existing = precheckExistingUrls(config, filtered)
	if (existing.isNotEmpty()) {
		println("Pre-check: skipping ${existing.size} URLs already in database")
	}
	val pending = filtered.filterNot { existing.contains(it) }
	if (config.maxUrls > 0) pending.take(config.maxUrls) else pending
} else {
	if (config.maxUrls > 0) filtered.take(config.maxUrls) else filtered
}

val queue = ArrayDeque(selectedAfterDbPrecheck)
val processed = mutableSetOf<String>()
println("Processing ${selectedAfterDbPrecheck.size} filtered recipe URLs")

val scraped = mutableListOf<RecipeData>()
var savedIndex = 0
while (queue.isNotEmpty()) {
	val url = queue.removeFirst()
	if (processed.add(url)) {
		val recipe = scrapeRecipeWithPython(url)
		if (recipe == null) {
			val extraLinks = extractRecipeLinksFromCollectionPage(url, config.simpleScraperTimeoutSeconds)
				.filterNot { processed.contains(it) }

			if (extraLinks.isNotEmpty()) {
				println("Expanded listing URL -> found ${extraLinks.size} recipe links: $url")
				extraLinks.forEach { queue.addLast(it) }
			} else {
				println("Skipped: $url")
			}
		} else {
			scraped += recipe
			savedIndex += 1
			val name = "%04d-%s.json".format(savedIndex, slugify(recipe.title).take(100))
			val file = File(recipesDir, name)
			file.writeText(json.encodeToString(JsonObject.serializer(), recipe.toJsonObject()))
			println("Saved: ${file.path}")

			if (config.maxUrls > 0 && scraped.size >= config.maxUrls) {
				println("Reached --max-urls limit (${config.maxUrls})")
				break
			}
		}
		if (config.sleepMillis > 0) Thread.sleep(config.sleepMillis)
	}
}

println("Successfully scraped ${scraped.size} recipes")

if (config.mode == "postgres") {
	runPostgresImport(config, scraped)
} else {
	val importPath = config.importFile ?: File(outputRoot, "recipes.ndjson").path
	writeNdjson(File(importPath), scraped)
	println("Wrote NDJSON import file: $importPath")
}
