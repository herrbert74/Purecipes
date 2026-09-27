package app.purecipes.feature.sharing.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.purecipes.shared.ui.icon.AppIcons

@Composable
fun ShareIconButton(
	onShare: () -> Unit,
	contentDescription: String,
	modifier: Modifier = Modifier,
) {
	IconButton(
		onClick = onShare,
		modifier = modifier,
	) {
		Icon(
			imageVector = AppIcons.Share,
			contentDescription = contentDescription,
		)
	}
}
