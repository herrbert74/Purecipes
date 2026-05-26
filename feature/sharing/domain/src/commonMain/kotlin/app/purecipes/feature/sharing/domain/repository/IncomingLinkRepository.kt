package app.purecipes.feature.sharing.domain.repository

import app.purecipes.feature.sharing.domain.model.PurecipesLink
import kotlinx.coroutines.flow.Flow

interface IncomingLinkRepository {

	fun observeLinks(): Flow<PurecipesLink>

	fun deliver(url: String)
}
