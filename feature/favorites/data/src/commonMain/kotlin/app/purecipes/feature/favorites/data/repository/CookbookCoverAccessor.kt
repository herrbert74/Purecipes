package app.purecipes.feature.favorites.data.repository

import app.purecipes.feature.favorites.data.datasource.CookbookCoverDataSource
import app.purecipes.feature.favorites.domain.repository.CookbookCoverRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlin.random.Random

@Inject
@ContributesBinding(AppScope::class)
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
