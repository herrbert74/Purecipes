package app.purecipes.feature.search.data.datasource

import app.purecipes.feature.search.domain.repository.SearchOutcome
import app.purecipes.shared.data.network.PurecipesApi
import app.purecipes.shared.data.util.runCatchingApi
import app.purecipes.shared.domain.model.HomeFeed
import app.purecipes.shared.domain.model.HomeShelfId
import app.purecipes.shared.domain.model.SearchResultsPage
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

@Inject
@ContributesBinding(AppScope::class)
class HomeFeedRemoteDataSource(
	private val api: PurecipesApi,
) : HomeFeedDataSource.Remote {

	override suspend fun getHomeFeed(): SearchOutcome<HomeFeed> = runCatchingApi {
		api.getHome(localHour = localHour())
	}

	override suspend fun getShelfPage(
		shelfId: HomeShelfId,
		pageNumber: Int,
		pageSize: Int,
	): SearchOutcome<SearchResultsPage> = runCatchingApi {
		api.getHomeShelfPage(
			shelfId = shelfId.name,
			localHour = localHour(),
			pageNumber = pageNumber,
			pageSize = pageSize,
		)
	}

	private fun localHour(): Int =
		Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour
}
