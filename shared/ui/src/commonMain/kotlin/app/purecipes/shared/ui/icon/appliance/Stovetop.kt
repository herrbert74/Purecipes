package app.purecipes.shared.ui.icon.appliance

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val stovetopIcon: ImageVector
	get() {
		val cached = cachedStovetop
		if (cached != null) {
			return cached
		}
		val icon =
			ImageVector.Builder(
				name = "Stovetop",
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
						moveTo(6f, 15f)
						quadToRelative(-1.25f, 0f, -2.12f, -0.88f)
						reflectiveQuadToRelative(-0.88f, -2.12f)
						verticalLineToRelative(-4f)
						horizontalLineToRelative(13.03f)
						quadToRelative(0.12f, -0.82f, 0.68f, -1.48f)
						reflectiveQuadToRelative(1.35f, -0.93f)
						lineToRelative(4.62f, -1.55f)
						lineToRelative(0.62f, 1.9f)
						lineToRelative(-4.62f, 1.55f)
						quadToRelative(-0.3f, 0.1f, -0.49f, 0.36f)
						reflectiveQuadTo(18f, 8.45f)
						verticalLineToRelative(3.55f)
						quadToRelative(0f, 1.25f, -0.88f, 2.12f)
						reflectiveQuadToRelative(-2.12f, 0.88f)
						horizontalLineTo(6f)
						close()
						moveToRelative(0f, -2f)
						horizontalLineToRelative(9f)
						quadToRelative(0.42f, 0f, 0.71f, -0.29f)
						reflectiveQuadTo(16f, 12f)
						verticalLineToRelative(-2f)
						horizontalLineTo(5f)
						verticalLineToRelative(2f)
						quadToRelative(0f, 0.42f, 0.29f, 0.71f)
						reflectiveQuadTo(6f, 13f)
						close()
						moveToRelative(3f, 8f)
						verticalLineToRelative(-3f)
						horizontalLineTo(2f)
						verticalLineToRelative(-2f)
						horizontalLineToRelative(7f)
						quadToRelative(0.82f, 0f, 1.41f, 0.59f)
						reflectiveQuadTo(11f, 18f)
						verticalLineToRelative(3f)
						horizontalLineToRelative(-2f)
						close()
						moveToRelative(4f, 0f)
						verticalLineToRelative(-3f)
						quadToRelative(0f, -0.82f, 0.59f, -1.41f)
						reflectiveQuadTo(15f, 16f)
						horizontalLineToRelative(7f)
						verticalLineToRelative(2f)
						horizontalLineTo(15f)
						verticalLineToRelative(3f)
						horizontalLineToRelative(-2f)
						close()
						moveTo(10.5f, 11.5f)
						close()
					}
				}
				.build()
		cachedStovetop = icon
		return icon
	}

private var cachedStovetop: ImageVector? = null
