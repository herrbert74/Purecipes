package app.purecipes.shared.ui.component

import androidx.compose.runtime.Composable

@Composable
expect fun HandleSystemBack(enabled: Boolean, onBack: () -> Unit)
