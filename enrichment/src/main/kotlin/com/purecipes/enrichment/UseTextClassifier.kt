package com.purecipes.enrichment

import org.tensorflow.SavedModelBundle
import org.tensorflow.ndarray.NdArrays
import org.tensorflow.ndarray.Shape
import org.tensorflow.types.TFloat32
import org.tensorflow.types.TString

internal class UseTextClassifier(modelPath: String) : AutoCloseable {

	private val bundle = SavedModelBundle.load(modelPath, "serve")

	fun encodeTexts(texts: List<String>): Array<FloatArray> {
		val shape = Shape.of(texts.size.toLong())
		val inputData = NdArrays.vectorOfObjects(*texts.toTypedArray())
		val input = TString.tensorOf(shape, inputData)
		input.use {
			val result = bundle.session().runner()
				.feed("serving_default_inputs", input)
				.fetch("StatefulPartitionedCall")
				.run()
			result[0].use { tensor ->
				val embeddings = tensor as TFloat32
				return Array(texts.size) { i ->
					FloatArray(EMBEDDING_DIM) { j -> embeddings.getFloat(i.toLong(), j.toLong()) }
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
