package app.purecipes.backend

import app.purecipes.backend.feature.subscription.UserPremiumRepository
import io.kotest.matchers.shouldBe
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlin.test.Test

class RevenueCatWebhookRouteTest {

	@Test
	fun `blank webhook auth yields 503`() = testApplication {
		application {
			module(
				db = createInMemoryDb("revenuecat_webhook"),
				revenueCatWebhookAuthorization = "",
			)
		}

		val response = client.post("/webhooks/revenuecat") {
			header(HttpHeaders.Authorization, "secret")
			contentType(ContentType.Application.Json)
			setBody(initialPurchaseBody(appUserId = "1"))
		}

		response.status shouldBe HttpStatusCode.ServiceUnavailable
	}

	@Test
	fun `mismatched webhook auth yields 401`() = testApplication {
		application {
			module(
				db = createInMemoryDb("revenuecat_webhook"),
				revenueCatWebhookAuthorization = "expected-secret",
			)
		}

		val response = client.post("/webhooks/revenuecat") {
			header(HttpHeaders.Authorization, "wrong-secret")
			contentType(ContentType.Application.Json)
			setBody(initialPurchaseBody(appUserId = "1"))
		}

		response.status shouldBe HttpStatusCode.Unauthorized
	}

	@Test
	fun `initial purchase with premium entitlement sets is premium`() = testApplication {
		val db = createInMemoryDb("revenuecat_webhook")
		seedAppUsersForSearchRouteTest(db, isPremium = false)
		application {
			module(
				db = db,
				revenueCatWebhookAuthorization = "webhook-secret",
			)
		}

		val response = client.post("/webhooks/revenuecat") {
			header(HttpHeaders.Authorization, "webhook-secret")
			contentType(ContentType.Application.Json)
			setBody(initialPurchaseBody(appUserId = "1"))
		}

		response.status shouldBe HttpStatusCode.OK
		UserPremiumRepository(db.dataSource).isPremium(1L) shouldBe true
	}

	@Test
	fun `expiration clears is premium`() = testApplication {
		val db = createInMemoryDb("revenuecat_webhook")
		seedAppUsersForSearchRouteTest(db, isPremium = true)
		application {
			module(
				db = db,
				revenueCatWebhookAuthorization = "webhook-secret",
			)
		}

		val response = client.post("/webhooks/revenuecat") {
			header(HttpHeaders.Authorization, "webhook-secret")
			contentType(ContentType.Application.Json)
			setBody(
				"""
					{
						"api_version": "1.0",
						"event": {
							"type": "EXPIRATION",
							"app_user_id": "1",
							"entitlement_ids": ["Purecipes Pro"]
						}
					}
				""".trimIndent(),
			)
		}

		response.status shouldBe HttpStatusCode.OK
		UserPremiumRepository(db.dataSource).isPremium(1L) shouldBe false
	}

	@Test
	fun `unparseable app user id still yields 200`() = testApplication {
		val db = createInMemoryDb("revenuecat_webhook")
		seedAppUsersForSearchRouteTest(db, isPremium = false)
		application {
			module(
				db = db,
				revenueCatWebhookAuthorization = "webhook-secret",
			)
		}

		val response = client.post("/webhooks/revenuecat") {
			header(HttpHeaders.Authorization, "webhook-secret")
			contentType(ContentType.Application.Json)
			setBody(initialPurchaseBody(appUserId = "not-a-number"))
		}

		response.status shouldBe HttpStatusCode.OK
		UserPremiumRepository(db.dataSource).isPremium(1L) shouldBe false
	}

	private fun initialPurchaseBody(appUserId: String): String =
		"""
			{
				"api_version": "1.0",
				"event": {
					"type": "INITIAL_PURCHASE",
					"app_user_id": "$appUserId",
					"entitlement_ids": ["Purecipes Pro"]
				}
			}
		""".trimIndent()
}
