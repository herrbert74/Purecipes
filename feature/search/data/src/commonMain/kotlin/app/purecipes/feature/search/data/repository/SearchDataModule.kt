package app.purecipes.feature.search.data.repository

import app.purecipes.feature.search.data.datasource.RecipeSearchDataSource
import app.purecipes.feature.search.data.datasource.RecipeSearchFilterDataSource
import app.purecipes.feature.search.data.datasource.RecipeSearchFilterInMemoryDataSource
import app.purecipes.feature.search.data.datasource.RecipeSearchFilterRemoteDataSource
import app.purecipes.feature.search.data.datasource.RecipeSearchRemoteDataSource
import app.purecipes.feature.search.data.datasource.UserPantryDataSource
import app.purecipes.feature.search.data.datasource.UserPantryInMemoryDataSource
import app.purecipes.feature.search.data.datasource.UserPantryRemoteDataSource
import app.purecipes.feature.search.domain.repository.RecipeSearchFilterRepository
import app.purecipes.feature.search.domain.repository.RecipeSearchRepository
import app.purecipes.feature.search.domain.repository.UserPantryRepository
import app.purecipes.shared.data.network.PurecipesApi
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface SearchDataModule {
	@Provides
	fun provideRecipeSearchRemoteDataSource(api: PurecipesApi): RecipeSearchDataSource.Remote {
		return RecipeSearchRemoteDataSource(api)
	}

	@Provides
	fun provideRecipeSearchRepository(remoteDataSource: RecipeSearchDataSource.Remote): RecipeSearchRepository {
		return RecipeSearchAccessor(remoteDataSource)
	}

	@Provides
	fun provideRecipeSearchFilterRemoteDataSource(api: PurecipesApi): RecipeSearchFilterDataSource.Remote {
		return RecipeSearchFilterRemoteDataSource(api)
	}

	@Provides
	fun provideRecipeSearchFilterLocalDataSource(): RecipeSearchFilterDataSource.Local {
		return RecipeSearchFilterInMemoryDataSource()
	}

	@Provides
	fun provideRecipeSearchFilterRepository(
		remoteDataSource: RecipeSearchFilterDataSource.Remote,
		localDataSource: RecipeSearchFilterDataSource.Local,
	): RecipeSearchFilterRepository {
		return RecipeSearchFilterAccessor(remoteDataSource, localDataSource)
	}

	@Provides
	fun provideUserPantryRemoteDataSource(api: PurecipesApi): UserPantryDataSource.Remote {
		return UserPantryRemoteDataSource(api)
	}

	@Provides
	fun provideUserPantryLocalDataSource(): UserPantryDataSource.Local {
		return UserPantryInMemoryDataSource()
	}

	@Provides
	fun provideUserPantryRepository(
		remoteDataSource: UserPantryDataSource.Remote,
		localDataSource: UserPantryDataSource.Local,
	): UserPantryRepository {
		return UserPantryAccessor(remoteDataSource, localDataSource)
	}
}
