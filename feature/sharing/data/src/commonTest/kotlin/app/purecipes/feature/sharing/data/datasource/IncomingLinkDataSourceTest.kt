package app.purecipes.feature.sharing.data.datasource

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class IncomingLinkDataSourceTest {

	@Test
	fun `delivers url emitted before collection starts`() = runTest {
		val dataSource = IncomingLinkDataSource()

		dataSource.deliver("https://purecipes.app/r/42")

		val collected = mutableListOf<String>()
		val job = launch {
			dataSource.incomingUrls.collect { collected += it }
		}

		advanceUntilIdle()

		collected shouldBe listOf("https://purecipes.app/r/42")
		job.cancel()
	}
}
