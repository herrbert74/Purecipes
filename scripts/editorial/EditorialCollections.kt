import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive
import java.io.IOException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.system.exitProcess

const val EDITORIAL_SCRIPTS_REVISION = "4"
const val EDITORIAL_SOURCE = "editorial"
const val QUERY_SOURCE = "query"
const val CATALOG_SORT = "catalog"
const val NEWEST_SORT = "newest"

private const val DEFAULT_HOST = "localhost"
private const val DEFAULT_DATABASE = "purecipes"
private const val DEFAULT_USER = "postgres"
private const val DEFAULT_PASSWORD = "postgres"
private const val DEFAULT_PORT = 5432
private const val LOCAL_SSL_MODE = "disable"
private const val MIN_PORT = 1
private const val MAX_PORT = 65535
private const val SLUG_MAX_LENGTH = 64
private const val SEARCH_LIMIT = 30
private const val POSITION_STEP = 10
private const val EMPTY_MAX_POSITION = -10
private const val TITLE_WIDTH = 40
private const val LOCAL_CONNECTION = "local"
private const val REMOTE_CONNECTION = "remote"
private const val CLEAR_FIELD = "-"
private const val QUIT_COMMAND = "quit"

private val slugRegex = Regex("^[a-z0-9]+(?:-[a-z0-9]+)*$")
private val json = Json { ignoreUnknownKeys = false }
private val timestampFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
private val filterFields = setOf(
	"dietaryPreferences",
	"cuisines",
	"mealTypes",
	"cookingTimeRanges",
	"difficultyLevels",
	"cookingMethods",
	"calorieRanges",
	"nutritionFilters",
)
private val sslModes = setOf("disable", "allow", "prefer", "require", "verify-ca", "verify-full")
private val sessionCommands = listOf(
	"list",
	"find",
	"create",
	"update",
	"recipes",
	"delete",
	QUIT_COMMAND,
)

private var resolvedPsql: String? = null

class EditorialInputException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

class EditorialEndpoint(
	val host: String,
	val port: Int,
	val database: String,
	val user: String,
	val sslMode: String,
)

class EditorialTarget(
	val endpoint: EditorialEndpoint,
	val password: CharArray,
) {

	fun clearPassword() {
		password.fill('\u0000')
	}
}

data class CollectionDraft(
	val slug: String,
	val title: String,
	val source: String,
	val filterPayload: String,
	val sort: String,
	val position: Int,
	val enabled: Boolean,
	val startsAt: String,
	val endsAt: String,
	val recipeIds: List<Int>,
) {

	fun variables(originalSlug: String = slug): Map<String, String> = mapOf(
		"slug" to slug,
		"original_slug" to originalSlug,
		"title" to title,
		"source" to source,
		"filter_payload" to filterPayload,
		"sort" to sort,
		"position" to position.toString(),
		"enabled" to if (enabled) "TRUE" else "FALSE",
		"starts_at" to startsAt,
		"ends_at" to endsAt,
	)
}

fun mainEditorial(command: (EditorialTarget) -> Unit) {
	val target = connectedTarget()
	try {
		command(target)
	} catch (error: EditorialInputException) {
		System.err.println(error.message)
		exitProcess(1)
	} finally {
		target.clearPassword()
	}
}

fun runEditorialSession() {
	println("Connection details stay in this process until you quit. They are not saved.")
	val target = connectedTarget()
	try {
		var command = ""
		while (command != QUIT_COMMAND) {
			command = promptChoice("Command", sessionCommands)
			if (command != QUIT_COMMAND) {
				runSessionCommand(target, command)
			}
		}
	} finally {
		target.clearPassword()
	}
}

private fun connectedTarget(): EditorialTarget {
	var target = promptTarget()
	var connected = false
	while (!connected) {
		try {
			openConnection(target)
			connected = true
		} catch (error: EditorialInputException) {
			System.err.println(error.message)
			target.clearPassword()
			target = promptTarget()
		}
	}
	return target
}

private fun openConnection(target: EditorialTarget) {
	try {
		runPsql(target, "SELECT 1", emptyMap(), tuplesOnly = true)
	} catch (error: EditorialInputException) {
		throw EditorialInputException("Could not connect.", error)
	}
	println("Connected.")
}

fun runListCollections(target: EditorialTarget) {
	val rows = queryObjects(target, listCollectionsSql)
	if (rows.isEmpty()) {
		println("No collections.")
		return
	}
	println("pos  slug  title  source  state  recipes  dates")
	rows.forEach { row -> println(formatCollection(row)) }
}

