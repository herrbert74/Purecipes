package app.purecipes.feature.search.data.datasource

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class UserPantryInMemoryDataSourceTest {

	@Test
	fun `returns empty pantry by default`() {
		val dataSource = UserPantryInMemoryDataSource()

		dataSource.getPantry() shouldBe emptySet()
	}

	@Test
	fun `returns saved pantry`() {
		val dataSource = UserPantryInMemoryDataSource()
		val pantry = setOf("Chicken", "Tomato")

		dataSource.savePantry(pantry)

		dataSource.getPantry() shouldBe pantry
	}

	@Test
	fun `overwrites previously saved pantry`() {
		val dataSource = UserPantryInMemoryDataSource()
		val first = setOf("Chicken")
		val second = setOf("Chicken", "Tomato")

		dataSource.savePantry(first)
		dataSource.savePantry(second)

		dataSource.getPantry() shouldBe second
	}
}
