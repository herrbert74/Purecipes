package app.purecipes.backend

import app.purecipes.backend.db.Db
import app.purecipes.backend.fake.FakeSessionService
import app.purecipes.backend.feature.subscription.UserPremiumRepository
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlin.test.Test

class RecipePrivacyRouteTest {

	@Test
	fun `created recipes are public by default`() = testApplication {
		val db = createInMemoryDb("recipe_privacy")
		seedAppUser(db, isPremium = false)
		val sessionService = authenticatedSession()

		application {
			module(db = db, sessionService = sessionService)
		}

		val createResponse = client.post("/recipes") {
			header(HttpHeaders.Authorization, "Bearer ${sessionService.session.accessToken}")
			contentType(ContentType.Application.Json)
			setBody(recipeWriteBody())
		}

		createResponse.status shouldBe HttpStatusCode.Created
		isRecipePrivateInDb(db, recipeId = 1) shouldBe false
	}

	@Test
	fun `free users can create a private recipe while temporary kill switch is on`() = testApplication {
		val db = createInMemoryDb("recipe_privacy")
		seedAppUser(db, isPremium = false)
		val sessionService = authenticatedSession()

		application {
			module(db = db, sessionService = sessionService)
		}

		val createResponse = client.post("/recipes") {
			header(HttpHeaders.Authorization, "Bearer ${sessionService.session.accessToken}")
			contentType(ContentType.Application.Json)
			setBody(recipeWriteBody(isPrivate = true))
		}

		createResponse.status shouldBe HttpStatusCode.Created
		createResponse.bodyAsText() shouldContain """"isPrivate":true"""
		isRecipePrivateInDb(db, recipeId = 1) shouldBe true
	}

	@Test
	fun `premium users can create a private recipe that other users cannot read or search`() = testApplication {
		val db = createInMemoryDb("recipe_privacy")
		seedAppUser(db, isPremium = true)
		val ownerSession = FakeSessionService.createSession()
		val otherSession = FakeSessionService.createSession(accessToken = "other-token", id = "2")
		val sessionService = FakeSessionService(
			initialSessions = listOf(ownerSession, otherSession),
			createMode = FakeSessionService.CreateMode.RETURN_FIRST_OR_GENERATE,
		)

		application {
			module(db = db, sessionService = sessionService)
		}

		val createResponse = client.post("/recipes") {
			header(HttpHeaders.Authorization, "Bearer ${ownerSession.accessToken}")
			contentType(ContentType.Application.Json)
			setBody(recipeWriteBody(title = "Secret Stew", isPrivate = true))
		}
		createResponse.status shouldBe HttpStatusCode.Created
		createResponse.bodyAsText() shouldContain """"isPrivate":true"""

		val ownerDetails = client.get("/recipes/1") {
			header(HttpHeaders.Authorization, "Bearer ${ownerSession.accessToken}")
		}
		ownerDetails.status shouldBe HttpStatusCode.OK
		ownerDetails.bodyAsText() shouldContain "Secret Stew"

		val anonymousDetails = client.get("/recipes/1")
		anonymousDetails.status shouldBe HttpStatusCode.NotFound

		val otherDetails = client.get("/recipes/1") {
			header(HttpHeaders.Authorization, "Bearer ${otherSession.accessToken}")
		}
		otherDetails.status shouldBe HttpStatusCode.NotFound

		val searchResponse = client.get("/recipes/search?query=Secret")
		searchResponse.status shouldBe HttpStatusCode.OK
		searchResponse.bodyAsText() shouldNotContain "Secret Stew"

		val ownerSearch = client.get("/recipes/search?query=Secret") {
			header(HttpHeaders.Authorization, "Bearer ${ownerSession.accessToken}")
		}
		ownerSearch.status shouldBe HttpStatusCode.OK
		ownerSearch.bodyAsText() shouldContain "Secret Stew"
	}

	@Test
	fun `lapsed premium users can keep an existing recipe private and make it public`() = testApplication {
		val db = createInMemoryDb("recipe_privacy")
		seedAppUser(db, isPremium = true)
		val sessionService = authenticatedSession()

		application {
			module(db = db, sessionService = sessionService)
		}

		client.post("/recipes") {
			header(HttpHeaders.Authorization, "Bearer ${sessionService.session.accessToken}")
			contentType(ContentType.Application.Json)
			setBody(recipeWriteBody(isPrivate = true))
		}.status shouldBe HttpStatusCode.Created

		UserPremiumRepository(db.dataSource).setPremium(userId = 1, isPremium = false)

		val keepPrivate = client.put("/recipes/1") {
			header(HttpHeaders.Authorization, "Bearer ${sessionService.session.accessToken}")
			contentType(ContentType.Application.Json)
			setBody(recipeWriteBody(title = "Still Private", isPrivate = true))
		}
		keepPrivate.status shouldBe HttpStatusCode.OK
		keepPrivate.bodyAsText() shouldContain """"isPrivate":true"""

		val makePublic = client.put("/recipes/1") {
			header(HttpHeaders.Authorization, "Bearer ${sessionService.session.accessToken}")
			contentType(ContentType.Application.Json)
			setBody(recipeWriteBody(title = "Now Public", isPrivate = false))
		}
		makePublic.status shouldBe HttpStatusCode.OK
		isRecipePrivateInDb(db, recipeId = 1) shouldBe false
	}

	private fun authenticatedSession(): FakeSessionService = FakeSessionService(
		initialSessions = listOf(FakeSessionService.createSession()),
		createMode = FakeSessionService.CreateMode.RETURN_FIRST_OR_GENERATE,
	)

	private fun seedAppUser(db: Db, isPremium: Boolean) {
		val premiumSql = if (isPremium) "TRUE" else "FALSE"
		db.dataSource.connection.use { connection ->
			connection.createStatement().use { statement ->
				statement.execute(
					"""
						INSERT INTO app_users (
							id,
							provider,
							external_user_id,
							email,
							display_name,
							first_name,
							family_name,
							profile_image_url,
							is_premium
						) VALUES (
							1,
							'GOOGLE',
							'user-one',
							'user-one@example.com',
							'User One',
							'User',
							'One',
							NULL,
							$premiumSql
						)
					""".trimIndent(),
				)
			}
		}
	}

	private fun isRecipePrivateInDb(db: Db, recipeId: Int): Boolean {
		db.dataSource.connection.use { connection ->
			connection.prepareStatement("SELECT is_private FROM recipes WHERE id = ?").use { statement ->
				statement.setInt(1, recipeId)
				statement.executeQuery().use { resultSet ->
					resultSet.next() shouldBe true
					return resultSet.getBoolean(1)
				}
			}
		}
	}

	private fun recipeWriteBody(title: String = "Roasted Carrots", isPrivate: Boolean = false): String {
		return """
			{
				"title": "$title",
				"description": "Sweet and savory side dish.",
				"ingredientGroups": [
					{
						"ingredients": ["8 carrots"]
					}
				],
				"steps": ["Trim the carrots", "Roast until tender"],
				"isPrivate": $isPrivate
			}
		""".trimIndent()
	}
}
