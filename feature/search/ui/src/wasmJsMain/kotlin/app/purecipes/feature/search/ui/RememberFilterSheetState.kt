package app.purecipes.feature.search.ui

import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable

@Composable
internal actual fun rememberFilterSheetState(): SheetState {
	return rememberModalBottomSheetState(skipPartiallyExpanded = true)
}
