package app.purecipes.shared.ui.component

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.purecipes.shared.ui.icon.AppIcons

@Composable
actual fun BackNavigationButton(onBack: () -> Unit, modifier: Modifier) {
	IconButton(onClick = onBack, modifier = modifier) {
		Icon(
			imageVector = AppIcons.ArrowBack,
			contentDescription = "Navigate up",
		)
	}
}
