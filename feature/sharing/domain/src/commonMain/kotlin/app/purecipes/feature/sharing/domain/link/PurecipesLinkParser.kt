package app.purecipes.feature.sharing.domain.link

import app.purecipes.feature.sharing.domain.model.PurecipesLink
import app.purecipes.feature.sharing.domain.model.PurecipesLinkUrls

object PurecipesLinkParser {

	private const val RECIPE_PATH_SEGMENT = "r"
	private const val COOKBOOK_PATH_SEGMENT = "c"

	private val COOKBOOK_SHARE_TOKEN_REGEX = Regex(
		"^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
	)

	fun parse(raw: String): PurecipesLink? {
		val segments = pathSegments(raw.trim()) ?: return null
		return linkFromSegments(segments)
	}

	private fun linkFromSegments(segments: List<String>): PurecipesLink? {
		if (segments.size < 2) {
			return null
		}
		val typeSegment = segments[0].lowercase()
		val valueSegment = segments[1]
		return when (typeSegment) {
			RECIPE_PATH_SEGMENT ->
				valueSegment.toIntOrNull()?.let { PurecipesLink.Recipe(it) }
			COOKBOOK_PATH_SEGMENT ->
				valueSegment
					.takeIf(::isCookbookShareToken)
					?.let { PurecipesLink.CookbookShare(it) }
			else -> null
		}
	}

	private fun isCookbookShareToken(value: String): Boolean = COOKBOOK_SHARE_TOKEN_REGEX.matches(value)

	internal fun pathSegments(raw: String): List<String>? {
		if (raw.isEmpty()) {
			return null
		}
		val withoutQuery = raw.substringBefore('?').substringBefore('#')
		return when {
			withoutQuery.startsWith("/") -> withoutQuery.trim('/').split('/').filter { it.isNotEmpty() }
			isCustomSchemeUrl(withoutQuery) -> customSchemeSegments(withoutQuery)
			isWebUrl(withoutQuery) -> webPathSegments(withoutQuery)
			else -> null
		}
	}

	private fun isCustomSchemeUrl(url: String): Boolean =
		url.startsWith("${PurecipesLinkUrls.CUSTOM_SCHEME}:", ignoreCase = true)

	private fun customSchemeSegments(url: String): List<String> {
		val afterScheme = url.substringAfter("://", missingDelimiterValue = url)
		return afterScheme.trim('/').split('/').filter { it.isNotEmpty() }
	}

	private fun isWebUrl(url: String): Boolean =
		url.contains(PurecipesLinkUrls.WEB_HOST, ignoreCase = true) ||
			url.contains("www.${PurecipesLinkUrls.WEB_HOST}", ignoreCase = true)

	private fun webPathSegments(url: String): List<String>? {
		val hostIndex = url.indexOf(PurecipesLinkUrls.WEB_HOST, ignoreCase = true)
		if (hostIndex < 0) {
			return null
		}
		val pathStart = url.indexOf('/', hostIndex + PurecipesLinkUrls.WEB_HOST.length)
		if (pathStart < 0) {
			return emptyList()
		}
		return url.substring(pathStart).trim('/').split('/').filter { it.isNotEmpty() }
	}
}
