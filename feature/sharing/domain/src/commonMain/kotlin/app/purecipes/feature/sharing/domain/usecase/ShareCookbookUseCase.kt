package app.purecipes.feature.sharing.domain.usecase

import app.purecipes.feature.sharing.domain.model.PurecipesLinkUrls
import app.purecipes.feature.sharing.domain.repository.ShareRepository
import com.github.michaelbull.result.get

class ShareCookbookUseCase(
	private val createCookbookShareUseCase: CreateCookbookShareUseCase,
	private val shareRepository: ShareRepository,
) {

	suspend operator fun invoke(cookbookId: Int, title: String?) {
		val share = createCookbookShareUseCase(cookbookId).get() ?: return
		shareRepository.shareText(
			text = PurecipesLinkUrls.canonicalCookbookShareUrl(share.token),
			title = title,
		)
	}
}
