package app.purecipes.shared.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font
import purecipes.shared.ui.generated.resources.Res
import purecipes.shared.ui.generated.resources.cabin_bold
import purecipes.shared.ui.generated.resources.cabin_italic
import purecipes.shared.ui.generated.resources.cabin_regular

@Composable
fun getCabinFontFamily(): FontFamily {
	return FontFamily(
		Font(Res.font.cabin_regular, weight = FontWeight.Normal),
		Font(Res.font.cabin_italic, weight = FontWeight.Normal),
		Font(Res.font.cabin_bold, weight = FontWeight.Bold),
	)
}

@Composable
fun getAppTypography(): Typography {
	val cabinFontFamily = getCabinFontFamily()

	val baseline = Typography()

	return Typography(
		displayLarge = baseline.displayLarge.copy(fontFamily = cabinFontFamily),
		displayMedium = baseline.displayMedium.copy(fontFamily = cabinFontFamily),
		displaySmall = baseline.displaySmall.copy(fontFamily = cabinFontFamily),
		headlineLarge = baseline.headlineLarge.copy(
			fontFamily = cabinFontFamily,
			fontWeight = FontWeight.Bold,
		),
		headlineMedium = baseline.headlineMedium.copy(
			fontFamily = cabinFontFamily,
			fontWeight = FontWeight.Bold,
		),
		headlineSmall = baseline.headlineSmall.copy(
			fontFamily = cabinFontFamily,
			fontWeight = FontWeight.Bold,
		),
		titleLarge = baseline.titleLarge.copy(
			fontFamily = cabinFontFamily,
			fontWeight = FontWeight.Bold,
		),
		titleMedium = baseline.titleMedium.copy(
			fontFamily = cabinFontFamily,
			fontWeight = FontWeight.Bold,
		),
		titleSmall = baseline.titleSmall.copy(
			fontFamily = cabinFontFamily,
			fontWeight = FontWeight.Bold,
		),
		bodyLarge = baseline.bodyLarge.copy(fontFamily = cabinFontFamily),
		bodyMedium = baseline.bodyMedium.copy(fontFamily = cabinFontFamily),
		bodySmall = baseline.bodySmall.copy(fontFamily = cabinFontFamily),
		labelLarge = baseline.labelLarge.copy(fontFamily = cabinFontFamily),
		labelMedium = baseline.labelMedium.copy(fontFamily = cabinFontFamily),
		labelSmall = baseline.labelSmall.copy(fontFamily = cabinFontFamily),
	)
}
