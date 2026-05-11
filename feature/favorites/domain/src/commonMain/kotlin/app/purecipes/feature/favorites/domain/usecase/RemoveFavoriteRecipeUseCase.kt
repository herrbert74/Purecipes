package app.purecipes.feature.favorites.domain.usecase

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.favorites.domain.repository.FavoritesRepository

class RemoveFavoriteRecipeUseCase(
	private val repository: FavoritesRepository,
) {

	suspend operator fun invoke(recipeId: Int): Outcome<Unit> {
		return repository.removeFavorite(recipeId)
	}
}
