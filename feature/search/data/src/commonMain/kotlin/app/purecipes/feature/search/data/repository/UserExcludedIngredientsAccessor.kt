package app.purecipes.feature.search.data.repository

import app.purecipes.feature.search.data.datasource.UserExcludedIngredientsDataSource
import app.purecipes.feature.search.domain.repository.SearchOutcome
import app.purecipes.feature.search.domain.repository.UserExcludedIngredientsRepository
import app.purecipes.shared.domain.model.ExcludedIngredientsDelta
import com.github.michaelbull.result.getOr
import com.github.michaelbull.result.map
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@Inject
@ContributesBinding(AppScope::class)
class UserExcludedIngredientsAccessor(
	private val remoteDataSource: UserExcludedIngredientsDataSource.Remote,
	private val localDataSource: UserExcludedIngredientsDataSource.Local,
) : UserExcludedIngredientsRepository {

	override suspend fun getExcludedIngredients(): Set<String> {
		val remoteExcludedIngredients = remoteDataSource.getExcludedIngredients().getOr(null)
		return if (remoteExcludedIngredients != null) {
			localDataSource.saveExcludedIngredients(remoteExcludedIngredients)
			remoteExcludedIngredients
		} else {
			localDataSource.getExcludedIngredients()
		}
	}

	override suspend fun updateExcludedIngredients(
		delta: ExcludedIngredientsDelta,
	): SearchOutcome<Set<String>> =
		remoteDataSource.updateExcludedIngredients(delta).map { remoteExcludedIngredients ->
			localDataSource.saveExcludedIngredients(remoteExcludedIngredients)
			remoteExcludedIngredients
		}
}
