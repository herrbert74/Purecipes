package app.purecipes.shared.ui.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import app.purecipes.shared.ui.theme.getAppTypography
import app.purecipes.shared.ui.theme.getCabinFontFamily

@Composable
fun TitleText(
	text: String,
	modifier: Modifier = Modifier,
	color: Color = Color.Unspecified
) {
	Text(
		text = text,
		modifier = modifier,
		fontFamily = getCabinFontFamily(),
		style = getAppTypography().titleMedium,
		maxLines = 1,
		color = color
	)
}

@Composable
fun BodyText(
	text: String,
	modifier: Modifier = Modifier,
	color: Color = Color.Unspecified
) {
	Text(
		text = text,
		modifier = modifier,
		fontFamily = getCabinFontFamily(),
		fontSize = 14.sp,
		fontWeight = FontWeight.Normal,
		color = color
	)
}
