package app.purecipes.feature.search.data.repository

import app.purecipes.feature.search.data.datasource.UserPantryDataSource
import app.purecipes.feature.search.domain.repository.UserPantryRepository
import app.purecipes.shared.domain.model.PantryDelta
import com.github.michaelbull.result.getOr

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

	override suspend fun updatePantry(delta: PantryDelta): Set<String> {
		val remotePantry = remoteDataSource.updatePantry(delta).getOr(null)
		return if (remotePantry != null) {
			localDataSource.savePantry(remotePantry)
			remotePantry
		} else {
			val current = localDataSource.getPantry()
			val updated = (current + delta.add) - delta.remove
			localDataSource.savePantry(updated)
			updated
		}
	}
}
