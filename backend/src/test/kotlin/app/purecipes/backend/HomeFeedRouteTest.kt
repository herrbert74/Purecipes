package app.purecipes.backend

import app.purecipes.backend.db.Db
import app.purecipes.backend.fake.FakeSessionService
import app.purecipes.shared.domain.model.HOME_SHELF_PAGE_SIZE
import app.purecipes.shared.domain.model.HomeFeed
import app.purecipes.shared.domain.model.HomeShelfId
import app.purecipes.shared.domain.model.MealType
import app.purecipes.shared.domain.model.SearchResultsPage
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import java.sql.Timestamp
import java.sql.Types
import kotlin.test.Test

class HomeFeedRouteTest {

	private val json = Json {
		ignoreUnknownKeys = true
		explicitNulls = false
	}

	@Test
	fun `new shelf is the newest public recipes that have a photo and a step`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		insertHomeRecipe(
			db,
			HomeRecipeSeed(
				id = 1,
				title = "Older Ready",
				createdAt = "2020-01-01 00:00:00",
				imageUrl = PHOTO,
				step = "Cook"
			),
		)
		insertHomeRecipe(
			db,
			HomeRecipeSeed(
				id = 2,
				title = "Newest Ready",
				createdAt = "2024-01-01 00:00:00",
				imageUrl = PHOTO,
				step = "Cook"
			),
		)
		insertHomeRecipe(
			db,
			HomeRecipeSeed(id = 3, title = "Photo Only", createdAt = "2025-01-01 00:00:00", imageUrl = PHOTO),
		)
		insertHomeRecipe(
			db,
			HomeRecipeSeed(id = 4, title = "Steps Only", createdAt = "2025-06-01 00:00:00", step = "Cook"),
		)
		insertHomeRecipe(
			db,
			HomeRecipeSeed(
				id = 5,
				title = "Private Ready",
				createdAt = "2026-01-01 00:00:00",
				imageUrl = PHOTO,
				step = "Cook",
				isPrivate = true,
			),
		)
		application { module(db = db) }

		val feed = homeFeed(localHour = BREAKFAST_HOUR)

