#!/usr/bin/env kotlin
@file:DependsOn("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")
@file:DependsOn("org.postgresql:postgresql:42.7.10")

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.sql.Connection
import java.sql.DriverManager
import java.util.Locale

data class SiteConfig(
	val url: String,
	val recipePattern: Regex,
)

val knownWebsites = mapOf(
	"allrecipes" to SiteConfig(
		url = "https://www.allrecipes.com",
		recipePattern = Regex("^/recipe/\\d+/"),
	),
	"foodnetwork" to SiteConfig(
		url = "https://foodnetwork.co.uk",
		recipePattern = Regex("^/recipes/[^/]+$"),
	),
	"bonappetit" to SiteConfig(
		url = "https://www.bonappetit.com",
		recipePattern = Regex("^/recipe/[^/]+$"),
	),
	"epicurious" to SiteConfig(
		url = "https://www.epicurious.com",
		recipePattern = Regex("^/recipes/food/views/"),
	),
	"seriouseats" to SiteConfig(
		url = "https://www.seriouseats.com",
		recipePattern = Regex("^/[^/]+-recipe-\\d+$|^/recipes/"),
	),
	"mob" to SiteConfig(
		url = "https://www.mob.co.uk",
		recipePattern = Regex("^/recipes/(?!collections|categories)[^/]+$"),
	),
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
	val debug: Boolean
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
		--mode web|json                            Default: web
		--urls-file <path>                        Optional pre-discovered URL list
		--simplescraper-endpoint <url>            Default: https://simplescraper.io/extracturls
		--simplescraper-api-key <key>             Or set SIMPLESCRAPER_API_KEY
		--simplescraper-timeout <seconds>         Default: 30
		--recipe-url-pattern <regex>              Default: /recipe/
		--max-urls <n>                            Default: 50 (<=0 means no limit)
		--sleep-seconds <decimal>                 Default: 0.4
		--debug true|false                        Default: false

		--db-dsn <jdbc-url>
		--db-host <host>                          Default: localhost
		--db-port <port>                          Default: 5432
		--db-name <name>                          Default: purecipes
		--db-user <user>                          Default: postgres
		--db-password <password>                  Default: postgres
		--precheck-db true|false                  Default: true (postgres mode)

	Modes:
		web   Scrape recipes from the web, save JSON files with DB IDs, and insert into Postgres.
		json  Read previously saved JSON files from <output_dir>/recipes/ and insert them into Postgres.

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
	"--debug",
	"--db-dsn",
	"--db-host",
	"--db-port",
	"--db-name",
	"--db-user",
	"--db-password",
	"--precheck-db"
)

fun parseOptions(scriptArgs: Array<String>): Config {
	if (scriptArgs.size < 2) printUsageAndExit("Missing required arguments: <website> <output_dir>")

	val website = scriptArgs[0]
	val outputDir = scriptArgs[1]
	val options = parseFlagArguments(scriptArgs.drop(2).toTypedArray())

	val mode = validateMode(options)
	val regex = parseRecipeUrlPattern(options)
	val maxUrls = options["--max-urls"]?.toIntOrExit("--max-urls must be an integer") ?: 50
	val sleepMillis = (options["--sleep-seconds"]?.toDoubleOrExit("--sleep-seconds must be a number") ?: 0.4) * 1000
	val timeout = options["--simplescraper-timeout"]?.toLongOrExit("--simplescraper-timeout must be an integer") ?: 30L
	val dbPort = options["--db-port"]?.toIntOrExit("--db-port must be an integer") ?: 5432
	val boolOptions = parseBoolOptions(options)

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
		sleepMillis = sleepMillis.toLong(),
		dbDsn = options["--db-dsn"],
		dbHost = options["--db-host"] ?: "localhost",
		dbPort = dbPort,
		dbName = options["--db-name"] ?: "purecipes",
		dbUser = options["--db-user"] ?: "postgres",
		dbPassword = options["--db-password"] ?: "postgres",
		precheckDb = boolOptions["precheck"] ?: true,
		debug = boolOptions["debug"] ?: false
	)
}

