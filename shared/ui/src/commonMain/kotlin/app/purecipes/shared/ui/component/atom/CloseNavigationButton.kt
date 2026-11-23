package app.purecipes.shared.ui.component.atom

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.purecipes.shared.ui.icon.AppIcons
import org.jetbrains.compose.resources.stringResource
import purecipes.shared.ui.generated.resources.Res
import purecipes.shared.ui.generated.resources.close

@Composable
fun CloseNavigationButton(onClose: () -> Unit, modifier: Modifier = Modifier) {
	IconButton(onClick = onClose, modifier = modifier) {
		Icon(
			imageVector = AppIcons.Close,
			contentDescription = stringResource(Res.string.close),
		)
	}
}
