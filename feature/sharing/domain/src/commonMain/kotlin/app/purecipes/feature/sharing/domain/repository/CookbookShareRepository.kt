package app.purecipes.feature.sharing.domain.repository

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.shared.domain.model.CookbookImportResult
import app.purecipes.shared.domain.model.CookbookShareToken

interface CookbookShareRepository {

	suspend fun createShare(cookbookId: Int): Outcome<CookbookShareToken>

	suspend fun importShare(token: String): Outcome<CookbookImportResult>
}
