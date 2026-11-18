package app.purecipes.feature.search.ui.filter

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.purecipes.shared.ui.icon.AppIcons
import app.purecipes.shared.ui.theme.PurecipesTheme
import org.jetbrains.compose.resources.stringResource
import purecipes.shared.ui.generated.resources.Res
import purecipes.shared.ui.generated.resources.collapse_section
import purecipes.shared.ui.generated.resources.expand_section
import purecipes.shared.ui.generated.resources.filter_section_premium
import purecipes.shared.ui.generated.resources.section_state_collapsed
import purecipes.shared.ui.generated.resources.section_state_expanded

private val FILTER_SECTION_HEADER_MIN_HEIGHT = 48.dp

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
	val toggleLabel = if (isCollapsed) {
		stringResource(Res.string.expand_section, title)
	} else {
		stringResource(Res.string.collapse_section, title)
	}
	val sectionStateDescription = if (isCollapsed) {
		stringResource(Res.string.section_state_collapsed)
	} else {
		stringResource(Res.string.section_state_expanded)
	}
	Row(
		modifier = modifier
			.fillMaxWidth()
			.then(
				if (onToggleCollapse != null) {
					Modifier
						.defaultMinSize(minHeight = FILTER_SECTION_HEADER_MIN_HEIGHT)
						.clickable(
							role = Role.Button,
							onClickLabel = toggleLabel,
							onClick = onToggleCollapse,
						)
						.then(
							if (!isLocked) {
								Modifier.semantics { stateDescription = sectionStateDescription }
							} else {
								Modifier
							},
						)
				} else {
					Modifier
				},
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
				imageVector = AppIcons.Lock,
				contentDescription = stringResource(Res.string.filter_section_premium, title),
				tint = if (titleColor == Color.Unspecified) {
					PurecipesTheme.colorScheme.onSurfaceVariant
				} else {
					titleColor
				},
				modifier = Modifier.padding(end = PurecipesTheme.space.xs),
			)
		} else if (onToggleCollapse != null) {
			Box(
				modifier = Modifier.size(FILTER_SECTION_HEADER_MIN_HEIGHT),
				contentAlignment = Alignment.Center,
			) {
				Icon(
					imageVector = AppIcons.ExpandMore,
					contentDescription = null,
					modifier = Modifier.rotate(chevronRotation),
				)
			}
		}
	}
}
