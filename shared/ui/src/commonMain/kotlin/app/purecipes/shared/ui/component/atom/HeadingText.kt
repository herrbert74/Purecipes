package app.purecipes.shared.ui.component.atom

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow

@Composable
expect fun HeadingText(
	text: String,
	modifier: Modifier = Modifier,
	maxLines: Int = Int.MAX_VALUE,
	overflow: TextOverflow = TextOverflow.Clip,
	style: TextStyle? = null,
)