		feed.shelf(HomeShelfId.NEW).recipes.map { recipe -> recipe.title } shouldContainExactly listOf(
			"Newest Ready",
			"Older Ready",
		)
	}

	@Test
	fun `right now follows the clock and catalog order`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		insertHomeRecipe(
			db,
			HomeRecipeSeed(
				id = 1,
				title = "Plain Breakfast",
				createdAt = "2024-01-01 00:00:00",
				mealType = "BREAKFAST",
			),
		)
		insertHomeRecipe(
			db,
			HomeRecipeSeed(
				id = 2,
				title = "Complete Breakfast",
				createdAt = "2020-01-01 00:00:00",
				mealType = "BREAKFAST",
				imageUrl = PHOTO,
				description = "Ready",
				cuisine = "Italian",
				totalTime = 20,
				step = "Cook",
				ingredient = "Eggs",
			),
		)
		insertHomeRecipe(
			db,
			HomeRecipeSeed(
				id = 3,
				title = "Dinner",
				createdAt = "2025-01-01 00:00:00",
				mealType = "DINNER",
				imageUrl = PHOTO,
				step = "Cook",
			),
		)
		application { module(db = db) }

		val breakfast = homeFeed(localHour = BREAKFAST_HOUR)
		breakfast.shelf(HomeShelfId.RIGHT_NOW).title shouldBe MealType.BREAKFAST.displayName
		breakfast.shelf(HomeShelfId.RIGHT_NOW).recipes.map { recipe -> recipe.title } shouldContainExactly listOf(
			"Complete Breakfast",
			"Plain Breakfast",
		)

		val dinner = homeFeed(localHour = DINNER_HOUR)
		dinner.shelf(HomeShelfId.RIGHT_NOW).title shouldBe MealType.DINNER.displayName
		dinner.shelf(HomeShelfId.RIGHT_NOW).recipes.map { recipe -> recipe.title } shouldContainExactly listOf("Dinner")
	}

	@Test
	fun `quick shelf stays hidden until four recipes match`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		repeat(QUICK_SHELF_BELOW_MINIMUM) { index ->
			insertHomeRecipe(
				db,
				HomeRecipeSeed(
					id = index + 1,
					title = "Quick ${index + 1}",
					createdAt = "2024-01-0${index + 1} 00:00:00",
					mealType = "LUNCH",
					totalTime = 20,
				),
			)
		}
		application { module(db = db) }

		homeFeed(localHour = LUNCH_HOUR).shelves.none { shelf -> shelf.id == HomeShelfId.QUICK } shouldBe true
	}

	@Test
	fun `quick shelf uses the current meal and catalog order`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		repeat(QUICK_SHELF_MINIMUM) { index ->
			insertHomeRecipe(
				db,
				HomeRecipeSeed(
					id = index + 1,
					title = "Quick ${index + 1}",
					createdAt = "2024-02-0${index + 1} 00:00:00",
					mealType = "LUNCH",
					totalTime = 20,
				),
			)
		}
		insertHomeRecipe(
			db,
			HomeRecipeSeed(
				id = 5,
				title = "Slow Lunch",
				createdAt = "2024-03-01 00:00:00",
				mealType = "LUNCH",
				totalTime = 45,
				imageUrl = PHOTO,
				step = "Cook",
				ingredient = "Rice",
				description = "Ready",
				cuisine = "Italian",
			),
		)
		insertHomeRecipe(
			db,
			HomeRecipeSeed(
				id = 6,
				title = "Quick Dinner",
				createdAt = "2024-03-02 00:00:00",
				mealType = "DINNER",
				totalTime = 15,
			),
		)
		application { module(db = db) }

		val quick = homeFeed(localHour = LUNCH_HOUR).shelf(HomeShelfId.QUICK)
		quick.title shouldBe "Quick"
		quick.recipes.map { recipe -> recipe.title } shouldContainExactly listOf(
			"Quick 4",
			"Quick 3",
			"Quick 2",
			"Quick 1",
		)
		quick.filters.mealTypes shouldBe setOf(MealType.LUNCH)
	}

	@Test
	fun `favorites are newest saves and stay hidden when signed out`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		seedAppUsersForSearchRouteTest(db)
		insertHomeRecipe(db, HomeRecipeSeed(id = 1, title = "Older Save", createdAt = "2020-01-01 00:00:00"))
		insertHomeRecipe(db, HomeRecipeSeed(id = 2, title = "Newer Save", createdAt = "2021-01-01 00:00:00"))
		insertFavorite(db, recipeId = 1, createdAt = "2024-01-01 00:00:00")
		insertFavorite(db, recipeId = 2, createdAt = "2025-01-01 00:00:00")
		val sessionService = FakeSessionService(
			initialSessions = listOf(FakeSessionService.createSession()),
			createMode = FakeSessionService.CreateMode.RETURN_FIRST_OR_GENERATE,
		)
		application { module(db = db, sessionService = sessionService) }

		val signedOut = homeFeed(localHour = BREAKFAST_HOUR)
		signedOut.shelves.none { shelf -> shelf.id == HomeShelfId.FAVORITES } shouldBe true

		val signedIn = homeFeed(localHour = BREAKFAST_HOUR, accessToken = "session-token")
		val favorites = signedIn.shelf(HomeShelfId.FAVORITES)
		favorites.title shouldBe "Your favorites"
		favorites.recipes.map { recipe -> recipe.title } shouldContainExactly listOf("Newer Save", "Older Save")
		favorites.recipes.all { recipe -> recipe.isFavorite } shouldBe true
	}

	@Test
	fun `feature request card is the top open request this user has not voted on`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		seedAppUsersForSearchRouteTest(db)
		insertFeatureRequest(db, id = 1, title = "Voted request", votes = 10, createdAt = "2024-01-01 00:00:00")
		insertFeatureRequest(db, id = 2, title = "Next request", votes = 4, createdAt = "2024-02-01 00:00:00")
		insertFeatureRequest(
			db,
			id = 3,
			title = "Done request",
			votes = 20,
			status = "DONE",
			createdAt = "2024-03-01 00:00:00",
		)
		insertFeatureRequestVote(db, requestId = 1)
		val sessionService = FakeSessionService(
			initialSessions = listOf(FakeSessionService.createSession()),
			createMode = FakeSessionService.CreateMode.RETURN_FIRST_OR_GENERATE,
		)
		application { module(db = db, sessionService = sessionService) }

		val signedIn = homeFeed(localHour = BREAKFAST_HOUR, accessToken = "session-token")
		signedIn.featureRequest?.title shouldBe "Next request"
		signedIn.featureRequest?.votedByCurrentUser shouldBe false

		val signedOut = homeFeed(localHour = BREAKFAST_HOUR)
		signedOut.featureRequest?.title shouldBe "Voted request"
	}

	@Test
	fun `feature request card stays expanded after this user has voted on every open request`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		seedAppUsersForSearchRouteTest(db)
		insertFeatureRequest(db, id = 1, title = "Voted request", votes = 10, createdAt = "2024-01-01 00:00:00")
		insertFeatureRequest(db, id = 2, title = "Next request", votes = 4, createdAt = "2024-02-01 00:00:00")
		insertFeatureRequestVote(db, requestId = 1)
		insertFeatureRequestVote(db, requestId = 2)
		val sessionService = FakeSessionService(
			initialSessions = listOf(FakeSessionService.createSession()),
			createMode = FakeSessionService.CreateMode.RETURN_FIRST_OR_GENERATE,
		)
		application { module(db = db, sessionService = sessionService) }

		val signedIn = homeFeed(localHour = BREAKFAST_HOUR, accessToken = "session-token")
		val featureRequest = signedIn.featureRequest
		featureRequest?.title shouldBe "Voted request"
		featureRequest?.description shouldBe "Description for Voted request"
		featureRequest?.voteCount shouldBe 11
		featureRequest?.votedByCurrentUser shouldBe true
	}

	@Test
	fun `new shelf page returns the next newest recipes`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		val newestFirst = (HOME_SHELF_PAGE_SIZE downTo 0).toList()
		newestFirst.forEach { index ->
			insertHomeRecipe(
				db,
				HomeRecipeSeed(
					id = index + 1,
					title = "Ready ${index + 1}",
					createdAt = "2024-06-01 00:${index.toString().padStart(2, '0')}:00",
					imageUrl = PHOTO,
					step = "Cook",
				),
			)
		}
		application { module(db = db) }

		val firstPage = shelfPage(shelfId = HomeShelfId.NEW, pageNumber = 1)
		firstPage.totalMatches shouldBe HOME_SHELF_PAGE_SIZE + 1
		firstPage.items.map { recipe -> recipe.title } shouldContainExactly
			(HOME_SHELF_PAGE_SIZE + 1 downTo 2).map { index -> "Ready $index" }

		val secondPage = shelfPage(shelfId = HomeShelfId.NEW, pageNumber = 2)
		secondPage.items.map { recipe -> recipe.title } shouldContainExactly listOf("Ready 1")
		secondPage.totalMatches shouldBe HOME_SHELF_PAGE_SIZE + 1
	}

	@Test
	fun `unknown shelf id is rejected`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		application { module(db = db) }

		val response = client.get("/home/shelves/HABIT?localHour=$BREAKFAST_HOUR")

		response.status shouldBe HttpStatusCode.BadRequest
	}

	private suspend fun ApplicationTestBuilder.homeFeed(
		localHour: Int,
		accessToken: String? = null,
	): HomeFeed {
		val response = client.get("/home?localHour=$localHour") {
			if (accessToken != null) {
				header(HttpHeaders.Authorization, "Bearer $accessToken")
			}
		}
		response.status shouldBe HttpStatusCode.OK
		return json.decodeFromString(HomeFeed.serializer(), response.bodyAsText())
	}

	private suspend fun ApplicationTestBuilder.shelfPage(
		shelfId: HomeShelfId,
		pageNumber: Int,
	): SearchResultsPage {
		val path = "/home/shelves/${shelfId.name}" +
			"?localHour=$BREAKFAST_HOUR&pageNumber=$pageNumber&pageSize=$HOME_SHELF_PAGE_SIZE"
		val response = client.get(path)
		response.status shouldBe HttpStatusCode.OK
		return json.decodeFromString(SearchResultsPage.serializer(), response.bodyAsText())
	}
}

