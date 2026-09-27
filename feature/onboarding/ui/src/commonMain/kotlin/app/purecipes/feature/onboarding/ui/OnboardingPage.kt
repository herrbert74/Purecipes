package app.purecipes.feature.onboarding.ui

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import app.purecipes.shared.ui.icon.AppIcons
import app.purecipes.shared.ui.theme.onPrimaryContainerLight
import app.purecipes.shared.ui.theme.onSecondaryContainerLight
import app.purecipes.shared.ui.theme.primaryContainerLight
import app.purecipes.shared.ui.theme.primaryLight
import app.purecipes.shared.ui.theme.secondaryContainerLight
import app.purecipes.shared.ui.theme.surfaceLight
import app.purecipes.shared.ui.theme.tertiaryContainerLight
import app.purecipes.shared.ui.theme.tertiaryLight
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class OnboardingPage(
	val title: String,
	val description: String,
	val icon: ImageVector,
	val backgroundColor: Color,
	val contentColor: Color,
	val accentColor: Color,
)

val onboardingPages: ImmutableList<OnboardingPage> = persistentListOf(
	OnboardingPage(
		title = "Find recipes\nyou will love",
		description = "Search thousands of recipes by name, cuisine, or the ingredients you already have.",
		icon = AppIcons.Search,
		backgroundColor = primaryLight,
		contentColor = primaryContainerLight,
		accentColor = secondaryContainerLight,
	),
	OnboardingPage(
		title = "Filter down to\nyour kitchen",
		description = "Narrow results by diet, cooking time, and pantry so you only see meals you can cook now.",
		icon = AppIcons.Tune,
		backgroundColor = secondaryContainerLight,
		contentColor = onSecondaryContainerLight,
		accentColor = primaryLight,
	),
	OnboardingPage(
		title = "Cook it step\nby step",
		description = "Follow one instruction at a time, with timers and measurements in the units you prefer.",
		icon = AppIcons.Restaurant,
		backgroundColor = surfaceLight,
		contentColor = onPrimaryContainerLight,
		accentColor = tertiaryLight,
	),
	OnboardingPage(
		title = "Build your\nown library",
		description = "Save favorites, group them into cookbooks, and add recipes of your own.",
		icon = AppIcons.Bookmarks,
		backgroundColor = tertiaryLight,
		contentColor = tertiaryContainerLight,
		accentColor = primaryContainerLight,
	),
)
