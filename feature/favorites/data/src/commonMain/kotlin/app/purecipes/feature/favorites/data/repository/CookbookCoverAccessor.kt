package app.purecipes.feature.favorites.data.repository

import app.purecipes.feature.favorites.data.datasource.CookbookCoverDataSource
import app.purecipes.feature.favorites.domain.repository.CookbookCoverRepository
import kotlin.random.Random

class CookbookCoverAccessor(
	private val localDataSource: CookbookCoverDataSource.Local,
) : CookbookCoverRepository {

	override fun getCookbookCoverImageUrl(
		cookbookId: Int,
		candidateImageUrls: List<String>,
		nowMillis: Long,
		random: Random,
	): String? {
		return localDataSource.getCookbookCoverImageUrl(
			cookbookId = cookbookId,
			candidateImageUrls = candidateImageUrls,
			nowMillis = nowMillis,
			random = random,
		)
	}
}
