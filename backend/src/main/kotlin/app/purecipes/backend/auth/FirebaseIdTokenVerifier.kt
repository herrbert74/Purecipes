package app.purecipes.backend.auth

import app.purecipes.shared.domain.model.EMAIL_NOT_VERIFIED_MESSAGE
import app.purecipes.shared.domain.model.VerifiedGoogleUser
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
import java.util.Properties

private const val BACKEND_CONFIG_RESOURCE = "purecipes-backend.properties"
private const val FIREBASE_PROJECT_ID_PROPERTY = "purecipes.firebaseProjectId"
private const val FIREBASE_PROJECT_ID_ENV = "PURECIPES_FIREBASE_PROJECT_ID"
private const val FIREBASE_PROJECT_NUMBER_PROPERTY = "purecipes.firebaseProjectNumber"
private const val FIREBASE_PROJECT_NUMBER_ENV = "PURECIPES_FIREBASE_PROJECT_NUMBER"
private const val FIREBASE_ISSUER_PREFIX = "https://securetoken.google.com/"

interface FirebaseIdTokenVerifier {

	suspend fun verify(idToken: String): GoogleIdTokenVerificationResult
}

class FirebaseTokenInfoIdTokenVerifier(
	private val client: HttpClient = HttpClient(CIO) {
		install(ContentNegotiation) {
			json(
				Json {
					ignoreUnknownKeys = true
					explicitNulls = false
				},
			)
		}
	},
	private val expectedProjectId: String? = resolveFirebaseProjectId(),
	private val expectedProjectNumber: String? = resolveFirebaseProjectNumber(),
) : FirebaseIdTokenVerifier {

	override suspend fun verify(idToken: String): GoogleIdTokenVerificationResult {
		val configuredProjectId = expectedProjectId?.trim().orEmpty()
		if (configuredProjectId.isBlank()) {
			return GoogleIdTokenVerificationResult.ConfigurationError(
				detail = "PURECIPES_FIREBASE_PROJECT_ID is not configured on the backend",
			)
		}

		val tokenInfo = fetchTokenInfo(idToken)
			?: return GoogleIdTokenVerificationResult.Invalid("Firebase token verification failed")

		val jwtClaims = decodeFirebaseJwtClaims(idToken)
		return tokenInfo.toVerificationResult(
			jwtClaims = jwtClaims,
			configuredProjectId = configuredProjectId,
			configuredProjectNumber = expectedProjectNumber?.trim()?.takeIf { it.isNotBlank() },
		)
	}

	private suspend fun fetchTokenInfo(idToken: String): FirebaseTokenInfoResponse? {
		return try {
			client.get("https://oauth2.googleapis.com/tokeninfo") {
				parameter("id_token", idToken)
			}.body<FirebaseTokenInfoResponse>()
		} catch (_: ClientRequestException) {
			null
		}
	}
}

internal fun resolveFirebaseProjectId(
	systemProperty: (String) -> String? = System::getProperty,
	environmentVariable: (String) -> String? = System::getenv,
	resourceProperty: (String) -> String? = ::readBundledFirebaseProperty,
): String? {
	return resolveFirebaseConfigValue(
		propertyKey = FIREBASE_PROJECT_ID_PROPERTY,
		envKey = FIREBASE_PROJECT_ID_ENV,
		systemProperty = systemProperty,
		environmentVariable = environmentVariable,
		resourceProperty = resourceProperty,
	)
}

internal fun resolveFirebaseProjectNumber(
	systemProperty: (String) -> String? = System::getProperty,
	environmentVariable: (String) -> String? = System::getenv,
	resourceProperty: (String) -> String? = ::readBundledFirebaseProperty,
): String? {
	return resolveFirebaseConfigValue(
		propertyKey = FIREBASE_PROJECT_NUMBER_PROPERTY,
		envKey = FIREBASE_PROJECT_NUMBER_ENV,
		systemProperty = systemProperty,
		environmentVariable = environmentVariable,
		resourceProperty = resourceProperty,
	)
}

internal fun resolveFirebaseConfigValue(
	propertyKey: String,
	envKey: String,
	systemProperty: (String) -> String?,
	environmentVariable: (String) -> String?,
	resourceProperty: (String) -> String?,
): String? {
	return systemProperty(propertyKey)
		?.trim()
		?.takeIf { it.isNotBlank() }
		?: systemProperty(envKey)
			?.trim()
			?.takeIf { it.isNotBlank() }
		?: environmentVariable(envKey)
			?.trim()
			?.takeIf { it.isNotBlank() }
		?: resourceProperty(propertyKey)
}

internal fun firebaseIssuerProjectId(issuer: String?): String? {
	val normalizedIssuer = issuer?.trim().orEmpty()
	if (!normalizedIssuer.startsWith(FIREBASE_ISSUER_PREFIX)) {
		return null
	}
	return normalizedIssuer
		.removePrefix(FIREBASE_ISSUER_PREFIX)
		.trim()
		.takeIf { it.isNotBlank() }
}