private fun validateMode(options: Map<String, String>): String {
	val mode = options["--mode"] ?: "web"
	if (mode !in setOf("web", "json")) printUsageAndExit("--mode must be web or json")
	return mode
}

private fun parseRecipeUrlPattern(options: Map<String, String>): Regex {
	return try {
		Regex(options["--recipe-url-pattern"] ?: "/recipe/", RegexOption.IGNORE_CASE)
	} catch (_: Exception) {
		printUsageAndExit("Invalid --recipe-url-pattern regex")
	}
}

private fun parseBoolOptions(options: Map<String, String>): Map<String, Boolean> {
	val boolOpts = mutableMapOf<String, Boolean>()
	boolOpts["precheck"] = (options["--precheck-db"] ?: "true").equals("true", ignoreCase = true)
	boolOpts["debug"] = (options["--debug"] ?: "false").equals("true", ignoreCase = true)
	return boolOpts
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
		knownWebsites[lower] != null -> knownWebsites[lower]!!.url
		trimmed.startsWith("http://") || trimmed.startsWith("https://") -> trimmed.trimEnd('/')
		else -> knownWebsites[trimmed.lowercase(Locale.getDefault())]?.url ?: "https://${trimmed.trimEnd('/')}"
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
	val siteConfig = knownWebsites.values.find { host.isNotBlank() && URI(it.url).host?.removePrefix("www.") == host }
	val effectivePattern = siteConfig?.recipePattern ?: pattern
	val rejectionCounts = mutableMapOf<String, Int>()
	val rejectionSamples = mutableMapOf<String, String>()
	fun reject(reason: String, url: String): String? {
		rejectionCounts[reason] = rejectionCounts.getOrDefault(reason, 0) + 1
		rejectionSamples.putIfAbsent(reason, url)
		return null
	}

	val filtered = urls.asSequence()
		.distinct()
		.mapNotNull { url ->
			val candidate = try {
				URI(url)
			} catch (_: Exception) {
				return@mapNotNull reject("invalid URL", url)
			}

			val candidateHost = candidate.host?.removePrefix("www.") ?: return@mapNotNull reject(
				"missing or invalid host",
				url
			)
			if (host.isNotBlank() && !candidateHost.contains(host)) {
				val reason = "host mismatch: expected containing '$host' but got '$candidateHost'"
				return@mapNotNull reject(reason, url)
			}

			val path = candidate.path ?: ""
			if (!effectivePattern.containsMatchIn(path)) {
				return@mapNotNull reject("path does not match pattern '${effectivePattern.pattern}'", url)
			}

			url
		}
		.toList()

	if (rejectionCounts.isNotEmpty()) {
		val totalRejected = rejectionCounts.values.sum()
		println("Filtered out $totalRejected URLs by reason:")
		rejectionCounts.forEach { (reason, count) ->
			println("  $count -> $reason (example: ${rejectionSamples[reason]})")
		}
	}

	return filtered
}

val python3Candidates = listOf(
	"python3",
	"/Library/Frameworks/Python.framework/Versions/3.12/bin/python3",
	"/usr/local/bin/python3",
	"/usr/bin/python3"
)

val resolvedPython3: String by lazy {
	for (candidate in python3Candidates) {
		val check = try {
			ProcessBuilder(candidate, "-c", "from recipe_scrapers import scrape_html; print('ok')")
				.redirectErrorStream(true)
				.start()
		} catch (_: Exception) {
			continue
		}
		val out = check.inputStream.bufferedReader().readText().trim()
		if (check.waitFor() == 0 && out == "ok") {
			println("Using Python: $candidate")
			return@lazy candidate
		}
	}
	error("No python3 with recipe-scrapers found. Install with: python3 -m pip install recipe-scrapers")
}

fun findPython3WithRecipeScrapers(): String = resolvedPython3

