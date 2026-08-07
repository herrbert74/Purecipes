package app.purecipes.feature.search.ui.filter

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import app.purecipes.shared.ui.theme.PurecipesTheme

internal fun filterSectionSubtitleTag(title: String): String =
	"filterSectionSubtitle${title.filter(Char::isLetterOrDigit)}"

@Composable
internal fun FilterSectionHeader(
	title: String,
	modifier: Modifier = Modifier,
	isCollapsed: Boolean = false,
	isLocked: Boolean = false,
	titleColor: Color = Color.Unspecified,
	subtitle: String? = null,
	onToggleCollapse: (() -> Unit)? = null,
) {
	val chevronRotation by animateFloatAsState(
		targetValue = if (isCollapsed) -90f else 0f,
		label = "chevron",
	)
	Row(
		modifier = modifier
			.fillMaxWidth()
			.then(
				if (onToggleCollapse != null) Modifier.clickable(onClick = onToggleCollapse) else Modifier,
			)
			.padding(
				start = PurecipesTheme.space.m,
				end = PurecipesTheme.space.xs,
				top = PurecipesTheme.space.s,
			),
		verticalAlignment = Alignment.CenterVertically,
	) {
		Column(modifier = Modifier.weight(1f)) {
			Text(
				text = title,
				style = PurecipesTheme.typography.titleMedium,
				color = titleColor,
			)
			if (subtitle != null) {
				Text(
					text = subtitle,
					style = PurecipesTheme.typography.bodySmall,
					color = PurecipesTheme.colorScheme.onSurfaceVariant,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
					modifier = Modifier.testTag(filterSectionSubtitleTag(title)),
				)
			}
		}
		if (isLocked) {
			Icon(
				imageVector = Icons.Default.Lock,
				contentDescription = "$title is a premium filter",
				tint = if (titleColor == Color.Unspecified) {
					PurecipesTheme.colorScheme.onSurfaceVariant
				} else {
					titleColor
				},
				modifier = Modifier.padding(end = PurecipesTheme.space.xs),
			)
		} else if (onToggleCollapse != null) {
			IconButton(onClick = onToggleCollapse) {
				Icon(
					imageVector = Icons.Default.ExpandMore,
					contentDescription = if (isCollapsed) "Expand $title" else "Collapse $title",
					modifier = Modifier.rotate(chevronRotation),
				)
			}
		}
	}
}
