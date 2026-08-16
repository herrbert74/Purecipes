package app.purecipes.feature.settings.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.purecipes.feature.search.domain.model.SearchPreferences
import app.purecipes.shared.ui.theme.PurecipesTheme
import dejavu.runRecompositionTrackingUiTest
import dejavu.setTrackedContent
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class SearchPreferencesPanelTest {

	@Test
	fun searchPreferencesPanelTogglesRecipeFiltersSetting() = runRecompositionTrackingUiTest {
		var preferences = SearchPreferences()
		setTrackedContent {
			PurecipesTheme {
				SearchPreferencesPanel(
					preferences = preferences,
					onPreferencesChange = { updated -> preferences = updated },
				)
			}
		}

		onNodeWithText("Search").assertIsDisplayed()
		onNodeWithTag(SEARCH_PREFERENCES_RECIPE_FILTERS_SWITCH_TAG).performClick()

		assertEquals(
			SearchPreferences(applyRecipeFiltersToTitleSearch = false),
			preferences,
		)
	}
}
