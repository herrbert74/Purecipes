package app.purecipes.feature.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.feature.search.domain.model.SearchPreferences
import app.purecipes.shared.ui.component.SectionHeader
import app.purecipes.shared.ui.theme.PurecipesTheme

internal const val SEARCH_PREFERENCES_RECIPE_FILTERS_SWITCH_TAG = "searchPreferencesRecipeFiltersSwitch"

@Composable
internal fun SearchPreferencesPanel(
	preferences: SearchPreferences,
	onPreferencesChange: (SearchPreferences) -> Unit,
	modifier: Modifier = Modifier,
) {
	Surface(
		modifier = modifier.fillMaxWidth(),
		shape = PurecipesTheme.shapes.large,
		tonalElevation = PurecipesTheme.space.quark,
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(PurecipesTheme.space.m),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween,
		) {
			SectionHeader(
				title = "Search",
				subtitle = "Apply diet, cuisine, and time filters when searching by name.",
				modifier = Modifier.weight(1f).padding(end = PurecipesTheme.space.m),
			)
			Switch(
				checked = preferences.applyRecipeFiltersToTitleSearch,
				onCheckedChange = { checked ->
					onPreferencesChange(
						preferences.copy(applyRecipeFiltersToTitleSearch = checked),
					)
				},
				modifier = Modifier.testTag(SEARCH_PREFERENCES_RECIPE_FILTERS_SWITCH_TAG),
			)
		}
	}
}

@Preview(
	name = "Search preferences light",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFFF5F5F5,
)
@Composable
private fun SearchPreferencesPanelLightPreview() {
	PurecipesTheme(darkTheme = false) {
		SearchPreferencesPanel(
			preferences = SearchPreferences(),
			onPreferencesChange = {},
			modifier = Modifier.padding(PurecipesTheme.space.m),
		)
	}
}

@Preview(
	name = "Search preferences dark",
	device = Devices.PIXEL_4,
	showBackground = true,
	backgroundColor = 0xFF121212,
)
@Composable
private fun SearchPreferencesPanelDarkPreview() {
	PurecipesTheme(darkTheme = true) {
		SearchPreferencesPanel(
			preferences = SearchPreferences(applyRecipeFiltersToTitleSearch = false),
			onPreferencesChange = {},
			modifier = Modifier.padding(PurecipesTheme.space.m),
		)
	}
}
