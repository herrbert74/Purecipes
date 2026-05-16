package app.purecipes.backend.auth

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.util.Base64

private const val BASE64_BLOCK_SIZE = 4

internal data class FirebaseJwtClaims(
	val issuer: String?,
	val audiences: List<String>,
	val emailVerified: Boolean?,
	val subject: String?,
	val email: String?,
	val name: String?,
	val givenName: String?,
	val familyName: String?,
	val picture: String?,
)

private val jwtPayloadJson = Json {
	ignoreUnknownKeys = true
}

internal fun decodeFirebaseJwtClaims(idToken: String): FirebaseJwtClaims? {
	val payloadSegment = idToken.split('.').getOrNull(1) ?: return null
	return try {
		val payloadJson = String(Base64.getUrlDecoder().decode(padBase64Url(payloadSegment)))
		val payloadObject = jwtPayloadJson.parseToJsonElement(payloadJson).jsonObject
		FirebaseJwtClaims(
			issuer = payloadObject.readStringField("iss"),
			audiences = payloadObject.readAudienceValues(),
			emailVerified = payloadObject.readBooleanField("email_verified"),
			subject = payloadObject.readStringField("sub"),
			email = payloadObject.readStringField("email"),
			name = payloadObject.readStringField("name"),
			givenName = payloadObject.readStringField("given_name"),
			familyName = payloadObject.readStringField("family_name"),
			picture = payloadObject.readStringField("picture"),
		)
	} catch (_: IllegalArgumentException) {
		null
	} catch (_: SerializationException) {
		null
	}
}

internal fun padBase64Url(value: String): String {
	val remainder = value.length % BASE64_BLOCK_SIZE
	if (remainder == 0) {
		return value
	}
	return value + "=".repeat(BASE64_BLOCK_SIZE - remainder)
}

private fun JsonObject.readStringField(name: String): String? {
	return this[name]?.jsonPrimitive?.content?.trim()?.takeIf { it.isNotBlank() }
}

private fun JsonObject.readBooleanField(name: String): Boolean? {
	val content = this[name]?.jsonPrimitive?.content ?: return null
	return when (content.lowercase()) {
		"true" -> true
		"false" -> false
		else -> null
	}
}

private fun JsonObject.readAudienceValues(): List<String> {
	return when (val audienceElement = this["aud"]) {
		is JsonPrimitive -> listOfNotNull(audienceElement.content.trim().takeIf { it.isNotBlank() })
		is JsonArray -> audienceElement.mapNotNull { element ->
			element.jsonPrimitive.content.trim().takeIf { it.isNotBlank() }
		}
		else -> emptyList()
	}
}

internal fun resolvedFirebaseAudiences(
	jwtClaims: FirebaseJwtClaims?,
	tokenInfoAudience: String?,
): List<String> {
	return buildList {
		jwtClaims?.audiences.orEmpty().forEach { add(it) }
		tokenInfoAudience
			.asOptionalField()
			?.split(',')
			.orEmpty()
			.map { it.trim() }
			.filter { it.isNotBlank() }
			.forEach { add(it) }
	}.distinct()
}

private fun String?.asOptionalField(): String? = this?.trim()?.takeIf { it.isNotBlank() }
