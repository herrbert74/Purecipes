package app.purecipes.backend

import app.purecipes.backend.db.Db
import app.purecipes.backend.fake.FakeSessionService
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.DietaryPreference
import app.purecipes.shared.domain.model.HOME_SHELF_PAGE_SIZE
import app.purecipes.shared.domain.model.HomeCollectionSource
import app.purecipes.shared.domain.model.HomeFeed
import app.purecipes.shared.domain.model.HomeShelfId
import app.purecipes.shared.domain.model.MealType
import app.purecipes.shared.domain.model.SearchFilters
import app.purecipes.shared.domain.model.SearchResultsPage
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
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
	fun `recently viewed is the newest open and stays hidden when signed out`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		seedAppUsersForSearchRouteTest(db)
		insertHomeRecipe(db, HomeRecipeSeed(id = 1, title = "Early", createdAt = "2020-01-01 00:00:00"))
		insertHomeRecipe(db, HomeRecipeSeed(id = 2, title = "Later", createdAt = "2021-01-01 00:00:00"))
		insertHomeRecipe(db, HomeRecipeSeed(id = 3, title = "Cooked only", createdAt = "2022-01-01 00:00:00"))
		insertHomeRecipe(
			db,
			HomeRecipeSeed(
				id = 4,
				title = "Private mine",
				createdAt = "2023-01-01 00:00:00",
				isPrivate = true,
				createdByUserId = 1,
			),
		)
		insertHomeRecipe(
			db,
			HomeRecipeSeed(
				id = 5,
				title = "Private theirs",
				createdAt = "2023-02-01 00:00:00",
				isPrivate = true,
			),
		)
		insertRecipeActivity(db, recipeId = 1, event = "OPEN", createdAt = "2024-01-01 00:00:00")
		insertRecipeActivity(db, recipeId = 2, event = "OPEN", createdAt = "2024-01-02 00:00:00")
		insertRecipeActivity(db, recipeId = 2, event = "OPEN", createdAt = "2024-03-01 00:00:00")
		insertRecipeActivity(db, recipeId = 3, event = "COOK_COMPLETE", createdAt = "2024-04-01 00:00:00")
		insertRecipeActivity(db, recipeId = 4, event = "OPEN", createdAt = "2024-02-01 00:00:00")
		insertRecipeActivity(db, recipeId = 5, event = "OPEN", createdAt = "2024-05-01 00:00:00")
		insertFavorite(db, recipeId = 2, createdAt = "2024-06-01 00:00:00")
		application { module(db = db, sessionService = signedInSessionService()) }

		val signedOut = homeFeed(localHour = BREAKFAST_HOUR)
		signedOut.shelves.none { shelf -> shelf.id == HomeShelfId.RECENTLY_VIEWED } shouldBe true

		val recentlyViewed = homeFeed(localHour = BREAKFAST_HOUR, accessToken = "session-token")
			.shelf(HomeShelfId.RECENTLY_VIEWED)
		recentlyViewed.title shouldBe "Recently viewed"
		recentlyViewed.recipes.map { recipe -> recipe.title } shouldContainExactly listOf(
			"Later",
			"Private mine",
			"Early",
		)
		recentlyViewed.recipes.map { recipe -> recipe.isFavorite } shouldContainExactly listOf(true, false, false)
	}

	@Test
	fun `recently viewed page follows the latest open`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		seedAppUsersForSearchRouteTest(db)
		repeat(HOME_SHELF_PAGE_SIZE + 1) { index ->
			val id = index + 1
			val day = id.toString().padStart(2, '0')
			insertHomeRecipe(
				db,
				HomeRecipeSeed(id = id, title = "Opened $id", createdAt = "2020-01-01 00:00:00"),
			)
			insertRecipeActivity(
				db,
				recipeId = id,
				event = "OPEN",
				createdAt = "2024-01-$day 00:00:00",
			)
		}
		application { module(db = db, sessionService = signedInSessionService()) }

		val signedOutPage = shelfPage(shelfId = HomeShelfId.RECENTLY_VIEWED, pageNumber = 1)
		signedOutPage.totalMatches shouldBe 0
		signedOutPage.items shouldBe emptyList()

		val firstPage = shelfPage(
			shelfId = HomeShelfId.RECENTLY_VIEWED,
			pageNumber = 1,
			accessToken = "session-token",
		)
		firstPage.totalMatches shouldBe HOME_SHELF_PAGE_SIZE + 1
		firstPage.items.map { recipe -> recipe.title } shouldContainExactly
			(HOME_SHELF_PAGE_SIZE + 1 downTo 2).map { index -> "Opened $index" }

		val secondPage = shelfPage(
			shelfId = HomeShelfId.RECENTLY_VIEWED,
			pageNumber = 2,
			accessToken = "session-token",
		)
		secondPage.items.map { recipe -> recipe.title } shouldContainExactly listOf("Opened 1")
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
	fun `habit shelf stays hidden when signed out`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		seedAppUsersForSearchRouteTest(db)
		insertItalianFavorites(db, count = HABIT_SHELF_MINIMUM_FAVORITES)
		application { module(db = db, sessionService = signedInSessionService()) }

		val signedOut = homeFeed(localHour = BREAKFAST_HOUR)
		signedOut.shelves.none { shelf -> shelf.id == HomeShelfId.HABIT } shouldBe true

		val signedOutPage = shelfPage(shelfId = HomeShelfId.HABIT, pageNumber = 1)
		signedOutPage.totalMatches shouldBe 0
		signedOutPage.items shouldBe emptyList()
	}

	@Test
	fun `habit shelf stays hidden until three favorites share a cuisine`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		seedAppUsersForSearchRouteTest(db)
		insertItalianFavorites(db, count = HABIT_SHELF_MINIMUM_FAVORITES - 1)
		insertHomeRecipe(
			db,
			HomeRecipeSeed(id = 3, title = "Mexican", createdAt = "2022-01-01 00:00:00", cuisine = "Mexican"),
		)
		insertHomeRecipe(
			db,
			HomeRecipeSeed(id = 4, title = "Also Mexican", createdAt = "2022-02-01 00:00:00", cuisine = "Mexican"),
		)
		insertFavorite(db, recipeId = 3, createdAt = "2024-03-01 00:00:00")
		insertFavorite(db, recipeId = 4, createdAt = "2024-04-01 00:00:00")
		application { module(db = db, sessionService = signedInSessionService()) }

		val feed = homeFeed(localHour = BREAKFAST_HOUR, accessToken = "session-token")
		feed.shelves.none { shelf -> shelf.id == HomeShelfId.HABIT } shouldBe true
		feed.shelf(HomeShelfId.FAVORITES).recipes.size shouldBe HABIT_SHELF_MINIMUM_FAVORITES + 1
	}

	@Test
	fun `habit shelf is the cuisine with the most favorites in catalog order`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		seedAppUsersForSearchRouteTest(db)
		insertHomeRecipe(
			db,
			HomeRecipeSeed(id = 1, title = "Older Italian", createdAt = "2020-01-01 00:00:00", cuisine = "Italian"),
		)
		insertHomeRecipe(
			db,
			HomeRecipeSeed(id = 2, title = "Middle Italian", createdAt = "2021-01-01 00:00:00", cuisine = "Italian"),
		)
		insertHomeRecipe(
			db,
			HomeRecipeSeed(id = 3, title = "Newer Italian", createdAt = "2022-01-01 00:00:00", cuisine = "Italian"),
		)
		insertHomeRecipe(
			db,
			HomeRecipeSeed(
				id = 4,
				title = "Complete Italian",
				createdAt = "2019-01-01 00:00:00",
				cuisine = "Italian",
				imageUrl = PHOTO,
				description = "Ready",
				mealType = "DINNER",
				totalTime = 40,
				step = "Cook",
				ingredient = "Pasta",
			),
		)
		insertHomeRecipe(
			db,
			HomeRecipeSeed(id = 5, title = "Mexican One", createdAt = "2023-01-01 00:00:00", cuisine = "Mexican"),
		)
		insertHomeRecipe(
			db,
			HomeRecipeSeed(id = 6, title = "Mexican Two", createdAt = "2023-02-01 00:00:00", cuisine = "Mexican"),
		)
		insertHomeRecipe(
			db,
			HomeRecipeSeed(id = 7, title = "French", createdAt = "2023-03-01 00:00:00", cuisine = "French"),
		)
		insertFavorite(db, recipeId = 1, createdAt = "2024-01-01 00:00:00")
		insertFavorite(db, recipeId = 2, createdAt = "2024-02-01 00:00:00")
		insertFavorite(db, recipeId = 3, createdAt = "2024-03-01 00:00:00")
		insertFavorite(db, recipeId = 5, createdAt = "2024-04-01 00:00:00")
		insertFavorite(db, recipeId = 6, createdAt = "2024-05-01 00:00:00")
		application { module(db = db, sessionService = signedInSessionService()) }

		val feed = homeFeed(localHour = BREAKFAST_HOUR, accessToken = "session-token")
		val habit = feed.shelf(HomeShelfId.HABIT)
		val favoriteIndex = feed.shelves.indexOfFirst { shelf -> shelf.id == HomeShelfId.FAVORITES }
		favoriteIndex shouldBe feed.shelves.indexOfFirst { shelf -> shelf.id == HomeShelfId.HABIT } + 1
		habit.title shouldBe Cuisine.ITALIAN.displayName
		habit.filters.cuisines shouldBe setOf(Cuisine.ITALIAN)
		habit.recipes.map { recipe -> recipe.title } shouldContainExactly listOf(
			"Complete Italian",
			"Newer Italian",
			"Middle Italian",
			"Older Italian",
		)
		habit.recipes.map { recipe -> recipe.isFavorite } shouldContainExactly listOf(false, true, true, true)
	}

	@Test
	fun `habit shelf breaks a cuisine tie with the newest save`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		seedAppUsersForSearchRouteTest(db)
		insertCuisineFavorites(
			db = db,
			cuisine = "Italian",
			titlePrefix = "Italian",
			firstId = 1,
			latestFavoriteAt = "2024-06-01 00:00:00",
		)
		insertCuisineFavorites(
			db = db,
			cuisine = "Mexican",
			titlePrefix = "Mexican",
			firstId = 4,
			latestFavoriteAt = "2025-01-01 00:00:00",
		)
		application { module(db = db, sessionService = signedInSessionService()) }

		val habit = homeFeed(localHour = BREAKFAST_HOUR, accessToken = "session-token").shelf(HomeShelfId.HABIT)
		habit.title shouldBe Cuisine.MEXICAN.displayName
		habit.recipes.map { recipe -> recipe.cuisine } shouldContainExactly List(HABIT_SHELF_MINIMUM_FAVORITES) {
			Cuisine.MEXICAN
		}
	}

	@Test
	fun `habit shelf breaks a remaining cuisine tie alphabetically`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		seedAppUsersForSearchRouteTest(db)
		val savedAt = "2024-06-01 00:00:00"
		insertCuisineFavorites(
			db = db,
			cuisine = "Mexican",
			titlePrefix = "Mexican",
			firstId = 1,
			latestFavoriteAt = savedAt,
		)
		insertCuisineFavorites(
			db = db,
			cuisine = "Italian",
			titlePrefix = "Italian",
			firstId = 4,
			latestFavoriteAt = savedAt,
		)
		application { module(db = db, sessionService = signedInSessionService()) }

		homeFeed(localHour = BREAKFAST_HOUR, accessToken = "session-token")
			.shelf(HomeShelfId.HABIT)
			.title shouldBe Cuisine.ITALIAN.displayName
	}

	@Test
	fun `a private favorite counts toward the habit cuisine and stays off the shelf`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		seedAppUsersForSearchRouteTest(db)
		insertHomeRecipe(
			db,
			HomeRecipeSeed(id = 1, title = "Public Italian", createdAt = "2021-01-01 00:00:00", cuisine = "Italian"),
		)
		insertHomeRecipe(
			db,
			HomeRecipeSeed(id = 2, title = "Another Italian", createdAt = "2022-01-01 00:00:00", cuisine = "Italian"),
		)
		insertHomeRecipe(
			db,
			HomeRecipeSeed(
				id = 3,
				title = "Private Italian",
				createdAt = "2023-01-01 00:00:00",
				cuisine = "Italian",
				isPrivate = true,
				createdByUserId = 1,
			),
		)
		insertFavorite(db, recipeId = 1, createdAt = "2024-01-01 00:00:00")
		insertFavorite(db, recipeId = 2, createdAt = "2024-02-01 00:00:00")
		insertFavorite(db, recipeId = 3, createdAt = "2024-03-01 00:00:00")
		application { module(db = db, sessionService = signedInSessionService()) }

		val habit = homeFeed(localHour = BREAKFAST_HOUR, accessToken = "session-token").shelf(HomeShelfId.HABIT)
		habit.title shouldBe Cuisine.ITALIAN.displayName
		habit.recipes.map { recipe -> recipe.title } shouldContainExactly listOf(
			"Another Italian",
			"Public Italian",
		)
	}

	@Test
	fun `habit shelf page returns the next recipes of that cuisine`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		seedAppUsersForSearchRouteTest(db)
		val newestFirst = (HOME_SHELF_PAGE_SIZE downTo 0).toList()
		newestFirst.forEach { index ->
			insertHomeRecipe(
				db,
				HomeRecipeSeed(
					id = index + 1,
					title = "Italian ${index + 1}",
					createdAt = "2024-06-01 00:${index.toString().padStart(2, '0')}:00",
					cuisine = "Italian",
				),
			)
		}
		repeat(HABIT_SHELF_MINIMUM_FAVORITES) { index ->
			insertFavorite(db, recipeId = index + 1, createdAt = "2025-01-0${index + 1} 00:00:00")
		}
		application { module(db = db, sessionService = signedInSessionService()) }

		val firstPage = shelfPage(
			shelfId = HomeShelfId.HABIT,
			pageNumber = 1,
			accessToken = "session-token",
		)
		firstPage.totalMatches shouldBe HOME_SHELF_PAGE_SIZE + 1
		firstPage.items.map { recipe -> recipe.title } shouldContainExactly
			(HOME_SHELF_PAGE_SIZE + 1 downTo 2).map { index -> "Italian $index" }

		val secondPage = shelfPage(
			shelfId = HomeShelfId.HABIT,
			pageNumber = 2,
			accessToken = "session-token",
		)
		secondPage.items.map { recipe -> recipe.title } shouldContainExactly listOf("Italian 1")
		secondPage.totalMatches shouldBe HOME_SHELF_PAGE_SIZE + 1
	}

	@Test
	fun `activity shelf stays hidden until three opens or finished cooks share a cuisine`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		seedAppUsersForSearchRouteTest(db)
		insertHomeRecipe(
			db,
			HomeRecipeSeed(id = 1, title = "Mexican", createdAt = "2022-01-01 00:00:00", cuisine = "Mexican"),
		)
		insertRecipeActivity(db, recipeId = 1, event = "OPEN", createdAt = "2024-01-01 00:00:00")
		insertRecipeActivity(db, recipeId = 1, event = "COOK_COMPLETE", createdAt = "2024-01-02 00:00:00")
		application { module(db = db, sessionService = signedInSessionService()) }

		val feed = homeFeed(localHour = BREAKFAST_HOUR, accessToken = "session-token")
		feed.shelves.none { shelf -> shelf.id == HomeShelfId.HABIT } shouldBe true
	}

	@Test
	fun `activity shelf is the cuisine with the most events in catalog order`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		seedAppUsersForSearchRouteTest(db)
		insertHomeRecipe(
			db,
			HomeRecipeSeed(id = 1, title = "Newer Mexican", createdAt = "2022-01-01 00:00:00", cuisine = "Mexican"),
		)
		insertHomeRecipe(
			db,
			HomeRecipeSeed(
				id = 2,
				title = "Complete Mexican",
				createdAt = "2019-01-01 00:00:00",
				cuisine = "Mexican",
				imageUrl = PHOTO,
				description = "Ready",
				mealType = "DINNER",
				totalTime = 40,
				step = "Cook",
				ingredient = "Beans",
			),
		)
		insertHomeRecipe(
			db,
			HomeRecipeSeed(id = 3, title = "Italian", createdAt = "2021-01-01 00:00:00", cuisine = "Italian"),
		)
		insertRecipeActivity(db, recipeId = 1, event = "OPEN", createdAt = "2024-01-01 00:00:00")
		insertRecipeActivity(db, recipeId = 1, event = "COOK_COMPLETE", createdAt = "2024-01-02 00:00:00")
		insertRecipeActivity(db, recipeId = 2, event = "OPEN", createdAt = "2024-01-03 00:00:00")
		insertRecipeActivity(db, recipeId = 2, event = "COOK_COMPLETE", createdAt = "2024-01-04 00:00:00")
		repeat(ACTIVITY_HABIT_MINIMUM_EVENTS) { index ->
			insertRecipeActivity(
				db,
				recipeId = 3,
				event = "OPEN",
				createdAt = "2024-02-0${index + 1} 00:00:00",
			)
		}
		application { module(db = db, sessionService = signedInSessionService()) }

		val habits = homeFeed(localHour = BREAKFAST_HOUR, accessToken = "session-token")
			.shelves
			.filter { shelf -> shelf.id == HomeShelfId.HABIT }
		habits.map { shelf -> shelf.title } shouldContainExactly listOf(
			Cuisine.MEXICAN.displayName,
			Cuisine.ITALIAN.displayName,
		)
		habits.first().habitCuisine shouldBe Cuisine.MEXICAN.displayName
		habits.first().filters.cuisines shouldBe setOf(Cuisine.MEXICAN)
		habits.first().recipes.map { recipe -> recipe.title } shouldContainExactly listOf(
			"Complete Mexican",
			"Newer Mexican",
		)
	}

	@Test
	fun `activity shelf breaks a cuisine tie with the newest event`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		seedAppUsersForSearchRouteTest(db)
		insertActivityCuisine(
			db,
			cuisine = "Italian",
			titlePrefix = "Italian",
			firstId = 1,
			latestEventAt = "2024-06-01 00:00:00",
		)
		insertActivityCuisine(
			db,
			cuisine = "Mexican",
			titlePrefix = "Mexican",
			firstId = 4,
			latestEventAt = "2025-01-01 00:00:00",
		)
		application { module(db = db, sessionService = signedInSessionService()) }

		val habits = homeFeed(localHour = BREAKFAST_HOUR, accessToken = "session-token")
			.shelves
			.filter { shelf -> shelf.id == HomeShelfId.HABIT }
		habits.map { shelf -> shelf.title } shouldContainExactly listOf(
			Cuisine.MEXICAN.displayName,
			Cuisine.ITALIAN.displayName,
		)
	}

	@Test
	fun `activity shelf breaks a remaining cuisine tie alphabetically`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		seedAppUsersForSearchRouteTest(db)
		val eventAt = "2024-06-01 00:00:00"
		insertActivityCuisine(
			db,
			cuisine = "Mexican",
			titlePrefix = "Mexican",
			firstId = 1,
			latestEventAt = eventAt,
		)
		insertActivityCuisine(
			db,
			cuisine = "Italian",
			titlePrefix = "Italian",
			firstId = 4,
			latestEventAt = eventAt,
		)
		application { module(db = db, sessionService = signedInSessionService()) }

		val habits = homeFeed(localHour = BREAKFAST_HOUR, accessToken = "session-token")
			.shelves
			.filter { shelf -> shelf.id == HomeShelfId.HABIT }
		habits.map { shelf -> shelf.title } shouldContainExactly listOf(
			Cuisine.ITALIAN.displayName,
			Cuisine.MEXICAN.displayName,
		)
	}

	@Test
	fun `a private cook counts toward the activity cuisine and stays off the shelf`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		seedAppUsersForSearchRouteTest(db)
		insertHomeRecipe(
			db,
			HomeRecipeSeed(id = 1, title = "Public Italian", createdAt = "2021-01-01 00:00:00", cuisine = "Italian"),
		)
		insertHomeRecipe(
			db,
			HomeRecipeSeed(id = 2, title = "Another Italian", createdAt = "2022-01-01 00:00:00", cuisine = "Italian"),
		)
		insertHomeRecipe(
			db,
			HomeRecipeSeed(
				id = 3,
				title = "Private Italian",
				createdAt = "2023-01-01 00:00:00",
				cuisine = "Italian",
				isPrivate = true,
				createdByUserId = 1,
			),
		)
		insertRecipeActivity(db, recipeId = 1, event = "OPEN", createdAt = "2024-01-01 00:00:00")
		insertRecipeActivity(db, recipeId = 2, event = "OPEN", createdAt = "2024-01-02 00:00:00")
		insertRecipeActivity(db, recipeId = 3, event = "COOK_COMPLETE", createdAt = "2024-01-03 00:00:00")
		application { module(db = db, sessionService = signedInSessionService()) }

		val habit = homeFeed(localHour = BREAKFAST_HOUR, accessToken = "session-token").shelf(HomeShelfId.HABIT)
		habit.title shouldBe Cuisine.ITALIAN.displayName
		habit.habitCuisine shouldBe Cuisine.ITALIAN.displayName
		habit.recipes.map { recipe -> recipe.title } shouldContainExactly listOf(
			"Another Italian",
			"Public Italian",
		)
	}

	@Test
	fun `activity shelf skips the favorites habit cuisine and home keeps two habit shelves`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		seedAppUsersForSearchRouteTest(db)
		insertCuisineFavorites(
			db = db,
			cuisine = "Italian",
			titlePrefix = "Italian",
			firstId = 1,
			latestFavoriteAt = "2024-01-01 00:00:00",
		)
		insertActivityCuisine(
			db,
			cuisine = "Mexican",
			titlePrefix = "Mexican",
			firstId = 4,
			latestEventAt = "2024-06-01 00:00:00",
		)
		insertActivityCuisine(
			db,
			cuisine = "Japanese",
			titlePrefix = "Japanese",
			firstId = 7,
			latestEventAt = "2024-05-01 00:00:00",
		)
		insertActivityCuisine(
			db,
			cuisine = "French",
			titlePrefix = "French",
			firstId = 10,
			latestEventAt = "2024-04-01 00:00:00",
		)
		repeat(ACTIVITY_HABIT_MINIMUM_EVENTS) { index ->
			insertRecipeActivity(
				db,
				recipeId = 4,
				event = "COOK_COMPLETE",
				createdAt = "2024-07-0${index + 1} 00:00:00",
			)
		}
		application { module(db = db, sessionService = signedInSessionService()) }

		val feed = homeFeed(localHour = BREAKFAST_HOUR, accessToken = "session-token")
		val habits = feed.shelves.filter { shelf -> shelf.id == HomeShelfId.HABIT }
		val recentlyViewedIndex = feed.shelves.indexOfFirst { shelf -> shelf.id == HomeShelfId.RECENTLY_VIEWED }
		val favoriteIndex = feed.shelves.indexOfFirst { shelf -> shelf.id == HomeShelfId.FAVORITES }
		feed.shelves.indexOfFirst { shelf -> shelf.id == HomeShelfId.HABIT } shouldBe recentlyViewedIndex + 1
		favoriteIndex shouldBe feed.shelves.indexOfLast { shelf -> shelf.id == HomeShelfId.HABIT } + 1
		habits.map { shelf -> shelf.title } shouldContainExactly listOf(
			Cuisine.ITALIAN.displayName,
			Cuisine.MEXICAN.displayName,
		)
		habits.map { shelf -> shelf.habitCuisine } shouldContainExactly listOf(null, Cuisine.MEXICAN.displayName)
	}

	@Test
	fun `activity shelf stays hidden when signed out`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		seedAppUsersForSearchRouteTest(db)
		insertActivityCuisine(
			db,
			cuisine = "Italian",
			titlePrefix = "Italian",
			firstId = 1,
			latestEventAt = "2024-06-01 00:00:00",
		)
		application { module(db = db, sessionService = signedInSessionService()) }

		val feed = homeFeed(localHour = BREAKFAST_HOUR)
		feed.shelves.none { shelf -> shelf.id == HomeShelfId.HABIT } shouldBe true
	}

	@Test
	fun `activity shelf page follows habitCuisine`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		seedAppUsersForSearchRouteTest(db)
		val newestFirst = (HOME_SHELF_PAGE_SIZE downTo 0).toList()
		newestFirst.forEach { index ->
			insertHomeRecipe(
				db,
				HomeRecipeSeed(
					id = index + 1,
					title = "Italian ${index + 1}",
					createdAt = "2024-06-01 00:${index.toString().padStart(2, '0')}:00",
					cuisine = "Italian",
				),
			)
		}
		repeat(ACTIVITY_HABIT_MINIMUM_EVENTS) { index ->
			insertRecipeActivity(db, recipeId = index + 1, event = "OPEN", createdAt = "2025-01-0${index + 1} 00:00:00")
		}
		application { module(db = db, sessionService = signedInSessionService()) }

		val favoritesPage = shelfPage(
			shelfId = HomeShelfId.HABIT,
			pageNumber = 1,
			accessToken = "session-token",
		)
		favoritesPage.totalMatches shouldBe 0
		favoritesPage.items shouldBe emptyList()

		val firstPage = shelfPage(
			shelfId = HomeShelfId.HABIT,
			pageNumber = 1,
			accessToken = "session-token",
			habitCuisine = Cuisine.ITALIAN.displayName,
		)
		firstPage.totalMatches shouldBe HOME_SHELF_PAGE_SIZE + 1
		firstPage.items.map { recipe -> recipe.title } shouldContainExactly
			(HOME_SHELF_PAGE_SIZE + 1 downTo 2).map { index -> "Italian $index" }

		val secondPage = shelfPage(
			shelfId = HomeShelfId.HABIT,
			pageNumber = 2,
			accessToken = "session-token",
			habitCuisine = Cuisine.ITALIAN.displayName,
		)
		secondPage.items.map { recipe -> recipe.title } shouldContainExactly listOf("Italian 1")
	}

	@Test
	fun `unknown habit cuisine is rejected`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		application { module(db = db) }

		val response = client.get(
			"/home/shelves/${HomeShelfId.HABIT.name}?localHour=$BREAKFAST_HOUR&habitCuisine=NotACuisine",
		)

		response.status shouldBe HttpStatusCode.BadRequest
	}

	@Test
	fun `recording an open requires a signed-in user and a visible recipe`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		seedAppUsersForSearchRouteTest(db)
		insertHomeRecipe(
			db,
			HomeRecipeSeed(id = 1, title = "Public Italian", createdAt = "2021-01-01 00:00:00", cuisine = "Italian"),
		)
		insertHomeRecipe(
			db,
			HomeRecipeSeed(
				id = 2,
				title = "Private Italian",
				createdAt = "2021-02-01 00:00:00",
				cuisine = "Italian",
				isPrivate = true,
			),
		)
		application { module(db = db, sessionService = signedInSessionService()) }

		val signedOut = client.post("/recipes/1/activity") {
			header(HttpHeaders.ContentType, "application/json")
			setBody("""{"event":"OPEN"}""")
		}
		signedOut.status shouldBe HttpStatusCode.Unauthorized

		val missing = client.post("/recipes/99/activity") {
			header(HttpHeaders.Authorization, "Bearer session-token")
			header(HttpHeaders.ContentType, "application/json")
			setBody("""{"event":"OPEN"}""")
		}
		missing.status shouldBe HttpStatusCode.NotFound

		val hidden = client.post("/recipes/2/activity") {
			header(HttpHeaders.Authorization, "Bearer session-token")
			header(HttpHeaders.ContentType, "application/json")
			setBody("""{"event":"COOK_COMPLETE"}""")
		}
		hidden.status shouldBe HttpStatusCode.NotFound

		val invalid = client.post("/recipes/1/activity") {
			header(HttpHeaders.Authorization, "Bearer session-token")
			header(HttpHeaders.ContentType, "application/json")
			setBody("""{"event":"SAVED"}""")
		}
		invalid.status shouldBe HttpStatusCode.BadRequest

		repeat(ACTIVITY_HABIT_MINIMUM_EVENTS) {
			val recorded = client.post("/recipes/1/activity") {
				header(HttpHeaders.Authorization, "Bearer session-token")
				header(HttpHeaders.ContentType, "application/json")
				setBody("""{"event":"OPEN"}""")
			}
			recorded.status shouldBe HttpStatusCode.NoContent
		}

		val habit = homeFeed(localHour = BREAKFAST_HOUR, accessToken = "session-token").shelf(HomeShelfId.HABIT)
		habit.title shouldBe Cuisine.ITALIAN.displayName
		habit.habitCuisine shouldBe Cuisine.ITALIAN.displayName
		habit.recipes.map { recipe -> recipe.title } shouldContainExactly listOf("Public Italian")
	}

	@Test
	fun `editorial collection is ordered by position while it is live`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		seedAppUsersForSearchRouteTest(db)
		insertHomeRecipe(db, HomeRecipeSeed(id = 1, title = "Later", createdAt = "2024-02-01 00:00:00"))
		insertHomeRecipe(db, HomeRecipeSeed(id = 2, title = "Sooner", createdAt = "2024-01-01 00:00:00"))
		insertHomeRecipe(
			db,
			HomeRecipeSeed(id = 3, title = "Private pick", createdAt = "2024-03-01 00:00:00", isPrivate = true),
		)
		insertHomeRecipe(db, HomeRecipeSeed(id = 4, title = "Unused", createdAt = "2024-04-01 00:00:00"))
		insertFavorite(db, recipeId = 2, createdAt = "2024-05-01 00:00:00")
		insertHomeCollection(
			db,
			HomeCollectionSeed(
				id = 1,
				slug = "this-week",
				title = "This week",
				source = "editorial",
				position = 2,
				recipePositions = listOf(1 to 2, 2 to 1, 3 to 3),
			),
		)
		insertHomeCollection(
			db,
			HomeCollectionSeed(
				id = 2,
				slug = "next-month",
				title = "Next month",
				source = "editorial",
				position = 1,
				recipePositions = listOf(4 to 1),
				startsAt = "2099-01-01 00:00:00",
			),
		)
		insertHomeCollection(
			db,
			HomeCollectionSeed(
				id = 3,
				slug = "last-month",
				title = "Last month",
				source = "editorial",
				position = 3,
				recipePositions = listOf(4 to 1),
				endsAt = "2000-01-01 00:00:00",
			),
		)
		insertHomeCollection(
			db,
			HomeCollectionSeed(
				id = 4,
				slug = "hidden",
				title = "Hidden",
				source = "editorial",
				position = 0,
				recipePositions = listOf(4 to 1),
				enabled = false,
			),
		)
		application { module(db = db, sessionService = signedInSessionService()) }

		val feed = homeFeed(localHour = BREAKFAST_HOUR, accessToken = "session-token")
		val week = feed.shelves.first { shelf -> shelf.collectionSlug == "this-week" }

		week.title shouldBe "This week"
		week.id shouldBe HomeShelfId.COLLECTION
		week.collectionSource shouldBe HomeCollectionSource.EDITORIAL
		week.filters shouldBe SearchFilters()
		week.recipes.map { recipe -> recipe.title } shouldContainExactly listOf("Sooner", "Later")
		week.recipes.first().isFavorite shouldBe true
		feed.shelves.mapNotNull { shelf -> shelf.collectionSlug } shouldContainExactly listOf("this-week")

		val secondRecipe = collectionPage(slug = "this-week", pageNumber = 2, accessToken = "session-token")
		secondRecipe.totalMatches shouldBe 2
		secondRecipe.items.map { recipe -> recipe.title } shouldContainExactly listOf("Later")

		collectionResponse("missing").status shouldBe HttpStatusCode.NotFound
		collectionResponse("next-month").status shouldBe HttpStatusCode.NotFound
		collectionResponse("Not-A-Slug").status shouldBe HttpStatusCode.BadRequest
	}

	@Test
	fun `query collection uses the saved filters and sort`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		insertHomeRecipe(
			db,
			HomeRecipeSeed(
				id = 1,
				title = "Complete Italian",
				createdAt = "2020-01-01 00:00:00",
				imageUrl = PHOTO,
				description = "Ready",
				cuisine = "Italian",
				mealType = "DINNER",
				totalTime = 40,
				step = "Cook",
				ingredient = "Pasta",
			),
		)
		insertHomeRecipe(
			db,
			HomeRecipeSeed(
				id = 2,
				title = "Plain Italian",
				createdAt = "2024-01-01 00:00:00",
				cuisine = "Italian",
			),
		)
		insertHomeRecipe(
			db,
			HomeRecipeSeed(
				id = 3,
				title = "French",
				createdAt = "2025-01-01 00:00:00",
				cuisine = "French",
			),
		)
		insertHomeCollection(
			db,
			HomeCollectionSeed(
				id = 1,
				slug = "italian-now",
				title = "Italian now",
				source = "query",
				position = 2,
				filterPayload = """{"cuisines":["Italian"]}""",
				sort = "catalog",
			),
		)
		insertHomeCollection(
			db,
			HomeCollectionSeed(
				id = 2,
				slug = "newest-italian",
				title = "Newest Italian",
				source = "query",
				position = 1,
				filterPayload = """{"cuisines":["Italian"]}""",
				sort = "newest",
			),
		)
		insertHomeCollection(
			db,
			HomeCollectionSeed(
				id = 3,
				slug = "broken-query",
				title = "Broken",
				source = "query",
				position = 3,
				filterPayload = """{"cuisines":["Not a cuisine"]}""",
			),
		)
		application { module(db = db) }

		val feed = homeFeed(localHour = BREAKFAST_HOUR)
		val slugs = feed.shelves.mapNotNull { shelf -> shelf.collectionSlug }

		slugs shouldContainExactly listOf("newest-italian", "italian-now")
		val newest = feed.shelves.first { shelf -> shelf.collectionSlug == "newest-italian" }
		val catalog = feed.shelves.first { shelf -> shelf.collectionSlug == "italian-now" }
		newest.collectionSource shouldBe HomeCollectionSource.QUERY
		newest.filters shouldBe SearchFilters(cuisines = setOf(Cuisine.ITALIAN))
		newest.recipes.map { recipe -> recipe.title } shouldContainExactly listOf("Plain Italian", "Complete Italian")
		catalog.recipes.map { recipe -> recipe.title } shouldContainExactly listOf("Complete Italian", "Plain Italian")
	}

	@Test
	fun `unknown shelf id is rejected`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		application { module(db = db) }

		val response = client.get("/home/shelves/NOT_A_SHELF?localHour=$BREAKFAST_HOUR")

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

	private suspend fun ApplicationTestBuilder.collectionPage(
		slug: String,
		pageNumber: Int,
		pageSize: Int = 1,
		accessToken: String? = null,
	): SearchResultsPage {
		val response = collectionResponse(slug, pageNumber, pageSize, accessToken)
		response.status shouldBe HttpStatusCode.OK
		return json.decodeFromString(SearchResultsPage.serializer(), response.bodyAsText())
	}

	private suspend fun ApplicationTestBuilder.collectionResponse(
		slug: String,
		pageNumber: Int = 1,
		pageSize: Int = 1,
		accessToken: String? = null,
	) = client.get("/home/collections/$slug?pageNumber=$pageNumber&pageSize=$pageSize") {
		if (accessToken != null) {
			header(HttpHeaders.Authorization, "Bearer $accessToken")
		}
	}

	private suspend fun ApplicationTestBuilder.shelfPage(
		shelfId: HomeShelfId,
		pageNumber: Int,
		accessToken: String? = null,
		habitCuisine: String? = null,
	): SearchResultsPage {
		val cuisineQuery = if (habitCuisine == null) "" else "&habitCuisine=$habitCuisine"
		val path = "/home/shelves/${shelfId.name}" +
			"?localHour=$BREAKFAST_HOUR&pageNumber=$pageNumber&pageSize=$HOME_SHELF_PAGE_SIZE$cuisineQuery"
		val response = client.get(path) {
			if (accessToken != null) {
				header(HttpHeaders.Authorization, "Bearer $accessToken")
			}
		}
		response.status shouldBe HttpStatusCode.OK
		return json.decodeFromString(SearchResultsPage.serializer(), response.bodyAsText())
	}

	@Test
	fun `home feed filters all shelves according to user dietary preferences`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		seedAppUsersForSearchRouteTest(db)
		insertSearchFilters(
			db = db,
			userId = 1L,
			filters = SearchFilters(dietaryPreferences = setOf(DietaryPreference.VEGAN)),
		)
		insertHomeRecipe(
			db,
			HomeRecipeSeed(
				id = 1,
				title = "Vegan Breakfast 1",
				createdAt = "2024-01-01 00:00:00",
				imageUrl = PHOTO,
				step = "Cook",
				cuisine = "Italian",
				mealType = "BREAKFAST",
				totalTime = 15,
				dietaryPreferences = listOf("VEGAN"),
			),
		)
		insertHomeRecipe(
			db,
			HomeRecipeSeed(
				id = 2,
				title = "Meat Breakfast",
				createdAt = "2024-01-02 00:00:00",
				imageUrl = PHOTO,
				step = "Cook",
				cuisine = "Italian",
				mealType = "BREAKFAST",
				totalTime = 15,
				dietaryPreferences = emptyList(),
			),
		)
		insertHomeRecipe(
			db,
			HomeRecipeSeed(
				id = 3,
				title = "Vegan Breakfast 2",
				createdAt = "2024-01-03 00:00:00",
				imageUrl = PHOTO,
				step = "Cook",
				cuisine = "Italian",
				mealType = "BREAKFAST",
				totalTime = 20,
				dietaryPreferences = listOf("VEGAN"),
			),
		)
		insertHomeRecipe(
			db,
			HomeRecipeSeed(
				id = 4,
				title = "Vegan Breakfast 3",
				createdAt = "2024-01-04 00:00:00",
				imageUrl = PHOTO,
				step = "Cook",
				cuisine = "Italian",
				mealType = "BREAKFAST",
				totalTime = 25,
				dietaryPreferences = listOf("VEGAN"),
			),
		)
		insertHomeRecipe(
			db,
			HomeRecipeSeed(
				id = 5,
				title = "Vegan Breakfast 4",
				createdAt = "2024-01-05 00:00:00",
				imageUrl = PHOTO,
				step = "Cook",
				cuisine = "Italian",
				mealType = "BREAKFAST",
				totalTime = 25,
				dietaryPreferences = listOf("VEGAN"),
			),
		)
		insertFavorite(db, recipeId = 1, createdAt = "2024-01-01 00:00:00")
		insertFavorite(db, recipeId = 2, createdAt = "2024-01-02 00:00:00")
		insertFavorite(db, recipeId = 3, createdAt = "2024-01-03 00:00:00")
		insertRecipeActivity(db, recipeId = 1, event = "OPEN", createdAt = "2024-01-01 00:00:00")
		insertRecipeActivity(db, recipeId = 2, event = "OPEN", createdAt = "2024-01-02 00:00:00")
		insertHomeCollection(
			db,
			HomeCollectionSeed(
				id = 1,
				slug = "editor-picks",
				title = "Editor Picks",
				source = HomeCollectionSource.EDITORIAL.name,
				position = 1,
				recipePositions = listOf(1 to 1, 2 to 2),
			),
		)
		application { module(db = db, sessionService = signedInSessionService()) }

		val unauthenticatedFeed = homeFeed(localHour = BREAKFAST_HOUR)
		unauthenticatedFeed.shelf(HomeShelfId.NEW).recipes.map { it.title } shouldContainExactly listOf(
			"Vegan Breakfast 4",
			"Vegan Breakfast 3",
			"Vegan Breakfast 2",
			"Meat Breakfast",
			"Vegan Breakfast 1",
		)
		unauthenticatedFeed.shelf(HomeShelfId.COLLECTION).recipes.map { it.title } shouldContainExactly listOf(
			"Vegan Breakfast 1",
			"Meat Breakfast",
		)

		val authenticatedFeed = homeFeed(localHour = BREAKFAST_HOUR, accessToken = "session-token")
		authenticatedFeed.shelves.forEach { shelf ->
			shelf.recipes.all { it.title != "Meat Breakfast" } shouldBe true
			shelf.filters.dietaryPreferences shouldBe setOf(DietaryPreference.VEGAN)
		}
		authenticatedFeed.shelf(HomeShelfId.NEW).recipes.map { it.title } shouldContainExactly listOf(
			"Vegan Breakfast 4",
			"Vegan Breakfast 3",
			"Vegan Breakfast 2",
			"Vegan Breakfast 1",
		)
		authenticatedFeed.shelf(HomeShelfId.FAVORITES).recipes.map { it.title } shouldContainExactly listOf(
			"Vegan Breakfast 2",
			"Vegan Breakfast 1",
		)
		authenticatedFeed.shelf(HomeShelfId.RECENTLY_VIEWED).recipes.map { it.title } shouldContainExactly listOf(
			"Vegan Breakfast 1",
		)
		authenticatedFeed.shelf(HomeShelfId.COLLECTION).recipes.map { it.title } shouldContainExactly listOf(
			"Vegan Breakfast 1",
		)
	}

	@Test
	fun `home shelf and collection pagination endpoints filter according to user dietary preferences`() =
		testApplication {
			val db = createRecipeSearchRouteTestDb()
			seedAppUsersForSearchRouteTest(db)
			insertSearchFilters(
				db = db,
				userId = 1L,
				filters = SearchFilters(dietaryPreferences = setOf(DietaryPreference.VEGAN)),
			)
			insertHomeRecipe(
				db,
				HomeRecipeSeed(
					id = 1,
					title = "Vegan Breakfast 1",
					createdAt = "2024-01-01 00:00:00",
					imageUrl = PHOTO,
					step = "Cook",
					cuisine = "Italian",
					mealType = "BREAKFAST",
					totalTime = 15,
					dietaryPreferences = listOf("VEGAN"),
				),
			)
			insertHomeRecipe(
				db,
				HomeRecipeSeed(
					id = 2,
					title = "Meat Breakfast",
					createdAt = "2024-01-02 00:00:00",
					imageUrl = PHOTO,
					step = "Cook",
					cuisine = "Italian",
					mealType = "BREAKFAST",
					totalTime = 15,
					dietaryPreferences = emptyList(),
				),
			)
			insertHomeRecipe(
				db,
				HomeRecipeSeed(
					id = 3,
					title = "Vegan Breakfast 2",
					createdAt = "2024-01-03 00:00:00",
					imageUrl = PHOTO,
					step = "Cook",
					cuisine = "Italian",
					mealType = "BREAKFAST",
					totalTime = 20,
					dietaryPreferences = listOf("VEGAN"),
				),
			)
			insertFavorite(db, recipeId = 1, createdAt = "2024-01-01 00:00:00")
			insertFavorite(db, recipeId = 2, createdAt = "2024-01-02 00:00:00")
			insertFavorite(db, recipeId = 3, createdAt = "2024-01-03 00:00:00")
			insertRecipeActivity(db, recipeId = 1, event = "OPEN", createdAt = "2024-01-01 00:00:00")
			insertRecipeActivity(db, recipeId = 2, event = "OPEN", createdAt = "2024-01-02 00:00:00")
			insertHomeCollection(
				db,
				HomeCollectionSeed(
					id = 1,
					slug = "editor-picks",
					title = "Editor Picks",
					source = HomeCollectionSource.EDITORIAL.name,
					position = 1,
					recipePositions = listOf(1 to 1, 2 to 2),
				),
			)
			application { module(db = db, sessionService = signedInSessionService()) }

			val unauthenticatedFavorites = shelfPage(HomeShelfId.FAVORITES, pageNumber = 1, accessToken = null)
			unauthenticatedFavorites.totalMatches shouldBe 0
			unauthenticatedFavorites.items shouldBe emptyList()

			val authenticatedFavorites = shelfPage(HomeShelfId.FAVORITES, pageNumber = 1, accessToken = "session-token")
			authenticatedFavorites.totalMatches shouldBe 2
			authenticatedFavorites.items.map { it.title } shouldContainExactly listOf(
				"Vegan Breakfast 2",
				"Vegan Breakfast 1",
			)

			val authenticatedRecent =
				shelfPage(HomeShelfId.RECENTLY_VIEWED, pageNumber = 1, accessToken = "session-token")
			authenticatedRecent.totalMatches shouldBe 1
			authenticatedRecent.items.map { it.title } shouldContainExactly listOf(
				"Vegan Breakfast 1",
			)

			val authenticatedNew = shelfPage(HomeShelfId.NEW, pageNumber = 1, accessToken = "session-token")
			authenticatedNew.totalMatches shouldBe 2
			authenticatedNew.items.map { it.title } shouldContainExactly listOf(
				"Vegan Breakfast 2",
				"Vegan Breakfast 1",
			)

			val authenticatedCollection = collectionPage(
				slug = "editor-picks",
				pageNumber = 1,
				pageSize = 10,
				accessToken = "session-token",
			)
			authenticatedCollection.totalMatches shouldBe 1
			authenticatedCollection.items.map { it.title } shouldContainExactly listOf("Vegan Breakfast 1")
		}
}

