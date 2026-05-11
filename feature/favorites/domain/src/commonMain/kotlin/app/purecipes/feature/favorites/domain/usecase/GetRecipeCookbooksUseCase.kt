package app.purecipes.feature.favorites.domain.usecase

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.favorites.domain.repository.CookbooksRepository
import app.purecipes.shared.domain.model.CookbookRef

class GetRecipeCookbooksUseCase(
	private val repository: CookbooksRepository,
) {

	suspend operator fun invoke(recipeId: Int): Outcome<List<CookbookRef>> {
		return repository.getRecipeCookbooks(recipeId)
	}
}
