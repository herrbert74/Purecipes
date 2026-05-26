package app.purecipes.feature.sharing.domain.usecase

import app.purecipes.feature.sharing.domain.model.PurecipesLinkUrls
import app.purecipes.feature.sharing.domain.repository.ShareRepository

class ShareCookbookUseCase(
	private val shareRepository: ShareRepository,
) {

	operator fun invoke(cookbookId: Int, title: String?) {
		shareRepository.shareText(
			text = PurecipesLinkUrls.canonicalCookbookShareUrl(cookbookId),
			title = title,
		)
	}
}
