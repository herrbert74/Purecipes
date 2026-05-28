package app.purecipes.feature.search.data.datasource

import app.purecipes.shared.domain.model.SearchFilters
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@Inject
@ContributesBinding(AppScope::class)
class RecipeSearchFilterInMemoryDataSource : RecipeSearchFilterDataSource.Local {

	private var filters = SearchFilters()

	override fun getFilters(): SearchFilters = filters

	override fun saveFilters(filters: SearchFilters) {
		this.filters = filters
	}
}
