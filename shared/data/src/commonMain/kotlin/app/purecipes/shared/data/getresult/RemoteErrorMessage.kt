package app.purecipes.shared.data.getresult

internal const val DEFAULT_SERVER_ERROR_MESSAGE = "Something went wrong. Please try again."

internal const val NETWORK_BLOCKED_MESSAGE =
	"Your network blocked access to Purecipes. Try a different connection or contact your network administrator."

private const val MAX_USER_FACING_MESSAGE_LENGTH = 300

private const val MIN_EXTRACTED_LINE_LENGTH = 3

private const val MIN_BODY_TEXT_LENGTH = 10

private const val MAX_EXTRACTED_HEADINGS = 3

private const val HTML_SAMPLE_LENGTH = 256

private val HTML_TAG_TEXT_REGEX = Regex(
	"""<(?:title|h[1-6]|p|div|span|td|th)(?:\s[^>]*)?>([\s\S]*?)</(?:title|h[1-6]|p|div|span|td|th)>""",
	RegexOption.IGNORE_CASE,
)

private val HTML_TAG_REGEX = Regex("<[^>]+>")

private val WHITESPACE_REGEX = Regex("\\s+")

private val NETWORK_BLOCK_KEYWORDS = listOf(
	"web page blocked",
	"usage policy",
	"fortiguard",
	"intrusion prevention",
	"web filter",
	"access denied",
	"firewall",
	"proxy",
	"blocked by",
)

fun String.toUserFacingRemoteErrorMessage(
	fallback: String = DEFAULT_SERVER_ERROR_MESSAGE,
): String {
	val trimmed = trim()
	return when {
		trimmed.isBlank() -> fallback
		looksLikeHtml(trimmed) -> extractFromHtml(trimmed) ?: networkBlockedOrFallback(trimmed, fallback)
		else -> sanitizedNonHtmlMessage(trimmed, fallback)
	}
}

private fun sanitizedNonHtmlMessage(text: String, fallback: String): String {
	val htmlStart = text.indexOf('<')
	if (htmlStart >= 0) {
		val htmlSuffix = text.substring(htmlStart)
		if (looksLikeHtml(htmlSuffix)) {
			return extractFromHtml(htmlSuffix) ?: networkBlockedOrFallback(htmlSuffix, fallback)
		}
	}
	return if (text.length > MAX_USER_FACING_MESSAGE_LENGTH) {
		fallback
	} else {
		text
	}
}

private fun networkBlockedOrFallback(text: String, fallback: String): String {
	return if (indicatesNetworkBlock(text)) {
		NETWORK_BLOCKED_MESSAGE
	} else {
		fallback
	}
}

private fun looksLikeHtml(text: String): Boolean {
	val sample = text.trimStart().take(HTML_SAMPLE_LENGTH).lowercase()
	return sample.startsWith("<!doctype") ||
		sample.startsWith("<html") ||
		(
			sample.contains('<') &&
				sample.contains('>') &&
				(
					sample.contains("<head") ||
						sample.contains("<body") ||
						sample.contains("<title") ||
						sample.contains("<h1")
					)
			)
}

private fun extractFromHtml(html: String): String? {
	val candidates = HTML_TAG_TEXT_REGEX
		.findAll(html)
		.map { match -> stripHtmlTags(match.groupValues[1]) }
		.filter { text ->
			text.isNotBlank() && text.length in MIN_EXTRACTED_LINE_LENGTH..MAX_USER_FACING_MESSAGE_LENGTH
		}
		.filterNot { text -> isNoiseLine(text) }
		.distinct()
		.toList()

	val headingMessage = candidates
		.take(MAX_EXTRACTED_HEADINGS)
		.joinToString(". ")
		.take(MAX_USER_FACING_MESSAGE_LENGTH)
		.takeIf { it.isNotBlank() }
	if (headingMessage != null) {
		return headingMessage
	}

	val bodyText = stripHtmlTags(html).take(MAX_USER_FACING_MESSAGE_LENGTH)
	return bodyText
		.takeIf { text ->
			text.length in MIN_BODY_TEXT_LENGTH..MAX_USER_FACING_MESSAGE_LENGTH && !text.contains('<')
		}
}

private fun isNoiseLine(text: String): Boolean {
	val normalized = text.lowercase()
	return normalized.startsWith("url:") ||
		normalized.startsWith("http://") ||
		normalized.startsWith("https://")
}

private fun indicatesNetworkBlock(text: String): Boolean {
	val normalized = text.lowercase()
	return NETWORK_BLOCK_KEYWORDS.any { keyword -> normalized.contains(keyword) }
}

private fun stripHtmlTags(text: String): String {
	var result = text
	while (result.contains('<')) {
		result = result.replace(HTML_TAG_REGEX, " ")
	}
	return decodeHtmlEntities(result).replace(WHITESPACE_REGEX, " ").trim()
}

private fun decodeHtmlEntities(text: String): String = text
	.replace("&nbsp;", " ")
	.replace("&amp;", "&")
	.replace("&lt;", "<")
	.replace("&gt;", ">")
	.replace("&quot;", "\"")
	.replace("&#39;", "'")
