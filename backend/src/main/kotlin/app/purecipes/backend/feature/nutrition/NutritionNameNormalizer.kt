package app.purecipes.backend.feature.nutrition

internal object NutritionNameNormalizer {
	private val NON_ALPHANUMERIC = Regex("[^a-z0-9]+")

	fun normalize(value: String): String =
		NON_ALPHANUMERIC.replace(value.lowercase(), " ").trim()
}
