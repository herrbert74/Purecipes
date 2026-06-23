package app.purecipes.backend.feature.ingredient

import java.util.concurrent.locks.ReentrantReadWriteLock
import javax.sql.DataSource
import kotlin.concurrent.read
import kotlin.concurrent.write

class IngredientMatchCorpusCache(
	private val dataSource: DataSource,
	ttlMillis: Long = defaultTtlMillis(),
) {

	private val ttlMillis = ttlMillis.coerceAtLeast(0L)
	private val lock = ReentrantReadWriteLock()
	private var cachedEntry: CachedEntry? = null

	private data class CachedEntry(
		val corpus: IngredientMatchCorpus,
		val loadedAtMillis: Long,
	)

	fun getCorpus(): IngredientMatchCorpus {
		val now = System.currentTimeMillis()
		lock.read {
			cachedEntry?.let { entry ->
				if (isFresh(entry, now)) {
					return entry.corpus
				}
			}
		}
		return refresh(now)
	}

	fun invalidate() {
		lock.write {
			cachedEntry = null
		}
	}

	private fun refresh(now: Long): IngredientMatchCorpus = lock.write {
		val existing = cachedEntry
		if (existing != null && isFresh(existing, now)) {
			return existing.corpus
		}

		val corpus = IngredientMatchCorpusLoader.load(dataSource)
		cachedEntry = CachedEntry(
			corpus = corpus,
			loadedAtMillis = now,
		)
		corpus
	}

	private fun isFresh(entry: CachedEntry, now: Long): Boolean =
		ttlMillis > 0L && now - entry.loadedAtMillis < ttlMillis

	companion object {

		private const val DEFAULT_TTL_MINUTES = 15L
		private const val MILLIS_PER_MINUTE = 60_000L
		private const val ENV_TTL_MINUTES = "PURECIPES_INGREDIENT_MATCH_CACHE_TTL_MINUTES"

		internal fun defaultTtlMillis(): Long {
			val minutes = System.getenv(ENV_TTL_MINUTES)?.toLongOrNull() ?: DEFAULT_TTL_MINUTES
			return minutes.coerceAtLeast(0L) * MILLIS_PER_MINUTE
		}
	}
}
