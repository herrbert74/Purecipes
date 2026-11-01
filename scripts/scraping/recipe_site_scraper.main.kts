#!/usr/bin/env kotlin
@file:DependsOn("org.jetbrains.kotlinx:kotlinx-serialization-json:1.12.0")
@file:DependsOn("org.postgresql:postgresql:42.7.14")
@file:Import("ScrapedIngredientLines.kt")
@file:Import("ScrapedAppliances.kt")

// cache-bust: alt-or-incomplete-v27

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
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
	"natashasbaking" to SiteConfig(
		url = "https://natashasbaking.com",
		recipePattern = Regex(
			"^/(?!category/|tag/|author/|page/|feed/|comments/|wp-|xmlrpc|" +
				"shop/?$|cart/?$|checkout/?$|courses/?$|privacypolicy/?$|" +
				"terms-conditions/?$|sourdough-guide/?$|panettone/?$|" +
				"croissant-recipes-and-techniques/?$)[a-z0-9-]+/?$",
		),
	),
	"breadtopia" to SiteConfig(
		url = "https://breadtopia.com",
		recipePattern = Regex(
			"^/(?!store(?:/|$)|faq(?:/|$)|category/|tag/|author/|page/|feed/|" +
				"comments/|wp-|xmlrpc|blog/?$|contact/?$|about/?$|my-account(?:/|$)|" +
				"my-lists(?:/|$)|track-order/?$|wishlist(?:/|$)|starter/?$|" +
				"regenerative-agriculture/?$|kudos/?$|breadtopia-rewards-program/?$|" +
				"how-to-bake-bread/?$|masienda/?$|check-my-gift-certificate/?$|" +
				"sdbook/?$|real-bread-bakers/?$|newsletter-signup/?$|wholesale/?$|" +
				"gluten-free/?$|live-sourdough-starter/?$|clay-baker/?$)[a-z0-9-]+/?$",
		),
	),
	"greatbritishrecipes" to SiteConfig(
		url = "https://greatbritishrecipes.com",
		recipePattern = Regex(
			"^/(?!recipes/?$|about-great-british-recipes/?$|subscribe/?$|" +
				"suscribe/?$|privacy-policy(?:-2)?/?$|disclosure/?$|contact/?$|" +
				"feed/|comments/|wp-|xmlrpc|category/|tag/|author/|page/)" +
				"(?![a-z0-9-]+-recipes/?$)[a-z0-9-]+/?$",
		),
	),
	"bbcgoodfood" to SiteConfig(
		url = "https://www.bbcgoodfood.com",
		recipePattern = Regex("^/recipes/(?!collection/|category/)[^/]+/?$"),
	),
	"jamieoliver" to SiteConfig(
		url = "https://www.jamieoliver.com/recipes/all",
		recipePattern = Regex(
			"^/recipes/(?!all/?$|new/?$|books/|course/|dishtype/|occasion/|" +
				"special-diets/|family-favourites/|tv/|world/|ingredients?/)" +
				"[^/]+/[^/]+/?$",
		),
	),
	"maryberry" to SiteConfig(
		url = "https://www.maryberry.co.uk/recipes",
		recipePattern = Regex("^/recipes/recipe/[^/]+/?$"),
	),
	"paulhollywood" to SiteConfig(
		url = "https://www.paulhollywood.com/recipes-1",
		recipePattern = Regex("^/post/[^/]+/?$"),
	),
	"prueleith" to SiteConfig(
		url = "https://prue-leith.com/cooking",
		recipePattern = Regex(
			"^/(?!cooking(?:/|$)|tv-show(?:/|$)|home-garden(?:/|$)|fashion(?:/|$)|" +
				"travel(?:/|$)|trueprue(?:/|$)|books(?:/|$)|products(?:/|$)|" +
				"search(?:/|$)|terms-and-conditions/?$|privacy-policy/?$|contact/?$|press/?$|" +
				"uncategorized(?:/|$)|feed/|comments/|wp-|xmlrpc|category/|tag/|" +
				"author/|page/)[a-z0-9-]+/?$",
		),
	),
)

val json = Json {
	prettyPrint = true
	ignoreUnknownKeys = true
}

data class RecipeData(
	val title: String,
	val ingredientGroups: List<Pair<String?, List<ProcessedScrapedIngredient>>>,
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
	val sourceUrl: String,
	val appliances: List<String> = emptyList(),
) {

	fun toJsonObject(): JsonObject = buildRecipeJsonObject(this)
}

private fun buildIngredientGroupsJson(
	ingredientGroups: List<Pair<String?, List<ProcessedScrapedIngredient>>>,
): JsonArray = buildJsonArray {
	ingredientGroups.forEach { (groupName, items) ->
		add(
			buildJsonObject {
				put("name", JsonPrimitive(groupName ?: ""))
				put("ingredients", buildIngredientItemsJson(items))
			},
		)
	}
}

private fun buildIngredientItemsJson(items: List<ProcessedScrapedIngredient>): JsonArray =
	buildJsonArray {
		items.forEach { ingredient ->
			add(
				buildJsonObject {
					put("text", JsonPrimitive(ingredient.text))
					put("requirement", JsonPrimitive(ingredient.requirement))
					ingredient.alternativeGroupKey?.let { groupKey ->
						put("alternativeGroupKey", JsonPrimitive(groupKey))
					}
				},
			)
		}
	}

private fun buildRecipeJsonObject(recipe: RecipeData): JsonObject {
	val ingredientGroupsArray = buildIngredientGroupsJson(recipe.ingredientGroups)
	val instructionListArray = buildJsonArray {
		recipe.instructionList.forEach { add(JsonPrimitive(it)) }
	}
	val nutrientsObject = buildJsonObject {
		recipe.nutrients.forEach { (k, v) -> put(k, if (v == null) JsonPrimitive("") else JsonPrimitive(v)) }
	}
	val linksObject = buildJsonObject {
		recipe.links.forEach { (k, v) -> put(k, if (v == null) JsonPrimitive("") else JsonPrimitive(v)) }
	}

	return buildJsonObject {
		put("title", JsonPrimitive(recipe.title))
		put("ingredient_groups", ingredientGroupsArray)
		put("instruction_list", instructionListArray)
		put("instructions", JsonPrimitive(recipe.instructions))
		putNullableInt("total_time", recipe.totalTime)
		putNullableInt("prep_time", recipe.prepTime)
		putNullableInt("cook_time", recipe.cookTime)
		put("yields", JsonPrimitive(recipe.yields ?: ""))
		put("image", JsonPrimitive(recipe.image ?: ""))
		put("nutrients", nutrientsObject)
		put("language", JsonPrimitive(recipe.language ?: "en"))
		put("cuisine", JsonPrimitive(recipe.cuisine ?: ""))
		put("category", JsonPrimitive(recipe.category ?: ""))
		put("links", linksObject)
		put("source_url", JsonPrimitive(recipe.sourceUrl))
		put(
			"appliances",
			buildJsonArray {
				recipe.appliances.forEach { add(JsonPrimitive(it)) }
			},
		)
	}
}

