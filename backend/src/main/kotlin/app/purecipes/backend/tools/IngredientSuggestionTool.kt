package app.purecipes.backend.tools

import app.purecipes.backend.feature.nutrition.FDC_BRANDED_SOURCE_NAME
import app.purecipes.backend.feature.nutrition.FdcFoodMatchingSupport
import app.purecipes.backend.feature.nutrition.IngredientLineParser
import app.purecipes.backend.feature.nutrition.NutritionFoodRecord
import app.purecipes.backend.feature.nutrition.NutritionLookupIndex
import app.purecipes.backend.feature.nutrition.NutritionLookupRepository
import app.purecipes.backend.feature.nutrition.NutritionNameNormalizer
import java.nio.file.Files
import java.nio.file.Path
import java.util.Locale
import javax.sql.DataSource
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

private const val MAX_CANDIDATES_PER_NAME = 5
private const val SYNONYM_ALIAS_SCORE = 0.95
private const val SUFFIX_ALIAS_BASE_SCORE = 0.86
private const val SUFFIX_TOKEN_BONUS = 0.03
private const val PREFIX_ALIAS_BASE_SCORE = 0.80
private const val PREFIX_TOKEN_BONUS = 0.03
private const val MIN_LEXICAL_TRIGRAM_SCORE = 0.42
private const val TOKEN_OVERLAP_WEIGHT = 0.55
private const val HEAD_NOUN_WEIGHT = 0.20
private const val TRIGRAM_WEIGHT = 0.25
private const val BRANDED_SOURCE_PENALTY = 0.12
private const val TRIGRAM_SIZE = 3
private const val SCORE_FORMAT_DECIMALS = "%.2f"
private const val MAX_ALTERNATIVE_CANDIDATES = 3
private const val MIN_SUFFIX_TOKEN_COUNT = 1
private const val MAX_SUFFIX_TOKEN_COUNT = 3
private const val MAX_PREFIX_TOKEN_COUNT = 2
private const val MIN_PLURAL_IES_ES_LENGTH = 4
private const val MIN_PLURAL_S_LENGTH = 3

internal enum class IngredientSuggestionScope {
	UNMATCHED,
	TOKENS,
	UNPINNED,
	ALL,
}

internal data class SampleIngredientContext(
	val recipeTitle: String,
	val rawText: String,
)

internal data class IngredientSuggestionRow(
	val lookupName: String,
	val status: String,
	val pantryItem: String,
	val fdcSource: String,
	val fdcId: String,
	val fdcDescription: String,
	val matchSource: String,
	val lineCount: Int,
	val confidence: String,
	val currentPantryItem: String,
	val currentFdcSource: String,
	val currentFdcId: String,
	val currentFdcDescription: String,
	val currentMatchSource: String,
	val sampleRecipeTitle: String,
	val sampleRawText: String,
	val alternativeCandidates: String,
	val rationale: String,
)

private object SuggestionColumnIndex {

	const val LOOKUP_NAME = 0
	const val STATUS = 1
	const val PANTRY_ITEM = 2
	const val FDC_SOURCE = 3
	const val FDC_ID = 4
	const val FDC_DESCRIPTION = 5
	const val MATCH_SOURCE = 6
	const val LINE_COUNT = 7
	const val CONFIDENCE = 8
	const val CURRENT_PANTRY_ITEM = 9
	const val CURRENT_FDC_SOURCE = 10
	const val CURRENT_FDC_ID = 11
	const val CURRENT_FDC_DESCRIPTION = 12
	const val CURRENT_MATCH_SOURCE = 13
	const val SAMPLE_RECIPE_TITLE = 14
	const val SAMPLE_RAW_TEXT = 15
	const val ALTERNATIVE_CANDIDATES = 16
	const val RATIONALE = 17
}

