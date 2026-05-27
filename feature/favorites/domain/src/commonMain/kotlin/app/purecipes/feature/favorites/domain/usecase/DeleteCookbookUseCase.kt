package app.purecipes.feature.favorites.domain.usecase

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.favorites.domain.repository.CookbooksRepository
import dev.zacsweers.metro.Inject

@Inject
class DeleteCookbookUseCase(
	private val repository: CookbooksRepository,
) {

	suspend operator fun invoke(cookbookId: Int): Outcome<Unit> {
		return repository.deleteCookbook(cookbookId)
	}
}
