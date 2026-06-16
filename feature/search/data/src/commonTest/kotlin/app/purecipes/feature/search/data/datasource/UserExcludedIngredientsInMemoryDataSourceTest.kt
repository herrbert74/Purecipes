package app.purecipes.feature.search.data.datasource

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class UserExcludedIngredientsInMemoryDataSourceTest {

	@Test
	fun `returns empty excluded ingredients by default`() {
		val dataSource = UserExcludedIngredientsInMemoryDataSource()

		dataSource.getExcludedIngredients() shouldBe emptySet()
	}

	@Test
	fun `returns saved excluded ingredients`() {
		val dataSource = UserExcludedIngredientsInMemoryDataSource()
		val excludedIngredients = setOf("Garlic", "Peanut")

		dataSource.saveExcludedIngredients(excludedIngredients)

		dataSource.getExcludedIngredients() shouldBe excludedIngredients
	}

	@Test
	fun `overwrites previously saved excluded ingredients`() {
		val dataSource = UserExcludedIngredientsInMemoryDataSource()
		val first = setOf("Garlic")
		val second = setOf("Garlic", "Peanut")

		dataSource.saveExcludedIngredients(first)
		dataSource.saveExcludedIngredients(second)

		dataSource.getExcludedIngredients() shouldBe second
	}
}
