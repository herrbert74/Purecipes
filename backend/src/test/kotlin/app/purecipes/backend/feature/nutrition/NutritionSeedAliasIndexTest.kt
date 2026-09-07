package app.purecipes.backend.feature.nutrition

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import kotlin.test.Test

class NutritionSeedAliasIndexTest {

	@Test
	fun mergeAppliesHandwrittenAliasesWithoutStoredRows() {
		val cornstarch = food(
			id = 3,
			displayName = "Cornstarch",
			normalizedName = "cornstarch",
		)
		val aliases = NutritionSeedAliasIndex.merge(
			foods = listOf(cornstarch),
			storedAliases = emptyMap(),
		)

		aliases["cornflour"] shouldBe 3
		aliases["cornstarch"] shouldBe 3
	}

	@Test
	fun mergeSkipsAliasesWhosePreferredFoodIsMissing() {
		val aliases = NutritionSeedAliasIndex.merge(
			foods = listOf(
				food(
					id = 1,
					displayName = "Sugars, granulated",
					normalizedName = "sugars granulated",
				),
			),
			storedAliases = emptyMap(),
		)

		aliases["cornflour"].shouldBeNull()
		aliases["caster sugar"] shouldBe 1
	}

	@Test
	fun mergePointsEggWhiteAtFoundationFood() {
		val foundationWhite = food(
			id = 11,
			displayName = "Eggs, Grade A, Large, egg white",
			normalizedName = "eggs grade a large egg white",
		)
		val legacyWhite = food(
			id = 12,
			displayName = "Egg, white, raw, fresh",
			normalizedName = "egg white raw fresh",
		)
		val aliases = NutritionSeedAliasIndex.merge(
			foods = listOf(legacyWhite, foundationWhite),
			storedAliases = emptyMap(),
		)

		aliases["egg white"] shouldBe 11
		aliases["egg whites"] shouldBe 11
	}

	@Test
	fun mergePrefersSeedAliasesOverStoredRows() {
		val unsalted = food(
			id = 8,
			displayName = "Butter, without salt",
			normalizedName = "butter without salt",
		)
		val aliases = NutritionSeedAliasIndex.merge(
			foods = listOf(unsalted),
			storedAliases = mapOf("unsalted butter" to 99),
		)

		aliases["unsalted butter"] shouldBe 8
	}

	private fun food(id: Int, displayName: String, normalizedName: String): NutritionFoodRecord =
		NutritionFoodRecord(
			id = id,
			displayName = displayName,
			normalizedName = normalizedName,
			nutrients = FdcNutrientsPer100g(
				calories = BigDecimal.ZERO,
				protein = null,
				carbohydrates = null,
				fat = null,
				fiber = null,
				sugar = null,
				sodium = null,
			),
		)
}
