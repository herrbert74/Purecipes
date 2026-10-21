package app.purecipes.shared.ui.icon.appliance

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val ovenIcon: ImageVector
	get() {
		val cached = cachedOven
		if (cached != null) {
			return cached
		}
		val icon =
			ImageVector.Builder(
				name = "Oven",
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
						moveTo(5f, 21f)
						quadToRelative(-0.82f, 0f, -1.41f, -0.59f)
						reflectiveQuadTo(3f, 19f)
						verticalLineToRelative(-14f)
						quadToRelative(0f, -0.82f, 0.59f, -1.41f)
						reflectiveQuadTo(5f, 3f)
						horizontalLineToRelative(14f)
						quadToRelative(0.82f, 0f, 1.41f, 0.59f)
						reflectiveQuadTo(21f, 5f)
						verticalLineToRelative(14f)
						quadToRelative(0f, 0.82f, -0.59f, 1.41f)
						reflectiveQuadTo(19f, 21f)
						horizontalLineTo(5f)
						close()
						moveToRelative(0f, -9f)
						verticalLineToRelative(7f)
						horizontalLineToRelative(14f)
						verticalLineToRelative(-7f)
						horizontalLineToRelative(-2f)
						verticalLineToRelative(5f)
						horizontalLineTo(7f)
						verticalLineToRelative(-5f)
						horizontalLineToRelative(-2f)
						close()
						moveToRelative(4f, 3f)
						horizontalLineToRelative(6f)
						verticalLineToRelative(-3f)
						horizontalLineTo(9f)
						verticalLineToRelative(3f)
						close()
						moveTo(5f, 10f)
						horizontalLineToRelative(14f)
						verticalLineToRelative(-5f)
						horizontalLineTo(5f)
						verticalLineToRelative(5f)
						close()
						moveToRelative(3f, -2f)
						quadToRelative(-0.42f, 0f, -0.71f, -0.29f)
						reflectiveQuadTo(7f, 7f)
						quadToRelative(0f, -0.42f, 0.29f, -0.71f)
						reflectiveQuadTo(8f, 6f)
						quadToRelative(0.42f, 0f, 0.71f, 0.29f)
						reflectiveQuadTo(9f, 7f)
						quadToRelative(0f, 0.42f, -0.29f, 0.71f)
						reflectiveQuadTo(8f, 8f)
						close()
						moveToRelative(4f, 0f)
						quadToRelative(-0.42f, 0f, -0.71f, -0.29f)
						reflectiveQuadTo(11f, 7f)
						quadToRelative(0f, -0.42f, 0.29f, -0.71f)
						reflectiveQuadTo(12f, 6f)
						quadToRelative(0.42f, 0f, 0.71f, 0.29f)
						reflectiveQuadTo(13f, 7f)
						quadToRelative(0f, 0.42f, -0.29f, 0.71f)
						reflectiveQuadTo(12f, 8f)
						close()
						moveToRelative(4f, 0f)
						quadToRelative(-0.42f, 0f, -0.71f, -0.29f)
						reflectiveQuadTo(15f, 7f)
						quadToRelative(0f, -0.42f, 0.29f, -0.71f)
						reflectiveQuadTo(16f, 6f)
						quadToRelative(0.42f, 0f, 0.71f, 0.29f)
						reflectiveQuadTo(17f, 7f)
						quadToRelative(0f, 0.42f, -0.29f, 0.71f)
						reflectiveQuadTo(16f, 8f)
						close()
					}
				}
				.build()
		cachedOven = icon
		return icon
	}

private var cachedOven: ImageVector? = null