fun runFindRecipes(target: EditorialTarget) {
	val rows = searchRecipes(target, promptRequired("Title contains"))
	if (rows.isNotEmpty()) {
		printRecipeRows(rows)
	}
}

fun runCreateCollection(target: EditorialTarget) {
	val draft = promptNewCollection(target)
	val found = reviewedRecipes(target, draft.recipeIds)
	if (found == null) {
		println("Not saved.")
		return
	}
	warnWindow(draft.startsAt, draft.endsAt)
	if (draft.recipeIds.isNotEmpty()) {
		printRecipePlan(draft.recipeIds, found)
	}
	if (!confirm(summary(draft))) {
		println("Not saved.")
		return
	}
	runPsql(target, insertSql(draft.recipeIds), draft.variables(), tuplesOnly = false)
	println("Saved ${draft.slug}.")
}

fun runUpdateCollection(target: EditorialTarget) {
	val current = loadCollection(target, promptSlug())
	val draft = promptUpdatedCollection(current)
	warnSourceChange(current, draft)
	warnWindow(draft.startsAt, draft.endsAt)
	if (!confirm(summary(draft))) {
		println("Not saved.")
		return
	}
	runPsql(target, updateCollectionSql, draft.variables(current.text("slug")), tuplesOnly = false)
	println("Updated ${draft.slug}.")
}

fun runUpdateCollectionRecipes(target: EditorialTarget) {
	val current = loadCollection(target, promptSlug())
	requireEditorial(current)
	printCurrentRecipes(target, current.text("slug"))
	val ids = promptRecipeIds(target)
	val found = reviewedRecipes(target, ids)
	if (found == null) {
		println("Not saved.")
		return
	}
	printRecipePlan(ids, found)
	if (!confirm("Replace the recipe list for ${current.text("slug")}?")) {
		println("Not saved.")
		return
	}
	runPsql(target, replaceRecipesSql(ids), mapOf("slug" to current.text("slug")), tuplesOnly = false)
	println("Updated recipes for ${current.text("slug")}.")
}

fun runDeleteCollection(target: EditorialTarget) {
	val current = loadCollection(target, promptSlug())
	val slug = current.text("slug")
	println("Delete ${current.text("title")} ($slug) and its recipe list.")
	if (promptLine("Type the slug to confirm") != slug) {
		println("Not deleted.")
		return
	}
	runPsql(target, DELETE_COLLECTION_SQL, mapOf("slug" to slug), tuplesOnly = false)
	println("Deleted $slug.")
}

fun validateSlug(slug: String): String {
	val trimmed = slug.trim()
	val tooLong = trimmed.length > SLUG_MAX_LENGTH
	if (tooLong || !slugRegex.matches(trimmed)) {
		throw EditorialInputException(
			"Slug must be lowercase letters, digits, and single hyphens, " +
				"with no hyphen at either end, up to $SLUG_MAX_LENGTH characters.",
		)
	}
	return trimmed
}

fun validateSource(source: String): String {
	val normalized = source.trim().lowercase()
	if (normalized != EDITORIAL_SOURCE && normalized != QUERY_SOURCE) {
		throw EditorialInputException("Source must be editorial or query.")
	}
	return normalized
}

fun validateSort(sort: String): String {
	val normalized = sort.trim().lowercase()
	if (normalized != CATALOG_SORT && normalized != NEWEST_SORT) {
		throw EditorialInputException("Sort must be catalog or newest.")
	}
	return normalized
}

fun normalizeTimestamp(value: String): String? {
	val trimmed = value.trim()
	val dateOnly = runCatching { LocalDate.parse(trimmed) }.getOrNull()
	if (dateOnly != null) {
		return "$trimmed 00:00:00"
	}
	val dateTime = runCatching { LocalDateTime.parse(trimmed, timestampFormatter) }.getOrNull()
	return dateTime?.format(timestampFormatter)
}

fun validateFilterPayload(payload: String): String {
	val trimmed = payload.trim()
	if (trimmed.isEmpty()) {
		return ""
	}
	rejectControlCharacters(trimmed, "Filter")
	val element = try {
		json.parseToJsonElement(trimmed)
	} catch (error: SerializationException) {
		throw EditorialInputException("Filter payload must be a JSON object. ${error.message}", error)
	}
	val payloadObject = element as? JsonObject
		?: throw EditorialInputException("Filter payload must be a JSON object.")
	validateFilterFields(payloadObject)
	return trimmed
}

fun likeContains(query: String): String {
	val escaped = query
		.replace("\\", "\\\\")
		.replace("%", "\\%")
		.replace("_", "\\_")
	return "%$escaped%"
}

