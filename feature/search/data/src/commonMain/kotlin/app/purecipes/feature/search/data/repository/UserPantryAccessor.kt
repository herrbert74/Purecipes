package app.purecipes.feature.search.data.repository

import app.purecipes.feature.search.data.datasource.UserPantryDataSource
import app.purecipes.feature.search.domain.repository.SearchOutcome
import app.purecipes.feature.search.domain.repository.UserPantryRepository
import app.purecipes.shared.domain.model.PantryDelta
import com.github.michaelbull.result.getOr
import com.github.michaelbull.result.map
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@Inject
@ContributesBinding(AppScope::class)
class UserPantryAccessor(
	private val remoteDataSource: UserPantryDataSource.Remote,
	private val localDataSource: UserPantryDataSource.Local,
) : UserPantryRepository {

	override suspend fun getPantry(): Set<String> {
		val remotePantry = remoteDataSource.getPantry().getOr(null)
		return if (remotePantry != null) {
			localDataSource.savePantry(remotePantry)
			remotePantry
		} else {
			localDataSource.getPantry()
		}
	}

	override suspend fun updatePantry(delta: PantryDelta): SearchOutcome<Set<String>> =
		remoteDataSource.updatePantry(delta).map { remotePantry ->
			localDataSource.savePantry(remotePantry)
			remotePantry
		}
}
