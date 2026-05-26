package app.purecipes.feature.sharing.domain.model

sealed interface PurecipesLink {
	data class Recipe(val id: Int) : PurecipesLink

	data class Cookbook(val id: Int) : PurecipesLink
}

object PurecipesLinkUrls {
	const val WEB_HOST = "purecipes.app"
	const val CUSTOM_SCHEME = "purecipes"

	fun recipeHttps(id: Int): String = "https://$WEB_HOST/r/$id"

	fun cookbookHttps(id: Int): String = "https://$WEB_HOST/c/$id"

	fun recipeCustomScheme(id: Int): String = "$CUSTOM_SCHEME://r/$id"

	fun cookbookCustomScheme(id: Int): String = "$CUSTOM_SCHEME://c/$id"

	fun canonicalRecipeShareUrl(id: Int): String = recipeHttps(id)

	fun canonicalCookbookShareUrl(id: Int): String = cookbookHttps(id)
}