private fun runSessionCommand(target: EditorialTarget, command: String) {
	try {
		dispatch(target, command)
	} catch (error: EditorialInputException) {
		System.err.println(error.message)
	}
}

private fun dispatch(target: EditorialTarget, command: String) {
	when (command) {
		"list" -> runListCollections(target)
		"find" -> runFindRecipes(target)
		"create" -> runCreateCollection(target)
		"update" -> runUpdateCollection(target)
		"recipes" -> runUpdateCollectionRecipes(target)
		"delete" -> runDeleteCollection(target)
		else -> Unit
	}
}

private fun promptNewCollection(target: EditorialTarget): CollectionDraft {
	val slug = promptSlug(null)
	val title = promptRequired("Title")
	val source = promptSource(EDITORIAL_SOURCE)
	val filterPayload = promptFilter(source, "")
	val sort = promptSort(source, CATALOG_SORT)
	val position = promptInt("Position", nextPosition(target))
	val enabled = promptEnabled(null)
	val startsAt = promptTimestamp("Starts at", "")
	val endsAt = promptTimestamp("Ends at", "")
	val recipeIds = if (source == EDITORIAL_SOURCE) promptRecipeIds(target) else emptyList()
	return CollectionDraft(
		slug = slug,
		title = title,
		source = source,
		filterPayload = filterPayload,
		sort = sort,
		position = position,
		enabled = enabled,
		startsAt = startsAt,
		endsAt = endsAt,
		recipeIds = recipeIds,
	)
}

private fun promptUpdatedCollection(current: JsonObject): CollectionDraft {
	println(formatCollection(current))
	val slug = promptSlug(current.text("slug"))
	val title = promptRequired("Title", current.text("title"))
	val source = promptSource(current.text("source"))
	return CollectionDraft(
		slug = slug,
		title = title,
		source = source,
		filterPayload = updatedFilter(source, current),
		sort = updatedSort(source, current),
		position = promptInt("Position", current.int("position")),
		enabled = promptEnabled(current.bool("enabled")),
		startsAt = promptTimestamp("Starts at", current.text("startsAt")),
		endsAt = promptTimestamp("Ends at", current.text("endsAt")),
		recipeIds = emptyList(),
	)
}

private fun updatedFilter(source: String, current: JsonObject): String {
	return if (source == QUERY_SOURCE) {
		promptFilter(source, current.text("filterPayload"))
	} else {
		current.text("filterPayload")
	}
}

private fun updatedSort(source: String, current: JsonObject): String {
	return if (source == QUERY_SOURCE) {
		promptSort(source, current.text("sort"))
	} else {
		current.text("sort").ifEmpty { CATALOG_SORT }
	}
}

private fun promptRecipeIds(target: EditorialTarget): List<Int> {
	println("Recipe ids in order, one per line. ?text searches titles. A blank line finishes.")
	val ids = mutableListOf<Int>()
	var finished = false
	while (!finished) {
		val line = promptLine("Recipe")
		finished = line.isEmpty()
		if (!finished) {
			readRecipeLine(target, ids, line)
		}
	}
	return ids
}

private fun readRecipeLine(target: EditorialTarget, ids: MutableList<Int>, line: String) {
	if (line.startsWith("?")) {
		appendFromSearch(target, ids, line.removePrefix("?").trim())
	} else {
		val id = line.toIntOrNull()
		if (id == null) {
			println("Enter a recipe id or ?search.")
		} else {
			appendRecipeId(ids, id)
		}
	}
}

private fun appendFromSearch(target: EditorialTarget, ids: MutableList<Int>, query: String) {
	val rows = searchRecipes(target, query)
	if (rows.isNotEmpty()) {
		printSearchRows(rows)
		val picked = pickSearchRow(rows)
		if (picked != null) {
			appendRecipeId(ids, picked)
		}
	}
}

private fun searchRecipes(target: EditorialTarget, query: String): List<JsonObject> {
	if (query.isEmpty()) {
		println("Enter text after ?.")
		return emptyList()
	}
	val rows = queryObjects(target, findRecipesSql, mapOf("pattern" to likeContains(query)))
	if (rows.isEmpty()) {
		println("No recipes.")
	}
	return rows
}

private fun printSearchRows(rows: List<JsonObject>) {
	rows.forEachIndexed { index, row ->
		val number = index + 1
		println("$number  ${row.int("id")}  ${row.text("title")}${privacyLabel(row)}")
	}
}

