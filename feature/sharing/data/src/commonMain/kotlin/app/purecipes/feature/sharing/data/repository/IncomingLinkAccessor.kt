package app.purecipes.feature.sharing.data.repository

import app.purecipes.feature.sharing.data.datasource.IncomingLinkDataSource
import app.purecipes.feature.sharing.domain.link.PurecipesLinkParser
import app.purecipes.feature.sharing.domain.model.PurecipesLink
import app.purecipes.feature.sharing.domain.repository.IncomingLinkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

internal class IncomingLinkAccessor(
	private val dataSource: IncomingLinkDataSource,
) : IncomingLinkRepository {

	override fun observeLinks(): Flow<PurecipesLink> =
		dataSource.incomingUrls.mapNotNull(PurecipesLinkParser::parse)

	override fun deliver(url: String) {
		dataSource.deliver(url)
	}
}
