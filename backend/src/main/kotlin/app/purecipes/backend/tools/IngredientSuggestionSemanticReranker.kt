package app.purecipes.backend.tools

import org.tensorflow.SavedModelBundle
import org.tensorflow.ndarray.NdArrays
import org.tensorflow.types.TFloat32
import org.tensorflow.types.TString
import java.io.File
import java.util.Locale

internal data class SuggestedFoodCandidate(
	val fdcSource: String,
	val fdcId: String,
	val fdcDescription: String,
	val method: String,
	val score: Double,
	val rationale: String,
)

internal data class IngredientSemanticRerankInput(
	val lookupName: String,
	val sampleRecipeTitle: String,
	val sampleRawText: String,
	val currentPantryItem: String,
	val suggestedPantryItem: String,
	val foodCandidates: List<SuggestedFoodCandidate>,
)

internal data class IngredientSemanticRerankDecision(
	val fdcSource: String,
	val fdcId: String,
	val fdcDescription: String,
	val pantryItem: String,
	val confidence: String,
	val rationale: String,
)

internal fun interface IngredientSuggestionReranker {

	fun rerank(input: IngredientSemanticRerankInput): IngredientSemanticRerankDecision?
}

internal class LocalEmbeddingIngredientReranker private constructor(
	private val bundle: SavedModelBundle,
) : IngredientSuggestionReranker, AutoCloseable {

	override fun rerank(input: IngredientSemanticRerankInput): IngredientSemanticRerankDecision? {
		if (input.foodCandidates.isEmpty()) {
			return null
		}
		val candidateTexts = input.foodCandidates.map(SuggestedFoodCandidate::fdcDescription)
		val embeddings = encodeTexts(listOf(input.lookupName) + candidateTexts)
		val bestMatch = embeddings?.let { findBestCandidateMatch(it, candidateTexts.size) }
		return if (bestMatch == null) {
			null
		} else {
			val bestCandidate = input.foodCandidates[bestMatch.first]
			val formattedScore = String.format(Locale.ROOT, SCORE_FORMAT, bestMatch.second)
			IngredientSemanticRerankDecision(
				fdcSource = bestCandidate.fdcSource,
				fdcId = bestCandidate.fdcId,
				fdcDescription = bestCandidate.fdcDescription,
				pantryItem = input.suggestedPantryItem,
				confidence = formattedScore,
				rationale = "Local ML embedding similarity (score=$formattedScore)",
			)
		}
	}

	private fun findBestCandidateMatch(
		embeddings: Array<FloatArray>,
		candidateCount: Int,
	): Pair<Int, Double>? {
		val queryEmb = embeddings[FIRST_INDEX]
		var bestIdx = -1
		var bestSimilarity = MIN_SIMILARITY_FLOOR
		for (i in 0 until candidateCount) {
			val similarity = cosineSimilarity(queryEmb, embeddings[i + CANDIDATE_OFFSET])
			if (similarity > bestSimilarity) {
				bestSimilarity = similarity
				bestIdx = i
			}
		}
		return if (bestIdx >= 0 && bestSimilarity >= MINIMUM_EMBEDDING_ACCEPTANCE_THRESHOLD) {
			bestIdx to bestSimilarity
		} else {
			null
		}
	}

	fun encodeTexts(texts: List<String>): Array<FloatArray>? =
		runCatching {
			val inputData = NdArrays.vectorOfObjects(*texts.toTypedArray())
			val input = TString.tensorOf(inputData)
			input.use {
				bundle.function("serving_default").call(mapOf("inputs" to input)).use { result ->
					val embeddings = (result.get("outputs").orElseGet { result.get(0) }) as? TFloat32
						?: return@runCatching null
					embeddings.use {
						tensorToEmbeddings(it, texts.size)
					}
				}
			}
		}.getOrNull()

	private fun tensorToEmbeddings(embeddings: TFloat32, textCount: Int): Array<FloatArray> {
		val shape = embeddings.shape()
		val dims = LongArray(shape.numDimensions()) { index -> shape.size(index) }
		return Array(textCount) { i ->
			FloatArray(EMBEDDING_DIM) { j ->
				extractFloatDimension(embeddings, dims, textCount, i, j)
			}
		}
	}

	private fun extractFloatDimension(
		embeddings: TFloat32,
		dims: LongArray,
		textCount: Int,
		i: Int,
		j: Int,
	): Float =
		when {
			dims.size == SINGLE_DIMENSION_COUNT &&
				dims[FIRST_INDEX] == EMBEDDING_DIM.toLong() &&
				textCount == SINGLE_TEXT_COUNT ->
				embeddings.getFloat(j.toLong())

			dims.size == TWO_DIMENSION_COUNT &&
				dims[FIRST_INDEX] == textCount.toLong() &&
				dims[SECOND_INDEX] >= EMBEDDING_DIM.toLong() ->
				embeddings.getFloat(i.toLong(), j.toLong())

			dims.size == THREE_DIMENSION_COUNT &&
				dims[FIRST_INDEX] == textCount.toLong() &&
				dims[SECOND_INDEX] == SINGLE_ROW_DIMENSION &&
				dims[THIRD_INDEX] >= EMBEDDING_DIM.toLong() ->
				embeddings.getFloat(i.toLong(), FIRST_AXIS_INDEX, j.toLong())

			dims.size == THREE_DIMENSION_COUNT &&
				dims[FIRST_INDEX] == SINGLE_ROW_DIMENSION &&
				dims[SECOND_INDEX] == textCount.toLong() &&
				dims[THIRD_INDEX] >= EMBEDDING_DIM.toLong() ->
				embeddings.getFloat(FIRST_AXIS_INDEX, i.toLong(), j.toLong())

			else -> error("Unexpected embedding tensor shape: ${dims.joinToString()}")
		}

	private fun cosineSimilarity(a: FloatArray, b: FloatArray): Double {
		var dot = 0.0
		var normA = 0.0
		var normB = 0.0
		for (i in a.indices) {
			val av = a[i].toDouble()
			val bv = b[i].toDouble()
			dot += av * bv
			normA += av * av
			normB += bv * bv
		}
		val denom = Math.sqrt(normA) * Math.sqrt(normB)
		return if (denom == 0.0) 0.0 else dot / denom
	}

	override fun close() {
		bundle.close()
	}

	companion object {

		const val EMBEDDING_DIM = 512
		private const val MIN_SIMILARITY_FLOOR = -1.0
		private const val MINIMUM_EMBEDDING_ACCEPTANCE_THRESHOLD = 0.45
		private const val SCORE_FORMAT = "%.2f"
		private const val FIRST_INDEX = 0
		private const val SECOND_INDEX = 1
		private const val THIRD_INDEX = 2
		private const val CANDIDATE_OFFSET = 1
		private const val SINGLE_DIMENSION_COUNT = 1
		private const val TWO_DIMENSION_COUNT = 2
		private const val THREE_DIMENSION_COUNT = 3
		private const val SINGLE_TEXT_COUNT = 1
		private const val FIRST_AXIS_INDEX = 0L
		private const val SINGLE_ROW_DIMENSION = 1L

		fun createOrNull(modelPath: String?): LocalEmbeddingIngredientReranker? {
			val dir = modelPath?.takeIf(String::isNotBlank)?.let(::File)
			return if (dir != null && dir.isDirectory) {
				runCatching {
					val bundle = SavedModelBundle.load(dir.absolutePath, "serve")
					LocalEmbeddingIngredientReranker(bundle)
				}.getOrNull()
			} else {
				null
			}
		}
	}
}
