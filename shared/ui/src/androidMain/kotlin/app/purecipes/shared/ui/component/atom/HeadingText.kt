package app.purecipes.shared.ui.component.atom

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow

@Composable
actual fun HeadingText(
	text: String,
	modifier: Modifier,
	maxLines: Int,
	overflow: TextOverflow,
	style: TextStyle?,
) {
	Text(
		text = text,
		modifier = modifier.semantics { heading() },
		style = style ?: LocalTextStyle.current,
		maxLines = maxLines,
		overflow = overflow,
	)
}
