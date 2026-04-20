package com.purecipes.shared.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.purecipes.shared.ui.theme.PurecipesTheme

@Composable
fun SectionHeader(
	title: String,
	subtitle: String,
	modifier: Modifier = Modifier,
	titleStyle: TextStyle = PurecipesTheme.typography.titleMedium,
	titleFontWeight: FontWeight = FontWeight.SemiBold,
	subtitleStyle: TextStyle = PurecipesTheme.typography.bodySmall,
) {
	Column(
		modifier = modifier,
		verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs),
	) {
		Text(
			text = title,
			style = titleStyle,
			fontWeight = titleFontWeight,
		)
		Text(
			text = subtitle,
			style = subtitleStyle,
			color = PurecipesTheme.colorScheme.onSurfaceVariant,
		)
	}
}

@Preview
@Composable
private fun SectionHeaderLightPreview() {
	PurecipesTheme(darkTheme = false) {
		Surface {
			SectionHeader(
				title = "Notifications",
				subtitle = "Manage push notification settings across devices.",
			)
		}
	}
}

@Preview
@Composable
private fun SectionHeaderDarkPreview() {
	PurecipesTheme(darkTheme = true) {
		Surface {
			SectionHeader(
				title = "Notifications",
				subtitle = "Manage push notification settings across devices.",
			)
		}
	}
}