private fun JsonObjectBuilder.putNullableInt(key: String, value: Int?) {
	if (value == null) {
		put(key, JsonPrimitive(""))
	} else {
		put(key, JsonPrimitive(value))
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
	val calculateNutrition: Boolean,
	val upsertExisting: Boolean = false,
	val debug: Boolean,
)

fun printUsageAndExit(message: String? = null): Nothing {
	if (!message.isNullOrBlank()) {
		System.err.println("Error: $message")
	}
	System.err.println(
		"""
	Usage:
		kotlin scripts/scraping/recipe_site_scraper.main.kts <website> <output_dir> [options]

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
		--calculate-nutrition true|false          Default: true (postgres import modes)
		--upsert-existing true|false              Default: true in json mode, false in web mode

	Modes:
		web   Scrape recipes from the web, save JSON files with DB IDs, and insert into Postgres.
		json  Read previously saved JSON files from <output_dir>/ and insert/upsert them into Postgres.

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
	"--precheck-db",
	"--calculate-nutrition",
	"--upsert-existing",
)

fun parseOptions(scriptArgs: Array<String>): Config {
	if (scriptArgs.size < 2) printUsageAndExit("Missing required arguments: <website> <output_dir>")
	return buildConfig(
		website = scriptArgs[0],
		outputDir = scriptArgs[1],
		options = parseFlagArguments(scriptArgs.drop(2).toTypedArray()),
	)
}

private fun buildConfig(website: String, outputDir: String, options: Map<String, String>): Config {
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
		precheckDb = boolOptions.getValue("precheck"),
		calculateNutrition = boolOptions.getValue("calculateNutrition"),
		upsertExisting = boolOptions.getValue("upsertExisting"),
		debug = boolOptions.getValue("debug"),
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
	boolOpts["calculateNutrition"] =
		(options["--calculate-nutrition"] ?: "true").equals("true", ignoreCase = true)
	val defaultUpsert = if ((options["--mode"] ?: "web") == "json") "true" else "false"
	boolOpts["upsertExisting"] =
		(options["--upsert-existing"] ?: defaultUpsert).equals("true", ignoreCase = true)
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
	from urllib.parse import urljoin, urlparse
	from bs4 import BeautifulSoup
	from recipe_scrapers import scrape_html

	def parse_minutes(text):
		if not text:
			return None
		t = text.lower()
		hm = re.search(r'(\d+)\s*(?:hours?|hrs?|h\b)', t)
		mm = re.search(r'(\d+)\s*(?:minutes?|mins?|m\b)', t)
		total = (int(hm.group(1)) * 60 if hm else 0) + (int(mm.group(1)) if mm else 0)
		return total if total > 0 else None

	def fallback_scrape_dom(html, url):
		soup = BeautifulSoup(html, "html.parser")
		host = urlparse(url).netloc.replace("www.", "").lower()
		h1 = soup.find("h1")
		title = h1.get_text(" ", strip=True) if h1 else ""
		og_img = soup.find("meta", property="og:image")
		image = urljoin(url, og_img["content"].strip()) if og_img and og_img.get("content") else None
		yields = None
		prep_time = None
		cook_time = None
		category = None
		ingredient_groups = []
		instruction_list = []

		if "maryberry.co.uk" in host:
			im_div = soup.select_one(".RecipeOverviewGrid .ImCellIm")
			if im_div and im_div.get("style"):
				m = re.search(r"url\(['\"]?([^'\")]+)['\"]?\)", im_div["style"])
				if m:
					image = urljoin(url, m.group(1))
			active_cats = [
				a.get_text(" ", strip=True)
				for a in soup.select(".CourseList a.Active")
				if a.get_text(strip=True)
			]
			if active_cats:
				category = ", ".join(active_cats)
			for label in soup.select(".RecipeMeta .Label"):
				txt = label.get_text(" ", strip=True)
				if txt.lower().startswith("serves:"):
					yields = txt.split(":", 1)[1].strip() or txt
				elif txt.lower().startswith("cook time:"):
					cook_time = parse_minutes(txt)
				elif txt.lower().startswith("prep time:"):
					prep_time = parse_minutes(txt)
			ul = soup.select_one("ul.IngredientsList")
			if ul:
				cur_name, cur_items = None, []
				for li in ul.find_all("li", recursive=False):
					if "Heading" in (li.get("class") or []):
						if cur_items:
							ingredient_groups.append((cur_name, cur_items))
							cur_items = []
						cur_name = li.get_text(" ", strip=True)
					else:
						val = li.get_text(" ", strip=True)
						if val:
							cur_items.append(val)
				if cur_items:
					ingredient_groups.append((cur_name, cur_items))
			for li in soup.select(".NumberedList li.Item"):
				divs = li.find_all("div", recursive=False)
				step_txt = divs[-1].get_text(" ", strip=True) if divs else li.get_text(" ", strip=True)
				if step_txt:
					instruction_list.append(step_txt)
		elif "paulhollywood.com" in host:
			article = soup.find("article") or soup.find("main") or soup
			ing_has_li = False
			check_ing = False
			for check_el in article.find_all(["h2", "h3", "li"]):
				c_low = check_el.get_text(" ", strip=True).lower()
				if check_el.name in ("h2", "h3"):
					if c_low == "ingredients":
						check_ing = True
						continue
					if c_low in ("method", "instructions") and check_ing:
						break
				elif check_ing and check_el.name == "li":
					ing_has_li = True
					break
			mode, cur_name, cur_items = None, None, []
			for el in article.find_all(["h2", "h3", "h4", "p", "li"]):
				txt = el.get_text(" ", strip=True).lstrip("\ufeff").strip()
				if not txt:
					continue
				low = txt.lower()
				if el.name in ("h2", "h3"):
					if low == "ingredients":
						mode = "ing"
						continue
					if low in ("method", "instructions"):
						if cur_items:
							ingredient_groups.append((cur_name, cur_items))
							cur_items = []
						mode = "inst"
						continue
					if mode is not None:
						break
				elif mode is None and el.name in ("h4", "p") and len(txt) < 40:
					if any(k in low for k in ("serves", "makes", "slices", "rolls", "buns", "loaf", "loaves", "portions")):
						yields = txt
				elif mode == "ing":
					if low.startswith("tags:") or low.startswith("taken from") or low.startswith("for more of paul"):
						if cur_items:
							ingredient_groups.append((cur_name, cur_items))
							cur_items = []
						mode = None
						continue
					if ing_has_li:
						if el.name in ("h4", "p") and el.find_parent("li") is None:
							if cur_items:
								ingredient_groups.append((cur_name, cur_items))
								cur_items = []
							cur_name = txt
						elif el.name == "li":
							cur_items.append(txt)
					else:
						if el.name == "h4":
							if cur_items:
								ingredient_groups.append((cur_name, cur_items))
								cur_items = []
							cur_name = txt
						elif el.name in ("p", "li") and not (el.name == "p" and el.find_parent("li") is not None):
							cur_items.append(txt)
				elif mode == "inst":
					if any(low.startswith(p) for p in ("taken from", "photograph", "tags:", "find more recipes", "for more of paul")):
						mode = None
						continue
					if el.name == "li":
						if not any(low == tag for tag in ("cakes", "pies", "bread", "breads", "baking", "biscuits", "desserts", "pastry", "christmas recipes", "paul hollywood", "paul hollywood recipes")):
							instruction_list.append(txt)
					elif el.name == "p" and el.find_parent("li") is None:
						instruction_list.append(txt)
			if cur_items:
				ingredient_groups.append((cur_name, cur_items))
		elif "prue-leith.com" in host:
			article = soup.find("article") or soup.find("main")
			if article:
				cat_el = article.select_one(".catergory, .category")
				if cat_el:
					category = cat_el.get_text(" ", strip=True)
				if category and any(non_food in category.lower() for non_food in ["fashion", "travel", "home & garden", "#trueprue", "books", "trueprue"]):
					article = None
			if article:
				for p in article.find_all(["p", "h3", "h4", "div"]):
					if p.find_parent(["ul", "ol"]):
						continue
					txt = p.get_text(" ", strip=True)
					low = txt.lower()
					if "prep time" in low and not prep_time:
						prep_time = parse_minutes(txt.split("cook time")[0] if "cook time" in low else txt)
					if "cook time" in low and not cook_time:
						cook_time = parse_minutes(txt[txt.find("cook time"):])
					if (low.startswith("serves") or low.startswith("makes") or "serves:" in low or "makes:" in low) and not yields:
						if len(txt) < 60:
							yields = txt

				def is_quantity_line(t):
					s = t.strip()
					return bool(s and re.match(r"^(?:\d|[¼½¾⅓⅔⅛⅜⅝⅞]|pinch|large pinch|small pinch|handful|few|a few|juice|zest|a sprig|sprig|silver charms)", s, re.I))

				raw_elements = [el for el in article.find_all(["h2", "h3", "h4", "h5", "h6", "p", "ul", "ol"]) if el.find_parent(["ul", "ol"]) is None]
				elements = []
				for el in raw_elements:
					txt = el.get_text(" ", strip=True)
					if txt:
						elements.append((el, txt, txt.lower().rstrip(":")))

				method_idx = None
				for idx, (el, txt, low) in enumerate(elements):
					if low in ["method", "instructions", "directions", "to make"] or low.startswith("method") or low.startswith("instructions"):
						method_idx = idx
						break
					if el.name == "ol":
						method_idx = idx
						break

				if method_idx is None:
					found_ings = False
					for idx, (el, txt, low) in enumerate(elements):
						if is_quantity_line(txt):
							found_ings = True
						elif found_ings and el.name == "p" and len(txt) > 35 and not is_quantity_line(txt):
							method_idx = idx
							break

				if method_idx is not None:
					ing_start_idx = None
					for idx in range(method_idx):
						el, txt, low = elements[idx]
						if low in ["ingredients"] or low.startswith("ingredients") or re.match(r"^for\s+(?:the\s+)?[a-z\s]+$", low):
							ing_start_idx = idx
							break
						if el.name == "ul":
							ing_start_idx = idx
							break
						if el.name == "p" and is_quantity_line(txt):
							ing_start_idx = idx
							break

					if ing_start_idx is not None:
						cur_name, cur_items = None, []
						for idx in range(ing_start_idx, method_idx):
							el, txt, low = elements[idx]
							if low in ["ingredients"] or low.startswith("ingredients"):
								continue
							if low.startswith("for ") or re.match(r"^(?:for\s+[a-z\s]+|to\s+serve|topping|filling|base|icing|cake|dough):?$", low):
								if cur_items:
									ingredient_groups.append((cur_name, cur_items))
									cur_items = []
								cur_name = txt.rstrip(":")
								continue
							if el.name == "ul":
								items = [li.get_text(" ", strip=True) for li in el.find_all("li", recursive=False) if li.get_text(strip=True)]
								cur_items.extend(items)
							elif el.name == "p":
								p_lines = [line.strip() for line in el.get_text("\n", strip=True).splitlines() if line.strip()]
								for p_line in p_lines:
									is_subgroup = (el.find(["strong", "b", "em", "h4", "h5"]) and len(p_line) < 40) or (len(p_line) < 30 and not is_quantity_line(p_line))
									if is_subgroup and not is_quantity_line(p_line):
										if cur_items:
											ingredient_groups.append((cur_name, cur_items))
											cur_items = []
										cur_name = p_line.rstrip(":")
									else:
										cur_items.append(p_line)
						if cur_items:
							ingredient_groups.append((cur_name, cur_items))

						for idx in range(method_idx, len(elements)):
							el, txt, low = elements[idx]
							if low in ["method", "instructions", "directions", "to make"] or low.startswith("method") or low.startswith("instructions"):
								continue
							if any(low.startswith(p) for p in ["tip:", "tips:", "taken from", "tags:", "photograph", "this cake will keep", "you might also like"]):
								break
							if el.name in ["ol", "ul"]:
								steps = [li.get_text(" ", strip=True) for li in el.find_all("li", recursive=False) if li.get_text(strip=True)]
								instruction_list.extend(steps)
							elif el.name == "p":
								if len(txt) > 20 and not (el.find("strong") and len(txt) < 40):
									instruction_list.append(txt)

		if not ingredient_groups:
			return None
		total_time = ((prep_time or 0) + (cook_time or 0)) or None
		return {
			"title": title or "Untitled Recipe",
			"ingredient_groups": [
				{"name": (n or ""), "ingredients": lst}
				for (n, lst) in ingredient_groups
			],
			"instruction_list": instruction_list,
			"instructions": "\n".join(instruction_list).strip(),
			"total_time": total_time,
			"prep_time": prep_time,
			"cook_time": cook_time,
			"yields": yields,
			"image": image,
			"nutrients": {},
			"language": "en",
			"cuisine": None,
			"category": category,
			"links": {"canonical": url},
			"source_url": url
		}

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
		try:
			s = scrape_html(html, org_url=url, supported_only=False)
		except Exception:
			fallback_payload = fallback_scrape_dom(html, url)
			if fallback_payload is not None:
				print(json.dumps(fallback_payload, ensure_ascii=False))
				raise SystemExit(0)
			host_check = urlparse(url).netloc.replace("www.", "").lower()
			if any(known in host_check for known in ("maryberry.co.uk", "paulhollywood.com", "prue-leith.com")):
				print("__SCRAPE_NO_DATA__:not a recipe page or missing recipe details")
				raise SystemExit(0)
			raise

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

		def extract_jamie_oliver_groups(soup):
			ing_div = soup.select_one(".ingredients-rich-text")
			if not ing_div:
				return None
			groups, cur_name, cur_items = [], None, []
			for p in ing_div.find_all("p"):
				txt = p.get_text(" ", strip=True)
				if not txt:
					continue
				strong = p.find("strong")
				is_heading = (
					"type-h5" in p.get("class", [])
					or (strong and strong.get_text(strip=True) == txt)
					or (txt.isupper() and not any(c.isdigit() for c in txt))
				)
				if is_heading:
					if cur_items or cur_name:
						groups.append((cur_name, cur_items))
						cur_items = []
					cur_name = txt
				else:
					cur_items.append(txt)
			if cur_items or cur_name:
				groups.append((cur_name, cur_items))
			return groups if len(groups) > 1 or (len(groups) == 1 and groups[0][0]) else None

		ingredient_groups = safe("ingredient_groups")
		if "jamieoliver.com" in url.lower():
			page_soup = getattr(s, "soup", None) or BeautifulSoup(html, "html.parser")
			jo_groups = extract_jamie_oliver_groups(page_soup)
			if jo_groups:
				ingredient_groups = jo_groups
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
					elif hasattr(g, "purpose") and hasattr(g, "ingredients"):
						name = g.purpose
						items = g.ingredients
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
	val pageData = fetchPageWithValidation(pageUrl, timeoutSeconds)
	val baseHost = pageData?.base?.host?.removePrefix("www.")
	if (pageData == null || baseHost == null) {
		return emptyList()
	}
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
	val pageUri = resolvePageUri(pageUrl, timeoutSeconds) ?: return null
	val response = try {
		HttpClient.newHttpClient().send(pageUri.second, HttpResponse.BodyHandlers.ofString())
	} catch (_: Exception) {
		null
	}
	return if (response != null && response.statusCode() in 200..299) {
		PageData(pageUri.first, response.body())
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

fun clampIngredientForDatabase(ingredient: String, sourceUrl: String): String {
	if (ingredient.length <= MAX_INGREDIENT_LENGTH) {
		return ingredient
	}
	println(
		"WARNING: ingredient exceeds $MAX_INGREDIENT_LENGTH characters for $sourceUrl; truncating: " +
			ingredient.take(80) + "…"
	)
	return ingredient.take(MAX_INGREDIENT_LENGTH)
}

fun parseStoredIngredientJson(ingredientEl: JsonElement): ProcessedScrapedIngredient? =
	when (ingredientEl) {
		is JsonPrimitive -> ingredientEl.contentOrNull?.trim()?.takeIf(String::isNotBlank)?.let {
			ProcessedScrapedIngredient(text = it)
		}

		is JsonObject -> {
			val text = ingredientEl["text"]?.jsonPrimitive?.contentOrNull?.trim().orEmpty()
			if (text.isBlank()) {
				null
			} else {
				val requirement = ingredientEl["requirement"]?.jsonPrimitive?.contentOrNull
					?.trim()
					?.uppercase(Locale.ROOT)
					?.takeIf { it.isNotBlank() }
					?: "REQUIRED"
				val alternativeGroupKey = ingredientEl["alternativeGroupKey"]?.jsonPrimitive?.contentOrNull
					?.toIntOrNull()
				ProcessedScrapedIngredient(
					text = text,
					requirement = requirement,
					alternativeGroupKey = alternativeGroupKey,
				)
			}
		}

		else -> null
	}

fun parseRecipe(rawJson: String): RecipeData? {
	val root = runCatching { json.parseToJsonElement(rawJson).jsonObject }.getOrNull() ?: return null
	val title = root["title"]?.jsonPrimitive?.contentOrNull?.trim().orEmpty()
	val ingredientGroups = parseIngredientGroupsFromRoot(root)
	val instructionList = parseInstructionListFromRoot(root)
	val nutrients = parseNutrientsFromRoot(root)
	val links = parseLinksFromRoot(root)
	val instructionsText = root["instructions"]?.jsonPrimitive?.contentOrNull.orEmpty().ifBlank {
		instructionList.joinToString("\n").trim()
	}
	val jsonAppliances = root["appliances"]?.jsonArray?.mapNotNull { it.jsonPrimitive.contentOrNull }
	val category = root["category"]?.jsonPrimitive?.contentOrNull
	val categoryColumns = mapCategoryToColumns(category)
	val appliances = if (!jsonAppliances.isNullOrEmpty()) {
		jsonAppliances
	} else {
		extractScrapedAppliances(
			instructionList = instructionList,
			instructions = instructionsText,
			title = title,
			description = root["description"]?.jsonPrimitive?.contentOrNull,
			cookingMethod = categoryColumns.cookingMethod,
		)
	}

	return if (title.isBlank() || ingredientGroups.isEmpty()) {
		null
	} else {
		RecipeData(
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
			category = category,
			links = links,
			sourceUrl = root["source_url"]?.jsonPrimitive?.contentOrNull.orEmpty(),
			appliances = appliances,
		)
	}
}

private fun parseIngredientGroupsFromRoot(
	root: JsonObject,
): List<Pair<String?, List<ProcessedScrapedIngredient>>> =
	root["ingredient_groups"]?.jsonArray
		?.mapNotNull(::parseStoredIngredientGroupJson)
		?.flatMap { (name, items) -> processStoredScrapedIngredientGroups(name, items) }
		?.filter { (_, items) -> items.isNotEmpty() }
		?: emptyList()

private fun parseStoredIngredientGroupJson(
	groupEl: JsonElement,
): Pair<String?, List<ProcessedScrapedIngredient>>? =
	when (groupEl) {
		is JsonObject -> parseIngredientGroupObject(groupEl)
		is JsonArray -> parseIngredientGroupArray(groupEl)
		else -> null
	}

private fun parseIngredientGroupObject(
	groupObject: JsonObject,
): Pair<String?, List<ProcessedScrapedIngredient>>? {
	val name = groupObject["name"]?.jsonPrimitive?.contentOrNull?.trim().orEmpty().ifBlank { null }
	val items = groupObject["ingredients"]?.jsonArray
		?.mapNotNull(::parseStoredIngredientJson)
		?: emptyList()
	return if (items.isEmpty()) null else (name to items)
}

private fun parseIngredientGroupArray(
	groupArray: JsonArray,
): Pair<String?, List<ProcessedScrapedIngredient>>? {
	val name = groupArray.getOrNull(0)?.jsonPrimitive?.contentOrNull?.trim().orEmpty().ifBlank { null }
	val items = groupArray.getOrNull(1)?.jsonArray
		?.mapNotNull(::parseStoredIngredientJson)
		?: emptyList()
	return if (items.isEmpty()) null else (name to items)
}

private fun parseInstructionListFromRoot(root: JsonObject): List<String> =
	root["instruction_list"]?.jsonArray
		?.mapNotNull { it.jsonPrimitive.contentOrNull?.trim() }
		?.filter { it.isNotBlank() }
		?: emptyList()

private fun parseNutrientsFromRoot(root: JsonObject): Map<String, String?> {
	val nutrientsObject = root["nutrients"]?.jsonObject ?: JsonObject(emptyMap())
	return normalizeScrapedNutrients(
		nutrientsObject.mapNotNull { (key, value) ->
			parseNutrientJsonValue(value)?.let { key to it }
		}.toMap(),
	)
}

private fun parseLinksFromRoot(root: JsonObject): Map<String, String?> {
	val linksObject = root["links"]?.jsonObject ?: JsonObject(emptyMap())
	return linksObject.mapValues { (_, value) -> value.jsonPrimitive.contentOrNull }
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
		meal_type TEXT,
		difficulty VARCHAR(20),
		cooking_method VARCHAR(50),
		calorie_range VARCHAR(20),
		dietary_preferences TEXT[],
		tags TEXT[],
		source_url TEXT UNIQUE,
		measurement_system VARCHAR(32),
		is_private BOOLEAN NOT NULL DEFAULT FALSE,
		scraped_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
		created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
		catalog_rank INTEGER,
		appliances TEXT[]
	);

	ALTER TABLE recipes ADD COLUMN IF NOT EXISTS catalog_rank INTEGER;
	ALTER TABLE recipes ADD COLUMN IF NOT EXISTS appliances TEXT[];

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
		order_index INTEGER NOT NULL,
		requirement VARCHAR(20) NOT NULL DEFAULT 'REQUIRED',
		alternative_group_key INTEGER
	);

	ALTER TABLE ingredients ADD COLUMN IF NOT EXISTS requirement VARCHAR(20) NOT NULL DEFAULT 'REQUIRED';
	ALTER TABLE ingredients ADD COLUMN IF NOT EXISTS alternative_group_key INTEGER;

	CREATE TABLE IF NOT EXISTS instruction_steps (
		id SERIAL PRIMARY KEY,
		recipe_id INTEGER NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
		step TEXT,
		order_index INTEGER NOT NULL
	);

	CREATE TABLE IF NOT EXISTS nutrition_foods (
		id SERIAL PRIMARY KEY,
		source_name VARCHAR(32) NOT NULL,
		source_id TEXT NOT NULL,
		display_name TEXT NOT NULL,
		normalized_name TEXT NOT NULL,
		calories_per_100g DECIMAL(10,2),
		protein_per_100g DECIMAL(10,2),
		carbohydrates_per_100g DECIMAL(10,2),
		fat_per_100g DECIMAL(10,2),
		fiber_per_100g DECIMAL(10,2),
		sugar_per_100g DECIMAL(10,2),
		sodium_per_100g DECIMAL(10,2),
		source_metadata TEXT,
		updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
		UNIQUE (source_name, source_id)
	);

	CREATE INDEX IF NOT EXISTS idx_nutrition_foods_normalized_name
	ON nutrition_foods (normalized_name);

	CREATE TABLE IF NOT EXISTS nutrition_food_aliases (
		id SERIAL PRIMARY KEY,
		food_id INTEGER NOT NULL REFERENCES nutrition_foods(id) ON DELETE CASCADE,
		alias TEXT NOT NULL,
		normalized_alias TEXT NOT NULL,
		UNIQUE (normalized_alias)
	);

	CREATE TABLE IF NOT EXISTS nutrition_food_measures (
		id SERIAL PRIMARY KEY,
		food_id INTEGER NOT NULL REFERENCES nutrition_foods(id) ON DELETE CASCADE,
		measure_name VARCHAR(32) NOT NULL,
		grams_per_measure DECIMAL(12,4) NOT NULL,
		UNIQUE (food_id, measure_name)
	);

	CREATE TABLE IF NOT EXISTS ingredient_measurements (
		ingredient_id INTEGER PRIMARY KEY REFERENCES ingredients(id) ON DELETE CASCADE,
		raw_text TEXT NOT NULL,
		quantity DECIMAL(12,4),
		unit VARCHAR(32),
		parsed_name VARCHAR(255),
		is_measurable BOOLEAN NOT NULL DEFAULT FALSE
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

	ALTER TABLE nutrition ADD COLUMN IF NOT EXISTS matched_ingredient_count INTEGER;
	ALTER TABLE nutrition ADD COLUMN IF NOT EXISTS total_ingredient_count INTEGER;
	ALTER TABLE nutrition ADD COLUMN IF NOT EXISTS calculation_source VARCHAR(32);
	ALTER TABLE nutrition ADD COLUMN IF NOT EXISTS confidence VARCHAR(32);
	ALTER TABLE nutrition ADD COLUMN IF NOT EXISTS is_complete BOOLEAN;
	ALTER TABLE nutrition ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
	ALTER TABLE nutrition ADD COLUMN IF NOT EXISTS total_weight_grams DECIMAL(12,4);
	ALTER TABLE nutrition ADD COLUMN IF NOT EXISTS serving_count DECIMAL(8,2);

	CREATE TABLE IF NOT EXISTS ingredient_nutrition_contributions (
		ingredient_id INTEGER PRIMARY KEY REFERENCES ingredients(id) ON DELETE CASCADE,
		grams_resolved DECIMAL(12,4),
		grams_source VARCHAR(32),
		calories DECIMAL(10,2),
		protein DECIMAL(10,2),
		carbohydrates DECIMAL(10,2),
		fat DECIMAL(10,2),
		fiber DECIMAL(10,2),
		sugar DECIMAL(10,2),
		sodium DECIMAL(10,2),
		override_calories DECIMAL(10,2),
		override_protein DECIMAL(10,2),
		override_carbohydrates DECIMAL(10,2),
		override_fat DECIMAL(10,2),
		override_fiber DECIMAL(10,2),
		override_sugar DECIMAL(10,2),
		override_sodium DECIMAL(10,2),
		uses_user_override BOOLEAN NOT NULL DEFAULT FALSE,
		updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
	);

	ALTER TABLE ingredient_nutrition_contributions ADD COLUMN IF NOT EXISTS grams_source VARCHAR(32);

	CREATE TABLE IF NOT EXISTS ingredient_nutrition_matches (
		id SERIAL PRIMARY KEY,
		ingredient_id INTEGER NOT NULL UNIQUE REFERENCES ingredients(id) ON DELETE CASCADE,
		raw_text TEXT NOT NULL,
		quantity DECIMAL(12,4),
		unit VARCHAR(32),
		parsed_name VARCHAR(255),
		food_id INTEGER REFERENCES nutrition_foods(id) ON DELETE SET NULL,
		confidence DECIMAL(5,4),
		match_source VARCHAR(32),
		updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
	);

	ALTER TABLE recipes ADD COLUMN IF NOT EXISTS meal_type TEXT;
	ALTER TABLE recipes ALTER COLUMN meal_type TYPE TEXT;
	ALTER TABLE recipes ADD COLUMN IF NOT EXISTS difficulty VARCHAR(20);
	ALTER TABLE recipes ADD COLUMN IF NOT EXISTS cooking_method VARCHAR(50);
	ALTER TABLE recipes ADD COLUMN IF NOT EXISTS calorie_range VARCHAR(20);
	ALTER TABLE recipes ADD COLUMN IF NOT EXISTS dietary_preferences TEXT[];
	ALTER TABLE recipes ADD COLUMN IF NOT EXISTS tags TEXT[];
	ALTER TABLE recipes ADD COLUMN IF NOT EXISTS is_private BOOLEAN NOT NULL DEFAULT FALSE;

	CREATE TABLE IF NOT EXISTS home_collections (
		id SERIAL PRIMARY KEY,
		slug VARCHAR(64) NOT NULL UNIQUE,
		title TEXT NOT NULL,
		source VARCHAR(16) NOT NULL,
		filter_payload TEXT,
		sort VARCHAR(16) NOT NULL DEFAULT 'catalog',
		position INTEGER NOT NULL,
		enabled BOOLEAN NOT NULL DEFAULT TRUE,
		starts_at TIMESTAMP,
		ends_at TIMESTAMP
	);

	CREATE INDEX IF NOT EXISTS idx_home_collections_position
	ON home_collections (enabled, position, id);

	CREATE TABLE IF NOT EXISTS home_collection_recipes (
		collection_id INTEGER NOT NULL REFERENCES home_collections(id) ON DELETE CASCADE,
		recipe_id INTEGER NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
		position INTEGER NOT NULL,
		PRIMARY KEY (collection_id, recipe_id)
	);

	CREATE INDEX IF NOT EXISTS idx_home_collection_recipes_order
	ON home_collection_recipes (collection_id, position, recipe_id);
	""".trimIndent()

	connection.createStatement().use { it.execute(sql) }
	connection.commit()
}

fun findRecipeIdBySourceUrl(connection: Connection, sourceUrl: String): Int? {
	connection.prepareStatement("SELECT id FROM recipes WHERE source_url = ?").use { ps ->
		ps.setString(1, sourceUrl)
		ps.executeQuery().use { rs ->
			return if (rs.next()) rs.getInt(1) else null
		}
	}
}

fun isDuplicate(connection: Connection, sourceUrl: String): Boolean =
	findRecipeIdBySourceUrl(connection, sourceUrl) != null

fun parseNumber(value: String?): Double? {
	if (value.isNullOrBlank()) return null
	val match = Regex("""-?\d+(?:\.\d+)?""").find(value)
	return match?.value?.toDoubleOrNull()
}

private val scrapedNutrientKeyAliases = mapOf(
	"calories" to listOf("calories", "energy", "calorie"),
	"protein" to listOf("protein", "proteinContent"),
	"carbohydrates" to listOf("carbohydrates", "carbs", "carbohydrateContent"),
	"fat" to listOf("fat", "fatContent"),
	"fiber" to listOf("fiber", "fiberContent"),
	"sugar" to listOf("sugar", "sugarContent"),
	"sodium" to listOf("sodium", "sodiumContent"),
)

fun parseNutrientJsonValue(element: JsonElement): String? =
	when (element) {
		is JsonPrimitive -> when {
			element.isString -> element.content.trim().takeIf { it.isNotBlank() }
			else -> element.content.toDoubleOrNull()?.toString()
		}

		else -> null
	}

fun normalizeScrapedNutrients(raw: Map<String, String?>): Map<String, String?> {
	val rawByLowerKey = raw.mapKeys { (key, _) -> key.lowercase(Locale.ROOT) }
	return scrapedNutrientKeyAliases.mapNotNull { (canonical, aliases) ->
		val value = aliases.firstNotNullOfOrNull { alias ->
			raw[alias]?.takeIf { it.isNotBlank() }
				?: rawByLowerKey[alias.lowercase(Locale.ROOT)]?.takeIf { it.isNotBlank() }
		}
		value?.let { canonical to it }
	}.toMap()
}

private data class ScrapedCategoryColumns(
	val mealType: String?,
	val difficulty: String?,
	val cookingMethod: String?,
	val dietaryPreferences: List<String>,
	val tags: List<String>,
	val unmatchedLabels: List<String>,
)

private fun splitCategoryLabels(category: String?): List<String> = category
	.orEmpty()
	.split(',', ';', '|', '/', '>')
	.asSequence()
	.map(String::trim)
	.filter(String::isNotBlank)
	.distinctBy(String::lowercase)
	.toList()

private fun normalizeCategoryLabel(label: String): String = label.lowercase()
	.replace("&", " and ")
	.replace(Regex("""[^a-z0-9+ -]"""), " ")
	.replace(Regex("""\s+"""), " ")
	.trim()

private fun matchesKeywords(token: String, keywords: List<String>): Boolean =
	keywords.any { keyword -> token.contains(keyword) }

private fun firstMappedValue(
	tokens: List<String>,
	mapping: List<Pair<String, List<String>>>,
): String? {
	for ((value, keywords) in mapping) {
		if (tokens.any { token -> matchesKeywords(token, keywords) }) {
			return value
		}
	}
	return null
}

private fun allMappedValues(
	tokens: List<String>,
	mapping: List<Pair<String, List<String>>>,
): List<String> {
	val results = mutableListOf<String>()
	for ((value, keywords) in mapping) {
		if (tokens.any { token -> matchesKeywords(token, keywords) }) {
			results += value
		}
	}
	return results.distinct()
}

private fun mapCategoryToColumns(category: String?): ScrapedCategoryColumns {
	val rawLabels = splitCategoryLabels(category)
	val tokens = rawLabels.map(::normalizeCategoryLabel)

	val mealTypeMapping = listOf(
		"BREAKFAST" to listOf("breakfast"),
		"BRUNCH" to listOf("brunch"),
		"LUNCH" to listOf("lunch"),
		"DINNER" to listOf("dinner", "main course", "main"),
		"SNACK" to listOf("snack"),
		"DESSERT" to listOf("dessert", "sweet", "pudding", "baking", "cake"),
		"APPETIZER" to listOf("appetizer", "starter", "small plate"),
		"DRINK" to listOf("drink", "cocktail", "smoothie", "juice", "beverage"),
		"SIDE_DISH" to listOf("side", "side dish"),
	)
	val difficultyMapping = listOf(
		"EASY" to listOf("easy", "simple", "quick", "beginner", "beginner friendly"),
		"MEDIUM" to listOf("medium", "intermediate"),
		"HARD" to listOf("hard", "advanced", "challenging", "expert"),
	)
	val cookingMethodMapping = listOf(
		"AIR_FRY" to listOf("air fry", "air fryer"),
		"STIR_FRY" to listOf("stir fry", "stir-fry", "wok"),
		"SLOW_COOK" to listOf("slow cook", "slow cooker", "crockpot"),
		"PRESSURE_COOK" to listOf("pressure cook", "pressure cooker", "instant pot"),
		"MICROWAVE" to listOf("microwave"),
		"SMOKE" to listOf("smoke", "smoked"),
		"ROAST" to listOf("roast", "roasted"),
		"BAKE" to listOf("bake", "baked", "traybake", "tray bake"),
		"GRILL" to listOf("grill", "grilled", "bbq", "barbecue", "barbeque"),
		"FRY" to listOf("fry", "fried", "deep fried", "pan fried"),
		"STEAM" to listOf("steam", "steamed"),
		"BOIL" to listOf("boil", "boiled", "simmered"),
		"RAW" to listOf("raw", "no cook", "no-cook"),
	)
	val dietaryPreferenceMapping = listOf(
		"VEGAN" to listOf("vegan", "plant based", "plant-based"),
		"VEGETARIAN" to listOf("vegetarian", "veggie", "meat free", "meat-free", "meatless"),
		"PESCATARIAN" to listOf("pescatarian"),
		"GLUTEN_FREE" to listOf("gluten free", "gluten-free", "glutenfree"),
		"DAIRY_FREE" to listOf("dairy free", "dairy-free", "lactose free", "lactose-free"),
		"NUT_FREE" to listOf("nut free", "nut-free", "peanut free", "peanut-free", "no nuts"),
		"EGG_FREE" to listOf("egg free", "egg-free", "eggless"),
		"SHELLFISH_FREE" to listOf("shellfish free", "shellfish-free"),
		"ALLIUM_FREE" to listOf("allium free", "allium-free"),
		"HALAL" to listOf("halal"),
		"KOSHER" to listOf("kosher"),
		"LOW_FODMAP" to listOf("low fodmap", "low-fodmap"),
		"PALEO" to listOf("paleo"),
		"KETO" to listOf("keto", "ketogenic"),
	)
	val tagMapping = listOf(
		"COMFORT" to listOf("comfort", "comfort food"),
		"HEALTHY" to listOf("healthy", "lighter"),
		"SPEEDY" to listOf("speedy"),
		"DINNER_PARTY" to listOf("dinner party", "entertaining"),
		"SEASONAL" to listOf("spring", "summer", "autumn", "fall", "winter"),
		"FAKEAWAY" to listOf("fakeaway", "takeaway fakeout"),
		"FAMILY" to listOf("family", "kid friendly", "kids", "family favourites"),
		"BUDGET" to listOf("budget", "cheap", "affordable"),
		"BATCH_COOK" to listOf("batch cook", "batch cooking", "freezer friendly"),
		"MEAL_PREP" to listOf("meal prep", "make ahead", "make-ahead"),
		"ONE_POT" to listOf("one pot", "one-pot", "one pan", "one-pan"),
		"PICNIC" to listOf("picnic", "packed lunch", "lunchbox"),
		"PARTY" to listOf("party food", "party", "celebration"),
	)

	val mealType = firstMappedValue(tokens, mealTypeMapping)
	val difficulty = firstMappedValue(tokens, difficultyMapping)
	val cookingMethod = firstMappedValue(tokens, cookingMethodMapping)
	val dietaryPreferences = allMappedValues(tokens, dietaryPreferenceMapping)
	val tagKeywordGroups = tagMapping.map { (_, keywords) -> keywords }
	val tags = rawLabels.filter { label ->
		val token = normalizeCategoryLabel(label)
		tagKeywordGroups.any { keywords -> matchesKeywords(token, keywords) }
	}
	val allKeywordGroups = (
		mealTypeMapping +
			difficultyMapping +
			cookingMethodMapping +
			dietaryPreferenceMapping +
			tagMapping
		)
		.map { (_, keywords) -> keywords }
	val unmatchedLabels = rawLabels.filterNot { label ->
		val token = normalizeCategoryLabel(label)
		allKeywordGroups.any { keywords -> matchesKeywords(token, keywords) }
	}

	return ScrapedCategoryColumns(
		mealType = mealType,
		difficulty = difficulty,
		cookingMethod = cookingMethod,
		dietaryPreferences = dietaryPreferences,
		tags = tags,
		unmatchedLabels = unmatchedLabels,
	)
}

fun detectMeasurementSystem(ingredientGroups: List<Pair<String?, List<ProcessedScrapedIngredient>>>): String? {
	var imperialHits = 0
	var metricHits = 0
	ingredientGroups.asSequence()
		.flatMap { (_, ingredients) -> ingredients.asSequence() }
		.forEach { ingredient ->
			val normalized = ingredient.text.lowercase()
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

fun saveRecipe(
	connection: Connection,
	recipe: RecipeData,
	upsertExisting: Boolean = false,
): Int? {
	val existingId = findRecipeIdBySourceUrl(connection, recipe.sourceUrl)
	if (existingId != null && !upsertExisting) return null
	val measurementSystem = detectMeasurementSystem(recipe.ingredientGroups)
	val categoryColumns = mapCategoryToColumns(recipe.category)
	if (categoryColumns.unmatchedLabels.isNotEmpty()) {
		println("Unmapped category labels for ${recipe.sourceUrl}: ${categoryColumns.unmatchedLabels.joinToString()}")
	}
	val recipeId = if (existingId == null) {
		insertRecipeRecord(connection, recipe, categoryColumns, measurementSystem)
	} else {
		updateRecipeRecord(connection, existingId, recipe, categoryColumns, measurementSystem)
		deleteRecipeDerivedRowsForUpsert(connection, existingId)
		existingId
	}
	saveRecipeIngredientGroups(connection, recipe, recipeId)
	saveRecipeInstructions(connection, recipe, recipeId)
	saveScrapedRecipeNutrition(connection, recipe, recipeId)
	connection.commit()
	return recipeId
}

private fun updateRecipeRecord(
	connection: Connection,
	recipeId: Int,
	recipe: RecipeData,
	categoryColumns: ScrapedCategoryColumns,
	measurementSystem: String?,
) {
	connection.prepareStatement(
		"""
		UPDATE recipes SET
			title = ?,
			instructions = ?,
			total_time = ?,
			prep_time = ?,
			cook_time = ?,
			yields = ?,
			image_url = ?,
			language = ?,
			cuisine = ?,
			meal_type = ?,
			difficulty = ?,
			cooking_method = ?,
			dietary_preferences = ?,
			tags = ?,
			measurement_system = ?,
			appliances = ?
		WHERE id = ?
		""".trimIndent(),
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
		ps.setString(10, categoryColumns.mealType)
		ps.setString(11, categoryColumns.difficulty)
		ps.setString(12, categoryColumns.cookingMethod)
		setTextArrayOrNull(ps, connection, 13, categoryColumns.dietaryPreferences)
		setTextArrayOrNull(ps, connection, 14, categoryColumns.tags)
		ps.setString(15, measurementSystem)
		setTextArrayOrNull(ps, connection, 16, recipe.appliances)
		ps.setInt(17, recipeId)
		ps.executeUpdate()
	}
}

private fun deleteRecipeDerivedRowsForUpsert(connection: Connection, recipeId: Int) {
	connection.prepareStatement("DELETE FROM ingredient_groups WHERE recipe_id = ?").use { ps ->
		ps.setInt(1, recipeId)
		ps.executeUpdate()
	}
	connection.prepareStatement("DELETE FROM instruction_steps WHERE recipe_id = ?").use { ps ->
		ps.setInt(1, recipeId)
		ps.executeUpdate()
	}
}

private fun insertRecipeRecord(
	connection: Connection,
	recipe: RecipeData,
	categoryColumns: ScrapedCategoryColumns,
	measurementSystem: String?,
): Int = connection.prepareStatement(
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
		difficulty,
		cooking_method,
		dietary_preferences,
		tags,
		source_url,
		measurement_system,
		appliances
	)
	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
	RETURNING id
	""".trimIndent(),
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
	ps.setString(10, categoryColumns.mealType)
	ps.setString(11, categoryColumns.difficulty)
	ps.setString(12, categoryColumns.cookingMethod)
	setTextArrayOrNull(ps, connection, 13, categoryColumns.dietaryPreferences)
	setTextArrayOrNull(ps, connection, 14, categoryColumns.tags)
	ps.setString(15, recipe.sourceUrl)
	ps.setString(16, measurementSystem)
	setTextArrayOrNull(ps, connection, 17, recipe.appliances)
	ps.executeQuery().use { rs ->
		rs.next()
		rs.getInt(1)
	}
}

private fun setTextArrayOrNull(
	ps: java.sql.PreparedStatement,
	connection: Connection,
	index: Int,
	values: List<String>,
) {
	if (values.isEmpty()) {
		ps.setNull(index, java.sql.Types.ARRAY)
	} else {
		ps.setArray(index, connection.createArrayOf("text", values.toTypedArray()))
	}
}

private fun saveRecipeIngredientGroups(connection: Connection, recipe: RecipeData, recipeId: Int) {
	val insertGroupSql = "INSERT INTO ingredient_groups (recipe_id, name, order_index) VALUES (?, ?, ?) RETURNING id"
	val insertIngredientSql =
		"""
		INSERT INTO ingredients (
			ingredient_group_id,
			ingredient,
			order_index,
			requirement,
			alternative_group_key
		) VALUES (?, ?, ?, ?, ?)
		""".trimIndent()

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
				ps.setString(2, clampIngredientForDatabase(ingredient.text, recipe.sourceUrl))
				ps.setInt(3, itemIndex)
				ps.setString(4, ingredient.requirement)
				if (ingredient.alternativeGroupKey == null) {
					ps.setNull(5, java.sql.Types.INTEGER)
				} else {
					ps.setInt(5, ingredient.alternativeGroupKey)
				}
				ps.addBatch()
			}
			ps.executeBatch()
		}
	}
}

private fun saveRecipeInstructions(connection: Connection, recipe: RecipeData, recipeId: Int) {
	if (recipe.instructionList.isEmpty()) {
		return
	}
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

private val scrapedServingsRegex = Regex(
	pattern = """(\d+(?:\.\d+)?)\s*(?:servings?|serves|portions?|people|items?|pieces?|pancakes?|""" +
		"""biscuits?|cookies?|rolls?|muffins?|cupcakes?|buns?|bars?|loaves|loaf|batch|pie)""",
	option = RegexOption.IGNORE_CASE,
)

private val scrapedLeadingCountRegex = Regex(
	pattern = """^(\d+(?:\.\d+)?)\s*(?:x\s*)?(?:servings?|serves|items?|pieces?)""",
	option = RegexOption.IGNORE_CASE,
)

private fun parseScrapedServingCount(yields: String?): Double? {
	val trimmed = yields?.trim().orEmpty()
	if (trimmed.isEmpty()) {
		return null
	}
	val match = scrapedServingsRegex.find(trimmed) ?: scrapedLeadingCountRegex.find(trimmed)
	val count = match?.groupValues?.getOrNull(1)?.toDoubleOrNull()
	return if (count == null || count <= 0.0) null else count
}

private fun scaleScrapedNutrient(value: String?, multiplier: Double): Double? {
	val parsed = parseNumber(value) ?: return null
	return java.math.BigDecimal.valueOf(parsed * multiplier)
		.setScale(2, java.math.RoundingMode.HALF_UP)
		.toDouble()
}

private fun saveScrapedRecipeNutrition(connection: Connection, recipe: RecipeData, recipeId: Int) {
	if (!recipe.hasScrapedNutrition()) {
		return
	}
	val servingCount = parseScrapedServingCount(recipe.yields)
	val multiplier = if (servingCount != null && servingCount > 0.0) servingCount else 1.0
	val nutrients = recipe.nutrients
	connection.prepareStatement(
		"""
		INSERT INTO nutrition (
			recipe_id,
			calories,
			protein,
			carbohydrates,
			fat,
			fiber,
			sugar,
			sodium,
			calculation_source,
			serving_count
		)
		VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
		ON CONFLICT (recipe_id) DO UPDATE SET
			calories = EXCLUDED.calories,
			protein = EXCLUDED.protein,
			carbohydrates = EXCLUDED.carbohydrates,
			fat = EXCLUDED.fat,
			fiber = EXCLUDED.fiber,
			sugar = EXCLUDED.sugar,
			sodium = EXCLUDED.sodium,
			calculation_source = EXCLUDED.calculation_source,
			serving_count = EXCLUDED.serving_count
		""".trimIndent(),
	).use { ps ->
		ps.setInt(1, recipeId)
		ps.setObject(2, scaleScrapedNutrient(nutrients["calories"], multiplier))
		ps.setObject(3, scaleScrapedNutrient(nutrients["protein"], multiplier))
		ps.setObject(4, scaleScrapedNutrient(nutrients["carbohydrates"], multiplier))
		ps.setObject(5, scaleScrapedNutrient(nutrients["fat"], multiplier))
		ps.setObject(6, scaleScrapedNutrient(nutrients["fiber"], multiplier))
		ps.setObject(7, scaleScrapedNutrient(nutrients["sugar"], multiplier))
		ps.setObject(8, scaleScrapedNutrient(nutrients["sodium"], multiplier))
		ps.setString(9, "scraped")
		ps.setObject(10, servingCount)
		ps.executeUpdate()
	}
}

private fun RecipeData.hasScrapedNutrition(): Boolean =
	nutrients.values.any { value ->
		!value.isNullOrBlank() && parseNumber(value) != null
	}

private fun findRepoRoot(): java.nio.file.Path? {
	val currentDirectory = java.nio.file.Path.of(System.getProperty("user.dir"))
	return generateSequence(currentDirectory) { path -> path.parent }
		.firstOrNull { candidate ->
			candidate.resolve("settings.gradle.kts").toFile().exists()
		}
}

fun nutritionDatabaseEnvironment(config: Config): Map<String, String> = buildMap {
	put("PURECIPES_DB_URL", buildJdbcUrl(config))
	val user = config.dbUser
		?: printUsageAndExit("For postgres mode provide --db-user (or include credentials in --db-dsn)")
	put("PURECIPES_DB_USER", user)
	config.dbPassword?.let { password -> put("PURECIPES_DB_PASSWORD", password) }
}

fun calculateNutritionForRecipes(config: Config, recipeIds: List<Int>) {
	if (recipeIds.isEmpty()) {
		return
	}

	val repoRoot = findRepoRoot()
	val gradlew = repoRoot?.resolve("gradlew")?.toFile()
	if (repoRoot == null || gradlew == null || !gradlew.exists()) {
		if (repoRoot == null) {
			println("Skipping nutrition calculation: could not find repository root (settings.gradle.kts)")
		} else {
			println("Skipping nutrition calculation: gradlew not found at ${gradlew?.path}")
		}
		return
	}

	println("Calculating nutrition for ${recipeIds.size} imported recipes...")
	val process = ProcessBuilder(
		gradlew.absolutePath,
		"calculateRecipeNutrition",
		"-Pnutrition.recipeIds=${recipeIds.joinToString(",")}",
		"-q",
	)
		.directory(repoRoot.toFile())
		.redirectErrorStream(true)
		.apply { environment().putAll(nutritionDatabaseEnvironment(config)) }
		.start()

	process.inputStream.bufferedReader().use { reader ->
		reader.forEachLine { line -> println(line) }
	}

	val exitCode = process.waitFor()
	if (exitCode != 0) {
		println("Nutrition calculation failed with exit code $exitCode")
	}
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
		val importedRecipeIds = importFilesToConnection(connection, jsonFiles, config.upsertExisting)
		if (config.calculateNutrition) {
			calculateNutritionForRecipes(config, importedRecipeIds)
		}
	}
}

fun importFilesToConnection(
	connection: Connection,
	jsonFiles: List<File>,
	upsertExisting: Boolean = true,
): List<Int> {
	var imported = 0
	var duplicates = 0
	var errors = 0
	val importedRecipeIds = mutableListOf<Int>()
	for (file in jsonFiles) {
		val (outcome, recipeId) = processRecipeFile(connection, file, upsertExisting)
		when (outcome) {
			ImportOutcome.SUCCESS -> {
				imported++
				recipeId?.let(importedRecipeIds::add)
				if (imported % 100 == 0) println("Imported $imported so far...")
			}

			ImportOutcome.DUPLICATE -> duplicates++
			ImportOutcome.ERROR -> errors++
		}
	}
	println("Import complete. Imported=$imported Duplicates=$duplicates Errors=$errors")
	return importedRecipeIds
}

private enum class ImportOutcome {
	SUCCESS, DUPLICATE, ERROR
}

private fun processRecipeFile(
	connection: Connection,
	file: File,
	upsertExisting: Boolean,
): Pair<ImportOutcome, Int?> {
	val recipe = safeParseRecipeFile(file)
	return when {
		recipe == null -> ImportOutcome.ERROR to null
		else -> {
			val recipeId = saveRecipe(connection, recipe, upsertExisting)
			if (recipeId == null) {
				ImportOutcome.DUPLICATE to null
			} else {
				ImportOutcome.SUCCESS to recipeId
			}
		}
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
val recipesDir = if (config.mode == "json") {
	outputRoot
} else {
	File(outputRoot, "recipes").apply { mkdirs() }
}

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

	val selectedAfterDbPrecheck = if (config.mode == "web" && config.precheckDb && !config.upsertExisting) {
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
				val dbId = dbConnection?.let { saveRecipe(it, recipe, config.upsertExisting) }
				scraped += (recipe to dbId)

				val siteName = try {
					URI(recipe.sourceUrl).host?.removePrefix("www.")?.lowercase(Locale.getDefault())
						?.ifBlank { "unknown" }
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

	if (config.calculateNutrition) {
		val importedRecipeIds = scraped.mapNotNull { (_, recipeId) -> recipeId }
		calculateNutritionForRecipes(config, importedRecipeIds)
	}
}
