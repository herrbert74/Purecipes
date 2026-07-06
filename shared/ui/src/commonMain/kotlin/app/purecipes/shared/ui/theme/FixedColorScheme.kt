package app.purecipes.shared.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

//region Fixed colours

@Immutable
data class FixedColorScheme(
	val primaryFixed: Color,
	val onPrimaryFixed: Color,
	val secondaryFixed: Color,
	val onSecondaryFixed: Color,
	val tertiaryFixed: Color,
	val onTertiaryFixed: Color,
	val quinaryFixed: Color,
	val onQuinaryFixed: Color,
	val primaryFixedDim: Color,
	val secondaryFixedDim: Color,
	val tertiaryFixedDim: Color,
	val inverseSurfaceFixed: Color,
	val inverseOnSurfaceFixed: Color,
)

fun getFixedColors() = FixedColorScheme(
	primaryFixed = primaryContainerLight,
	onPrimaryFixed = onPrimaryContainerLight,
	secondaryFixed = secondaryContainerLight,
	onSecondaryFixed = onSecondaryContainerLight,
	tertiaryFixed = tertiaryLight,
	onTertiaryFixed = onTertiaryLight,
	quinaryFixed = surfaceContainerHighLight,
	onQuinaryFixed = onSurfaceLight,
	primaryFixedDim = primaryDark,
	secondaryFixedDim = secondaryDark,
	tertiaryFixedDim = tertiaryDark,
	inverseSurfaceFixed = inverseSurfaceLight,
	inverseOnSurfaceFixed = inverseOnSurfaceLight,
)

val LocalFixedColors = staticCompositionLocalOf { getFixedColors() }

//endregion
