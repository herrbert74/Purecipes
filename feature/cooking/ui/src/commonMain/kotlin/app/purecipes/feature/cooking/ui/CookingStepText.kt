package app.purecipes.feature.cooking.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import app.purecipes.shared.ui.preview.PurecipesPreviewScaffold
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
internal fun CookingStepText(
	step: String,
	onDurationClick: (CookingStepHighlight.Duration) -> Unit,
	modifier: Modifier = Modifier,
) {
	val highlightColor = PurecipesTheme.colorScheme.primary
	val highlights = remember(step) { CookingStepHighlightParser.parse(step) }
	val annotated = remember(step, highlights, highlightColor) {
		buildAnnotatedString {
			var cursor = 0
			highlights.forEach { highlight ->
				if (highlight.startIndex > cursor) {
					append(step.substring(cursor, highlight.startIndex))
				}
				when (highlight) {
					is CookingStepHighlight.Duration -> {
						withLink(
							LinkAnnotation.Clickable(
								tag = "duration:${highlight.startIndex}",
								styles = TextLinkStyles(
									style = SpanStyle(
										color = highlightColor,
										fontWeight = FontWeight.Bold,
									),
								),
								linkInteractionListener = { onDurationClick(highlight) },
							),
						) {
							append(highlight.text)
						}
					}

					is CookingStepHighlight.Temperature -> {
						withStyle(
							SpanStyle(
								color = highlightColor,
								fontWeight = FontWeight.Bold,
							),
						) {
							append(highlight.text)
						}
					}
				}
				cursor = highlight.endIndex
			}
			if (cursor < step.length) {
				append(step.substring(cursor))
			}
		}
	}
	Text(
		text = annotated,
		modifier = modifier
			.fillMaxWidth()
			.testTag(STEP_BY_STEP_CURRENT_STEP_TEXT_TAG),
		style = PurecipesTheme.typography.headlineSmall,
		color = PurecipesTheme.colorScheme.onSurface,
	)
}

@Preview(showBackground = true)
@Composable
private fun CookingStepTextPreview() {
	PurecipesPreviewScaffold {
		CookingStepText(
			step = "Bake at 180°C for 25 minutes, then rest for 5 min.",
			onDurationClick = {},
		)
	}
}
