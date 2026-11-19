package app.purecipes.backend

import app.purecipes.backend.db.Db
import app.purecipes.backend.fake.FakeSessionService
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import java.sql.Connection
import java.sql.Statement
import java.sql.Timestamp
import java.sql.Types
import kotlin.test.Test

class RecipeSearchCatalogOrderRouteTest {

	@Test
	fun `default feed sorts catalog rank then photo steps ingredients and metadata`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		seedCatalogOrderRecipes(db)

		application { module(db = db) }

		val responseBody = searchWithFiltersForSearchRouteTest(
			"""
				{
					"query": "",
					"filters": {}
				}
			""".trimIndent(),
		)

		assertCatalogOrderTiers(mainSearchItemTitles(responseBody))
	}

	@Test
	fun `filtered browse uses catalog order`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		seedCatalogOrderRecipes(db, includeOtherCuisine = true)

		application { module(db = db) }

		val responseBody = searchWithFiltersForSearchRouteTest(
			"""
				{
					"query": "",
					"filters": {
						"cuisines": ["Italian"]
					}
				}
			""".trimIndent(),
		)

		assertCatalogOrderTiers(mainSearchItemTitles(responseBody))
	}

	@Test
	fun `pantry filtering keeps catalog order`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		seedAppUsersForSearchRouteTest(db)
		val sessionService = FakeSessionService(
			initialSessions = listOf(
				FakeSessionService.createSession(),
			),
			createMode = FakeSessionService.CreateMode.RETURN_FIRST_OR_GENERATE,
		)
		insertCatalogOrderRecipe(
			db,
			CatalogOrderRecipeSeed(
				id = 1,
				title = "Older Complete",
				createdAt = "2020-01-01 00:00:00",
				imageUrl = PHOTO_URL,
				description = "Ready to cook",
				mealType = "DINNER",
				totalTime = 30,
				ingredients = PANTRY_INGREDIENTS,
				step = "Cook",
			),
		)
		insertCatalogOrderRecipe(
			db,
			CatalogOrderRecipeSeed(
				id = 2,
				title = "Newer Plain",
				createdAt = "2024-01-01 00:00:00",
				ingredients = PANTRY_INGREDIENTS,
			),
		)

		application {
			module(
				db = db,
				sessionService = sessionService,
			)
		}
		updatePantryForSearchRouteTest(
			accessToken = sessionService.session.accessToken,
			add = PANTRY_INGREDIENTS,
		)

		val responseBody = searchWithFiltersForSearchRouteTest(
			"""
				{
					"query": "",
					"filters": {}
				}
			""".trimIndent(),
			accessToken = sessionService.session.accessToken,
		)

		mainSearchItemTitles(responseBody) shouldBe listOf("Older Complete", "Newer Plain")
	}

	@Test
	fun `title search uses completeness instead of catalog rank or newest`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		seedTitleSearchRecipes(db)

		application { module(db = db) }

		val responseBody = searchWithFiltersForSearchRouteTest(
			"""
				{
					"query": "Soup",
					"filters": {}
				}
			""".trimIndent(),
		)

		assertTitleSearchTiers(mainSearchItemTitles(responseBody))
	}

	@Test
	fun `title search breaks pantry ties with completeness`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		seedAppUsersForSearchRouteTest(db)
		val sessionService = FakeSessionService(
			initialSessions = listOf(
				FakeSessionService.createSession(),
			),
			createMode = FakeSessionService.CreateMode.RETURN_FIRST_OR_GENERATE,
		)
		seedTitleSearchRecipes(db)

		application {
			module(
				db = db,
				sessionService = sessionService,
			)
		}
		updatePantryForSearchRouteTest(
			accessToken = sessionService.session.accessToken,
			add = PANTRY_INGREDIENTS,
		)

		val responseBody = searchWithFiltersForSearchRouteTest(
			"""
				{
					"query": "Soup",
					"filters": {}
				}
			""".trimIndent(),
			accessToken = sessionService.session.accessToken,
		)

		assertTitleSearchTiers(mainSearchItemTitles(responseBody))
	}

	@Test
	fun `keyword search uses completeness instead of catalog rank or newest`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		seedTitleSearchRecipes(db)

		application { module(db = db) }

		val response = client.get("/recipes/search?query=Soup")

		response.status shouldBe HttpStatusCode.OK
		assertTitleSearchTiers(mainSearchItemTitles(response.bodyAsText()))
	}

	@Test
	fun `search results break ties randomly instead of newest first`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		(1..10).forEach { id ->
			insertCatalogOrderRecipe(
				db,
				CatalogOrderRecipeSeed(
					id = id,
					title = "Recipe $id",
					createdAt = "2024-01-${id.toString().padStart(2, '0')} 00:00:00",
					imageUrl = PHOTO_URL,
					description = "Ready to cook",
					mealType = "DINNER",
					totalTime = 20,
					ingredients = listOf("Salt"),
					step = "Cook",
				),
			)
		}

		application { module(db = db) }

		val newestFirstOrder = (10 downTo 1).map { "Recipe $it" }
		val observedOrders = (1..5).map {
			mainSearchItemTitles(
				searchWithFiltersForSearchRouteTest(
					"""
						{
							"query": "",
							"filters": {}
						}
					""".trimIndent(),
				),
			)
		}

		observedOrders.forEach { order ->
			order shouldContainExactlyInAnyOrder newestFirstOrder
		}
		observedOrders.distinct().size shouldBeGreaterThan 1
		observedOrders.any { order -> order != newestFirstOrder } shouldBe true
	}

	@Test
	fun `paginating search results returns all recipes without duplicates`() = testApplication {
		val db = createRecipeSearchRouteTestDb()
		(1..6).forEach { id ->
			insertCatalogOrderRecipe(
				db,
				CatalogOrderRecipeSeed(
					id = id,
					title = "Recipe $id",
					createdAt = "2024-01-0$id 00:00:00",
					imageUrl = PHOTO_URL,
				),
			)
		}

		application { module(db = db) }

		val pageOne = mainSearchItemTitles(
			searchWithFiltersForSearchRouteTest(
				"""
					{
						"query": "",
						"filters": {},
						"pageNumber": 1,
						"pageSize": 3
					}
				""".trimIndent(),
			),
		)
		val pageTwo = mainSearchItemTitles(
			searchWithFiltersForSearchRouteTest(
				"""
					{
						"query": "",
						"filters": {},
						"pageNumber": 2,
						"pageSize": 3
					}
				""".trimIndent(),
			),
		)

		pageOne.size shouldBe 3
		pageTwo.size shouldBe 3
		(pageOne + pageTwo) shouldContainExactlyInAnyOrder (1..6).map { "Recipe $it" }
	}
}

