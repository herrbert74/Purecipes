package app.purecipes.shared.ui.component

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation3.ui.LocalNavAnimatedContentScope

val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }

@Composable
fun Modifier.recipeImageSharedElement(
	recipeId: Int,
): Modifier {
	val sharedTransitionScope = LocalSharedTransitionScope.current
	if (recipeId <= 0 || sharedTransitionScope == null) {
		return this
	}
	val animatedVisibilityScope: AnimatedVisibilityScope = LocalNavAnimatedContentScope.current
	return with(sharedTransitionScope) {
		this@recipeImageSharedElement.sharedElement(
			sharedContentState = rememberSharedContentState(key = "recipe-image-$recipeId"),
			animatedVisibilityScope = animatedVisibilityScope,
		)
	}
}
