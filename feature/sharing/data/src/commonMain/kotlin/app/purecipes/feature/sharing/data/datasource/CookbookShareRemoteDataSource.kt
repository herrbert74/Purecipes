package app.purecipes.feature.sharing.data.datasource

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.shared.data.network.PurecipesApi
import app.purecipes.shared.data.util.runCatchingApi
import app.purecipes.shared.domain.model.CookbookImportResult
import app.purecipes.shared.domain.model.CookbookShareToken

class CookbookShareRemoteDataSource(
	private val api: PurecipesApi,
) {

	suspend fun createShare(cookbookId: Int): Outcome<CookbookShareToken> = runCatchingApi {
		api.createCookbookShare(cookbookId)
	}

	suspend fun importShare(token: String): Outcome<CookbookImportResult> = runCatchingApi {
		api.importCookbookShare(token)
	}
}
