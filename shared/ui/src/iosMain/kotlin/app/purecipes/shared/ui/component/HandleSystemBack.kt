package app.purecipes.shared.ui.component

import androidx.compose.runtime.Composable
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.rememberNavigationEventState
import androidx.navigationevent.compose.NavigationBackHandler as ComposeNavigationBackHandler

@Composable
actual fun HandleSystemBack(enabled: Boolean, onBack: () -> Unit) {
	val navigationEventState = rememberNavigationEventState(currentInfo = NavigationEventInfo.None)
	ComposeNavigationBackHandler(
		state = navigationEventState,
		isBackEnabled = enabled,
		onBackCompleted = onBack,
	)
}
