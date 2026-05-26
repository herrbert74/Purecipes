package app.purecipes.feature.sharing.domain.model

sealed interface PurecipesLink {
	data class Recipe(val id: Int) : PurecipesLink

	data class CookbookShare(val token: String) : PurecipesLink
}

object PurecipesLinkUrls {
	const val WEB_HOST = "purecipes.app"
	const val CUSTOM_SCHEME = "purecipes"

	fun recipeHttps(id: Int): String = "https://$WEB_HOST/r/$id"

	fun cookbookHttps(token: String): String = "https://$WEB_HOST/c/$token"

	fun recipeCustomScheme(id: Int): String = "$CUSTOM_SCHEME://r/$id"

	fun cookbookCustomScheme(token: String): String = "$CUSTOM_SCHEME://c/$token"

	fun canonicalRecipeShareUrl(id: Int): String = recipeHttps(id)

	fun canonicalCookbookShareUrl(token: String): String = cookbookHttps(token)
}
