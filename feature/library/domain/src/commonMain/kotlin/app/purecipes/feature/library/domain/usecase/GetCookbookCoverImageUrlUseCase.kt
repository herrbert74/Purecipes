package app.purecipes.feature.library.domain.usecase

import app.purecipes.feature.library.domain.repository.CookbookCoverRepository
import dev.zacsweers.metro.Inject
import kotlin.random.Random

@Inject
class GetCookbookCoverImageUrlUseCase(
	private val repository: CookbookCoverRepository,
) {

	operator fun invoke(
		cookbookId: Int,
		candidateImageUrls: List<String>,
		nowMillis: Long,
		random: Random,
	): String? {
		return repository.getCookbookCoverImageUrl(
			cookbookId = cookbookId,
			candidateImageUrls = candidateImageUrls,
			nowMillis = nowMillis,
			random = random,
		)
	}
}
