package app.purecipes.feature.search.data.datasource

import app.purecipes.feature.search.domain.repository.SearchOutcome
import app.purecipes.shared.data.network.PurecipesApi
import app.purecipes.shared.data.util.runCatchingApi
import app.purecipes.shared.domain.model.ExcludedIngredientsDelta
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@Inject
@ContributesBinding(AppScope::class)
class UserExcludedIngredientsRemoteDataSource(
	private val api: PurecipesApi,
) : UserExcludedIngredientsDataSource.Remote {

	override suspend fun getExcludedIngredients(): SearchOutcome<Set<String>> = runCatchingApi {
		api.getUserExcludedIngredients()
	}

	override suspend fun updateExcludedIngredients(
		delta: ExcludedIngredientsDelta,
	): SearchOutcome<Set<String>> = runCatchingApi {
		api.updateUserExcludedIngredients(delta)
	}
}