private fun assertCatalogOrderTiers(titles: List<String>) {
	titles.size shouldBe 9
	titles.take(5) shouldBe listOf(
		"Ranked First",
		"Ranked Second",
		"Full Score",
		"Almost Complete",
		"Photo And Steps",
	)
	titles.subList(5, 7) shouldContainExactlyInAnyOrder listOf("Newer Photo", "Photo Only")
	titles.drop(7) shouldBe listOf("Steps And Ingredients", "Blank Photo")
}

private fun assertTitleSearchTiers(titles: List<String>) {
	titles.size shouldBe 3
	titles.first() shouldBe "Zucchini Soup"
	titles.drop(1) shouldContainExactlyInAnyOrder listOf("Apple Soup", "Mango Soup")
}

private fun seedCatalogOrderRecipes(db: Db, includeOtherCuisine: Boolean = false) {
	catalogOrderSeeds().forEach { seed ->
		insertCatalogOrderRecipe(db, seed)
	}
	if (includeOtherCuisine) {
		insertCatalogOrderRecipe(
			db,
			CatalogOrderRecipeSeed(
				id = 99,
				title = "Excluded Tacos",
				createdAt = "2024-06-01 00:00:00",
				catalogRank = 1,
				cuisine = "Mexican",
				imageUrl = PHOTO_URL,
				description = "Not Italian",
				mealType = "DINNER",
				totalTime = 20,
				ingredients = listOf("Beef"),
				step = "Cook",
			),
		)
	}
}