internal class IngredientSuggestionTool(
	private val dataSource: DataSource,
	private val reranker: IngredientSuggestionReranker? = null,
) {

	fun generateSuggestions(
		snapshot: IngredientSnapshotData = IngredientSnapshotExporter(dataSource).collect(),
		scope: IngredientSuggestionScope = IngredientSuggestionScope.UNMATCHED,
		queryFilter: String = "",
		limit: Int? = null,
	): List<IngredientSuggestionRow> {
		val lookupIndex = NutritionLookupRepository(dataSource).loadIndex()
		val sampleContextByLookupName = loadSampleContextByLookupName()
		val targetNames = snapshot.names
			.filter { row -> matchesScope(row, scope) && matchesQuery(row.lookupName, queryFilter) }
			.let { rows -> if (limit != null && limit > 0) rows.take(limit) else rows }

		return targetNames.map { row ->
			buildSuggestionForRow(
				row = row,
				lookupIndex = lookupIndex,
				sampleContext = sampleContextByLookupName[row.lookupName],
			)
		}
	}

	private fun matchesScope(row: IngredientNameSnapshotRow, scope: IngredientSuggestionScope): Boolean =
		when (scope) {
			IngredientSuggestionScope.UNMATCHED ->
				row.status != IngredientSnapshotExporter.STATUS_PINNED && row.fdcId.isBlank()

			IngredientSuggestionScope.TOKENS ->
				row.status != IngredientSnapshotExporter.STATUS_PINNED &&
					(row.fdcId.isBlank() || row.matchSource == "tokens")

			IngredientSuggestionScope.UNPINNED ->
				row.status != IngredientSnapshotExporter.STATUS_PINNED

			IngredientSuggestionScope.ALL -> true
		}

	private fun matchesQuery(lookupName: String, queryFilter: String): Boolean {
		val trimmed = queryFilter.trim().lowercase(Locale.ROOT)
		return trimmed.isEmpty() || lookupName.lowercase(Locale.ROOT).contains(trimmed)
	}

	private fun buildSuggestionForRow(
		row: IngredientNameSnapshotRow,
		lookupIndex: NutritionLookupIndex,
		sampleContext: SampleIngredientContext?,
	): IngredientSuggestionRow {
		val candidates = IngredientCandidateScorer.rankFoodCandidates(row.lookupName, lookupIndex)
		val suggestedPantry = suggestPantryItem(row.lookupName, row.pantryItem)
		val sampleTitle = sampleContext?.recipeTitle.orEmpty()
		val sampleRawText = sampleContext?.rawText.orEmpty()
		val semanticDecision = reranker?.rerank(
			IngredientSemanticRerankInput(
				lookupName = row.lookupName,
				sampleRecipeTitle = sampleTitle,
				sampleRawText = sampleRawText,
				currentPantryItem = row.pantryItem,
				suggestedPantryItem = suggestedPantry,
				foodCandidates = candidates,
			),
		)
		val topCandidate = candidates.firstOrNull()
		val resolvedSource = semanticDecision?.fdcSource ?: topCandidate?.fdcSource.orEmpty()
		val resolvedId = semanticDecision?.fdcId ?: topCandidate?.fdcId.orEmpty()
		val resolvedDesc = semanticDecision?.fdcDescription ?: topCandidate?.fdcDescription.orEmpty()
		val resolvedMethod = if (semanticDecision != null) "embedding" else topCandidate?.method.orEmpty()
		val resolvedConfidence = semanticDecision?.confidence
			?: topCandidate?.score?.let { String.format(Locale.ROOT, SCORE_FORMAT_DECIMALS, it) }.orEmpty()
		val resolvedRationale = semanticDecision?.rationale ?: topCandidate?.rationale.orEmpty()
		val runnerUps = candidates
			.filterNot { it.fdcSource == resolvedSource && it.fdcId == resolvedId }
			.take(MAX_ALTERNATIVE_CANDIDATES)
			.joinToString(" | ") { candidate ->
				"${candidate.fdcSource}:${candidate.fdcId} (${candidate.fdcDescription})"
			}

		return IngredientSuggestionRow(
			lookupName = row.lookupName,
			status = STATUS_SUGGESTED,
			pantryItem = semanticDecision?.pantryItem ?: suggestedPantry,
			fdcSource = resolvedSource,
			fdcId = resolvedId,
			fdcDescription = resolvedDesc,
			matchSource = resolvedMethod,
			lineCount = row.lineCount,
			confidence = resolvedConfidence,
			currentPantryItem = row.pantryItem,
			currentFdcSource = row.fdcSource,
			currentFdcId = row.fdcId,
			currentFdcDescription = row.fdcDescription,
			currentMatchSource = row.matchSource,
			sampleRecipeTitle = sampleTitle,
			sampleRawText = sampleRawText,
			alternativeCandidates = runnerUps,
			rationale = resolvedRationale,
		)
	}

	private fun suggestPantryItem(lookupName: String, currentPantryItem: String): String {
		if (currentPantryItem.isNotBlank()) {
			return currentPantryItem
		}
		val candidates = IngredientCandidateScorer.expandQueryVariants(lookupName)
		return candidates.firstNotNullOfOrNull { variant ->
			PantryItemResolver.resolve(variant).takeIf(String::isNotBlank)
		}.orEmpty()
	}

	private fun loadSampleContextByLookupName(): Map<String, SampleIngredientContext> =
		dataSource.connection.use { connection ->
			connection.prepareStatement(
				"""
				SELECT
					r.title,
					i.ingredient,
					im.parsed_name
				FROM recipes r
				JOIN ingredient_groups ig ON ig.recipe_id = r.id
				JOIN ingredients i ON i.ingredient_group_id = ig.id
				LEFT JOIN ingredient_measurements im ON im.ingredient_id = i.id
				WHERE r.source_url IS NOT NULL
					AND TRIM(r.source_url) <> ''
					AND i.ingredient IS NOT NULL
				ORDER BY r.source_url ASC, ig.order_index ASC, i.order_index ASC
				""".trimIndent(),
			).use { statement ->
				statement.executeQuery().use(::extractSampleContextMap)
			}
		}

	private fun extractSampleContextMap(rs: java.sql.ResultSet): Map<String, SampleIngredientContext> {
		val map = linkedMapOf<String, SampleIngredientContext>()
		while (rs.next()) {
			val title = rs.getString("title")?.trim().orEmpty()
			val rawText = rs.getString("ingredient")?.trim().orEmpty()
			val storedParsed = rs.getString("parsed_name")?.trim().orEmpty()
			val lookupName = if (storedParsed.isNotBlank()) {
				NutritionNameNormalizer.forLookup(storedParsed)
			} else {
				val parsed = IngredientLineParser.parse(rawText)
				NutritionNameNormalizer.forLookup(parsed.parsedName)
			}
			if (lookupName.isNotBlank() && lookupName !in map) {
				map[lookupName] = SampleIngredientContext(
					recipeTitle = title,
					rawText = rawText,
				)
			}
		}
		return map
	}

	companion object {

		const val SUGGESTIONS_FILE_NAME = "suggestions.tsv"
		const val STATUS_SUGGESTED = "suggested"

		private const val SUGGESTIONS_HEADER =
			"lookup_name\tstatus\tpantry_item\tfdc_source\tfdc_id\tfdc_description\tmatch_source\tline_count\t" +
				"confidence\tcurrent_pantry_item\tcurrent_fdc_source\tcurrent_fdc_id\tcurrent_fdc_description\t" +
				"current_match_source\tsample_recipe_title\tsample_raw_text\talternative_candidates\trationale"

		fun writeSuggestionsFile(outputDir: Path, rows: List<IngredientSuggestionRow>): Path {
			Files.createDirectories(outputDir)
			val file = outputDir.resolve(SUGGESTIONS_FILE_NAME)
			file.writeText(formatSuggestionsTsv(rows))
			return file
		}

		fun formatSuggestionsTsv(rows: List<IngredientSuggestionRow>): String = buildString {
			appendLine(SUGGESTIONS_HEADER)
			rows.sortedBy(IngredientSuggestionRow::lookupName).forEach { row ->
				append(escapeTsv(row.lookupName)).append('\t')
				append(escapeTsv(row.status)).append('\t')
				append(escapeTsv(row.pantryItem)).append('\t')
				append(escapeTsv(row.fdcSource)).append('\t')
				append(escapeTsv(row.fdcId)).append('\t')
				append(escapeTsv(row.fdcDescription)).append('\t')
				append(escapeTsv(row.matchSource)).append('\t')
				append(row.lineCount).append('\t')
				append(escapeTsv(row.confidence)).append('\t')
				append(escapeTsv(row.currentPantryItem)).append('\t')
				append(escapeTsv(row.currentFdcSource)).append('\t')
				append(escapeTsv(row.currentFdcId)).append('\t')
				append(escapeTsv(row.currentFdcDescription)).append('\t')
				append(escapeTsv(row.currentMatchSource)).append('\t')
				append(escapeTsv(row.sampleRecipeTitle)).append('\t')
				append(escapeTsv(row.sampleRawText)).append('\t')
				append(escapeTsv(row.alternativeCandidates)).append('\t')
				appendLine(escapeTsv(row.rationale))
			}
		}

		fun parseSuggestionsTsv(content: String): List<IngredientSuggestionRow> =
			content.lineSequence()
				.drop(1)
				.filter { it.isNotBlank() }
				.map { line ->
					val cols = line.split('\t')
					IngredientSuggestionRow(
						lookupName = cols.getOrElse(SuggestionColumnIndex.LOOKUP_NAME) { "" },
						status = cols.getOrElse(SuggestionColumnIndex.STATUS) { STATUS_SUGGESTED },
						pantryItem = cols.getOrElse(SuggestionColumnIndex.PANTRY_ITEM) { "" },
						fdcSource = cols.getOrElse(SuggestionColumnIndex.FDC_SOURCE) { "" },
						fdcId = cols.getOrElse(SuggestionColumnIndex.FDC_ID) { "" },
						fdcDescription = cols.getOrElse(SuggestionColumnIndex.FDC_DESCRIPTION) { "" },
						matchSource = cols.getOrElse(SuggestionColumnIndex.MATCH_SOURCE) { "" },
						lineCount = cols.getOrElse(SuggestionColumnIndex.LINE_COUNT) { "0" }.toIntOrNull() ?: 0,
						confidence = cols.getOrElse(SuggestionColumnIndex.CONFIDENCE) { "" },
						currentPantryItem = cols.getOrElse(SuggestionColumnIndex.CURRENT_PANTRY_ITEM) { "" },
						currentFdcSource = cols.getOrElse(SuggestionColumnIndex.CURRENT_FDC_SOURCE) { "" },
						currentFdcId = cols.getOrElse(SuggestionColumnIndex.CURRENT_FDC_ID) { "" },
						currentFdcDescription = cols.getOrElse(SuggestionColumnIndex.CURRENT_FDC_DESCRIPTION) { "" },
						currentMatchSource = cols.getOrElse(SuggestionColumnIndex.CURRENT_MATCH_SOURCE) { "" },
						sampleRecipeTitle = cols.getOrElse(SuggestionColumnIndex.SAMPLE_RECIPE_TITLE) { "" },
						sampleRawText = cols.getOrElse(SuggestionColumnIndex.SAMPLE_RAW_TEXT) { "" },
						alternativeCandidates = cols.getOrElse(SuggestionColumnIndex.ALTERNATIVE_CANDIDATES) { "" },
						rationale = cols.getOrElse(SuggestionColumnIndex.RATIONALE) { "" },
					)
				}
				.toList()

		fun acceptPinnedSuggestions(outputDir: Path): Int {
			val namesFile = outputDir.resolve(IngredientSnapshotExporter.NAMES_FILE_NAME)
			val suggestionsFile = outputDir.resolve(SUGGESTIONS_FILE_NAME)
			if (!namesFile.exists() || !suggestionsFile.exists()) {
				return 0
			}
			val existingNames = IngredientSnapshotExporter.parseNamesTsv(namesFile.readText())
				.associateBy(IngredientNameSnapshotRow::lookupName)
				.toMutableMap()
			val acceptedRows = parseSuggestionsTsv(suggestionsFile.readText())
				.filter { row -> row.status == IngredientSnapshotExporter.STATUS_PINNED && row.lookupName.isNotBlank() }
			acceptedRows.forEach { suggestion ->
				val existing = existingNames[suggestion.lookupName]
				existingNames[suggestion.lookupName] = IngredientNameSnapshotRow(
					lookupName = suggestion.lookupName,
					status = IngredientSnapshotExporter.STATUS_PINNED,
					pantryItem = suggestion.pantryItem,
					fdcSource = suggestion.fdcSource,
					fdcId = suggestion.fdcId,
					fdcDescription = suggestion.fdcDescription,
					matchSource = IngredientSnapshotExporter.STATUS_PINNED,
					lineCount = existing?.lineCount ?: suggestion.lineCount,
				)
			}
			if (acceptedRows.isNotEmpty()) {
				val sorted = existingNames.values.sortedBy(IngredientNameSnapshotRow::lookupName)
				namesFile.writeText(IngredientSnapshotExporter.formatNamesTsv(sorted))
			}
			return acceptedRows.size
		}

		private fun escapeTsv(value: String): String =
			value
				.replace('\t', ' ')
				.replace('\r', ' ')
				.replace('\n', ' ')
				.trim()
	}
}

