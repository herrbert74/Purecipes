package app.purecipes.feature.favorites.domain.usecase

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.favorites.domain.repository.CookbooksRepository
import dev.zacsweers.metro.Inject

@Inject
class AddRecipeToCookbookUseCase(
	private val repository: CookbooksRepository,
) {

	suspend operator fun invoke(cookbookId: Int, recipeId: Int): Outcome<Unit> {
		return repository.addRecipeToCookbook(cookbookId, recipeId)
	}
}
