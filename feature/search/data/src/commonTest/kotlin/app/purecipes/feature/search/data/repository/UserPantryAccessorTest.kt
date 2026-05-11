package app.purecipes.feature.search.data.repository

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import app.purecipes.base.kotlin.result.Failure
import app.purecipes.feature.search.data.datasource.UserPantryDataSource
import app.purecipes.feature.search.data.datasource.UserPantryInMemoryDataSource
import app.purecipes.feature.search.domain.repository.SearchOutcome
import app.purecipes.shared.domain.model.PantryDelta
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class UserPantryAccessorTest {

	@Test
	fun `getPantry returns remote pantry when remote succeeds`() = runTest {
		val remotePantry = setOf("Chicken", "Tomato")
		val remote = FakeRemotePantryDataSource(getResult = Ok(remotePantry), updateResult = Ok(remotePantry))
		val accessor = UserPantryAccessor(remote, UserPantryInMemoryDataSource())

		val result = accessor.getPantry()

		result shouldBe remotePantry
	}

	@Test
	fun `getPantry falls back to local when remote fails`() = runTest {
		val local = UserPantryInMemoryDataSource().apply { savePantry(setOf("Rice")) }
		val remote = FakeRemotePantryDataSource(
			getResult = Err(Failure.ServerError("Network error")),
			updateResult = Err(Failure.ServerError("Network error")),
		)
		val accessor = UserPantryAccessor(remote, local)

		val result = accessor.getPantry()

		result shouldBe setOf("Rice")
	}

	@Test
	fun `updatePantry falls back to local delta when remote fails`() = runTest {
		val local = UserPantryInMemoryDataSource().apply { savePantry(setOf("Chicken")) }
		val remote = FakeRemotePantryDataSource(
			getResult = Err(Failure.ServerError("Network error")),
			updateResult = Err(Failure.ServerError("Network error")),
		)
		val accessor = UserPantryAccessor(remote, local)

		val result = accessor.updatePantry(
			PantryDelta(
				add = setOf("Tomato"),
				remove = setOf("Chicken"),
			),
		)

		result shouldBe setOf("Tomato")
		local.getPantry() shouldBe setOf("Tomato")
	}

	private class FakeRemotePantryDataSource(
		private val getResult: SearchOutcome<Set<String>>,
		private val updateResult: SearchOutcome<Set<String>>,
	) : UserPantryDataSource.Remote {

		override suspend fun getPantry(): SearchOutcome<Set<String>> = getResult

		override suspend fun updatePantry(delta: PantryDelta): SearchOutcome<Set<String>> = updateResult
	}
}
