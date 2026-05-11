package app.purecipes.feature.search.data.datasource

class UserPantryInMemoryDataSource : UserPantryDataSource.Local {

	private var pantry = emptySet<String>()

	override fun getPantry(): Set<String> = pantry

	override fun savePantry(pantry: Set<String>) {
		this.pantry = pantry
	}
}
