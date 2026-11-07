package app.purecipes.backend.tools

import java.util.Locale

internal data class PinnedNeighborMatch(
	val row: IngredientNameSnapshotRow,
	val similarity: Double,
	val method: String,
	val rationale: String,
)

private const val VECTOR_WEIGHT = 0.65
private const val LEXICAL_WEIGHT = 0.35
private const val DEFAULT_MAX_NEIGHBORS = 3
private const val DEFAULT_MIN_ACCEPTANCE_THRESHOLD = 0.50
private const val EXACT_MATCH_SCORE = 1.0
private const val EMBEDDING_BATCH_SIZE = 256
private const val SCORE_FORMAT = "%.2f"
private const val TRIGRAM_LEN = 3
private const val HEAD_MATCH_BONUS = 0.30
private const val HEAD_MISMATCH_PENALTY = 0.35
private const val CONTAINMENT_BONUS = 0.20
private const val TRIGRAM_WEIGHT = 0.20
private const val JACCARD_WEIGHT = 0.50

private val TOKEN_DELIMITER_REGEX = Regex("[\\s,;()\\-–—/]+")

internal class PinnedIngredientIndex(
	pinnedRows: List<IngredientNameSnapshotRow>,
	private val embedder: IngredientTextEmbedder? = null,
) {

	private val pinnedEntries: List<IngredientNameSnapshotRow> = pinnedRows
		.filter { it.status == IngredientSnapshotExporter.STATUS_PINNED && it.lookupName.isNotBlank() }
		.distinctBy { it.lookupName.lowercase(Locale.ROOT) }

	private val pinnedTokensList: List<List<String>> = pinnedEntries.map { extractTokens(it.lookupName) }

	private val pinnedEmbeddings: Array<FloatArray>? =
		if (embedder != null && pinnedEntries.isNotEmpty()) {
			precomputeEmbeddings(embedder, pinnedEntries.map(IngredientNameSnapshotRow::lookupName))
		} else {
			null
		}

	val size: Int get() = pinnedEntries.size

	fun findNearestNeighbors(
		query: String,
		maxNeighbors: Int = DEFAULT_MAX_NEIGHBORS,
		minScore: Double = DEFAULT_MIN_ACCEPTANCE_THRESHOLD,
	): List<PinnedNeighborMatch> {
		val trimmedQuery = query.trim().lowercase(Locale.ROOT)
		if (trimmedQuery.isEmpty() || pinnedEntries.isEmpty()) {
			return emptyList()
		}
		val queryTokens = extractTokens(trimmedQuery)
		val queryHead = queryTokens.lastOrNull().orEmpty()
		val queryEmb = pinnedEmbeddings?.let {
			embedder?.encodeTexts(listOf(trimmedQuery))?.firstOrNull()
		}

		val scored = pinnedEntries.indices.mapNotNull { index ->
			val row = pinnedEntries[index]
			val targetTokens = pinnedTokensList[index]
			val targetHead = targetTokens.lastOrNull().orEmpty()
			val lexical = scoreLexical(
				query = trimmedQuery,
				target = row.lookupName.lowercase(Locale.ROOT),
				queryTokens = queryTokens,
				targetTokens = targetTokens,
				queryHead = queryHead,
				targetHead = targetHead,
			)
			val score = computeFinalScore(index, lexical, queryEmb)
			if (score >= minScore) {
				val method = if (queryEmb != null) "pinned_knn" else "pinned_lexical"
				val formattedScore = String.format(Locale.ROOT, SCORE_FORMAT, score)
				val rationale = "Nearest pinned neighbor '${row.lookupName}' " +
					"(score=$formattedScore, pantry=${row.pantryItem.ifBlank { "none" }})"
				PinnedNeighborMatch(
					row = row,
					similarity = score,
					method = method,
					rationale = rationale,
				)
			} else {
				null
			}
		}

		return scored
			.sortedByDescending(PinnedNeighborMatch::similarity)
			.take(maxNeighbors)
	}

	fun findBestNeighbor(
		query: String,
		minScore: Double = DEFAULT_MIN_ACCEPTANCE_THRESHOLD,
	): PinnedNeighborMatch? = findNearestNeighbors(query, maxNeighbors = 1, minScore = minScore).firstOrNull()

	private fun computeFinalScore(index: Int, lexical: Double, queryEmb: FloatArray?): Double {
		val embeddings = pinnedEmbeddings
		return if (queryEmb != null && embeddings != null && index < embeddings.size) {
			val vectorSim = cosineSimilarity(queryEmb, embeddings[index])
			(vectorSim * VECTOR_WEIGHT + lexical * LEXICAL_WEIGHT).coerceIn(0.0, 1.0)
		} else {
			lexical
		}
	}

	private fun scoreLexical(
		query: String,
		target: String,
		queryTokens: List<String>,
		targetTokens: List<String>,
		queryHead: String,
		targetHead: String,
	): Double {
		if (query == target) {
			return EXACT_MATCH_SCORE
		}
		val jaccard = jaccardSimilarity(queryTokens, targetTokens)
		val containment = computeContainment(queryTokens, targetTokens)
		val trigram = characterTrigramSimilarity(query, target)
		val headScore = computeHeadScore(queryHead, targetHead)
		val base = jaccard * JACCARD_WEIGHT + containment * CONTAINMENT_BONUS + trigram * TRIGRAM_WEIGHT
		return (base + headScore).coerceIn(0.0, 1.0)
	}

	private fun computeContainment(queryTokens: List<String>, targetTokens: List<String>): Double =
		when {
			queryTokens.isEmpty() || targetTokens.isEmpty() -> 0.0
			queryTokens.containsAll(targetTokens) || targetTokens.containsAll(queryTokens) -> 1.0
			else -> 0.0
		}

	private fun computeHeadScore(queryHead: String, targetHead: String): Double =
		when {
			queryHead.isEmpty() || targetHead.isEmpty() -> 0.0
			queryHead == targetHead -> HEAD_MATCH_BONUS
			isSynonymMatch(queryHead, targetHead) -> HEAD_MATCH_BONUS
			else -> -HEAD_MISMATCH_PENALTY
		}

	private fun isSynonymMatch(a: String, b: String): Boolean {
		val synA = IngredientCandidateScorer.tokenSynonyms[a]
		val synB = IngredientCandidateScorer.tokenSynonyms[b]
		return (synA != null && synA == b) ||
			(synB != null && synB == a) ||
			(synA != null && synA == synB)
	}

	private fun jaccardSimilarity(a: List<String>, b: List<String>): Double {
		if (a.isEmpty() || b.isEmpty()) return 0.0
		val setA = a.toSet()
		val setB = b.toSet()
		val intersectionSize = setA.intersect(setB).size
		val unionSize = setA.union(setB).size
		return if (unionSize == 0) 0.0 else intersectionSize.toDouble() / unionSize
	}

	private fun characterTrigramSimilarity(a: String, b: String): Double {
		if (a.length < TRIGRAM_LEN || b.length < TRIGRAM_LEN) {
			return if (a.equals(b, ignoreCase = true)) 1.0 else 0.0
		}
		val trigramsA = a.windowed(TRIGRAM_LEN)
		val trigramsB = b.windowed(TRIGRAM_LEN)
		val setB = trigramsB.toMutableList()
		var matches = 0
		for (tri in trigramsA) {
			val idx = setB.indexOf(tri)
			if (idx >= 0) {
				matches++
				setB.removeAt(idx)
			}
		}
		val total = trigramsA.size + trigramsB.size
		return if (total == 0) 0.0 else (2.0 * matches) / total
	}

	private fun extractTokens(text: String): List<String> =
		text.lowercase(Locale.ROOT)
			.split(TOKEN_DELIMITER_REGEX)
			.filter { it.isNotBlank() }
			.map { token -> IngredientCandidateScorer.tokenSynonyms[token] ?: token }

	companion object {

		private fun precomputeEmbeddings(
			embedder: IngredientTextEmbedder,
			texts: List<String>,
		): Array<FloatArray>? {
			val result = ArrayList<FloatArray>(texts.size)
			for (chunk in texts.chunked(EMBEDDING_BATCH_SIZE)) {
				val chunkEmbeddings = embedder.encodeTexts(chunk) ?: return null
				for (emb in chunkEmbeddings) {
					result.add(emb)
				}
			}
			return result.toTypedArray()
		}
	}
}
