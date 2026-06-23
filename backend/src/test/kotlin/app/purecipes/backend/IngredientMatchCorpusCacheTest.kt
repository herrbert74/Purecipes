package app.purecipes.backend

import app.purecipes.backend.feature.ingredient.IngredientMatchCorpusCache
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import kotlin.test.Test

class IngredientMatchCorpusCacheTest {

	@Test
	fun `cache reuses corpus until invalidated`() {
		val db = createInMemoryDb("ingredient_match_corpus_cache")
		insertRecipeIngredientsForIngredientMatchTest(
			dataSource = db.dataSource,
			ingredients = listOf("2 tbsp fresh tarragon"),
		)
		val cache = IngredientMatchCorpusCache(
			dataSource = db.dataSource,
			ttlMillis = Long.MAX_VALUE,
		)

		val first = cache.getCorpus()
		val second = cache.getCorpus()

		first shouldBeSameInstanceAs second
		cache.invalidate()
		val third = cache.getCorpus()

		third shouldNotBeSameInstanceAs first
		third.vocabulary shouldBe first.vocabulary
	}

	@Test
	fun `zero ttl reloads corpus on every access`() {
		val db = createInMemoryDb("ingredient_match_corpus_cache_zero_ttl")
		insertRecipeIngredientsForIngredientMatchTest(
			dataSource = db.dataSource,
			ingredients = listOf("Tomato"),
		)
		val cache = IngredientMatchCorpusCache(
			dataSource = db.dataSource,
			ttlMillis = 0L,
		)

		val first = cache.getCorpus()
		val second = cache.getCorpus()

		second shouldNotBeSameInstanceAs first
		second.vocabulary shouldBe first.vocabulary
	}
}