internal object IngredientCandidateScorer {

	private val tokenSynonyms = mapOf(
		"aubergine" to "eggplant",
		"aubergines" to "eggplant",
		"bucatini" to "pasta",
		"bulkie" to "roll",
		"caster" to "granulated",
		"chile" to "chili",
		"chiles" to "chili",
		"chilies" to "chili",
		"chilli" to "chili",
		"chillies" to "chili",
		"conserve" to "jam",
		"coriander" to "cilantro",
		"courgette" to "zucchini",
		"courgettes" to "zucchini",
		"dpring" to "spring",
		"elbows" to "macaroni",
		"flanken" to "beef short ribs",
		"fries" to "french fries",
		"fusilli" to "pasta",
		"galangal" to "ginger",
		"rigatoni" to "pasta",
		"stock" to "broth",
		"yoghurt" to "yogurt",
	)

	private val phraseSynonyms = mapOf(
		"crema acida" to "sour cream",
		"crema acida found latin markets" to "sour cream",
		"deli rolls" to "dinner rolls",
		"spring onions" to "green onions",
		"dpring onions" to "green onions",
	)

	fun expandQueryVariants(lookupName: String): List<String> {
		val normalized = NutritionNameNormalizer.forLookup(lookupName)
		val tokens = NutritionNameNormalizer.tokens(normalized)
		if (tokens.isEmpty()) {
			return emptyList()
		}
		val synonymReplaced = phraseSynonyms[normalized] ?: tokens.joinToString(" ") { token ->
			tokenSynonyms[token] ?: token
		}
		val suffixes = (MAX_SUFFIX_TOKEN_COUNT downTo MIN_SUFFIX_TOKEN_COUNT)
			.filter { len -> len < tokens.size }
			.flatMap { len ->
				val rawSuffix = tokens.takeLast(len).joinToString(" ")
				val synSuffix = tokens.takeLast(len).joinToString(" ") { token -> tokenSynonyms[token] ?: token }
				listOf(rawSuffix, synSuffix)
			}
		val prefixes = (MAX_PREFIX_TOKEN_COUNT downTo MIN_SUFFIX_TOKEN_COUNT)
			.filter { len -> len < tokens.size }
			.flatMap { len ->
				val rawPrefix = tokens.take(len).joinToString(" ")
				val synPrefix = tokens.take(len).joinToString(" ") { token -> tokenSynonyms[token] ?: token }
				listOf(rawPrefix, synPrefix)
			}
		return (listOf(normalized, synonymReplaced) + suffixes + prefixes)
			.map(NutritionNameNormalizer::forLookup)
			.filter(String::isNotBlank)
			.distinct()
	}

