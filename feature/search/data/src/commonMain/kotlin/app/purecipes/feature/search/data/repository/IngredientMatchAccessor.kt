package app.purecipes.feature.search.data.repository

import app.purecipes.feature.search.data.datasource.IngredientMatchDataSource
import app.purecipes.feature.search.domain.repository.IngredientMatchRepository
import app.purecipes.feature.search.domain.repository.SearchOutcome
import app.purecipes.shared.domain.model.IngredientMatchResponse
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@Inject
@ContributesBinding(AppScope::class)
class IngredientMatchAccessor(
	private val remoteDataSource: IngredientMatchDataSource.Remote,
) : IngredientMatchRepository {

	override suspend fun matchIngredient(name: String): SearchOutcome<IngredientMatchResponse> =
		remoteDataSource.matchIngredient(name)
}
