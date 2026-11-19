package app.purecipes.backend.feature.recipe

internal data class PantryCoverage(
	val coveredSlots: Int,
	val totalRequiredSlots: Int,
) {

	val missingSlots: Int
		get() = (totalRequiredSlots - coveredSlots).coerceAtLeast(0)
}
