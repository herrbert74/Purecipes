package app.purecipes.feature.sharing.domain.usecase

import app.purecipes.feature.sharing.domain.model.PurecipesLinkUrls
import app.purecipes.feature.sharing.domain.repository.ShareRepository
import com.github.michaelbull.result.get

class ShareCookbookUseCase(
	private val createCookbookShareUseCase: CreateCookbookShareUseCase,
	private val shareRepository: ShareRepository,
) {

	suspend operator fun invoke(cookbookId: Int, recipeCount: Int, title: String?) {
		val share = createCookbookShareUseCase(cookbookId).get() ?: return
		val shareUrl = PurecipesLinkUrls.canonicalCookbookShareUrl(share.token)
		shareRepository.shareText(
			text = formatCookbookShareText(recipeCount, shareUrl),
			title = title,
		)
	}
}

internal fun formatCookbookShareText(recipeCount: Int, shareUrl: String): String {
	val recipePhrase = when (recipeCount) {
		1 -> "1 recipe"
		else -> "$recipeCount recipes"
	}
	return "The below link will save $recipePhrase to your favorites, unless you already saved them, " +
		"and save a new cookbook into your account.\n\n$shareUrl"
}
