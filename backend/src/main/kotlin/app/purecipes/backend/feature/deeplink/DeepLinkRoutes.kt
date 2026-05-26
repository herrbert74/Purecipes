package app.purecipes.backend.feature.deeplink

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

private const val ANDROID_RELEASE_PACKAGE = "app.purecipes"
private const val ANDROID_DEBUG_PACKAGE = "app.purecipes.debug"
private const val ANDROID_STAGING_PACKAGE = "app.purecipes.staging"
private const val IOS_BUNDLE_ID = "app.purecipes.PurecipesIOSApp"

fun Route.deepLinkRoutes() {
	get("/.well-known/assetlinks.json") {
		val payload = buildAssetLinksEntries()
		if (payload.isEmpty()) {
			call.respond(HttpStatusCode.NotFound)
			return@get
		}
		call.respondText(
			text = Json.encodeToString(ListSerializer(AssetLinksEntry.serializer()), payload),
			contentType = ContentType.Application.Json,
		)
	}
	get("/.well-known/apple-app-site-association") {
		val teamId = System.getenv("PURECIPES_IOS_TEAM_ID")?.trim().orEmpty()
		if (teamId.isEmpty()) {
			call.respond(HttpStatusCode.NotFound)
			return@get
		}
		val payload = AppleAppSiteAssociation(
			applinks = AppleAppLinks(
				apps = emptyList(),
				details = listOf(
					AppleAppLinkDetail(
						appID = "$teamId.$IOS_BUNDLE_ID",
						paths = listOf("/r/*", "/c/*"),
					),
				),
			),
		)
		call.respondText(
			text = Json.encodeToString(AppleAppSiteAssociation.serializer(), payload),
			contentType = ContentType.Application.Json,
		)
	}
}

private fun buildAssetLinksEntries(): List<AssetLinksEntry> {
	val entries = mutableListOf<AssetLinksEntry>()
	parseFingerprints("PURECIPES_ANDROID_RELEASE_SHA256_CERT_FINGERPRINTS")
		.ifEmpty { parseFingerprints("PURECIPES_ANDROID_SHA256_CERT_FINGERPRINTS") }
		.takeIf { it.isNotEmpty() }
		?.let { fingerprints ->
			entries += assetLinksEntry(ANDROID_RELEASE_PACKAGE, fingerprints)
		}
	parseFingerprints("PURECIPES_ANDROID_DEBUG_SHA256_CERT_FINGERPRINTS")
		.takeIf { it.isNotEmpty() }
		?.let { fingerprints ->
			entries += assetLinksEntry(ANDROID_DEBUG_PACKAGE, fingerprints)
		}
	parseFingerprints("PURECIPES_ANDROID_STAGING_SHA256_CERT_FINGERPRINTS")
		.takeIf { it.isNotEmpty() }
		?.let { fingerprints ->
			entries += assetLinksEntry(ANDROID_STAGING_PACKAGE, fingerprints)
		}
	return entries
}

private fun parseFingerprints(envName: String): List<String> =
	System.getenv(envName)
		?.split(',')
		?.map(::normalizeAssetLinksFingerprint)
		?.filter { it.isNotEmpty() }
		.orEmpty()

private fun normalizeAssetLinksFingerprint(raw: String): String =
	raw.trim()
		.replace(" ", "")
		.uppercase()

private fun assetLinksEntry(
	packageName: String,
	sha256CertFingerprints: List<String>,
): AssetLinksEntry =
	AssetLinksEntry(
		relation = listOf("delegate_permission/common.handle_all_urls"),
		target = AssetLinksTarget(
			namespace = "android_app",
			packageName = packageName,
			sha256CertFingerprints = sha256CertFingerprints,
		),
	)

@Serializable
internal data class AssetLinksEntry(
	val relation: List<String>,
	val target: AssetLinksTarget,
)

@Serializable
internal data class AssetLinksTarget(
	val namespace: String,
	@SerialName("package_name") val packageName: String,
	@SerialName("sha256_cert_fingerprints") val sha256CertFingerprints: List<String>,
)

@Serializable
private data class AppleAppSiteAssociation(
	val applinks: AppleAppLinks,
)

@Serializable
private data class AppleAppLinks(
	val apps: List<String>,
	val details: List<AppleAppLinkDetail>,
)

@Serializable
private data class AppleAppLinkDetail(
	val appID: String,
	val paths: List<String>,
)
