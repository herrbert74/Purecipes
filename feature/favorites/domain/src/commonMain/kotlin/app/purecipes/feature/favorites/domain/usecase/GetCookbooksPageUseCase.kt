package app.purecipes.feature.favorites.domain.usecase

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.favorites.domain.repository.CookbooksRepository
import app.purecipes.shared.domain.model.CookbookListPage
import dev.zacsweers.metro.Inject

@Inject
class GetCookbooksPageUseCase(
	private val repository: CookbooksRepository,
) {

	suspend operator fun invoke(pageNumber: Int, pageSize: Int): Outcome<CookbookListPage> {
		return repository.getCookbooksPage(pageNumber, pageSize)
	}
}
