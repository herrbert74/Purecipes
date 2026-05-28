package app.purecipes.feature.search.data.repository

import app.purecipes.feature.search.data.datasource.RecipeSearchFilterDataSource
import app.purecipes.feature.search.domain.repository.RecipeSearchFilterRepository
import app.purecipes.shared.domain.model.SearchFilters
import com.github.michaelbull.result.getOr
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@Inject
@ContributesBinding(AppScope::class)
class RecipeSearchFilterAccessor(
	private val remoteDataSource: RecipeSearchFilterDataSource.Remote,
	private val localDataSource: RecipeSearchFilterDataSource.Local,
) : RecipeSearchFilterRepository {

	override suspend fun getFilters(): SearchFilters {
		val remoteFilters = remoteDataSource.getFilters().getOr(null)
		return if (remoteFilters != null) {
			localDataSource.saveFilters(remoteFilters)
			remoteFilters
		} else {
			localDataSource.getFilters()
		}
	}

	override suspend fun saveFilters(filters: SearchFilters) {
		localDataSource.saveFilters(filters)
		remoteDataSource.saveFilters(filters)
	}
}
