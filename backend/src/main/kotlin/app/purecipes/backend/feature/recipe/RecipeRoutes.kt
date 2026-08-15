package app.purecipes.backend.feature.recipe

import app.purecipes.backend.auth.SessionService
import app.purecipes.backend.db.Db
import app.purecipes.backend.feature.ingredient.IngredientMatchCorpusCache
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.recipeRoutes(
	sessionService: SessionService,
	dbProvider: () -> Db,
	ingredientMatchCorpusCache: IngredientMatchCorpusCache,
) {
	route("/recipes") {
		get("/mine") {
			call.respondMyRecipes(sessionService, dbProvider)
		}

		recipeNutritionEstimateRoutes(sessionService, dbProvider)

		post {
			call.respondCreateRecipe(sessionService, dbProvider, ingredientMatchCorpusCache)
		}

		get("/search") {
			call.respondKeywordSearch(dbProvider)
		}

		post("/search") {
			call.respondFilteredSearch(sessionService, dbProvider)
		}

		get("/{id}/cookbooks") {
			call.respondRecipeCookbooks(sessionService, dbProvider)
		}

		get("/{id}") {
			call.respondRecipeDetails(sessionService, dbProvider)
		}

		put("/{id}") {
			call.respondUpdateRecipe(sessionService, dbProvider, ingredientMatchCorpusCache)
		}

		delete("/{id}") {
			call.respondDeleteRecipe(sessionService, dbProvider, ingredientMatchCorpusCache)
		}
	}
}
