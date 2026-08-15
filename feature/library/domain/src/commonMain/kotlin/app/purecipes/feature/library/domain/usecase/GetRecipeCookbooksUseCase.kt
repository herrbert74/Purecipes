package app.purecipes.feature.library.domain.usecase

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.library.domain.repository.CookbooksRepository
import app.purecipes.shared.domain.model.CookbookRef
import dev.zacsweers.metro.Inject

@Inject
class GetRecipeCookbooksUseCase(
	private val repository: CookbooksRepository,
) {

	suspend operator fun invoke(recipeId: Int): Outcome<List<CookbookRef>> {
		return repository.getRecipeCookbooks(recipeId)
	}
}
