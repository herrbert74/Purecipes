package app.purecipes.feature.favorites.domain.usecase

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.favorites.domain.repository.CookbooksRepository

class RemoveRecipeFromCookbookUseCase(
	private val repository: CookbooksRepository,
) {

	suspend operator fun invoke(cookbookId: Int, recipeId: Int): Outcome<Unit> {
		return repository.removeRecipeFromCookbook(cookbookId, recipeId)
	}
}
