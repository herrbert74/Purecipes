package app.purecipes.feature.sharing.data.repository

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.sharing.data.datasource.CookbookShareRemoteDataSource
import app.purecipes.feature.sharing.domain.repository.CookbookShareRepository
import app.purecipes.shared.domain.model.CookbookImportResult
import app.purecipes.shared.domain.model.CookbookShareToken

class CookbookShareAccessor(
	private val remoteDataSource: CookbookShareRemoteDataSource,
) : CookbookShareRepository {

	override suspend fun createShare(cookbookId: Int): Outcome<CookbookShareToken> =
		remoteDataSource.createShare(cookbookId)

	override suspend fun importShare(token: String): Outcome<CookbookImportResult> =
		remoteDataSource.importShare(token)
}
