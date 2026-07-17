package app.purecipes.backend.feature.subscription

import app.purecipes.backend.db.Db
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.header
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

private const val PREMIUM_ENTITLEMENT_ID = "Purecipes Pro"

fun Route.revenueCatWebhookRoutes(
	dbProvider: () -> Db,
	webhookAuthorization: String =
		System.getenv("PURECIPES_REVENUECAT_WEBHOOK_AUTH")?.trim().orEmpty(),
) {
	route("/webhooks/revenuecat") {
		post {
			if (webhookAuthorization.isBlank()) {
				call.respond(HttpStatusCode.ServiceUnavailable)
				return@post
			}
			val authorization = call.request.header(HttpHeaders.Authorization)?.trim().orEmpty()
			if (authorization != webhookAuthorization) {
				call.respond(HttpStatusCode.Unauthorized)
				return@post
			}

			val payload = call.receive<RevenueCatWebhookPayload>()
			applyRevenueCatPremiumUpdate(
				repository = UserPremiumRepository(dbProvider().dataSource),
				event = payload.event,
			)
			call.respond(HttpStatusCode.OK)
		}
	}
}

internal fun applyRevenueCatPremiumUpdate(
	repository: UserPremiumRepository,
	event: RevenueCatWebhookEvent,
) {
	val userId = event.appUserId?.trim()?.toLongOrNull() ?: return
	when (event.type) {
		"EXPIRATION" -> repository.setPremium(userId, isPremium = false)
		"INITIAL_PURCHASE",
		"RENEWAL",
		"UNCANCELLATION",
		"NON_RENEWING_PURCHASE",
		"PRODUCT_CHANGE",
		"TEMPORARY_ENTITLEMENT_GRANT",
			-> {
			val hasPremium = event.entitlementIds.orEmpty().contains(PREMIUM_ENTITLEMENT_ID)
			repository.setPremium(userId, isPremium = hasPremium)
		}

		else -> Unit
	}
}

@Serializable
internal data class RevenueCatWebhookPayload(
	@SerialName("api_version")
	val apiVersion: String? = null,
	val event: RevenueCatWebhookEvent,
)

@Serializable
internal data class RevenueCatWebhookEvent(
	val type: String,
	@SerialName("app_user_id")
	val appUserId: String? = null,
	@SerialName("entitlement_ids")
	val entitlementIds: List<String>? = null,
)
