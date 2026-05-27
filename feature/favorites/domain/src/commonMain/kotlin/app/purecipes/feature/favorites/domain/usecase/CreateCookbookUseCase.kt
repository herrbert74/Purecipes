package app.purecipes.feature.favorites.domain.usecase

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.favorites.domain.repository.CookbooksRepository
import app.purecipes.shared.domain.model.CookbookSummary
import dev.zacsweers.metro.Inject

@Inject
class CreateCookbookUseCase(
	private val repository: CookbooksRepository,
) {

	suspend operator fun invoke(name: String): Outcome<CookbookSummary> {
		return repository.createCookbook(name)
	}
}