private fun HomeFeed.shelf(id: HomeShelfId) = shelves.first { shelf -> shelf.id == id }

private fun insertHomeRecipe(db: Db, seed: HomeRecipeSeed) {
	db.dataSource.connection.use { connection ->
		connection.prepareStatement(
			"""
				INSERT INTO recipes (
					id, title, description, image_url, cuisine, meal_type, total_time, is_private, created_at
				) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
			""".trimIndent(),
		).use { statement ->
			statement.setInt(1, seed.id)
			statement.setString(2, seed.title)
			statement.setString(3, seed.description)
			statement.setString(4, seed.imageUrl)
			statement.setString(5, seed.cuisine)
			statement.setString(6, seed.mealType)
			if (seed.totalTime == null) {
				statement.setNull(7, Types.INTEGER)
			} else {
				statement.setInt(7, seed.totalTime)
			}
			statement.setBoolean(8, seed.isPrivate)
			statement.setTimestamp(9, Timestamp.valueOf(seed.createdAt))
			statement.executeUpdate()
		}
		if (seed.ingredient != null) {
			val groupId = connection.prepareStatement(
				"""
					INSERT INTO ingredient_groups (recipe_id, name, order_index)
					VALUES (?, NULL, 0)
				""".trimIndent(),
				java.sql.Statement.RETURN_GENERATED_KEYS,
			).use { statement ->
				statement.setInt(1, seed.id)
				statement.executeUpdate()
				statement.generatedKeys.use { keys ->
					check(keys.next())
					keys.getInt(1)
				}
			}
			connection.prepareStatement(
				"""
					INSERT INTO ingredients (ingredient_group_id, ingredient, order_index)
					VALUES (?, ?, 0)
				""".trimIndent(),
			).use { statement ->
				statement.setInt(1, groupId)
				statement.setString(2, seed.ingredient)
				statement.executeUpdate()
			}
		}
		if (seed.step != null) {
			connection.prepareStatement(
				"""
					INSERT INTO instruction_steps (recipe_id, step, order_index)
					VALUES (?, ?, 0)
				""".trimIndent(),
			).use { statement ->
				statement.setInt(1, seed.id)
				statement.setString(2, seed.step)
				statement.executeUpdate()
			}
		}
	}
}

