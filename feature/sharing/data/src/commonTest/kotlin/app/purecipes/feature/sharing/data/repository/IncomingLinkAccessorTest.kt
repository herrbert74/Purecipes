package app.purecipes.feature.sharing.data.repository

import app.purecipes.feature.sharing.data.datasource.IncomingLinkDataSource
import app.purecipes.feature.sharing.domain.model.PurecipesLink
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class IncomingLinkAccessorTest {

	@Test
	fun `deliver and observe share the same data source`() = runTest {
		val dataSource = IncomingLinkDataSource()
		val repository = IncomingLinkAccessor(dataSource)
		val collected = mutableListOf<PurecipesLink>()
		val job = launch {
			repository.observeLinks().collect { collected += it }
		}

		advanceUntilIdle()
		repository.deliver("https://purecipes.app/r/7")
		advanceUntilIdle()

		collected shouldBe listOf(PurecipesLink.Recipe(7))
		job.cancel()
	}
}
