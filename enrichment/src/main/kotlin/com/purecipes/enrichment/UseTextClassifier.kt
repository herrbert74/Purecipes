package com.purecipes.enrichment

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
					dims.size == 1 && dims[0] == EMBEDDING_DIM.toLong() && textCount == 1 -> {
						embeddings.getFloat(j.toLong())
					}
					dims.size == 2 && dims[0] == textCount.toLong() && dims[1] >= EMBEDDING_DIM.toLong() -> {
						embeddings.getFloat(i.toLong(), j.toLong())
					}
					dims.size == 3 && dims[0] == textCount.toLong() && dims[1] == 1L && dims[2] >= EMBEDDING_DIM.toLong() -> {
						embeddings.getFloat(i.toLong(), 0L, j.toLong())
					}
					dims.size == 3 && dims[0] == 1L && dims[1] == textCount.toLong() && dims[2] >= EMBEDDING_DIM.toLong() -> {
						embeddings.getFloat(0L, i.toLong(), j.toLong())
					}
					else -> error("Unexpected USE embedding shape: ${dims.joinToString(prefix = "[", postfix = "]")}")
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
	}
}
