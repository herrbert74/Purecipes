package app.purecipes.shared.ui.icon.appliance

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val pastaMakerIcon: ImageVector
	get() {
		val cached = cachedPastaMaker
		if (cached != null) {
			return cached
		}
		val icon =
			ImageVector.Builder(
				name = "PastaMaker",
				defaultWidth = 24.dp,
				defaultHeight = 24.dp,
				viewportWidth = 24f,
				viewportHeight = 24f,
			)
				.apply {
					path(
						fill = SolidColor(Color.Black),
						fillAlpha = 1f,
						stroke = null,
						strokeAlpha = 1f,
						strokeLineWidth = 1f,
						strokeLineCap = StrokeCap.Butt,
						strokeLineJoin = StrokeJoin.Bevel,
						strokeLineMiter = 1f,
						pathFillType = PathFillType.NonZero,
					) {
						moveTo(10f, 20f)
						horizontalLineToRelative(4f)
						verticalLineToRelative(-1.1f)
						lineToRelative(1.25f, -0.5f)
						quadToRelative(1.62f, -0.65f, 2.76f, -1.81f)
						reflectiveQuadTo(19.65f, 14f)
						horizontalLineTo(4.35f)
						quadToRelative(0.5f, 1.43f, 1.62f, 2.59f)
						reflectiveQuadTo(8.75f, 18.4f)
						lineToRelative(1.25f, 0.5f)
						verticalLineToRelative(1.1f)
						close()
						moveToRelative(-2f, 2f)
						verticalLineToRelative(-1.75f)
						quadToRelative(-2.67f, -1.05f, -4.34f, -3.25f)
						reflectiveQuadTo(2f, 12f)
						horizontalLineToRelative(2f)
						verticalLineToRelative(-8f)
						lineToRelative(18f, -2f)
						verticalLineToRelative(1.5f)
						lineToRelative(-11.5f, 1.3f)
						verticalLineToRelative(1.7f)
						horizontalLineToRelative(11.5f)
						verticalLineToRelative(1.5f)
						horizontalLineTo(10.5f)
						verticalLineToRelative(4f)
						horizontalLineToRelative(11.5f)
						quadToRelative(0f, 2.8f, -1.66f, 5f)
						reflectiveQuadTo(16f, 20.25f)
						verticalLineToRelative(1.75f)
						horizontalLineTo(8f)
						close()
						moveToRelative(0f, -15.5f)
						horizontalLineToRelative(1f)
						verticalLineToRelative(-1.55f)
						lineToRelative(-1f, 0.12f)
						verticalLineToRelative(1.43f)
						close()
						moveToRelative(-2.5f, 0f)
						horizontalLineToRelative(1f)
						verticalLineToRelative(-1.25f)
						lineToRelative(-1f, 0.1f)
						verticalLineToRelative(1.15f)
						close()
						moveToRelative(2.5f, 5.5f)
						horizontalLineToRelative(1f)
						verticalLineToRelative(-4f)
						horizontalLineToRelative(-1f)
						verticalLineToRelative(4f)
						close()
						moveToRelative(-2.5f, 0f)
						horizontalLineToRelative(1f)
						verticalLineToRelative(-4f)
						horizontalLineToRelative(-1f)
						verticalLineToRelative(4f)
						close()
						moveToRelative(6.5f, 2f)
						close()
					}
				}
				.build()
		cachedPastaMaker = icon
		return icon
	}

private var cachedPastaMaker: ImageVector? = null
