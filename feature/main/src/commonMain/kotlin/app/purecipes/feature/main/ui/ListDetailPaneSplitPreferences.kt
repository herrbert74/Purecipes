package app.purecipes.feature.main.ui

import com.russhwolf.settings.Settings

internal class ListDetailPaneSplitPreferences(
	private val settings: Settings = Settings(),
	private val preferencesKey: String = DEFAULT_PREFERENCES_KEY,
) {

	fun preferredDetailAnchorIndex(anchorCount: Int): Int {
		val stored = settings.getInt(preferencesKey, defaultValue = DEFAULT_DETAIL_ANCHOR_INDEX)
		return stored.coerceIn(0, (anchorCount - 1).coerceAtLeast(0))
	}

	fun setPreferredDetailAnchorIndex(index: Int, anchorCount: Int) {
		val clamped = index.coerceIn(0, (anchorCount - 1).coerceAtLeast(0))
		settings.putInt(preferencesKey, clamped)
	}

	private companion object {

		const val DEFAULT_PREFERENCES_KEY = "purecipes.layout.detail_split_anchor_index"
	}
}
