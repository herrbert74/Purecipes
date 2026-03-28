package com.purecipes.backend.auth

import com.purecipes.shared.domain.model.VerifiedGoogleUser
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

interface GoogleIdTokenVerifier {

	suspend fun verify(idToken: String): GoogleIdTokenVerificationResult
}

sealed interface GoogleIdTokenVerificationResult {

	data class Success(val user: VerifiedGoogleUser) : GoogleIdTokenVerificationResult

	data class Invalid(val detail: String) : GoogleIdTokenVerificationResult

	data class ConfigurationError(val detail: String) : GoogleIdTokenVerificationResult
}

class GoogleTokenInfoGoogleIdTokenVerifier(
	private val client: HttpClient = HttpClient(CIO) {
		install(ContentNegotiation) {
			json(
				Json {
					ignoreUnknownKeys = true
					explicitNulls = false
				}
			)
		}
	},
	private val expectedAudience: String? = System.getenv("PURECIPES_GOOGLE_WEB_CLIENT_ID"),
) : GoogleIdTokenVerifier {

	override suspend fun verify(idToken: String): GoogleIdTokenVerificationResult {
		val configuredAudience = expectedAudience?.trim().orEmpty()
		if (configuredAudience.isBlank()) {
			return GoogleIdTokenVerificationResult.ConfigurationError(
				detail = "PURECIPES_GOOGLE_WEB_CLIENT_ID is not configured on the backend",
			)
		}

		val tokenInfo = fetchTokenInfo(idToken)
			?: return GoogleIdTokenVerificationResult.Invalid("Google token verification failed")

		return tokenInfo.toVerificationResult(configuredAudience)
	}

	private suspend fun fetchTokenInfo(idToken: String): GoogleTokenInfoResponse? {
		return try {
			client.get("https://oauth2.googleapis.com/tokeninfo") {
				parameter("id_token", idToken)
			}.body<GoogleTokenInfoResponse>()
		} catch (_: ClientRequestException) {
			null
		}
	}
}

@Serializable
private data class GoogleTokenInfoResponse(
	@SerialName("sub")
	val subject: String? = null,
	@SerialName("email")
	val email: String? = null,
	@SerialName("email_verified")
	val emailVerified: String? = null,
	@SerialName("name")
	val name: String? = null,
	@SerialName("given_name")
	val givenName: String? = null,
	@SerialName("family_name")
	val familyName: String? = null,
	@SerialName("picture")
	val picture: String? = null,
	@SerialName("aud")
	val audience: String? = null,
)

private fun GoogleTokenInfoResponse.toVerificationResult(configuredAudience: String): GoogleIdTokenVerificationResult {
	val subject = subject.asOptionalField()
	val email = email.asOptionalField()
	val invalidDetail = when {
		audience != configuredAudience -> "Google token audience does not match this backend"
		!emailVerified.isVerifiedGoogleEmail() -> "Google email address is not verified"
		subject == null -> "Google token did not include a subject"
		email == null -> "Google token did not include an email address"
		else -> null
	}

	if (invalidDetail != null) {
		return GoogleIdTokenVerificationResult.Invalid(detail = invalidDetail)
	}

	return GoogleIdTokenVerificationResult.Success(
		user = VerifiedGoogleUser(
			id = subject.orEmpty(),
			email = email.orEmpty().lowercase(),
			displayName = name.asOptionalField() ?: email.orEmpty().fallbackDisplayName(),
			firstName = givenName.asOptionalField(),
			familyName = familyName.asOptionalField(),
			profileImageUrl = picture.asOptionalField(),
		),
	)
}

private fun String?.asOptionalField(): String? = this?.trim()?.takeIf { it.isNotBlank() }

private fun String?.isVerifiedGoogleEmail(): Boolean = this.equals("true", ignoreCase = true)

private fun String.fallbackDisplayName(): String {
	return substringBefore('@').replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}
