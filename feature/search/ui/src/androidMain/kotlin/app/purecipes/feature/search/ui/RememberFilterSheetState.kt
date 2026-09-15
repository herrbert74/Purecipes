package app.purecipes.feature.search.ui

import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable

@Composable
internal actual fun rememberFilterSheetState(): SheetState {
	return rememberBottomSheetState(
		initialValue = SheetValue.Hidden,
		enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded),
	)
}
