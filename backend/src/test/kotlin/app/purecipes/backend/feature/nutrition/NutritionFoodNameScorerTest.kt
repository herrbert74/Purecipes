package app.purecipes.backend.feature.nutrition

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class NutritionFoodNameScorerTest {

	@Test
	fun scoreRejectsCharacterPrefixFalsePositives() {
		NutritionFoodNameScorer.score(
			queryNormalized = "butter",
			candidateNormalized = "butterbur",
		).shouldBeNull()
		NutritionFoodNameScorer.score(
			queryNormalized = "vegetable oil",
			candidateNormalized = "vegetable oil butter spread",
		).shouldBeNull()
	}

	@Test
	fun scoreAcceptsWholeTokenFoodsAndPrefersFewerExtras() {
		val oliveOil = NutritionFoodNameScorer.score(
			queryNormalized = "olive oil",
			candidateNormalized = "oil olive extra virgin",
		)
		oliveOil.shouldNotBeNull()

		val chickenBreast = NutritionFoodNameScorer.score(
			queryNormalized = "chicken breast",
			candidateNormalized = "chicken breast boneless skinless raw",
		)
		chickenBreast.shouldNotBeNull()
		chickenBreast.score shouldBe 35
		oliveOil.score shouldBe 30
	}

	@Test
	fun scoreMatchesSimplePlurals() {
		NutritionFoodNameScorer.score(
			queryNormalized = "onions",
			candidateNormalized = "onion yellow raw",
		).shouldNotBeNull()
	}
}
