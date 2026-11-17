package app.purecipes.shared.ui.component.atom

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.clearAndSetSemantics
import app.purecipes.shared.ui.theme.LocalReducedMotion
import app.purecipes.shared.ui.theme.PurecipesTheme
import com.eygraber.compose.placeholder.PlaceholderHighlight
import com.eygraber.compose.placeholder.material3.placeholder
import com.eygraber.compose.placeholder.material3.shimmer

@Composable
fun Modifier.purecipesPlaceholder(
	visible: Boolean = true,
	shape: Shape = RoundedCornerShape(PurecipesTheme.space.s),
): Modifier = if (visible) {
	val highlight = if (LocalReducedMotion.current) null else PlaceholderHighlight.shimmer()
	clearAndSetSemantics {}
		.placeholder(
			visible = true,
			shape = shape,
			highlight = highlight,
		)
} else {
	this
}
