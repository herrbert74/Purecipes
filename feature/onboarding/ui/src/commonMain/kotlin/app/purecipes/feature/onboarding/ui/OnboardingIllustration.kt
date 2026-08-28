package app.purecipes.feature.onboarding.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

private val ILLUSTRATION_SIZE = 220.dp
private val ILLUSTRATION_ICON_SIZE = 104.dp

@Composable
internal fun OnboardingIllustration(
	page: OnboardingPage,
	sparkleTime: () -> Float,
	modifier: Modifier = Modifier,
) {
	Box(
		modifier = modifier
			.size(ILLUSTRATION_SIZE)
			.clip(CircleShape)
			.background(
				brush = Brush.linearGradient(
					colors = listOf(page.contentColor, page.accentColor),
				),
			)
			.sparkleOverlay(
				color = page.backgroundColor,
				time = sparkleTime,
			),
		contentAlignment = Alignment.Center,
	) {
		Icon(
			imageVector = page.icon,
			contentDescription = null,
			modifier = Modifier.size(ILLUSTRATION_ICON_SIZE),
			tint = page.backgroundColor,
		)
	}
}
