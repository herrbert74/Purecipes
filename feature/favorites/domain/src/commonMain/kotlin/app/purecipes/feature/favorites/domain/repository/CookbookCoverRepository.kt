package app.purecipes.feature.favorites.domain.repository

import kotlin.random.Random

interface CookbookCoverRepository {

	fun getCookbookCoverImageUrl(
		cookbookId: Int,
		candidateImageUrls: List<String>,
		nowMillis: Long,
		random: Random,
	): String?
}
