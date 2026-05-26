package app.purecipes.feature.sharing.domain.usecase

import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.feature.sharing.domain.repository.CookbookShareRepository
import app.purecipes.shared.domain.model.CookbookImportResult

class ImportCookbookShareUseCase(
	private val cookbookShareRepository: CookbookShareRepository,
) {

	suspend operator fun invoke(token: String): Outcome<CookbookImportResult> =
		cookbookShareRepository.importShare(token)
}
