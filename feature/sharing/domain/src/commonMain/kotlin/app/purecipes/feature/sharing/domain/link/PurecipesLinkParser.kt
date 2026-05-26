package app.purecipes.feature.sharing.domain.link

import app.purecipes.feature.sharing.domain.model.PurecipesLink
import app.purecipes.feature.sharing.domain.model.PurecipesLinkUrls

object PurecipesLinkParser {

	private const val RECIPE_PATH_SEGMENT = "r"
	private const val COOKBOOK_PATH_SEGMENT = "c"

	fun parse(raw: String): PurecipesLink? {
		val segments = pathSegments(raw.trim()) ?: return null
		return linkFromSegments(segments)
	}

	private fun linkFromSegments(segments: List<String>): PurecipesLink? {
		if (segments.size < 2) {
			return null
		}
		val typeSegment = segments[0].lowercase()
		val id = segments[1].toIntOrNull() ?: return null
		return when (typeSegment) {
			RECIPE_PATH_SEGMENT -> PurecipesLink.Recipe(id)
			COOKBOOK_PATH_SEGMENT -> PurecipesLink.Cookbook(id)
			else -> null
		}
	}

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
