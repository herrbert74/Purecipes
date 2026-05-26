package app.purecipes.feature.sharing.domain.usecase

import app.purecipes.feature.sharing.domain.model.PurecipesLinkUrls
import app.purecipes.feature.sharing.domain.repository.ShareRepository

class ShareRecipeUseCase(
	private val shareRepository: ShareRepository,
) {

	operator fun invoke(recipeId: Int, title: String?) {
		shareRepository.shareText(
			text = PurecipesLinkUrls.canonicalRecipeShareUrl(recipeId),
			title = title,
		)
	}
}