private fun catalogOrderSeeds(): List<CatalogOrderRecipeSeed> = listOf(
	CatalogOrderRecipeSeed(
		id = 1,
		title = "Ranked First",
		createdAt = "2020-01-01 00:00:00",
		catalogRank = 1,
	),
	CatalogOrderRecipeSeed(
		id = 2,
		title = "Ranked Second",
		createdAt = "2024-01-01 00:00:00",
		catalogRank = 2,
		imageUrl = PHOTO_URL,
		description = "Ready to cook",
		mealType = "DINNER",
		totalTime = 25,
		ingredients = listOf("Salt"),
		step = "Cook",
	),
	CatalogOrderRecipeSeed(
		id = 3,
		title = "Full Score",
		createdAt = "2021-01-01 00:00:00",
		imageUrl = PHOTO_URL,
		description = "Ready to cook",
		mealType = "DINNER",
		totalTime = 40,
		ingredients = listOf("Salt"),
		step = "Cook",
	),
	CatalogOrderRecipeSeed(
		id = 4,
		title = "Almost Complete",
		createdAt = "2022-01-01 00:00:00",
		imageUrl = PHOTO_URL,
		description = "Ready to cook",
		mealType = "DINNER",
		ingredients = listOf("Salt"),
		step = "Cook",
	),
	CatalogOrderRecipeSeed(
		id = 5,
		title = "Photo And Steps",
		createdAt = "2023-01-01 00:00:00",
		imageUrl = PHOTO_URL,
		step = "Cook",
	),
	CatalogOrderRecipeSeed(
		id = 6,
		title = "Newer Photo",
		createdAt = "2022-06-01 00:00:00",
		imageUrl = PHOTO_URL,
	),
	CatalogOrderRecipeSeed(
		id = 7,
		title = "Photo Only",
		createdAt = "2020-06-01 00:00:00",
		imageUrl = PHOTO_URL,
	),
	CatalogOrderRecipeSeed(
		id = 8,
		title = "Steps And Ingredients",
		createdAt = "2024-03-01 00:00:00",
		description = "Ready to cook",
		mealType = "DINNER",
		totalTime = 20,
		ingredients = listOf("Salt"),
		step = "Cook",
	),
	CatalogOrderRecipeSeed(
		id = 9,
		title = "Blank Photo",
		createdAt = "2024-04-01 00:00:00",
		imageUrl = "   ",
		step = "Cook",
	),
)

private fun seedTitleSearchRecipes(db: Db) {
	insertCatalogOrderRecipe(
		db,
		CatalogOrderRecipeSeed(
			id = 1,
			title = "Zucchini Soup",
			createdAt = "2020-01-01 00:00:00",
			imageUrl = PHOTO_URL,
			description = "Ready to cook",
			mealType = "DINNER",
			totalTime = 30,
			ingredients = PANTRY_INGREDIENTS,
			step = "Cook",
		),
	)
	insertCatalogOrderRecipe(
		db,
		CatalogOrderRecipeSeed(
			id = 2,
			title = "Apple Soup",
			createdAt = "2024-01-01 00:00:00",
			ingredients = PANTRY_INGREDIENTS,
		),
	)
	insertCatalogOrderRecipe(
		db,
		CatalogOrderRecipeSeed(
			id = 3,
			title = "Mango Soup",
			createdAt = "2022-01-01 00:00:00",
			catalogRank = 1,
			ingredients = PANTRY_INGREDIENTS,
		),
	)
}

private fun insertCatalogOrderRecipe(db: Db, seed: CatalogOrderRecipeSeed) {
	db.dataSource.connection.use { connection ->
		connection.prepareStatement(
			"""
				INSERT INTO recipes (
					id,
					title,
					description,
					image_url,
					cuisine,
					meal_type,
					total_time,
					catalog_rank,
					created_at
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
			if (seed.catalogRank == null) {
				statement.setNull(8, Types.INTEGER)
			} else {
				statement.setInt(8, seed.catalogRank)
			}
			statement.setTimestamp(9, Timestamp.valueOf(seed.createdAt))
			statement.executeUpdate()
		}
		seed.ingredients.forEachIndexed { index, ingredient ->
			insertCatalogOrderIngredient(connection, seed.id, ingredient, index)
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

private fun insertCatalogOrderIngredient(
	connection: Connection,
	recipeId: Int,
	ingredient: String,
	orderIndex: Int,
) {
	val groupId = connection.prepareStatement(
		"""
			INSERT INTO ingredient_groups (recipe_id, name, order_index)
			VALUES (?, NULL, ?)
		""".trimIndent(),
		Statement.RETURN_GENERATED_KEYS,
	).use { statement ->
		statement.setInt(1, recipeId)
		statement.setInt(2, orderIndex)
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
		statement.setString(2, ingredient)
		statement.executeUpdate()
	}
}

private data class CatalogOrderRecipeSeed(
	val id: Int,
	val title: String,
	val createdAt: String,
	val catalogRank: Int? = null,
	val imageUrl: String? = null,
	val description: String? = null,
	val cuisine: String? = "Italian",
	val mealType: String? = null,
	val totalTime: Int? = null,
	val ingredients: List<String> = emptyList(),
	val step: String? = null,
)

private const val PHOTO_URL = "https://example.com/photo.jpg"

private val PANTRY_INGREDIENTS = listOf("Chicken", "Tomato", "Salt")
