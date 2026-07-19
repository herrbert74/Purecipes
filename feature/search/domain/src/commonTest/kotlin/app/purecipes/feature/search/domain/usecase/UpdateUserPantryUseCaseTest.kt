package app.purecipes.feature.search.domain.usecase

import app.purecipes.shared.domain.model.PantryDelta
import app.purecipes.shared.testfixtures.fake.FakeUserPantryRepository
import com.github.michaelbull.result.Ok
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

		result shouldBe Ok(setOf("Tomato"))
	}
}
