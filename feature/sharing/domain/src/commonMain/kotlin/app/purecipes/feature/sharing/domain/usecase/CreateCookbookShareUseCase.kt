package app.purecipes.feature.sharing.domain.usecase

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.sharing.domain.repository.CookbookShareRepository
import app.purecipes.shared.domain.model.CookbookShareToken

class CreateCookbookShareUseCase(
	private val cookbookShareRepository: CookbookShareRepository,
) {

	suspend operator fun invoke(cookbookId: Int): Outcome<CookbookShareToken> =
		cookbookShareRepository.createShare(cookbookId)
}