	fun rankFoodCandidates(
		lookupName: String,
		lookupIndex: NutritionLookupIndex,
	): List<SuggestedFoodCandidate> {
		val normalized = NutritionNameNormalizer.forLookup(lookupName)
		val tokens = NutritionNameNormalizer.tokens(normalized)
		if (tokens.isEmpty()) {
			return emptyList()
		}
		val candidateByFoodId = linkedMapOf<Int, SuggestedFoodCandidate>()

		collectSynonymAndSubphraseCandidates(normalized, tokens, lookupIndex, candidateByFoodId)
		collectLexicalTrigramCandidates(normalized, tokens, lookupIndex, candidateByFoodId)

		return candidateByFoodId.values
			.sortedWith(
				compareByDescending<SuggestedFoodCandidate> { it.score }
					.thenBy { FdcFoodMatchingSupport.sourcePriority(it.fdcSource) }
					.thenBy { it.fdcDescription.length },
			)
			.take(MAX_CANDIDATES_PER_NAME)
	}

	private fun collectSynonymAndSubphraseCandidates(
		normalized: String,
		tokens: List<String>,
		lookupIndex: NutritionLookupIndex,
		candidateByFoodId: MutableMap<Int, SuggestedFoodCandidate>,
	) {
		val synonymQuery = phraseSynonyms[normalized] ?: tokens.joinToString(" ") { token ->
			tokenSynonyms[token] ?: token
		}
		if (synonymQuery != normalized) {
			findDirectFood(synonymQuery, lookupIndex)?.let { food ->
				putCandidateIfBetter(
					candidateByFoodId = candidateByFoodId,
					food = food,
					method = "synonym_alias",
					score = SYNONYM_ALIAS_SCORE,
					rationale = "Matched normalized synonym '$synonymQuery'",
				)
			}
		}

		for (len in minOf(MAX_SUFFIX_TOKEN_COUNT, tokens.size - 1) downTo MIN_SUFFIX_TOKEN_COUNT) {
			val suffixVariants = listOf(
				tokens.takeLast(len).joinToString(" "),
				tokens.takeLast(len).joinToString(" ") { token -> tokenSynonyms[token] ?: token },
			).distinct()
			suffixVariants.forEach { suffix ->
				findDirectFood(suffix, lookupIndex)?.let { food ->
					putCandidateIfBetter(
						candidateByFoodId = candidateByFoodId,
						food = food,
						method = "suffix_alias",
						score = SUFFIX_ALIAS_BASE_SCORE + len * SUFFIX_TOKEN_BONUS,
						rationale = "Matched trailing head phrase '$suffix'",
					)
				}
			}
		}

		for (len in minOf(MAX_PREFIX_TOKEN_COUNT, tokens.size - 1) downTo MIN_SUFFIX_TOKEN_COUNT) {
			val prefixVariants = listOf(
				tokens.take(len).joinToString(" "),
				tokens.take(len).joinToString(" ") { token -> tokenSynonyms[token] ?: token },
			).distinct()
			prefixVariants.forEach { prefix ->
				findDirectFood(prefix, lookupIndex)?.let { food ->
					putCandidateIfBetter(
						candidateByFoodId = candidateByFoodId,
						food = food,
						method = "prefix_alias",
						score = PREFIX_ALIAS_BASE_SCORE + len * PREFIX_TOKEN_BONUS,
						rationale = "Matched leading food phrase '$prefix'",
					)
				}
			}
		}
	}