private fun insertFavorite(db: Db, recipeId: Int, createdAt: String) {
	db.dataSource.connection.use { connection ->
		connection.prepareStatement(
			"""
				INSERT INTO favorites (user_id, recipe_id, created_at)
				VALUES (1, ?, ?)
			""".trimIndent(),
		).use { statement ->
			statement.setInt(1, recipeId)
			statement.setTimestamp(2, Timestamp.valueOf(createdAt))
			statement.executeUpdate()
		}
	}
}

private fun insertFeatureRequest(
	db: Db,
	id: Int,
	title: String,
	votes: Int,
	createdAt: String,
	status: String = "OPEN",
) {
	db.dataSource.connection.use { connection ->
		connection.prepareStatement(
			"""
				INSERT INTO feature_requests (id, title, description, status, created_at)
				VALUES (?, ?, ?, ?, ?)
			""".trimIndent(),
		).use { statement ->
			statement.setInt(1, id)
			statement.setString(2, title)
			statement.setString(3, "Description for $title")
			statement.setString(4, status)
			statement.setTimestamp(5, Timestamp.valueOf(createdAt))
			statement.executeUpdate()
		}
		repeat(votes) { index ->
			connection.prepareStatement(
				"""
					INSERT INTO app_users (
						id, provider, external_user_id, email, display_name, is_premium
					) VALUES (?, 'GOOGLE', ?, ?, ?, FALSE)
				""".trimIndent(),
			).use { statement ->
				val userId = VOTER_ID_BASE + id * VOTE_ID_STRIDE + index
				statement.setLong(1, userId)
				statement.setString(2, "voter-$userId")
				statement.setString(3, "voter-$userId@example.com")
				statement.setString(4, "Voter $userId")
				statement.executeUpdate()
			}
			connection.prepareStatement(
				"""
					INSERT INTO feature_request_votes (user_id, request_id)
					VALUES (?, ?)
				""".trimIndent(),
			).use { statement ->
				statement.setLong(1, VOTER_ID_BASE + id * VOTE_ID_STRIDE + index)
				statement.setInt(2, id)
				statement.executeUpdate()
			}
		}
	}
}

private fun insertFeatureRequestVote(db: Db, requestId: Int) {
	db.dataSource.connection.use { connection ->
		connection.prepareStatement(
			"""
				INSERT INTO feature_request_votes (user_id, request_id)
				VALUES (1, ?)
			""".trimIndent(),
		).use { statement ->
			statement.setInt(1, requestId)
			statement.executeUpdate()
		}
	}
}

private data class HomeRecipeSeed(
	val id: Int,
	val title: String,
	val createdAt: String,
	val imageUrl: String? = null,
	val description: String? = null,
	val cuisine: String? = null,
	val mealType: String? = null,
	val totalTime: Int? = null,
	val step: String? = null,
	val ingredient: String? = null,
	val isPrivate: Boolean = false,
)

private const val PHOTO = "https://example.com/photo.jpg"
private const val BREAKFAST_HOUR = 8
private const val LUNCH_HOUR = 12
private const val DINNER_HOUR = 19
private const val QUICK_SHELF_BELOW_MINIMUM = 3
private const val QUICK_SHELF_MINIMUM = 4
private const val VOTER_ID_BASE = 100L
private const val VOTE_ID_STRIDE = 100