private fun pickSearchRow(rows: List<JsonObject>): Int? {
	val pick = promptLine("Pick a number, or leave blank to skip")
	val index = pick.toIntOrNull()?.minus(1)
	val row = if (pick.isEmpty() || index == null) null else rows.getOrNull(index)
	if (pick.isNotEmpty() && row == null) {
		println("That number is not in the list.")
	}
	return row?.int("id")
}

private fun appendRecipeId(ids: MutableList<Int>, id: Int) {
	if (id in ids) {
		println("Recipe $id is already in the list.")
	} else {
		ids.add(id)
	}
}

private fun reviewedRecipes(target: EditorialTarget, ids: List<Int>): List<JsonObject>? {
	val found = loadRecipes(target, ids)
	val missing = ids.filter { requested -> found.none { it.int("id") == requested } }
	if (missing.isNotEmpty()) {
		throw EditorialInputException("Unknown recipe ids: ${missing.joinToString(", ")}.")
	}
	if (!acceptPrivate(found)) {
		return null
	}
	return found
}

private fun acceptPrivate(found: List<JsonObject>): Boolean {
	val privateIds = found.filter { it.bool("private") }.map { it.int("id") }
	if (privateIds.isEmpty()) {
		return true
	}
	println("Private recipes stay hidden on the shelf: ${privateIds.joinToString(", ")}.")
	return promptYes("Keep them in the list anyway", defaultYes = false)
}

private fun promptTarget(): EditorialTarget {
	println("Nothing is written to a config file or to shell history.")
	println("local   localhost, user postgres. Enter accepts the shown password.")
	println("remote  host, database user, and password.")
	return if (promptChoice("Connection", listOf(LOCAL_CONNECTION, REMOTE_CONNECTION)) == LOCAL_CONNECTION) {
		localTarget()
	} else {
		remoteTarget()
	}
}

private fun localTarget(): EditorialTarget {
	val database = promptToken("Database", DEFAULT_DATABASE)
	return EditorialTarget(
		endpoint = EditorialEndpoint(
			host = DEFAULT_HOST,
			port = DEFAULT_PORT,
			database = database,
			user = DEFAULT_USER,
			sslMode = LOCAL_SSL_MODE,
		),
		password = promptPassword(DEFAULT_PASSWORD),
	)
}

private fun remoteTarget(): EditorialTarget {
	val host = promptToken("Host")
	val database = promptToken("Database", DEFAULT_DATABASE)
	val user = promptToken("User")
	return EditorialTarget(
		endpoint = EditorialEndpoint(
			host = host,
			port = promptPort(),
			database = database,
			user = user,
			sslMode = promptSslMode(),
		),
		password = promptPassword(),
	)
}

private fun promptPort(): Int {
	var port = 0
	while (port !in MIN_PORT..MAX_PORT) {
		port = promptInt("Port", DEFAULT_PORT)
		if (port !in MIN_PORT..MAX_PORT) {
			println("Port must be between $MIN_PORT and $MAX_PORT.")
		}
	}
	return port
}

private fun promptSslMode(): String {
	var mode = ""
	while (mode !in sslModes) {
		mode = promptLine("SSL mode", "require")
		if (mode !in sslModes) {
			println("SSL mode must be one of: ${sslModes.joinToString(", ")}.")
		}
	}
	return mode
}

private fun promptSlug(current: String? = null): String {
	var slug: String? = null
	while (slug == null) {
		slug = readSlug(promptLine("Slug", current))
	}
	return slug
}

private fun readSlug(entered: String): String? {
	return try {
		validateSlug(entered)
	} catch (error: EditorialInputException) {
		println(error.message)
		null
	}
}

private fun promptSource(current: String): String {
	var source: String? = null
	while (source == null) {
		source = readSource(promptLine("Source", current))
	}
	return source
}

private fun readSource(entered: String): String? {
	return try {
		validateSource(entered)
	} catch (error: EditorialInputException) {
		println(error.message)
		null
	}
}

private fun promptSort(source: String, current: String): String {
	if (source != QUERY_SOURCE) {
		return CATALOG_SORT
	}
	var sort: String? = null
	while (sort == null) {
		sort = readSort(promptLine("Sort", current))
	}
	return sort
}

private fun readSort(entered: String): String? {
	return try {
		validateSort(entered)
	} catch (error: EditorialInputException) {
		println(error.message)
		null
	}
}

private fun promptFilter(source: String, current: String): String {
	if (source != QUERY_SOURCE) {
		return ""
	}
	println("Filter JSON on one line. Blank means every public recipe.")
	println("""Example: {"cuisines":["Italian"],"mealTypes":["DINNER"]}""")
	var payload: String? = null
	while (payload == null) {
		payload = readFilter(promptLine("Filter", current))
	}
	return payload
}

