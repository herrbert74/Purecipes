package app.purecipes.backend.feature.nutrition

internal object FdcFoodMatchingSupport {

	fun mergeForMatching(
		storedFoods: List<FdcFoundationFood>,
		importedFoods: List<FdcFoundationFood>,
	): List<FdcFoundationFood> =
		(storedFoods + importedFoods)
			.groupBy { food -> food.fdcId }
			.values
			.map { foodsWithSameId -> foodsWithSameId.minBy { food -> sourcePriority(food.sourceName) } }
			.sortedWith(compareBy({ sourcePriority(it.sourceName) }, { it.description }))

	fun sourcePriority(sourceName: String): Int =
		when (sourceName) {
			FDC_FOUNDATION_SOURCE_NAME -> FOUNDATION_PRIORITY
			FDC_SR_LEGACY_SOURCE_NAME -> SR_LEGACY_PRIORITY
			FDC_BRANDED_SOURCE_NAME -> BRANDED_PRIORITY
			else -> UNKNOWN_PRIORITY
		}

	private const val FOUNDATION_PRIORITY = 0
	private const val SR_LEGACY_PRIORITY = 1
	private const val BRANDED_PRIORITY = 2
	private const val UNKNOWN_PRIORITY = 3
}
