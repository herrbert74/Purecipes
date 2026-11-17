package app.purecipes.shared.ui.component

import androidx.compose.runtime.Composable

@Composable
internal actual fun ProvideDynamicTypeFontScale(content: @Composable () -> Unit) {
	content()
}