private fun readFilter(entered: String): String? {
	return try {
		validateFilterPayload(entered)
	} catch (error: EditorialInputException) {
		println(error.message)
		null
	}
}

private fun promptTimestamp(label: String, current: String): String {
	println("$label is YYYY-MM-DD or YYYY-MM-DD HH:MM:SS. Blank keeps the shown value. - clears it.")
	var chosen: String? = null
	while (chosen == null) {
		val entered = promptLine(label, current)
		chosen = when (entered) {
			CLEAR_FIELD -> ""
			current -> current
			else -> normalizeTimestamp(entered)
		}
		if (chosen == null) {
			println("Use YYYY-MM-DD or YYYY-MM-DD HH:MM:SS.")
		}
	}
	return chosen
}

private fun promptEnabled(current: Boolean?): Boolean {
	val defaultYes = current != false
	return promptYes("Enabled", defaultYes)
}

private fun promptChoice(label: String, choices: List<String>): String {
	println("$label: ${choices.joinToString(", ")}")
	var chosen = ""
	while (chosen !in choices) {
		val entered = promptLine(label).lowercase()
		if (entered in choices) {
			chosen = entered
		} else {
			println("Enter one of: ${choices.joinToString(", ")}.")
		}
	}
	return chosen
}

private fun promptYes(label: String, defaultYes: Boolean): Boolean {
	val hint = if (defaultYes) "Y/n" else "y/N"
	var answer = ""
	while (answer != "y" && answer != "n") {
		val entered = promptLine("$label [$hint]").lowercase()
		answer = when {
			entered.isEmpty() && defaultYes -> "y"
			entered.isEmpty() -> "n"
			entered == "y" || entered == "n" -> entered
			else -> ""
		}
		if (answer.isEmpty()) {
			println("Enter y or n.")
		}
	}
	return answer == "y"
}

private fun promptInt(label: String, default: Int? = null): Int {
	while (true) {
		val parsed = promptLine(label, default?.toString()).toIntOrNull()
		if (parsed != null) {
			return parsed
		}
		println("$label must be a whole number.")
	}
}

private fun promptToken(label: String, default: String? = null): String {
	var value = ""
	while (value.isEmpty()) {
		value = acceptedToken(label, promptLine(label, default))
	}
	return value
}

private fun acceptedToken(label: String, value: String): String {
	val rejected = value.isEmpty() || value.any { it.isWhitespace() }
	if (rejected) {
		println("$label cannot be empty or contain whitespace.")
		return ""
	}
	return value
}

private fun promptRequired(label: String, default: String? = null): String {
	var value = ""
	while (value.isEmpty()) {
		value = promptLine(label, default)
		if (value.isEmpty()) {
			println("$label is required.")
		}
	}
	rejectControlCharacters(value, label)
	return value
}

private fun promptLine(label: String, default: String? = null): String {
	val hint = if (default.isNullOrEmpty()) "" else " [$default]"
	print("$label$hint: ")
	System.out.flush()
	val entered = readlnOrNull()?.trim().orEmpty()
	return if (entered.isEmpty() && default != null) default else entered
}

private fun promptPassword(default: String? = null): CharArray {
	var password = CharArray(0)
	while (password.isEmpty()) {
		val entered = readPassword(default)
		password = if (entered.isEmpty() && default != null) default.toCharArray() else entered
		if (password.isEmpty()) {
			println("Password is required.")
		}
	}
	return password
}

private fun readPassword(default: String?): CharArray {
	val hint = if (default == null) "" else " [$default]"
	val console = System.console()
	val entered = if (console == null) {
		print("Password$hint (visible here): ")
		System.out.flush()
		readlnOrNull()?.toCharArray()
	} else {
		console.readPassword("Password$hint: ")
	}
	return entered ?: CharArray(0)
}

private fun confirm(message: String): Boolean {
	println(message)
	return promptYes("Save", defaultYes = false)
}

private fun summary(draft: CollectionDraft): String {
	val dates = "starts ${draft.startsAt.ifEmpty { "open" }}, ends ${draft.endsAt.ifEmpty { "open" }}"
	val filter = draft.filterPayload.ifEmpty { "none" }
	return "${draft.slug}  ${draft.title}  ${draft.source}  position ${draft.position}  " +
		"enabled ${draft.enabled}  $dates  sort ${draft.sort}  filter $filter"
}

private fun warnWindow(startsAt: String, endsAt: String) {
	val reversed = startsAt.isNotEmpty() && endsAt.isNotEmpty() && endsAt <= startsAt
	if (reversed) {
		println("The end is not after the start, so this shelf will stay hidden.")
	}
}

