package app.purecipes.shared.ui.icon.appliance

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val pressureCookerIcon: ImageVector
	get() {
		val cached = cachedPressureCooker
		if (cached != null) {
			return cached
		}
		val icon =
			ImageVector.Builder(
				name = "PressureCooker",
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
						moveTo(3f, 8f)
						horizontalLineToRelative(18f)
						verticalLineToRelative(9f)
						quadToRelative(0f, 1.25f, -0.88f, 2.12f)
						reflectiveQuadToRelative(-2.12f, 0.88f)
						horizontalLineTo(6f)
						quadToRelative(-1.25f, 0f, -2.12f, -0.88f)
						reflectiveQuadToRelative(-0.88f, -2.12f)
						verticalLineToRelative(-9f)
						close()
						moveToRelative(2f, 2f)
						verticalLineToRelative(7f)
						quadToRelative(0f, 0.42f, 0.29f, 0.71f)
						reflectiveQuadTo(6f, 18f)
						horizontalLineToRelative(12f)
						quadToRelative(0.42f, 0f, 0.71f, -0.29f)
						reflectiveQuadTo(19f, 17f)
						verticalLineToRelative(-7f)
						horizontalLineTo(5f)
						close()
						moveToRelative(-2f, -3f)
						verticalLineToRelative(-2f)
						horizontalLineToRelative(6f)
						verticalLineToRelative(-1f)
						quadToRelative(0f, -0.42f, 0.29f, -0.71f)
						reflectiveQuadTo(10f, 3f)
						horizontalLineToRelative(4f)
						quadToRelative(0.42f, 0f, 0.71f, 0.29f)
						reflectiveQuadTo(15f, 4f)
						verticalLineToRelative(1f)
						horizontalLineToRelative(6f)
						verticalLineToRelative(2f)
						horizontalLineTo(3f)
						close()
						moveToRelative(9f, 7f)
						close()
					}
				}
				.build()
		cachedPressureCooker = icon
		return icon
	}

private var cachedPressureCooker: ImageVector? = null
