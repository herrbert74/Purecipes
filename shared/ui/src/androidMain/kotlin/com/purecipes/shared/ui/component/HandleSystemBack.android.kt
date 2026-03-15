package com.purecipes.shared.ui.component

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

@Composable
actual fun HandleSystemBack(enabled: Boolean, onBack: () -> Unit) {
	BackHandler(enabled = enabled, onBack = onBack)
}