private fun warnSourceChange(current: JsonObject, draft: CollectionDraft) {
	val wasEditorial = current.text("source") == EDITORIAL_SOURCE
	val becomesQuery = wasEditorial && draft.source == QUERY_SOURCE
	val becomesEditorial = !wasEditorial && draft.source == EDITORIAL_SOURCE
	if (becomesQuery) {
		println("The hand-picked rows stay in the database and are ignored while this shelf is a query.")
	}
	if (becomesEditorial) {
		println("This shelf will use the hand-picked list. Add recipes if that list is empty.")
	}
}

private fun requireEditorial(current: JsonObject) {
	if (current.text("source") != EDITORIAL_SOURCE) {
		throw EditorialInputException("Query shelves do not use a hand-picked recipe list.")
	}
}

private fun loadCollection(target: EditorialTarget, slug: String): JsonObject {
	val rows = queryObjects(target, oneCollectionSql, mapOf("slug" to slug))
	return rows.firstOrNull() ?: throw EditorialInputException("No collection with slug $slug.")
}

private fun loadRecipes(target: EditorialTarget, ids: List<Int>): List<JsonObject> {
	if (ids.isEmpty()) {
		return emptyList()
	}
	return queryObjects(target, recipesByIdSql(ids))
}

private fun printCurrentRecipes(target: EditorialTarget, slug: String) {
	val rows = queryObjects(target, collectionRecipesSql, mapOf("slug" to slug))
	if (rows.isEmpty()) {
		println("No recipes in this list.")
		return
	}
	rows.forEach { row ->
		println("${row.int("position")}  ${row.int("id")}  ${row.text("title")}${privacyLabel(row)}")
	}
}

private fun printRecipeRows(rows: List<JsonObject>) {
	rows.forEach { row ->
		println("${row.int("id")}  ${row.text("title")}${privacyLabel(row)}")
	}
}

private fun printRecipePlan(ids: List<Int>, found: List<JsonObject>) {
	if (ids.isEmpty()) {
		println("No recipes.")
		return
	}
	val byId = found.associateBy { it.int("id") }
	ids.forEachIndexed { index, id ->
		val title = byId[id]?.text("title").orEmpty()
		println("$index  $id  $title")
	}
}

private fun privacyLabel(row: JsonObject): String = if (row.bool("private")) "  private" else ""

private fun formatCollection(row: JsonObject): String {
	val state = if (row.bool("live")) "live" else "hidden"
	val dates = listOf(row.text("startsAt"), row.text("endsAt"))
		.filter { it.isNotEmpty() }
		.joinToString(" .. ")
		.ifEmpty { "no dates" }
	val title = clip(row.text("title"), TITLE_WIDTH)
	return "${row.int("position")}  ${row.text("slug")}  $title  ${row.text("source")}  " +
		"$state  ${row.int("recipeCount")} recipes  $dates"
}

private fun clip(value: String, width: Int): String {
	return if (value.length <= width) value else value.take(width - 1) + "…"
}

private fun nextPosition(target: EditorialTarget): Int {
	val output = runPsql(target, nextPositionSql, emptyMap(), tuplesOnly = true).trim()
	return output.toIntOrNull() ?: 0
}

private fun queryObjects(
	target: EditorialTarget,
	sql: String,
	variables: Map<String, String> = emptyMap(),
): List<JsonObject> {
	val output = runPsql(target, sql, variables, tuplesOnly = true)
	return output.lineSequence().map { it.trim() }.filter { it.isNotEmpty() }.map { parseObject(it) }.toList()
}

private fun parseObject(line: String): JsonObject {
	val element = try {
		json.parseToJsonElement(line)
	} catch (error: SerializationException) {
		throw EditorialInputException("Unexpected database response. ${error.message}", error)
	}
	return element as? JsonObject ?: throw EditorialInputException("Unexpected database response.")
}

private fun runPsql(
	target: EditorialTarget,
	sql: String,
	variables: Map<String, String>,
	tuplesOnly: Boolean,
): String {
	val process = startPsql(target, sql, variables, tuplesOnly)
	val output = process.inputStream.bufferedReader().use { reader -> reader.readText() }
	val exitCode = process.waitFor()
	if (exitCode != 0) {
		throw EditorialInputException("The database command failed.")
	}
	if (!tuplesOnly && output.isNotBlank()) {
		print(output)
	}
	return output
}

