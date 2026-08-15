package app.purecipes.feature.library.data.datasource

import com.russhwolf.settings.Settings
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.random.Random

private val COVER_JSON = Json { ignoreUnknownKeys = true }

private const val SETTINGS_KEY = "cookbook_cover_state_v1"

private const val MIN_ROTATION_MILLIS = 7L * 24L * 60L * 60L * 1000L

private const val MAX_ROTATION_MILLIS = 14L * 24L * 60L * 60L * 1000L

@Serializable
private data class CookbookCoverEntry(
	val imageUrl: String,
	val nextRotationEpochMillis: Long,
)

@Inject
@ContributesBinding(AppScope::class)
class SettingsCookbookCoverLocalDataSource(
	private val settings: Settings = Settings(),
) : CookbookCoverDataSource.Local {

	override fun getCookbookCoverImageUrl(
		cookbookId: Int,
		candidateImageUrls: List<String>,
		nowMillis: Long,
		random: Random,
	): String? {
		val pool = candidateImageUrls.map { it.trim() }.filter { it.isNotEmpty() }.distinct()
		val map = readMap()
		val existing = map[cookbookId]
		if (pool.isEmpty()) {
			return existing?.imageUrl
		}
		val needsRotation = existing == null || nowMillis >= existing.nextRotationEpochMillis
		return if (needsRotation) {
			val picked = pool[random.nextInt(pool.size)]
			val span = random.nextLong(MIN_ROTATION_MILLIS, MAX_ROTATION_MILLIS + 1)
			map[cookbookId] = CookbookCoverEntry(
				imageUrl = picked,
				nextRotationEpochMillis = nowMillis + span,
			)
			writeMap(map)
			picked
		} else {
			existing.imageUrl
		}
	}

	private fun readMap(): MutableMap<Int, CookbookCoverEntry> {
		val raw = settings.getStringOrNull(SETTINGS_KEY) ?: return mutableMapOf()
		return runCatching {
			COVER_JSON.decodeFromString<Map<String, CookbookCoverEntry>>(raw)
				.mapKeys { it.key.toInt() }
				.toMutableMap()
		}.getOrDefault(mutableMapOf())
	}

	private fun writeMap(map: Map<Int, CookbookCoverEntry>) {
		val asStringKeys = map.mapKeys { it.key.toString() }
		settings.putString(SETTINGS_KEY, COVER_JSON.encodeToString(asStringKeys))
	}
}
