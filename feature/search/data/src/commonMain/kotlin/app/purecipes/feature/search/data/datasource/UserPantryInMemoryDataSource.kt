package app.purecipes.feature.search.data.datasource

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

@Inject
@ContributesBinding(AppScope::class)
class UserPantryInMemoryDataSource : UserPantryDataSource.Local {

	private var pantry = emptySet<String>()

	override fun getPantry(): Set<String> = pantry

	override fun savePantry(pantry: Set<String>) {
		this.pantry = pantry
	}
}
