package app.purecipes.backend.tools

import app.purecipes.backend.db.Db
import java.io.File
import java.util.Locale

private const val ARG_DIR_PREFIX = "--dir="
private const val ARG_SCOPE_PREFIX = "--scope="
private const val ARG_QUERY_PREFIX = "--query="
private const val ARG_LIMIT_PREFIX = "--limit="
private const val ARG_MODEL_PATH_PREFIX = "--model-path="
private const val ARG_ACCEPT = "--accept"

fun main(args: Array<String>) {
	val dirPath = args.firstOrNull { it.startsWith(ARG_DIR_PREFIX) }
		?.removePrefix(ARG_DIR_PREFIX)
		?.takeIf(String::isNotBlank)
	val snapshotDir = resolveSuggestionDir(dirPath)

	if (args.contains(ARG_ACCEPT)) {
		val accepted = IngredientSuggestionTool.acceptPinnedSuggestions(snapshotDir.toPath())
		println("Accepted $accepted pinned suggestions into ${snapshotDir.absolutePath}/names.tsv")
		return
	}

	val scopeArg = args.firstOrNull { it.startsWith(ARG_SCOPE_PREFIX) }
		?.removePrefix(ARG_SCOPE_PREFIX)
		?.lowercase(Locale.ROOT)
		.orEmpty()
	val scope = when (scopeArg) {
		"tokens" -> IngredientSuggestionScope.TOKENS
		"unpinned" -> IngredientSuggestionScope.UNPINNED
		"all" -> IngredientSuggestionScope.ALL
		else -> IngredientSuggestionScope.UNMATCHED
	}
	val queryFilter = args.firstOrNull { it.startsWith(ARG_QUERY_PREFIX) }
		?.removePrefix(ARG_QUERY_PREFIX)
		.orEmpty()
	val limit = args.firstOrNull { it.startsWith(ARG_LIMIT_PREFIX) }
		?.removePrefix(ARG_LIMIT_PREFIX)
		?.toIntOrNull()
	val modelPath = args.firstOrNull { it.startsWith(ARG_MODEL_PATH_PREFIX) }
		?.removePrefix(ARG_MODEL_PATH_PREFIX)
		?.takeIf(String::isNotBlank)
		?: System.getenv("USE_MODEL_PATH").orEmpty()

	val dataSource = Db.create().dataSource
	val namesFile = File(snapshotDir, IngredientSnapshotExporter.NAMES_FILE_NAME)
	val existingNames = if (namesFile.isFile) {
		IngredientSnapshotExporter.parseNamesTsv(namesFile.readText())
	} else {
		emptyList()
	}
	val snapshot = IngredientSnapshotExporter(dataSource).collect(existingNames = existingNames)
	val reranker = LocalEmbeddingIngredientReranker.createOrNull(modelPath)
	reranker.use { activeReranker ->
		val suggestions = IngredientSuggestionTool(dataSource, activeReranker).generateSuggestions(
			snapshot = snapshot,
			scope = scope,
			queryFilter = queryFilter,
			limit = limit,
		)
		val writtenFile = IngredientSuggestionTool.writeSuggestionsFile(snapshotDir.toPath(), suggestions)
		val withFoodCandidate = suggestions.count { it.fdcId.isNotBlank() }
		val withPantryCandidate = suggestions.count { it.pantryItem.isNotBlank() }
		val scopeName = scope.name.lowercase(Locale.ROOT)
		val targetPath = writtenFile.toAbsolutePath()
		println(
			"Wrote ${suggestions.size} suggestions ($withFoodCandidate food, $withPantryCandidate pantry, " +
				"scope=$scopeName, ml=${activeReranker != null}) to $targetPath",
		)
	}
}

private fun resolveSuggestionDir(explicitPath: String?): File {
	if (!explicitPath.isNullOrBlank()) {
		return File(explicitPath)
	}
	val isBackendWorkingDir = File("build.gradle.kts").isFile && File("../settings.gradle.kts").isFile
	return if (isBackendWorkingDir) {
		File("data/ingredients")
	} else {
		File("backend/data/ingredients")
	}
}
