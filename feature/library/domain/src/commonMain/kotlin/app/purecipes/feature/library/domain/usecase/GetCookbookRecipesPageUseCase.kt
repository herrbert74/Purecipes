package app.purecipes.feature.library.domain.usecase

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.library.domain.repository.CookbooksRepository
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
