package app.purecipes.feature.search.data.datasource

import app.purecipes.feature.search.domain.model.SearchPreferences
import com.russhwolf.settings.Settings
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Inject
@ContributesBinding(AppScope::class)
class SearchPreferencesLocalDataSource(
	private val settings: Settings = Settings(),
	private val json: Json = Json {
		ignoreUnknownKeys = true
		explicitNulls = false
	},
	private val preferencesKey: String = DEFAULT_PREFERENCES_KEY,
) : SearchPreferencesDataSource.Local {

	private val preferencesFlow = sharedPreferencesFlow(
		preferencesKey = preferencesKey,
		preferences = loadPreferences(),
	)

	override fun observeSearchPreferences(): Flow<SearchPreferences> = preferencesFlow

	override fun getSearchPreferences(): SearchPreferences = preferencesFlow.value

	override fun saveSearchPreferences(preferences: SearchPreferences) {
		persist(preferences)
	}

	private fun loadPreferences(): SearchPreferences {
		return settings.getStringOrNull(preferencesKey)
			?.let { stored ->
				runCatching { json.decodeFromString<StoredSearchPreferences>(stored) }.getOrNull()
					?.toDomain()
			}
			?: SearchPreferences()
	}

	private fun persist(preferences: SearchPreferences) {
		settings.putString(preferencesKey, json.encodeToString(preferences.toStored()))
		preferencesFlow.value = preferences
	}

	private companion object {

		const val DEFAULT_PREFERENCES_KEY = "purecipes.search.preferences"

		val sharedPreferencesFlows = mutableMapOf<String, MutableStateFlow<SearchPreferences>>()

		fun sharedPreferencesFlow(
			preferencesKey: String,
			preferences: SearchPreferences,
		): MutableStateFlow<SearchPreferences> {
			return sharedPreferencesFlows.getOrPut(preferencesKey) {
				MutableStateFlow(preferences)
			}
		}
	}
}

@Serializable
private data class StoredSearchPreferences(
	val applyRecipeFiltersToTitleSearch: Boolean = true,
)

private fun StoredSearchPreferences.toDomain(): SearchPreferences = SearchPreferences(
	applyRecipeFiltersToTitleSearch = applyRecipeFiltersToTitleSearch,
)

private fun SearchPreferences.toStored(): StoredSearchPreferences = StoredSearchPreferences(
	applyRecipeFiltersToTitleSearch = applyRecipeFiltersToTitleSearch,
)
