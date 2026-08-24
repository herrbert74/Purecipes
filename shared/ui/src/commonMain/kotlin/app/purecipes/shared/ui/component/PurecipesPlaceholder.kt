package app.purecipes.shared.ui.component

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import app.purecipes.shared.ui.theme.PurecipesTheme
import com.eygraber.compose.placeholder.PlaceholderHighlight
import com.eygraber.compose.placeholder.material3.placeholder
import com.eygraber.compose.placeholder.material3.shimmer

@Composable
fun Modifier.purecipesPlaceholder(
	visible: Boolean = true,
	shape: Shape = RoundedCornerShape(PurecipesTheme.space.s),
): Modifier = placeholder(
	visible = visible,
	shape = shape,
	highlight = PlaceholderHighlight.shimmer(),
)
