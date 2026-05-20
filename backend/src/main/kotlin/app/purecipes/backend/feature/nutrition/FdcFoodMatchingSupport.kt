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
			FDC_FOUNDATION_SOURCE_NAME -> 0
			FDC_SR_LEGACY_SOURCE_NAME -> 1
			else -> 2
		}
}
