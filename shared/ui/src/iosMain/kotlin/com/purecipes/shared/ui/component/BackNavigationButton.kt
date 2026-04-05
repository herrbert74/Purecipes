package com.purecipes.shared.ui.component

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun BackNavigationButton(onBack: () -> Unit, modifier: Modifier) {
	TextButton(onClick = onBack, modifier = modifier) {
		Text(text = "Back")
	}
}
