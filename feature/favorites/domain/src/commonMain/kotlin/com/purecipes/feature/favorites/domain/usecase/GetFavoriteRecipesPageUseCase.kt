package com.purecipes.feature.favorites.domain.usecase

import com.purecipes.base.kotlin.result.Outcome
import com.purecipes.feature.favorites.domain.repository.FavoritesRepository
import com.purecipes.shared.domain.model.SearchResultsPage

class GetFavoriteRecipesPageUseCase(
	private val repository: FavoritesRepository,
) {

	suspend operator fun invoke(pageNumber: Int, pageSize: Int): Outcome<SearchResultsPage> {
		return repository.getFavoriteRecipesPage(pageNumber, pageSize)
	}
}