	private fun collectLexicalTrigramCandidates(
		normalized: String,
		tokens: List<String>,
		lookupIndex: NutritionLookupIndex,
		candidateByFoodId: MutableMap<Int, SuggestedFoodCandidate>,
	) {
		val expandedQueryTokens = tokens
			.flatMap { token -> listOfNotNull(token, tokenSynonyms[token], singularize(token)) }
			.toSet()
		val headNoun = tokens.last()
		val headVariants = setOfNotNull(headNoun, tokenSynonyms[headNoun], singularize(headNoun))
		val queryTrigrams = trigrams(normalized)

		lookupIndex.foodById.values.forEach { food ->
			val candidateTokens = NutritionNameNormalizer.tokens(food.normalizedName)
			val candidateTokenSet = candidateTokens
				.flatMap { token -> listOfNotNull(token, singularize(token)) }
				.toSet()
			val sharedCount = expandedQueryTokens.count { it in candidateTokenSet }
			if (sharedCount > 0) {
				val overlapRatio = sharedCount.toDouble() / tokens.size.coerceAtLeast(1).toDouble()
				val headMatch =
					if (candidateTokens.take(2).any { it in headVariants || singularize(it) in headVariants }) {
						1.0
					} else if (candidateTokenSet.any { it in headVariants }) {
						0.5
					} else {
						0.0
					}
				val trigramSim = trigramDiceSimilarity(queryTrigrams, trigrams(food.normalizedName))
				val brandedPenalty = if (food.sourceName == FDC_BRANDED_SOURCE_NAME) BRANDED_SOURCE_PENALTY else 0.0
				val combinedScore = (
					overlapRatio * TOKEN_OVERLAP_WEIGHT +
						headMatch * HEAD_NOUN_WEIGHT +
						trigramSim * TRIGRAM_WEIGHT -
						brandedPenalty
					).coerceIn(0.0, 0.90)
				if (combinedScore >= MIN_LEXICAL_TRIGRAM_SCORE) {
					putCandidateIfBetter(
						candidateByFoodId = candidateByFoodId,
						food = food,
						method = "lexical_trigram",
						score = combinedScore,
						rationale = "Token overlap ($sharedCount/${tokens.size}) + trigram similarity",
					)
				}
			}
		}
	}

