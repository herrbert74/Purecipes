package app.purecipes.feature.search.data.datasource

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@Inject
@ContributesBinding(AppScope::class)
class UserExcludedIngredientsInMemoryDataSource : UserExcludedIngredientsDataSource.Local {

	private var excludedIngredients = emptySet<String>()

	override fun getExcludedIngredients(): Set<String> = excludedIngredients

	override fun saveExcludedIngredients(excludedIngredients: Set<String>) {
		this.excludedIngredients = excludedIngredients
	}
}
