package com.purecipes.feature.favorites.domain.usecase

import com.purecipes.feature.favorites.domain.repository.CookbookCoverRepository
import kotlin.random.Random

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
