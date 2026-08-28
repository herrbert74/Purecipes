package app.purecipes.feature.onboarding.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.purecipes.shared.ui.theme.PurecipesTheme
import kotlin.math.absoluteValue

private const val BACKGROUND_PARALLAX_FRACTION = 0.42f
private const val ILLUSTRATION_SCALE_DECAY = 0.3f
private const val ILLUSTRATION_TWIST_DEGREES = 25f
private const val ILLUSTRATION_DRIFT_FRACTION = 0.15f
private const val ILLUSTRATION_FADE_DEPTH = 0.8f
private const val TEXT_SCALE_DECAY = 0.15f
private const val TEXT_COUNTER_PARALLAX_FRACTION = 0.6f
private const val TEXT_GRAVITY_DROP = 120f
private const val TEXT_TILT_DEGREES = -45f
private const val TEXT_VISIBILITY_START = 0.2f

private val ILLUSTRATION_TEXT_GAP = 40.dp
private val TITLE_DESCRIPTION_GAP = 16.dp
private val DESCRIPTION_HORIZONTAL_PADDING = 40.dp

/**
 * A single onboarding page: a parallaxed colour field, a circular illustration that spins away like
 * a tossed object, and a text block that tilts and drops away like a falling glass pane.
 *
 * All transforms read [pageOffset] inside `graphicsLayer` so swiping only invalidates the layer,
 * never recomposition.
 */
@Composable
internal fun OnboardingPageContent(
	page: OnboardingPage,
	pageOffset: () -> Float,
	sparkleTime: () -> Float,
	sheenSweep: () -> Float,
	modifier: Modifier = Modifier,
) {
	val displayStyle = PurecipesTheme.typography.displaySmall
	val titleStyle = remember(page, displayStyle) {
		displayStyle.copy(
			brush = Brush.linearGradient(
				colors = listOf(page.contentColor, page.accentColor, page.contentColor),
			),
			fontWeight = FontWeight.Bold,
			textAlign = TextAlign.Center,
		)
	}

	Box(modifier = modifier.fillMaxSize()) {
		Box(
			modifier = Modifier
				.fillMaxSize()
				.background(page.backgroundColor)
				.graphicsLayer {
					val offset = pageOffset()
					val settle = FastOutSlowInEasing.transform(
						1f - offset.absoluteValue.coerceIn(0f, 1f),
					)
					val drift = size.width * (1f - settle) * BACKGROUND_PARALLAX_FRACTION
					translationX = if (offset <= 0f) drift else -drift
				},
		)
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(bottom = ONBOARDING_CONTENT_BOTTOM_PADDING),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center,
		) {
			OnboardingIllustration(
				page = page,
				sparkleTime = sparkleTime,
				modifier = Modifier.graphicsLayer {
					val offset = pageOffset()
					val progress = offset.absoluteValue.coerceIn(0f, 1f)
					val scale = 1f - progress * ILLUSTRATION_SCALE_DECAY
					scaleX = scale
					scaleY = scale
					rotationZ = offset * ILLUSTRATION_TWIST_DEGREES
					translationX = offset * size.width * ILLUSTRATION_DRIFT_FRACTION * (1f - progress)
					alpha = 1f - progress * ILLUSTRATION_FADE_DEPTH
				},
			)
			Spacer(modifier = Modifier.height(ILLUSTRATION_TEXT_GAP))
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.graphicsLayer {
						val offset = pageOffset()
						val progress = offset.absoluteValue.coerceIn(0f, 1f)
						val scale = 1f - progress * TEXT_SCALE_DECAY
						scaleX = scale
						scaleY = scale
						translationX = -offset * size.width * TEXT_COUNTER_PARALLAX_FRACTION
						translationY = progress * TEXT_GRAVITY_DROP
						rotationX = progress * TEXT_TILT_DEGREES
						alpha = ((1f - progress) - TEXT_VISIBILITY_START).coerceIn(0f, 1f) /
							(1f - TEXT_VISIBILITY_START)
					},
				horizontalAlignment = Alignment.CenterHorizontally,
			) {
				Text(
					text = page.title,
					style = titleStyle,
					modifier = Modifier.textSheen(
						color = page.accentColor,
						sweep = sheenSweep,
					),
				)
				Spacer(modifier = Modifier.height(TITLE_DESCRIPTION_GAP))
				Text(
					text = page.description,
					style = PurecipesTheme.typography.bodyLarge,
					color = page.contentColor,
					textAlign = TextAlign.Center,
					modifier = Modifier.padding(horizontal = DESCRIPTION_HORIZONTAL_PADDING),
				)
			}
		}
	}
}
