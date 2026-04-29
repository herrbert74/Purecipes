package com.purecipes.feature.search.domain.usecase

import com.purecipes.shared.domain.model.PantryDelta
import com.purecipes.shared.testfixtures.fake.FakeUserPantryRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class UpdateUserPantryUseCaseTest {

	@Test
	fun `forwards pantry delta to repository`() = runTest {
		val repository = FakeUserPantryRepository(setOf("Chicken"))
		val useCase = UpdateUserPantryUseCase(repository)

		val result = useCase(
			PantryDelta(
				add = setOf("Tomato"),
				remove = setOf("Chicken"),
			),
		)

		result shouldBe setOf("Tomato")
	}
}
