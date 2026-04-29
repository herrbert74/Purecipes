package com.purecipes.feature.search.data.repository

import com.purecipes.feature.search.data.datasource.RecipeSearchDataSource
import com.purecipes.feature.search.data.datasource.RecipeSearchFilterDataSource
import com.purecipes.feature.search.data.datasource.RecipeSearchFilterInMemoryDataSource
import com.purecipes.feature.search.data.datasource.RecipeSearchFilterRemoteDataSource
import com.purecipes.feature.search.data.datasource.RecipeSearchRemoteDataSource
import com.purecipes.feature.search.data.datasource.UserPantryDataSource
import com.purecipes.feature.search.data.datasource.UserPantryInMemoryDataSource
import com.purecipes.feature.search.data.datasource.UserPantryRemoteDataSource
import com.purecipes.feature.search.domain.repository.RecipeSearchFilterRepository
import com.purecipes.feature.search.domain.repository.RecipeSearchRepository
import com.purecipes.feature.search.domain.repository.UserPantryRepository
import com.purecipes.feature.search.domain.usecase.GetSearchFiltersUseCase
import com.purecipes.feature.search.domain.usecase.GetUserPantryUseCase
import com.purecipes.feature.search.domain.usecase.SaveSearchFiltersUseCase
import com.purecipes.feature.search.domain.usecase.SearchRecipesUseCase
import com.purecipes.feature.search.domain.usecase.UpdateUserPantryUseCase
import com.purecipes.shared.data.network.PurecipesApi
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
	fun provideSearchRecipesUseCase(repository: RecipeSearchRepository): SearchRecipesUseCase {
		return SearchRecipesUseCase(repository)
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
	fun provideGetSearchFiltersUseCase(repository: RecipeSearchFilterRepository): GetSearchFiltersUseCase {
		return GetSearchFiltersUseCase(repository)
	}

	@Provides
	fun provideSaveSearchFiltersUseCase(repository: RecipeSearchFilterRepository): SaveSearchFiltersUseCase {
		return SaveSearchFiltersUseCase(repository)
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

	@Provides
	fun provideGetUserPantryUseCase(repository: UserPantryRepository): GetUserPantryUseCase {
		return GetUserPantryUseCase(repository)
	}

	@Provides
	fun provideUpdateUserPantryUseCase(repository: UserPantryRepository): UpdateUserPantryUseCase {
		return UpdateUserPantryUseCase(repository)
	}
}
