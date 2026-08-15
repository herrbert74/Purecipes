package app.purecipes.feature.library.domain.usecase

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.library.domain.repository.FavoritesRepository
import dev.zacsweers.metro.Inject

@Inject
class RemoveFavoriteRecipeUseCase(
	private val repository: FavoritesRepository,
) {

	suspend operator fun invoke(recipeId: Int): Outcome<Unit> {
		return repository.removeFavorite(recipeId)
	}
}
