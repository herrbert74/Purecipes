package app.purecipes.enrichment

import org.tensorflow.SavedModelBundle
import org.tensorflow.ndarray.NdArrays
import org.tensorflow.types.TFloat32
import org.tensorflow.types.TString

internal class UseTextClassifier(modelPath: String) : AutoCloseable {

	private val bundle = SavedModelBundle.load(modelPath, "serve")

	fun encodeTexts(texts: List<String>): Array<FloatArray> {
		val inputData = NdArrays.vectorOfObjects(*texts.toTypedArray())
		val input = TString.tensorOf(inputData)
		input.use {
			bundle.function("serving_default").call(mapOf("inputs" to input)).use { result ->
				val embeddings = (result.get("outputs").orElseGet { result.get(0) }) as? TFloat32
					?: error("serving_default did not return a float embedding tensor")
				embeddings.use {
					return tensorToEmbeddings(it, texts.size)
				}
			}
		}
	}

	private fun tensorToEmbeddings(embeddings: TFloat32, textCount: Int): Array<FloatArray> {
		val shape = embeddings.shape()
		val dims = LongArray(shape.numDimensions()) { index -> shape.size(index) }
		return Array(textCount) { i ->
			FloatArray(EMBEDDING_DIM) { j ->
				when {
					dims.size == SINGLE_DIMENSION_COUNT &&
						dims[FIRST_DIMENSION] == EMBEDDING_DIM.toLong() &&
						textCount == SINGLE_TEXT_COUNT ->
						embeddings.getFloat(j.toLong())

					dims.size == TWO_DIMENSION_COUNT &&
						dims[FIRST_DIMENSION] == textCount.toLong() &&
						dims[SECOND_DIMENSION] >= EMBEDDING_DIM.toLong() ->
						embeddings.getFloat(i.toLong(), j.toLong())

					dims.size == THREE_DIMENSION_COUNT &&
						dims[FIRST_DIMENSION] == textCount.toLong() &&
						dims[SECOND_DIMENSION] == SINGLE_ROW_DIMENSION &&
						dims[THIRD_DIMENSION] >= EMBEDDING_DIM.toLong() ->
						embeddings.getFloat(i.toLong(), FIRST_AXIS_INDEX, j.toLong())

					dims.size == THREE_DIMENSION_COUNT &&
						dims[FIRST_DIMENSION] == SINGLE_ROW_DIMENSION &&
						dims[SECOND_DIMENSION] == textCount.toLong() &&
						dims[THIRD_DIMENSION] >= EMBEDDING_DIM.toLong() ->
						embeddings.getFloat(FIRST_AXIS_INDEX, i.toLong(), j.toLong())

					else -> error(
						"Unexpected USE embedding shape: " +
							dims.joinToString(prefix = "[", postfix = "]")
					)
				}
			}
		}
	}

	fun <T> buildClassCentroids(seeds: Map<T, List<String>>): Map<T, FloatArray> {
		val allPhrases = seeds.values.flatten()
		val allEmbeddings = encodeTexts(allPhrases)
		var offset = 0
		return seeds.mapValues { (_, phrases) ->
			val centroid = FloatArray(EMBEDDING_DIM)
			for (i in phrases.indices) {
				val emb = allEmbeddings[offset + i]
				for (j in 0 until EMBEDDING_DIM) centroid[j] += emb[j]
			}
			for (j in 0 until EMBEDDING_DIM) centroid[j] /= phrases.size
			offset += phrases.size
			centroid
		}
	}

	fun <T> classifySingle(textEmb: FloatArray, centroids: Map<T, FloatArray>): T =
		centroids.maxBy { (_, centroid) -> cosineSimilarity(textEmb, centroid) }.key

	fun <T> classifySingle(
		textEmb: FloatArray,
		centroids: Map<T, FloatArray>,
		threshold: Float,
	): T? {
		val best = centroids.maxBy { (_, centroid) -> cosineSimilarity(textEmb, centroid) }
		return if (cosineSimilarity(textEmb, best.value) >= threshold) best.key else null
	}

	fun <T> classifyMultiLabel(
		textEmb: FloatArray,
		centroids: Map<T, FloatArray>,
		threshold: Float,
	): Set<T> = centroids
		.filter { (_, centroid) -> cosineSimilarity(textEmb, centroid) >= threshold }
		.keys
		.toSet()

	fun cosineSimilarity(a: FloatArray, b: FloatArray): Float {
		var dot = 0f
		var normA = 0f
		var normB = 0f
		for (i in a.indices) {
			dot += a[i] * b[i]
			normA += a[i] * a[i]
			normB += b[i] * b[i]
		}
		val denom = Math.sqrt(normA.toDouble()) * Math.sqrt(normB.toDouble())
		return if (denom == 0.0) 0f else (dot / denom).toFloat()
	}

	override fun close() {
		bundle.close()
	}

	companion object {
		const val EMBEDDING_DIM = 512
		const val FIRST_DIMENSION = 0
		const val SECOND_DIMENSION = 1
		const val THIRD_DIMENSION = 2
		const val SINGLE_DIMENSION_COUNT = 1
		const val TWO_DIMENSION_COUNT = 2
		const val THREE_DIMENSION_COUNT = 3
		const val SINGLE_TEXT_COUNT = 1
		const val FIRST_AXIS_INDEX = 0L
		const val SINGLE_ROW_DIMENSION = 1L
	}
}
