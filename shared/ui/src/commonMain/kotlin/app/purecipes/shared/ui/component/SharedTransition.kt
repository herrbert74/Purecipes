package app.purecipes.shared.ui.component

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope

val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }

@Composable
fun Modifier.recipeImageSharedElement(
	key: String?,
	cornerRadius: Dp = 0.dp,
): Modifier {
	val sharedTransitionScope = LocalSharedTransitionScope.current
	if (key.isNullOrBlank() || sharedTransitionScope == null) {
		return this
	}
	val animatedVisibilityScope: AnimatedVisibilityScope = LocalNavAnimatedContentScope.current
	val overlayClip = remember(sharedTransitionScope, cornerRadius) {
		with(sharedTransitionScope) {
			OverlayClip(recipeImageOverlayShape(cornerRadius))
		}
	}
	return with(sharedTransitionScope) {
		this@recipeImageSharedElement.sharedElement(
			sharedContentState = rememberSharedContentState(key = key),
			animatedVisibilityScope = animatedVisibilityScope,
			clipInOverlayDuringTransition = overlayClip,
		)
	}
}

private fun recipeImageOverlayShape(cornerRadius: Dp): Shape = if (cornerRadius == 0.dp) {
	RectangleShape
} else {
	RoundedCornerShape(cornerRadius)
}
