package app.purecipes.feature.search.data.repository

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import app.purecipes.base.kotlin.result.Failure
import app.purecipes.feature.search.data.datasource.RecipeSearchFilterDataSource
import app.purecipes.feature.search.data.datasource.RecipeSearchFilterInMemoryDataSource
import app.purecipes.feature.search.domain.repository.SearchOutcome
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.SearchFilters
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class RecipeSearchFilterAccessorTest {

	@Test
	fun `getFilters returns remote filters when remote succeeds`() = runTest {
		val remoteFilters = SearchFilters(cuisines = setOf(Cuisine.ITALIAN))
		val remote = FakeRemoteFilterDataSource(Ok(remoteFilters))
		val accessor = RecipeSearchFilterAccessor(remote, RecipeSearchFilterInMemoryDataSource())

		val result = accessor.getFilters()

		result shouldBe remoteFilters
	}

	@Test
	fun `getFilters caches remote filters into local data source`() = runTest {
		val remoteFilters = SearchFilters(cuisines = setOf(Cuisine.ITALIAN))
		val local = RecipeSearchFilterInMemoryDataSource()
		val remote = FakeRemoteFilterDataSource(Ok(remoteFilters))
		val accessor = RecipeSearchFilterAccessor(remote, local)

		accessor.getFilters()

		local.getFilters() shouldBe remoteFilters
	}

	@Test
	fun `getFilters falls back to local when remote fails`() = runTest {
		val localFilters = SearchFilters(cuisines = setOf(Cuisine.FRENCH))
		val local = RecipeSearchFilterInMemoryDataSource()
		local.saveFilters(localFilters)
		val remote = FakeRemoteFilterDataSource(Err(Failure.ServerError("Network error")))
		val accessor = RecipeSearchFilterAccessor(remote, local)

		val result = accessor.getFilters()

		result shouldBe localFilters
	}

	@Test
	fun `getFilters returns empty filters when remote and local both empty`() = runTest {
		val remote = FakeRemoteFilterDataSource(Err(Failure.ServerError("Network error")))
		val accessor = RecipeSearchFilterAccessor(remote, RecipeSearchFilterInMemoryDataSource())

		val result = accessor.getFilters()

		result shouldBe SearchFilters()
	}

	@Test
	fun `saveFilters persists to local`() = runTest {
		val local = RecipeSearchFilterInMemoryDataSource()
		val remote = FakeRemoteFilterDataSource(Ok(SearchFilters()))
		val accessor = RecipeSearchFilterAccessor(remote, local)
		val filters = SearchFilters(cuisines = setOf(Cuisine.CHINESE))

		accessor.saveFilters(filters)

		local.getFilters() shouldBe filters
	}

	@Test
	fun `saveFilters sends to remote`() = runTest {
		val remote = FakeRemoteFilterDataSource(Ok(SearchFilters()))
		val accessor = RecipeSearchFilterAccessor(remote, RecipeSearchFilterInMemoryDataSource())
		val filters = SearchFilters(cuisines = setOf(Cuisine.MEXICAN))

		accessor.saveFilters(filters)

		remote.savedFilters shouldBe filters
	}

	private class FakeRemoteFilterDataSource(
		private val getResult: SearchOutcome<SearchFilters>,
	) : RecipeSearchFilterDataSource.Remote {

		var savedFilters: SearchFilters? = null

		override suspend fun getFilters(): SearchOutcome<SearchFilters> = getResult

		override suspend fun saveFilters(filters: SearchFilters): SearchOutcome<SearchFilters> {
			savedFilters = filters
			return Ok(filters)
		}
	}
}
