package app.purecipes.backend

import app.purecipes.backend.fake.FakeSessionService
import app.purecipes.backend.feature.ingredient.IngredientMatchRepository
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test

class IngredientMatchRouteTest {

	@Test
	fun `missing name yields 400`() = testApplication {
		application { module(db = createInMemoryDb("ingredient_match_route")) }

		val response = client.get("/ingredients/match")
		response.status shouldBe HttpStatusCode.BadRequest
		response.bodyAsText() shouldBe """{"message":"Invalid request","detail":"Missing query parameter: name"}"""
	}

	@Test
	fun `unauthenticated request yields 401`() = testApplication {
		application { module(db = createInMemoryDb("ingredient_match_route")) }

		val response = client.get("/ingredients/match?name=tarragon")
		response.status shouldBe HttpStatusCode.Unauthorized
	}

	@Test
	fun `exact ingredient match returns recipe count`() = testApplication {
		val db = createInMemoryDb("ingredient_match_route")
		insertRecipeIngredientsForIngredientMatchTest(
			dataSource = db.dataSource,
			ingredients = listOf("2 tbsp fresh tarragon", "Chicken"),
		)
		insertRecipeIngredientsForIngredientMatchTest(
			dataSource = db.dataSource,
			ingredients = listOf("1 tsp dried tarragon"),
		)
		insertRecipeIngredientsForIngredientMatchTest(
			dataSource = db.dataSource,
			ingredients = listOf("Tomato", "Basil"),
		)
		val sessionService = FakeSessionService(
			initialSessions = listOf(FakeSessionService.createSession()),
		)

		application {
			module(db = db, sessionService = sessionService)
		}

		val response = client.get("/ingredients/match?name=tarragon") {
			header(HttpHeaders.Authorization, "Bearer ${sessionService.session.accessToken}")
		}

		response.status shouldBe HttpStatusCode.OK
		response.bodyAsText() shouldBe
			"""{"query":"tarragon","exactMatches":[{"ingredient":"Tarragon","recipeCount":2}]}"""
	}

	@Test
	fun `likely ingredient match returns typo suggestion`() = testApplication {
		val db = createInMemoryDb("ingredient_match_route")
		insertRecipeIngredientsForIngredientMatchTest(
			dataSource = db.dataSource,
			ingredients = listOf("2 tbsp fresh tarragon"),
		)
		val sessionService = FakeSessionService(
			initialSessions = listOf(FakeSessionService.createSession()),
		)

		application {
			module(db = db, sessionService = sessionService)
		}

		val response = client.get("/ingredients/match?name=tarragone") {
			header(HttpHeaders.Authorization, "Bearer ${sessionService.session.accessToken}")
		}

		response.status shouldBe HttpStatusCode.OK
		response.bodyAsText().contains(""""ingredient":"Tarragon"""")
		response.bodyAsText().contains(""""recipeCount":1""")
		response.bodyAsText().contains(""""likelyMatches":[{""")
		response.bodyAsText().contains(""""exactMatches":[]""")
	}
}

class IngredientMatchRepositoryTest {

	@Test
	fun `repository classifies exact and likely matches with deduped recipe counts`() {
		val db = createInMemoryDb("ingredient_match_repository")
		insertRecipeIngredientsForIngredientMatchTest(
			dataSource = db.dataSource,
			ingredients = listOf("Taragon"),
		)
		insertRecipeIngredientsForIngredientMatchTest(
			dataSource = db.dataSource,
			ingredients = listOf("2 tbsp tarragon"),
		)
		val repository = IngredientMatchRepository(db.dataSource)

		val response = repository.matchIngredient("taragon")

		response.exactMatches.map { it.ingredient } shouldContainExactly listOf("Taragon")
		response.exactMatches.single().recipeCount shouldBe 1
		response.likelyMatches.map { it.ingredient } shouldContainExactly listOf("Tarragon")
		response.likelyMatches.single().recipeCount shouldBe 1
	}

	@Test
	fun `repository matches non catalogue ingredient tokens`() {
		val db = createInMemoryDb("ingredient_match_repository_tokens")
		insertRecipeIngredientsForIngredientMatchTest(
			dataSource = db.dataSource,
			ingredients = listOf("1 cup amaranth flour"),
		)
		insertRecipeIngredientsForIngredientMatchTest(
			dataSource = db.dataSource,
			ingredients = listOf("2 habanero peppers, seeded"),
		)
		insertRecipeIngredientsForIngredientMatchTest(
			dataSource = db.dataSource,
			ingredients = listOf("3 tbsp bourbon"),
		)
		val repository = IngredientMatchRepository(db.dataSource)

		repository.matchIngredient("amaranth").exactMatches.single().let { match ->
			match.ingredient shouldBe "Amaranth"
			match.recipeCount shouldBe 1
		}
		repository.matchIngredient("habanero").exactMatches.single().let { match ->
			match.ingredient shouldBe "Habanero"
			match.recipeCount shouldBe 1
		}
		repository.matchIngredient("bourbon").exactMatches.single().let { match ->
			match.ingredient shouldBe "Bourbon"
			match.recipeCount shouldBe 1
		}
	}

	@Test
	fun `repository returns empty matches for blank query`() {
		val db = createInMemoryDb("ingredient_match_repository")
		val repository = IngredientMatchRepository(db.dataSource)

		val response = repository.matchIngredient("   ")

		response.query shouldBe ""
		response.exactMatches.shouldBeEmpty()
		response.likelyMatches.shouldBeEmpty()
	}
}