fun scrapeRecipeWithPython(url: String): RecipeData? {
	val py = """
	import json
	import re
	import sys
	import urllib.request
	from recipe_scrapers import scrape_html

	url = sys.argv[1]
	try:
		req = urllib.request.Request(url, headers={
			"User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36"
		})
		html = urllib.request.urlopen(req, timeout=30).read().decode("utf-8")
		html = re.sub(
			r'<script[^>]*type=["\x27]application/ld\+json["\x27][^>]*>\s*</script>',
			"",
			html,
		)
		s = scrape_html(html, org_url=url)

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
		title_missing = not title

		ingredient_groups = safe("ingredient_groups")
		ingredients = safe("ingredients", []) or []
		if ingredient_groups is None:
			ingredient_groups = [(None, ingredients)]
		else:
			# Normalize to list of (name, [ingredients]) and filter blanks.
			# If the recipe provides no grouped ingredients, fall back to the
			# flat ingredients list so we still consider it valid.
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
			if not normalized and ingredients:
				normalized = [(None, [str(x).strip() for x in ingredients if str(x).strip()])]
			ingredient_groups = normalized

		if title_missing and ingredient_groups:
			title = "Untitled Recipe"
			print("__SCRAPE_NOTE__:missing title, using fallback title")

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

		if not ingredient_groups:
			print("__SCRAPE_NO_DATA__:missing ingredients")
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

	val python3 = findPython3WithRecipeScrapers()
	val command = listOf(python3, "-c", py, url)
	val process = ProcessBuilder(command)
		.redirectErrorStream(true)
		.start()

	val output = process.inputStream.bufferedReader().readText().trim()
	val code = process.waitFor()
	if (code != 0) {
		if (output.contains("No module named 'recipe_scrapers'")) {
			error("Missing Python dependency: recipe-scrapers. Install with: python3 -m pip install recipe-scrapers")
		}
		println("SCRAPE FAILED [exit=$code] $url")
		println("  Reason: ${output.take(400)}")
	} else if (output.startsWith("__SCRAPE_ERROR__:")) {
		val reason = output.removePrefix("__SCRAPE_ERROR__:").take(400)
		println("SCRAPE ERROR $url")
		println("  Reason: $reason")
	} else if (output.startsWith("__SCRAPE_NO_DATA__")) {
		val reason = output.removePrefix("__SCRAPE_NO_DATA__:").take(400)
		println("SCRAPE NO DATA $url")
		println("  Reason: $reason")
	} else if (output.isBlank()) {
		println("SCRAPE BLANK $url")
		println("  Reason: Python returned no output")
	} else {
		return parseRecipe(output)
	}
	return null
}

fun extractRecipeLinksFromCollectionPage(pageUrl: String, timeoutSeconds: Long): List<String> {
	val pageData = fetchPageWithValidation(pageUrl, timeoutSeconds) ?: return emptyList()
	val baseHost = pageData.base.host?.removePrefix("www.") ?: return emptyList()
	val hrefRegex = Regex("""href\s*=\s*"([^\"]+)"""", RegexOption.IGNORE_CASE)
	val siteConfig = knownWebsites.values.find { URI(it.url).host?.removePrefix("www.") == baseHost }
	val linkPattern = siteConfig?.recipePattern ?: Regex("/recipe/", RegexOption.IGNORE_CASE)
	val seen = mutableSetOf<String>()
	val links = mutableListOf<String>()

	hrefRegex.findAll(pageData.body).forEach { match ->
		val raw = match.groupValues[1].trim()
		if (raw.isBlank() || raw.startsWith("#") || raw.startsWith("javascript:")) {
			return@forEach
		}

		val resolved = try {
			pageData.base.resolve(raw)
		} catch (_: Exception) {
			return@forEach
		}

		val resolvedHost = resolved.host?.removePrefix("www.") ?: return@forEach
		if (!resolvedHost.contains(baseHost)) return@forEach

		val path = resolved.path ?: return@forEach
		if (!linkPattern.containsMatchIn(path)) return@forEach

		val normalized = resolved.toString().substringBefore('#')
		if (seen.add(normalized)) {
			links += normalized
		}
	}

	return links
}

private data class PageData(val base: URI, val body: String)

private fun fetchPageWithValidation(pageUrl: String, timeoutSeconds: Long): PageData? {
	val (base, request) = resolvePageUri(pageUrl, timeoutSeconds) ?: return null

	val response = try {
		HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString())
	} catch (_: Exception) {
		return null
	}

	return if (response.statusCode() in 200..299) {
		PageData(base, response.body())
	} else {
		null
	}
}

private fun resolvePageUri(pageUrl: String, timeoutSeconds: Long): Pair<URI, HttpRequest>? {
	val base = try {
		URI(pageUrl)
	} catch (_: URISyntaxException) {
		return null
	}

	val request = HttpRequest.newBuilder()
		.uri(base)
		.timeout(java.time.Duration.ofSeconds(timeoutSeconds))
		.GET()
		.build()

	return base to request
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
		description TEXT,
		instructions TEXT,
		total_time INTEGER,
		prep_time INTEGER,
		cook_time INTEGER,
		yields VARCHAR(255),
		image_url VARCHAR(512),
		language VARCHAR(10) DEFAULT 'en',
		cuisine VARCHAR(255),
		meal_type VARCHAR(50),
		source_url TEXT UNIQUE,
		measurement_system VARCHAR(32),
		scraped_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
		created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
	);

	CREATE TABLE IF NOT EXISTS ingredient_groups (
		id SERIAL PRIMARY KEY,
		recipe_id INTEGER NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
		name VARCHAR(255),
		order_index INTEGER NOT NULL
	);

	CREATE TABLE IF NOT EXISTS ingredients (
		id SERIAL PRIMARY KEY,
		ingredient_group_id INTEGER NOT NULL REFERENCES ingredient_groups(id) ON DELETE CASCADE,
		ingredient VARCHAR(255),
		order_index INTEGER NOT NULL
	);

	CREATE TABLE IF NOT EXISTS instruction_steps (
		id SERIAL PRIMARY KEY,
		recipe_id INTEGER NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
		step TEXT,
		order_index INTEGER NOT NULL
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

fun saveRecipe(connection: Connection, recipe: RecipeData): Int? {
	if (isDuplicate(connection, recipe.sourceUrl)) return null
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
			meal_type,
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
	return recipeId
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

fun importJsonFilesToDb(config: Config, recipesDir: File) {
	val jsonFiles = recipesDir.walkTopDown()
		.filter { it.isFile && it.extension == "json" }
		.toList()
		.sortedBy { it.name }

	if (jsonFiles.isEmpty()) {
		println("No JSON files found under ${recipesDir.path}")
		return
	}

	println("Found ${jsonFiles.size} JSON files to import")

	openConnection(config).use { connection ->
		connection.autoCommit = false
		ensureSchema(connection)
		importFilesToConnection(connection, jsonFiles)
	}
}

fun importFilesToConnection(connection: Connection, jsonFiles: List<File>) {
	var imported = 0
	var duplicates = 0
	var errors = 0
	for (file in jsonFiles) {
		val outcome = processRecipeFile(connection, file)
		when (outcome) {
			ImportOutcome.SUCCESS -> {
				imported++
				if (imported % 100 == 0) println("Imported $imported so far...")
			}
			ImportOutcome.DUPLICATE -> duplicates++
			ImportOutcome.ERROR -> errors++
		}
	}
	println("Import complete. Imported=$imported Duplicates=$duplicates Errors=$errors")
}

private enum class ImportOutcome {
	SUCCESS, DUPLICATE, ERROR
}

private fun processRecipeFile(connection: Connection, file: File): ImportOutcome {
	val recipe = safeParseRecipeFile(file)
	return when {
		recipe == null -> ImportOutcome.ERROR
		saveRecipe(connection, recipe) != null -> ImportOutcome.SUCCESS
		else -> ImportOutcome.DUPLICATE
	}
}

private fun safeParseRecipeFile(file: File): RecipeData? {
	return try {
		parseRecipe(file.readText())
	} catch (e: IOException) {
		println("IMPORT ERROR ${file.path}: ${e.message?.take(200)}")
		null
	} catch (e: SerializationException) {
		println("IMPORT ERROR ${file.path}: ${e.message?.take(200)}")
		null
	}
}

val config = parseOptions(args)
val websiteUrl = normalizeWebsite(config.website)
val outputRoot = File(expandPath(config.outputDir)).apply { mkdirs() }
val recipesDir = File(outputRoot, "recipes").apply { mkdirs() }

if (config.mode == "json") {
	println("Import mode: reading JSON files from ${recipesDir.path} into database")
	importJsonFilesToDb(config, recipesDir)
} else {
println("Discovering URLs from $websiteUrl")
val discovered = extractUrls(config, websiteUrl)
println("Discovered ${discovered.size} candidate URLs")

// `discovered` URLs are the raw set returned by the discovery service. They may
// include listing pages, unrelated pages, duplicates, or pages from a different
// subdomain. `filterRecipeUrls` applies host and path filtering to keep only
// likely recipe pages for the configured website.
val filtered = filterRecipeUrls(discovered, websiteUrl, config.recipeUrlPattern)
println("Filtered to ${filtered.size} recipe URLs after host/pattern filtering")

val selectedAfterDbPrecheck = if (config.mode == "web" && config.precheckDb) {
	val existing = precheckExistingUrls(config, filtered)
	if (existing.isNotEmpty()) {
		println("Pre-check: skipping ${existing.size} URLs already in database")
	}
	val pending = filtered.filterNot { existing.contains(it) }
	if (config.maxUrls > 0) pending.take(config.maxUrls) else pending
} else {
	if (config.maxUrls > 0) filtered.take(config.maxUrls) else filtered
}

// At this point we have already filtered discovered URLs down to recipe candidates.
// If `selectedAfterDbPrecheck` is empty, it means either no URLs passed the host/pattern
// rules or the database precheck excluded all remaining candidates.
val queue = ArrayDeque(selectedAfterDbPrecheck)
val processed = mutableSetOf<String>()
println("Processing ${selectedAfterDbPrecheck.size} filtered recipe URLs")

val dbConnection: Connection? = if (config.mode == "web") {
	openConnection(config).also { conn ->
		conn.autoCommit = false
		ensureSchema(conn)
	}
} else {
	null
}

val scraped = mutableListOf<Pair<RecipeData, Int?>>()
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
			val dbId = dbConnection?.let { saveRecipe(it, recipe) }
			scraped += (recipe to dbId)

			val siteName = try {
				URI(recipe.sourceUrl).host?.removePrefix("www.")?.lowercase(Locale.getDefault())?.ifBlank { "unknown" }
			} catch (_: Exception) {
				null
			} ?: "unknown"
			val siteDir = File(recipesDir, siteName).apply { mkdirs() }
			if (dbId != null) {
				val name = "%05d-%s.json".format(dbId, slugify(recipe.title).take(100))
				val file = File(siteDir, name)
				file.writeText(json.encodeToString(JsonObject.serializer(), recipe.toJsonObject()))
				println("Saved [db=$dbId]: ${file.path}")
			} else if (dbConnection != null) {
				println("Duplicate in DB, skipped file: ${recipe.sourceUrl}")
			} else {
				val name = "%s.json".format(slugify(recipe.title).take(100))
				val file = File(siteDir, name)
				file.writeText(json.encodeToString(JsonObject.serializer(), recipe.toJsonObject()))
				println("Saved: ${file.path}")
			}

			if (config.maxUrls > 0 && scraped.size >= config.maxUrls) {
				println("Reached --max-urls limit (${config.maxUrls})")
				break
			}
		}
		if (config.sleepMillis > 0) Thread.sleep(config.sleepMillis)
	}
}

dbConnection?.close()

println("Successfully scraped ${scraped.size} recipes")

val alreadyInserted = scraped.count { (_, id) -> id != null }
val duplicates = scraped.size - alreadyInserted
println("Database import complete. Imported=$alreadyInserted Duplicates=$duplicates")
}
