package com.purecipes.shared.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable

@Composable
actual fun BackNavigationButton(onBack: () -> Unit) {
	IconButton(onClick = onBack) {
		Icon(
			imageVector = Icons.AutoMirrored.Filled.ArrowBack,
			contentDescription = "Navigate up",
		)
	}
}