internal fun matchesConfiguredFirebaseProject(
	issuer: String?,
	audiences: Collection<String>,
	configuredProjectId: String,
	configuredProjectNumber: String? = null,
): Boolean {
	val issuerProjectId = firebaseIssuerProjectId(issuer)
	val normalizedAudiences = audiences.map { it.trim() }.filter { it.isNotBlank() }
	return issuerProjectId == configuredProjectId ||
		normalizedAudiences.any { it == configuredProjectId } ||
		(configuredProjectNumber != null && normalizedAudiences.any { it == configuredProjectNumber })
}

private fun readBundledFirebaseProperty(key: String): String? {
	val stream = FirebaseTokenInfoIdTokenVerifier::class.java.classLoader
		.getResourceAsStream(BACKEND_CONFIG_RESOURCE)
		?: return null

	return stream.use {
		Properties()
			.apply { load(it) }
			.getProperty(key)
			?.trim()
			?.takeIf { value -> value.isNotBlank() }
	}
}

@Serializable
private data class FirebaseTokenInfoResponse(
	@SerialName("sub")
	val subject: String? = null,
	@SerialName("user_id")
	val userId: String? = null,
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
	@SerialName("iss")
	val issuer: String? = null,
)

private fun FirebaseTokenInfoResponse.toVerificationResult(
	jwtClaims: FirebaseJwtClaims?,
	configuredProjectId: String,
	configuredProjectNumber: String?,
): GoogleIdTokenVerificationResult {
	val resolvedSubject = jwtClaims?.subject ?: subject.asOptionalField() ?: userId.asOptionalField()
	val resolvedEmail = jwtClaims?.email ?: email.asOptionalField()
	val resolvedName = jwtClaims?.name ?: name.asOptionalField()
	val resolvedGivenName = jwtClaims?.givenName ?: givenName.asOptionalField()
	val resolvedFamilyName = jwtClaims?.familyName ?: familyName.asOptionalField()
	val resolvedPicture = jwtClaims?.picture ?: picture.asOptionalField()
	val resolvedIssuer = jwtClaims?.issuer ?: issuer
	val resolvedAudiences = resolvedFirebaseAudiences(jwtClaims = jwtClaims, tokenInfoAudience = audience)
	val issuerProjectId = firebaseIssuerProjectId(resolvedIssuer)
	val invalidDetail = when {
		!matchesConfiguredFirebaseProject(
			issuer = resolvedIssuer,
			audiences = resolvedAudiences,
			configuredProjectId = configuredProjectId,
			configuredProjectNumber = configuredProjectNumber,
		) -> projectMismatchDetail(
			issuerProjectId = issuerProjectId,
			configuredProjectId = configuredProjectId,
			audiences = resolvedAudiences,
			issuer = resolvedIssuer,
		)
		!isEmailVerified(jwtClaims) -> EMAIL_NOT_VERIFIED_MESSAGE
		resolvedSubject == null -> "Sign-in could not be completed. Please try again."
		resolvedEmail == null -> "Sign-in did not include an email address."
		else -> null
	}

	if (invalidDetail != null) {
		return GoogleIdTokenVerificationResult.Invalid(detail = invalidDetail)
	}

	return GoogleIdTokenVerificationResult.Success(
		user = VerifiedGoogleUser(
			id = resolvedSubject.orEmpty(),
			email = resolvedEmail.orEmpty().lowercase(),
			displayName = resolvedName ?: resolvedEmail.orEmpty().fallbackDisplayName(),
			firstName = resolvedGivenName,
			familyName = resolvedFamilyName,
			profileImageUrl = resolvedPicture,
		),
	)
}

private fun projectMismatchDetail(
	issuerProjectId: String?,
	configuredProjectId: String,
	audiences: List<String>,
	issuer: String?,
): String {
	if (issuerProjectId != null) {
		return "Firebase token is for project '$issuerProjectId', but this backend expects " +
			"'$configuredProjectId'. Set PURECIPES_FIREBASE_PROJECT_ID to '$issuerProjectId' " +
			"(the Firebase project ID, not a web client ID or domain)."
	}
	if (audiences.any { it.contains(".apps.googleusercontent.com") }) {
		return "This looks like a Google OAuth token, not a Firebase Auth token. " +
			"Use email sign-in after verifying your email."
	}
	val audienceSummary = audiences.takeIf { it.isNotEmpty() }?.joinToString() ?: "unknown"
	return "Firebase token is not issued for project '$configuredProjectId' " +
		"(issuer: ${issuer.asOptionalField() ?: "unknown"}, audience: $audienceSummary)."
}

private fun String?.asOptionalField(): String? = this?.trim()?.takeIf { it.isNotBlank() }

private fun FirebaseTokenInfoResponse.isEmailVerified(jwtClaims: FirebaseJwtClaims?): Boolean {
	if (jwtClaims?.emailVerified == true) {
		return true
	}
	return emailVerified.equals("true", ignoreCase = true)
}

private fun String.fallbackDisplayName(): String {
	return substringBefore('@').replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}
