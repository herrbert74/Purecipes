package app.purecipes.feature.favorites.domain.usecase

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.favorites.domain.repository.FavoritesRepository
import app.purecipes.shared.domain.model.SearchResultsPage
import dev.zacsweers.metro.Inject

@Inject
class GetFavoriteRecipesPageUseCase(
	private val repository: FavoritesRepository,
) {

	suspend operator fun invoke(pageNumber: Int, pageSize: Int): Outcome<SearchResultsPage> {
		return repository.getFavoriteRecipesPage(pageNumber, pageSize)
	}
}