private fun startPsql(
	target: EditorialTarget,
	sql: String,
	variables: Map<String, String>,
	tuplesOnly: Boolean,
): Process {
	val builder = ProcessBuilder(psqlCommand(target, sql, variables, tuplesOnly))
	builder.redirectError(ProcessBuilder.Redirect.INHERIT)
	applyConnectionEnvironment(builder, target)
	val process = try {
		builder.start()
	} catch (error: IOException) {
		throw EditorialInputException("Could not run psql. ${error.message}", error)
	}
	process.outputStream.close()
	return process
}

private fun applyConnectionEnvironment(builder: ProcessBuilder, target: EditorialTarget) {
	val environment = builder.environment()
	environment["PGPASSWORD"] = target.password.concatToString()
	environment["PGSSLMODE"] = target.endpoint.sslMode
	environment["PGAPPNAME"] = "purecipes-editorial"
}

private fun psqlCommand(
	target: EditorialTarget,
	sql: String,
	variables: Map<String, String>,
	tuplesOnly: Boolean,
): List<String> {
	val command = mutableListOf<String>()
	command.add(psqlPath())
	command.add("-X")
	command.add("-w")
	command.add("-q")
	command.add("-v")
	command.add("ON_ERROR_STOP=1")
	command.add("-d")
	command.add(target.endpoint.database)
	appendConnection(command, target)
	if (tuplesOnly) {
		command.add("-A")
		command.add("-t")
	}
	command.add("-c")
	command.add(renderEditorialSql(sql, variables))
	return command
}

fun renderEditorialSql(sql: String, variables: Map<String, String>): String {
	var rendered = sql
	variables.forEach { (name, value) ->
		checkVariable(name, value)
		rendered = rendered.replace(":'$name'", sqlLiteral(value))
	}
	listOf("position", "enabled").forEach { name ->
		val value = variables[name]
		val token = rawSqlToken(name)
		if (value != null && token.containsMatchIn(rendered)) {
			rendered = rendered.replace(token, sqlRaw(name, value))
		}
	}
	return rendered
}

private fun sqlLiteral(value: String): String = "'${value.replace("'", "''")}'"

private fun sqlRaw(name: String, value: String): String {
	val allowed = when (name) {
		"position" -> value.toIntOrNull() != null
		"enabled" -> value == "TRUE" || value == "FALSE"
		else -> false
	}
	if (!allowed) {
		throw EditorialInputException("Invalid value for $name.")
	}
	return value
}

private fun rawSqlToken(name: String): Regex = Regex("(?<!:):$name\\b")

private fun appendConnection(command: MutableList<String>, target: EditorialTarget) {
	command.add("-h")
	command.add(target.endpoint.host)
	command.add("-p")
	command.add(target.endpoint.port.toString())
	command.add("-U")
	command.add(target.endpoint.user)
}

private fun checkVariable(name: String, value: String) {
	val validName = name.matches(Regex("^[A-Za-z_][A-Za-z0-9_]*$"))
	if (!validName) {
		throw EditorialInputException("Invalid query variable.")
	}
	rejectControlCharacters(value, name)
}

private fun psqlPath(): String {
	val cached = resolvedPsql
	if (cached != null) {
		return cached
	}
	val found = locatePsql()
	resolvedPsql = found
	return found
}

private fun locatePsql(): String {
	val which = try {
		ProcessBuilder("which", "psql").redirectError(ProcessBuilder.Redirect.DISCARD).start()
	} catch (error: IOException) {
		throw EditorialInputException("psql was not found on PATH. ${error.message}", error)
	}
	val output = which.inputStream.bufferedReader().use { reader -> reader.readText() }.trim()
	val exitCode = which.waitFor()
	val path = output.lineSequence().firstOrNull { it.isNotBlank() }
	if (exitCode != 0 || path == null) {
		throw EditorialInputException("psql was not found on PATH.")
	}
	return path
}

private fun validateFilterFields(payload: JsonObject) {
	payload.forEach { (field, value) ->
		if (field !in filterFields) {
			throw EditorialInputException("Unknown filter field: $field.")
		}
		if (!isStringList(value)) {
			throw EditorialInputException("$field must be a list of strings.")
		}
	}
}

private fun isStringList(value: JsonElement): Boolean {
	val items = value as? JsonArray ?: return false
	return items.all { item -> item is JsonPrimitive && item.isString }
}

private fun rejectControlCharacters(value: String, label: String) {
	if (value.any { it == '\n' || it == '\r' || it == '\u0000' }) {
		throw EditorialInputException("$label cannot contain line breaks.")
	}
}

private fun JsonObject.text(name: String): String {
	val value = this[name]
	return if (value == null || value is JsonNull) "" else value.jsonPrimitive.content
}

