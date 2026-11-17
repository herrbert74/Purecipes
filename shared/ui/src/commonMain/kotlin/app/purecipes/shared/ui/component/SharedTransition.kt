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
import app.purecipes.shared.ui.theme.LocalReducedMotion

val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }

@Composable
fun Modifier.recipeImageSharedElement(
	key: String?,
	cornerRadius: Dp = 0.dp,
): Modifier {
	val sharedTransitionScope = LocalSharedTransitionScope.current
	val reducedMotion = LocalReducedMotion.current
	if (!shouldUseSharedElement(
			key = key,
			hasSharedTransitionScope = sharedTransitionScope != null,
			reducedMotion = reducedMotion
		)
	) {
		return this
	}
	val scope = checkNotNull(sharedTransitionScope)
	val animatedVisibilityScope: AnimatedVisibilityScope = LocalNavAnimatedContentScope.current
	val overlayClip = remember(scope, cornerRadius) {
		with(scope) {
			OverlayClip(recipeImageOverlayShape(cornerRadius))
		}
	}
	return with(scope) {
		this@recipeImageSharedElement.sharedElement(
			sharedContentState = rememberSharedContentState(key = checkNotNull(key)),
			animatedVisibilityScope = animatedVisibilityScope,
			clipInOverlayDuringTransition = overlayClip,
		)
	}
}

internal fun shouldUseSharedElement(
	key: String?,
	hasSharedTransitionScope: Boolean,
	reducedMotion: Boolean,
): Boolean = !reducedMotion && !key.isNullOrBlank() && hasSharedTransitionScope

private fun recipeImageOverlayShape(cornerRadius: Dp): Shape = if (cornerRadius == 0.dp) {
	RectangleShape
} else {
	RoundedCornerShape(cornerRadius)
}