	private fun findDirectFood(
		query: String,
		lookupIndex: NutritionLookupIndex,
	): NutritionFoodRecord? {
		val normalized = NutritionNameNormalizer.normalize(query)
		val lookup = NutritionNameNormalizer.forLookup(query)
		val aliasId = lookupIndex.foodIdByNormalizedAlias[normalized]
			?: lookupIndex.foodIdByNormalizedAlias[lookup]
		val exactFood = lookupIndex.foodById.values
			.filter { it.normalizedName == normalized || it.normalizedName == lookup }
			.minByOrNull { FdcFoodMatchingSupport.sourcePriority(it.sourceName) }
		return aliasId?.let(lookupIndex::food) ?: exactFood
	}

	private fun putCandidateIfBetter(
		candidateByFoodId: MutableMap<Int, SuggestedFoodCandidate>,
		food: NutritionFoodRecord,
		method: String,
		score: Double,
		rationale: String,
	) {
		if (food.sourceName.isBlank() || food.sourceId.isBlank()) {
			return
		}
		val existing = candidateByFoodId[food.id]
		if (existing == null || score > existing.score) {
			candidateByFoodId[food.id] = SuggestedFoodCandidate(
				fdcSource = food.sourceName,
				fdcId = food.sourceId,
				fdcDescription = food.displayName,
				method = method,
				score = score,
				rationale = rationale,
			)
		}
	}

	private fun singularize(token: String): String? =
		when {
			token.endsWith("ies") && token.length > MIN_PLURAL_IES_ES_LENGTH ->
				token.removeSuffix("ies") + "y"

			token.endsWith("es") && token.length > MIN_PLURAL_IES_ES_LENGTH ->
				token.removeSuffix("es")

			token.endsWith("s") && !token.endsWith("ss") && token.length > MIN_PLURAL_S_LENGTH ->
				token.removeSuffix("s")

			else -> null
		}

	internal fun trigrams(text: String): Set<String> {
		val padded = "  ${text.trim().lowercase(Locale.ROOT)} "
		if (padded.length < TRIGRAM_SIZE) {
			return emptySet()
		}
		return (0..(padded.length - TRIGRAM_SIZE))
			.map { idx -> padded.substring(idx, idx + TRIGRAM_SIZE) }
			.toSet()
	}

	internal fun trigramDiceSimilarity(left: Set<String>, right: Set<String>): Double =
		if (left.isEmpty() || right.isEmpty()) {
			0.0
		} else {
			val intersection = left.count { it in right }
			(2.0 * intersection.toDouble()) / (left.size + right.size).toDouble()
		}
}
