package app.purecipes.feature.library.domain.repository

import kotlin.random.Random

interface CookbookCoverRepository {

	fun getCookbookCoverImageUrl(
		cookbookId: Int,
		candidateImageUrls: List<String>,
		nowMillis: Long,
		random: Random,
	): String?
}
