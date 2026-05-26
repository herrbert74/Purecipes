package app.purecipes.feature.favorites.ui

import app.purecipes.base.kotlin.result.Failure
import app.purecipes.feature.sharing.domain.repository.CookbookShareRepository
import app.purecipes.feature.sharing.domain.repository.ShareRepository
import app.purecipes.feature.sharing.domain.usecase.CreateCookbookShareUseCase
import app.purecipes.feature.sharing.domain.usecase.ImportCookbookShareUseCase
import app.purecipes.feature.sharing.domain.usecase.ShareCookbookUseCase
import com.github.michaelbull.result.Err

internal fun unusedImportCookbookShareUseCase(): ImportCookbookShareUseCase =
	ImportCookbookShareUseCase(unusedCookbookShareRepository())

internal fun unusedShareCookbookUseCase(): ShareCookbookUseCase =
	ShareCookbookUseCase(
		createCookbookShareUseCase = CreateCookbookShareUseCase(unusedCookbookShareRepository()),
		shareRepository = unusedShareRepository(),
	)

private fun unusedCookbookShareRepository(): CookbookShareRepository =
	object : CookbookShareRepository {
		override suspend fun createShare(cookbookId: Int) = Err(Failure.ServerError("unused"))

		override suspend fun importShare(token: String) = Err(Failure.ServerError("unused"))
	}

private fun unusedShareRepository(): ShareRepository =
	object : ShareRepository {
		override fun shareText(text: String, title: String?) = Unit
	}