private fun HomeFeed.shelf(id: HomeShelfId) = shelves.first { shelf -> shelf.id == id }

private fun insertHomeRecipe(db: Db, seed: HomeRecipeSeed) {
	db.dataSource.connection.use { connection ->
		connection.prepareStatement(
			"""
				INSERT INTO recipes (
					id, title, description, image_url, cuisine, meal_type, total_time, is_private, created_at,
					created_by_user_id, dietary_preferences
				) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
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
			if (seed.createdByUserId == null) {
				statement.setNull(10, Types.BIGINT)
			} else {
				statement.setLong(10, seed.createdByUserId)
			}
			statement.setArray(11, connection.createArrayOf("text", seed.dietaryPreferences.toTypedArray()))
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

private fun insertSearchFilters(db: Db, userId: Long, filters: SearchFilters) {
	val json = Json.encodeToString(SearchFilters.serializer(), filters)
	db.dataSource.connection.use { connection ->
		connection.prepareStatement(
			"""
				INSERT INTO search_filters (user_id, filters_json, updated_at)
				VALUES (?, ?, CURRENT_TIMESTAMP)
			""".trimIndent(),
		).use { statement ->
			statement.setLong(1, userId)
			statement.setString(2, json)
			statement.executeUpdate()
		}
	}
}

private fun signedInSessionService() = FakeSessionService(
	initialSessions = listOf(FakeSessionService.createSession()),
	createMode = FakeSessionService.CreateMode.RETURN_FIRST_OR_GENERATE,
)

private fun insertItalianFavorites(db: Db, count: Int) {
	repeat(count) { index ->
		val id = index + 1
		insertHomeRecipe(
			db,
			HomeRecipeSeed(
				id = id,
				title = "Italian $id",
				createdAt = "2021-01-0${index + 1} 00:00:00",
				cuisine = "Italian",
			),
		)
		insertFavorite(db, recipeId = id, createdAt = "2024-01-0${index + 1} 00:00:00")
	}
}

private fun insertCuisineFavorites(
	db: Db,
	cuisine: String,
	titlePrefix: String,
	firstId: Int,
	latestFavoriteAt: String,
) {
	repeat(HABIT_SHELF_MINIMUM_FAVORITES) { index ->
		val id = firstId + index
		insertHomeRecipe(
			db,
			HomeRecipeSeed(
				id = id,
				title = "$titlePrefix $id",
				createdAt = "2020-01-0${index + 1} 00:00:00",
				cuisine = cuisine,
			),
		)
		val favoriteAt = if (index == HABIT_SHELF_MINIMUM_FAVORITES - 1) {
			latestFavoriteAt
		} else {
			"2020-02-0${index + 1} 00:00:00"
		}
		insertFavorite(db, recipeId = id, createdAt = favoriteAt)
	}
}

private fun insertHomeCollection(db: Db, seed: HomeCollectionSeed) {
	db.dataSource.connection.use { connection ->
		connection.prepareStatement(
			"""
				INSERT INTO home_collections (
					id, slug, title, source, filter_payload, sort, position, enabled, starts_at, ends_at
				) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
			""".trimIndent(),
		).use { statement ->
			statement.setInt(1, seed.id)
			statement.setString(2, seed.slug)
			statement.setString(3, seed.title)
			statement.setString(4, seed.source)
			statement.setString(5, seed.filterPayload)
			statement.setString(6, seed.sort)
			statement.setInt(7, seed.position)
			statement.setBoolean(8, seed.enabled)
			if (seed.startsAt == null) {
				statement.setNull(9, Types.TIMESTAMP)
			} else {
				statement.setTimestamp(9, Timestamp.valueOf(seed.startsAt))
			}
			if (seed.endsAt == null) {
				statement.setNull(10, Types.TIMESTAMP)
			} else {
				statement.setTimestamp(10, Timestamp.valueOf(seed.endsAt))
			}
			statement.executeUpdate()
		}
		seed.recipePositions.forEach { (recipeId, position) ->
			connection.prepareStatement(
				"""
					INSERT INTO home_collection_recipes (collection_id, recipe_id, position)
					VALUES (?, ?, ?)
				""".trimIndent(),
			).use { statement ->
				statement.setInt(1, seed.id)
				statement.setInt(2, recipeId)
				statement.setInt(3, position)
				statement.executeUpdate()
			}
		}
	}
}

private fun insertRecipeActivity(
	db: Db,
	recipeId: Int,
	event: String,
	createdAt: String,
) {
	db.dataSource.connection.use { connection ->
		connection.prepareStatement(
			"""
				INSERT INTO recipe_activity (user_id, recipe_id, event, created_at)
				VALUES (1, ?, ?, ?)
			""".trimIndent(),
		).use { statement ->
			statement.setInt(1, recipeId)
			statement.setString(2, event)
			statement.setTimestamp(3, Timestamp.valueOf(createdAt))
			statement.executeUpdate()
		}
	}
}

private fun insertActivityCuisine(
	db: Db,
	cuisine: String,
	titlePrefix: String,
	firstId: Int,
	latestEventAt: String,
) {
	repeat(ACTIVITY_HABIT_MINIMUM_EVENTS) { index ->
		val id = firstId + index
		insertHomeRecipe(
			db,
			HomeRecipeSeed(
				id = id,
				title = "$titlePrefix $id",
				createdAt = "2021-01-0${index + 1} 00:00:00",
				cuisine = cuisine,
			),
		)
		val createdAt = if (index == ACTIVITY_HABIT_MINIMUM_EVENTS - 1) {
			latestEventAt
		} else {
			"2024-01-0${index + 1} 00:00:00"
		}
		insertRecipeActivity(db, recipeId = id, event = "OPEN", createdAt = createdAt)
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

private data class HomeCollectionSeed(
	val id: Int,
	val slug: String,
	val title: String,
	val source: String,
	val position: Int,
	val recipePositions: List<Pair<Int, Int>> = emptyList(),
	val filterPayload: String? = null,
	val sort: String = "catalog",
	val enabled: Boolean = true,
	val startsAt: String? = null,
	val endsAt: String? = null,
)

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
	val createdByUserId: Long? = null,
	val dietaryPreferences: List<String> = emptyList(),
)

private const val PHOTO = "https://example.com/photo.jpg"
private const val BREAKFAST_HOUR = 8
private const val LUNCH_HOUR = 12
private const val DINNER_HOUR = 19
private const val QUICK_SHELF_BELOW_MINIMUM = 3
private const val QUICK_SHELF_MINIMUM = 4
private const val HABIT_SHELF_MINIMUM_FAVORITES = 3
private const val ACTIVITY_HABIT_MINIMUM_EVENTS = 3
private const val VOTER_ID_BASE = 100L
private const val VOTE_ID_STRIDE = 100
