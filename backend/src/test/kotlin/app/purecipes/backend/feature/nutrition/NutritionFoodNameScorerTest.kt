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

	@Test
	fun scoreRejectsIncidentalMentionsThatAreNotTheFood() {
		NutritionFoodNameScorer.score(
			queryNormalized = "water",
			candidateNormalized = "cream of wheat regular or quick made with water no added fat",
		).shouldBeNull()
		NutritionFoodNameScorer.score(
			queryNormalized = "butter",
			candidateNormalized = "potato roasted from fresh peel not eaten made with butter",
		).shouldBeNull()
		NutritionFoodNameScorer.score(
			queryNormalized = "lemon zest",
			candidateNormalized = "lemon zest sparkling natural mineral water lemon zest",
		).shouldBeNull()
	}

	@Test
	fun scoreKeepsGenericFoodsWhoseNameStartsWithTheQuery() {
		NutritionFoodNameScorer.score(
			queryNormalized = "water",
			candidateNormalized = "water tap",
		).shouldNotBeNull()
		NutritionFoodNameScorer.score(
			queryNormalized = "coriander",
			candidateNormalized = "spices coriander seed",
		).shouldNotBeNull()
	}

	@Test
	fun scoreRejectsInsufficientSoloQueryTokens() {
		NutritionFoodNameScorer.score(
			queryNormalized = "side",
			candidateNormalized = "bacon or side pork fresh cooked",
		).shouldBeNull()
		NutritionFoodNameScorer.score(
			queryNormalized = "box",
			candidateNormalized = "box grater",
		).shouldBeNull()
	}
}
