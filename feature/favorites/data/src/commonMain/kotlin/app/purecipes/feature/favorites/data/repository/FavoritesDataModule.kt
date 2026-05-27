package app.purecipes.feature.favorites.data.repository

import app.purecipes.feature.favorites.data.datasource.CookbookCoverDataSource
import app.purecipes.feature.favorites.data.datasource.CookbooksDataSource
import app.purecipes.feature.favorites.data.datasource.CookbooksRemoteDataSource
import app.purecipes.feature.favorites.data.datasource.FavoritesDataSource
import app.purecipes.feature.favorites.data.datasource.FavoritesRemoteDataSource
import app.purecipes.feature.favorites.data.datasource.SettingsCookbookCoverLocalDataSource
import app.purecipes.feature.favorites.domain.repository.CookbookCoverRepository
import app.purecipes.feature.favorites.domain.repository.CookbooksRepository
import app.purecipes.feature.favorites.domain.repository.FavoritesRepository
import app.purecipes.shared.data.network.PurecipesApi
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface FavoritesDataModule {

	@Provides
	fun provideFavoritesRemoteDataSource(api: PurecipesApi): FavoritesDataSource.Remote {
		return FavoritesRemoteDataSource(api)
	}

	@Provides
	fun provideCookbooksRemoteDataSource(api: PurecipesApi): CookbooksDataSource.Remote {
		return CookbooksRemoteDataSource(api)
	}

	@Provides
	fun provideFavoritesRepository(remoteDataSource: FavoritesDataSource.Remote): FavoritesRepository {
		return FavoritesAccessor(remoteDataSource)
	}

	@Provides
	fun provideCookbooksRepository(remoteDataSource: CookbooksDataSource.Remote): CookbooksRepository {
		return CookbooksAccessor(remoteDataSource)
	}

	@Provides
	fun provideCookbookCoverLocalDataSource(): CookbookCoverDataSource.Local {
		return SettingsCookbookCoverLocalDataSource()
	}

	@Provides
	fun provideCookbookCoverRepository(
		localDataSource: CookbookCoverDataSource.Local,
	): CookbookCoverRepository {
		return CookbookCoverAccessor(localDataSource)
	}
}
