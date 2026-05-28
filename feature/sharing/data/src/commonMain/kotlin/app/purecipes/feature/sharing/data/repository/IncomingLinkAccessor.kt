package app.purecipes.feature.sharing.data.repository

import app.purecipes.feature.sharing.data.datasource.IncomingLinkDataSource
import app.purecipes.feature.sharing.domain.link.PurecipesLinkParser
import app.purecipes.feature.sharing.domain.model.PurecipesLink
import app.purecipes.feature.sharing.domain.repository.IncomingLinkRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class IncomingLinkAccessor(
	private val dataSource: IncomingLinkDataSource,
) : IncomingLinkRepository {

	override fun observeLinks(): Flow<PurecipesLink> =
		dataSource.incomingUrls.mapNotNull(PurecipesLinkParser::parse)

	override fun deliver(url: String) {
		dataSource.deliver(url)
	}
}
