package app.purecipes.feature.sharing.data.repository

import app.purecipes.feature.sharing.data.datasource.CookbookShareRemoteDataSource
import app.purecipes.feature.sharing.data.datasource.IncomingLinkDataSource
import app.purecipes.feature.sharing.data.datasource.SharePlatformDataSource
import app.purecipes.feature.sharing.data.datasource.WebLaunchLinkPlatformDataSource
import app.purecipes.feature.sharing.domain.repository.CookbookShareRepository
import app.purecipes.feature.sharing.domain.repository.IncomingLinkRepository
import app.purecipes.feature.sharing.domain.repository.ShareRepository
import app.purecipes.feature.sharing.domain.repository.WebLaunchLinkRepository
import app.purecipes.shared.data.network.PurecipesApi
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@ContributesTo(AppScope::class)
interface SharingDataModule {

	@SingleIn(AppScope::class)
	@Provides
	fun provideIncomingLinkRepository(): IncomingLinkRepository {
		return IncomingLinkAccessor(IncomingLinkDataSource())
	}

	@Provides
	fun provideShareRepository(): ShareRepository {
		return ShareAccessor(SharePlatformDataSource())
	}

	@Provides
	fun provideWebLaunchLinkRepository(): WebLaunchLinkRepository {
		return WebLaunchLinkAccessor(WebLaunchLinkPlatformDataSource())
	}

	@Provides
	fun provideCookbookShareRemoteDataSource(api: PurecipesApi): CookbookShareRemoteDataSource {
		return CookbookShareRemoteDataSource(api)
	}

	@Provides
	fun provideCookbookShareRepository(
		remoteDataSource: CookbookShareRemoteDataSource,
	): CookbookShareRepository {
		return CookbookShareAccessor(remoteDataSource)
	}
}