private fun JsonObject.int(name: String): Int = text(name).toInt()

private fun JsonObject.bool(name: String): Boolean = text(name).toBoolean()

private fun insertSql(recipeIds: List<Int>): String {
	return "BEGIN; $insertCollectionSql; ${recipeInsertSql(recipeIds)} COMMIT;"
}

private fun replaceRecipesSql(recipeIds: List<Int>): String {
	return "BEGIN; $deleteRecipesSql; ${recipeInsertSql(recipeIds)} COMMIT;"
}

private fun recipeInsertSql(recipeIds: List<Int>): String {
	if (recipeIds.isEmpty()) {
		return ""
	}
	val ids = recipeIds.joinToString(",")
	return """
		INSERT INTO home_collection_recipes (collection_id, recipe_id, position)
		SELECT c.id, listed.recipe_id, listed.ordinality::int - 1
		FROM home_collections c
		CROSS JOIN unnest(ARRAY[$ids]::int[]) WITH ORDINALITY AS listed(recipe_id, ordinality)
		WHERE c.slug = :'slug';
	""".trimIndent()
}

private fun recipesByIdSql(ids: List<Int>): String {
	val idList = ids.joinToString(",")
	return """
		SELECT json_build_object(
			'id', id,
			'title', title,
			'private', is_private
		)::text
		FROM recipes
		WHERE id IN ($idList)
	""".trimIndent()
}

private val collectionSelectSql = """
	SELECT json_build_object(
		'id', id,
		'slug', slug,
		'title', title,
		'source', source,
		'filterPayload', filter_payload,
		'sort', sort,
		'position', position,
		'enabled', enabled,
		'startsAt', to_char(starts_at, 'YYYY-MM-DD HH24:MI:SS'),
		'endsAt', to_char(ends_at, 'YYYY-MM-DD HH24:MI:SS'),
		'live', (
			enabled
			AND (starts_at IS NULL OR starts_at <= CURRENT_TIMESTAMP)
			AND (ends_at IS NULL OR ends_at > CURRENT_TIMESTAMP)
		),
		'recipeCount', (
			SELECT COUNT(*)::int
			FROM home_collection_recipes AS membership
			WHERE membership.collection_id = home_collections.id
		)
	)::text
	FROM home_collections
""".trimIndent()

private val listCollectionsSql = "$collectionSelectSql ORDER BY position, id"

private val oneCollectionSql = "$collectionSelectSql WHERE slug = :'slug'"

private val findRecipesSql = """
	SELECT json_build_object(
		'id', id,
		'title', title,
		'private', is_private
	)::text
	FROM recipes
	WHERE title ILIKE :'pattern' ESCAPE '\'
	ORDER BY title, id
	LIMIT $SEARCH_LIMIT
""".trimIndent()

private val collectionRecipesSql = """
	SELECT json_build_object(
		'id', r.id,
		'title', r.title,
		'private', r.is_private,
		'position', hcr.position
	)::text
	FROM home_collection_recipes hcr
	JOIN recipes r ON r.id = hcr.recipe_id
	JOIN home_collections c ON c.id = hcr.collection_id
	WHERE c.slug = :'slug'
	ORDER BY hcr.position, r.id
""".trimIndent()

private val nextPositionSql =
	"SELECT COALESCE(MAX(position), $EMPTY_MAX_POSITION) + $POSITION_STEP FROM home_collections"

private val insertCollectionSql = """
	INSERT INTO home_collections (
		slug, title, source, filter_payload, sort, position, enabled, starts_at, ends_at
	) VALUES (
		:'slug',
		:'title',
		:'source',
		NULLIF(:'filter_payload', ''),
		:'sort',
		:position,
		:enabled,
		NULLIF(:'starts_at', '')::timestamp,
		NULLIF(:'ends_at', '')::timestamp
	)
""".trimIndent()

private val updateCollectionSql = """
	UPDATE home_collections
	SET slug = :'slug',
		title = :'title',
		source = :'source',
		filter_payload = NULLIF(:'filter_payload', ''),
		sort = :'sort',
		position = :position,
		enabled = :enabled,
		starts_at = NULLIF(:'starts_at', '')::timestamp,
		ends_at = NULLIF(:'ends_at', '')::timestamp
	WHERE slug = :'original_slug'
""".trimIndent()

private val deleteRecipesSql = """
	DELETE FROM home_collection_recipes
	WHERE collection_id = (SELECT id FROM home_collections WHERE slug = :'slug')
""".trimIndent()

private const val DELETE_COLLECTION_SQL = "DELETE FROM home_collections WHERE slug = :'slug'"
