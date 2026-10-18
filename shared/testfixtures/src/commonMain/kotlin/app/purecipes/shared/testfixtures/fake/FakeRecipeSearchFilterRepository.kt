package app.purecipes.shared.testfixtures.fake

import app.purecipes.feature.search.domain.repository.RecipeSearchFilterRepository
import app.purecipes.shared.domain.model.SearchFilters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class FakeRecipeSearchFilterRepository(
	var savedFilters: SearchFilters = SearchFilters(),
) : RecipeSearchFilterRepository {

	val filterChanges = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

	override suspend fun getFilters(): SearchFilters = savedFilters

	override suspend fun saveFilters(filters: SearchFilters) {
		savedFilters = filters
		filterChanges.tryEmit(Unit)
	}

	override fun observeFilterChanges(): Flow<Unit> = filterChanges.asSharedFlow()
}
