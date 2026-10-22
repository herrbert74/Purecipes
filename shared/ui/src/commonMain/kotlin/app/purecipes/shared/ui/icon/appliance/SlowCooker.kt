package app.purecipes.shared.ui.icon.appliance

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val slowCookerIcon: ImageVector
	get() {
		val cached = cachedSlowCooker
		if (cached != null) {
			return cached
		}
		val icon =
			ImageVector.Builder(
				name = "SlowCooker",
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
						moveTo(8f, 5f)
						verticalLineToRelative(-1f)
						quadToRelative(0f, -0.82f, 0.59f, -1.41f)
						reflectiveQuadTo(10f, 2f)
						horizontalLineToRelative(4f)
						quadToRelative(0.82f, 0f, 1.41f, 0.59f)
						reflectiveQuadTo(16f, 4f)
						verticalLineToRelative(1f)
						horizontalLineToRelative(3f)
						quadToRelative(0.82f, 0f, 1.41f, 0.59f)
						reflectiveQuadTo(21f, 7f)
						verticalLineToRelative(12f)
						quadToRelative(0f, 0.82f, -0.59f, 1.41f)
						reflectiveQuadTo(19f, 21f)
						horizontalLineTo(5f)
						quadToRelative(-0.82f, 0f, -1.41f, -0.59f)
						reflectiveQuadTo(3f, 19f)
						verticalLineToRelative(-12f)
						quadToRelative(0f, -0.82f, 0.59f, -1.41f)
						reflectiveQuadTo(5f, 5f)
						horizontalLineToRelative(3f)
						close()
						moveTo(5f, 19f)
						horizontalLineToRelative(14f)
						verticalLineToRelative(-9f)
						horizontalLineToRelative(-2f)
						verticalLineToRelative(2f)
						quadToRelative(0f, 0.82f, -0.59f, 1.41f)
						reflectiveQuadTo(15f, 14f)
						horizontalLineTo(9f)
						quadToRelative(-0.82f, 0f, -1.41f, -0.59f)
						reflectiveQuadTo(7f, 12f)
						verticalLineToRelative(-2f)
						horizontalLineToRelative(-2f)
						verticalLineToRelative(9f)
						close()
						moveToRelative(3f, -1f)
						quadToRelative(-0.42f, 0f, -0.71f, -0.29f)
						reflectiveQuadTo(7f, 17f)
						quadToRelative(0f, -0.42f, 0.29f, -0.71f)
						reflectiveQuadTo(8f, 16f)
						quadToRelative(0.42f, 0f, 0.71f, 0.29f)
						reflectiveQuadTo(9f, 17f)
						quadToRelative(0f, 0.42f, -0.29f, 0.71f)
						reflectiveQuadTo(8f, 18f)
						close()
						moveToRelative(4f, 0f)
						quadToRelative(-0.42f, 0f, -0.71f, -0.29f)
						reflectiveQuadTo(11f, 17f)
						quadToRelative(0f, -0.42f, 0.29f, -0.71f)
						reflectiveQuadTo(12f, 16f)
						quadToRelative(0.42f, 0f, 0.71f, 0.29f)
						reflectiveQuadTo(13f, 17f)
						quadToRelative(0f, 0.42f, -0.29f, 0.71f)
						reflectiveQuadTo(12f, 18f)
						close()
						moveToRelative(4f, 0f)
						quadToRelative(-0.42f, 0f, -0.71f, -0.29f)
						reflectiveQuadTo(15f, 17f)
						quadToRelative(0f, -0.42f, 0.29f, -0.71f)
						reflectiveQuadTo(16f, 16f)
						quadToRelative(0.42f, 0f, 0.71f, 0.29f)
						reflectiveQuadTo(17f, 17f)
						quadToRelative(0f, 0.42f, -0.29f, 0.71f)
						reflectiveQuadTo(16f, 18f)
						close()
						moveTo(9f, 12f)
						horizontalLineToRelative(6f)
						verticalLineToRelative(-2f)
						horizontalLineTo(9f)
						verticalLineToRelative(2f)
						close()
						moveTo(5f, 8f)
						horizontalLineToRelative(14f)
						verticalLineToRelative(-1f)
						horizontalLineTo(5f)
						verticalLineToRelative(1f)
						close()
						moveToRelative(5f, -3f)
						horizontalLineToRelative(4f)
						verticalLineToRelative(-1f)
						horizontalLineTo(10f)
						verticalLineToRelative(1f)
						close()
					}
				}
				.build()
		cachedSlowCooker = icon
		return icon
	}

private var cachedSlowCooker: ImageVector? = null
