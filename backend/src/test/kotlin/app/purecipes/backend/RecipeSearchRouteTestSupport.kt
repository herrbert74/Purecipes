package app.purecipes.backend

import app.purecipes.backend.db.Db
import app.purecipes.shared.domain.model.IngredientRequirement
import app.purecipes.shared.domain.model.RecipeIngredient
import io.kotest.matchers.shouldBe
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.ApplicationTestBuilder

internal fun recipeIngredientsForRouteTest(vararg texts: String): List<RecipeIngredient> =
	texts.map { text -> RecipeIngredient(text = text) }

internal fun alternativeRecipeIngredientsForRouteTest(vararg texts: String): List<RecipeIngredient> =
	texts.map { text ->
		RecipeIngredient(
			text = text,
			requirement = IngredientRequirement.ALTERNATIVE,
			alternativeGroupKey = 1,
		)
	}

internal fun optionalRecipeIngredientForRouteTest(text: String): RecipeIngredient =
	RecipeIngredient(text = text, requirement = IngredientRequirement.OPTIONAL)

internal suspend fun ApplicationTestBuilder.createRecipeForSearchRouteTest(
	accessToken: String,
	title: String,
	ingredients: List<RecipeIngredient>,
) {
	val ingredientsJson = ingredients.joinToString(separator = ",") { ingredient ->
		buildString {
			append("""{"text":"${ingredient.text}","requirement":"${ingredient.requirement.name}"""")
			ingredient.alternativeGroupKey?.let { groupKey ->
				append(""","alternativeGroupKey":$groupKey""")
			}
			append("}")
		}
	}
	val response = client.post("/recipes") {
		header(HttpHeaders.Authorization, "Bearer $accessToken")
		contentType(ContentType.Application.Json)
		setBody(
			"""
				{
					"title": "$title",
					"description": "Recipe for $title",
					"ingredientGroups": [
						{
							"ingredients": [$ingredientsJson]
						}
					],
					"steps": ["Step 1"]
				}
			""".trimIndent(),
		)
	}
	response.status shouldBe HttpStatusCode.Created
}

internal suspend fun ApplicationTestBuilder.seedRecipeCatalogForSearchRouteTest(accessToken: String) {
	createRecipeForSearchRouteTest(
		accessToken = accessToken,
		title = "Chicken Tomato Stew",
		ingredients = recipeIngredientsForRouteTest("Chicken breast", "Tomato", "Salt"),
	)
	createRecipeForSearchRouteTest(
		accessToken = accessToken,
		title = "Chicken Rice Bowl",
		ingredients = recipeIngredientsForRouteTest("Chicken breast", "Rice", "Salt"),
	)
	createRecipeForSearchRouteTest(
		accessToken = accessToken,
		title = "Tomato Basil Soup",
		ingredients = recipeIngredientsForRouteTest("Tomato", "Basil", "Garlic"),
	)
	createRecipeForSearchRouteTest(
		accessToken = accessToken,
		title = "Garlic Rice",
		ingredients = recipeIngredientsForRouteTest("Garlic", "Rice", "Butter"),
	)
	createRecipeForSearchRouteTest(
		accessToken = accessToken,
		title = "Veggie Omelette",
		ingredients = recipeIngredientsForRouteTest("Eggs", "Tomato", "Onion"),
	)
}

internal suspend fun ApplicationTestBuilder.searchWithFiltersForSearchRouteTest(
	requestBody: String,
	accessToken: String? = null,
): String {
	val response = client.post("/recipes/search") {
		if (accessToken != null) {
			header(HttpHeaders.Authorization, "Bearer $accessToken")
		}
		contentType(ContentType.Application.Json)
		setBody(requestBody)
	}
	response.status shouldBe HttpStatusCode.OK
	return response.bodyAsText()
}

internal fun mainSearchItemTitles(responseBody: String): List<String> =
	searchResultSectionTitles(responseBody, sectionName = "items")

internal fun nearMissSearchItemTitles(responseBody: String): List<String> =
	searchResultSectionTitles(responseBody, sectionName = "nearMissRecipes")

private fun searchResultSectionTitles(responseBody: String, sectionName: String): List<String> {
	val sectionStart = responseBody.indexOf("\"$sectionName\"")
	val arrayStart = if (sectionStart >= 0) responseBody.indexOf('[', sectionStart) else -1
	val arrayEnd = if (arrayStart >= 0) responseBody.indexOf(']', arrayStart) else -1
	val section = if (arrayStart >= 0 && arrayEnd >= 0) {
		responseBody.substring(arrayStart, arrayEnd + 1)
	} else {
		""
	}
	return TITLE_FIELD_REGEX.findAll(section)
		.map { match -> match.groupValues[1] }
		.toList()
}

private val TITLE_FIELD_REGEX = Regex(""""title"\s*:\s*"([^"]+)"""")

internal suspend fun ApplicationTestBuilder.updatePantryForSearchRouteTest(
	accessToken: String,
	add: List<String>,
) {
	val addJson = add.joinToString(separator = ",") { "\"$it\"" }
	val response = client.patch("/settings/pantry") {
		header(HttpHeaders.Authorization, "Bearer $accessToken")
		contentType(ContentType.Application.Json)
		setBody(
			"""
				{
					"add": [$addJson],
					"remove": []
				}
			""".trimIndent(),
		)
	}
	response.status shouldBe HttpStatusCode.OK
}

internal suspend fun ApplicationTestBuilder.updateExcludedIngredientsForSearchRouteTest(
	accessToken: String,
	add: List<String>,
) {
	val addJson = add.joinToString(separator = ",") { "\"$it\"" }
	val response = client.patch("/settings/excluded-ingredients") {
		header(HttpHeaders.Authorization, "Bearer $accessToken")
		contentType(ContentType.Application.Json)
		setBody(
			"""
				{
					"add": [$addJson],
					"remove": []
				}
			""".trimIndent(),
		)
	}
	response.status shouldBe HttpStatusCode.OK
}

internal fun createRecipeSearchRouteTestDb(): Db = createInMemoryDb("recipe_search")

internal fun seedAppUsersForSearchRouteTest(db: Db) {
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
						profile_image_url
					) VALUES (
						1,
						'GOOGLE',
						'user-one',
						'user-one@example.com',
						'User One',
						'User',
						'One',
						NULL
					)
				""".trimIndent(),
			)
		}
	}
}
