package app.purecipes.feature.library.data.datasource

import kotlin.random.Random

interface CookbookCoverDataSource {

	interface Local {
		fun getCookbookCoverImageUrl(
			cookbookId: Int,
			candidateImageUrls: List<String>,
			nowMillis: Long,
			random: Random,
		): String?
	}
}
