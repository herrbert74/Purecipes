package app.purecipes.feature.favorites.domain.usecase

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.favorites.domain.repository.CookbooksRepository
import app.purecipes.shared.domain.model.SearchResultsPage
import dev.zacsweers.metro.Inject

@Inject
class GetCookbookRecipesPageUseCase(
	private val repository: CookbooksRepository,
) {

	suspend operator fun invoke(
		cookbookId: Int,
		pageNumber: Int,
		pageSize: Int,
	): Outcome<SearchResultsPage> {
		return repository.getCookbookRecipesPage(cookbookId, pageNumber, pageSize)
	}
}
