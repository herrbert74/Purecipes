package app.purecipes.shared.ui.icon.appliance

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val blenderIcon: ImageVector
	get() {
		val cached = cachedBlender
		if (cached != null) {
			return cached
		}
		val icon =
			ImageVector.Builder(
				name = "Blender",
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
						moveTo(8f, 22f)
						quadToRelative(-0.82f, 0f, -1.41f, -0.59f)
						reflectiveQuadTo(6f, 20f)
						verticalLineToRelative(-1f)
						quadToRelative(0f, -1.18f, 0.51f, -2.17f)
						reflectiveQuadToRelative(1.34f, -1.68f)
						lineToRelative(-0.62f, -4.15f)
						horizontalLineToRelative(-2.23f)
						quadToRelative(-0.82f, 0f, -1.41f, -0.59f)
						reflectiveQuadTo(3f, 9f)
						verticalLineToRelative(-4f)
						quadToRelative(0f, -0.82f, 0.59f, -1.41f)
						reflectiveQuadTo(5f, 3f)
						horizontalLineToRelative(5f)
						verticalLineToRelative(-1f)
						horizontalLineToRelative(4f)
						verticalLineToRelative(1f)
						horizontalLineToRelative(3.98f)
						lineToRelative(-1.82f, 12.15f)
						quadToRelative(0.82f, 0.68f, 1.34f, 1.68f)
						reflectiveQuadToRelative(0.51f, 2.17f)
						verticalLineToRelative(1f)
						quadToRelative(0f, 0.82f, -0.59f, 1.41f)
						reflectiveQuadTo(16f, 22f)
						horizontalLineTo(8f)
						close()
						moveToRelative(-1.07f, -13f)
						lineToRelative(-0.6f, -4f)
						horizontalLineToRelative(-1.32f)
						verticalLineToRelative(4f)
						horizontalLineToRelative(1.93f)
						close()
						moveToRelative(5.08f, 10f)
						quadToRelative(0.42f, 0f, 0.71f, -0.29f)
						reflectiveQuadTo(13f, 18f)
						quadToRelative(0f, -0.42f, -0.29f, -0.71f)
						reflectiveQuadTo(12f, 17f)
						quadToRelative(-0.42f, 0f, -0.71f, 0.29f)
						reflectiveQuadTo(11f, 18f)
						quadToRelative(0f, 0.42f, 0.29f, 0.71f)
						reflectiveQuadTo(12f, 19f)
						close()
						moveToRelative(-2.3f, -5f)
						horizontalLineToRelative(4.6f)
						lineToRelative(1.35f, -9f)
						horizontalLineTo(8.35f)
						lineToRelative(1.35f, 9f)
						close()
						moveToRelative(-1.7f, 6f)
						horizontalLineToRelative(8f)
						verticalLineToRelative(-1f)
						quadToRelative(0f, -1.25f, -0.88f, -2.12f)
						reflectiveQuadToRelative(-2.12f, -0.88f)
						horizontalLineToRelative(-2f)
						quadToRelative(-1.25f, 0f, -2.12f, 0.88f)
						reflectiveQuadToRelative(-0.88f, 2.12f)
						verticalLineToRelative(1f)
						close()
						moveToRelative(4f, -2f)
						close()
					}
				}
				.build()
		cachedBlender = icon
		return icon
	}

private var cachedBlender: ImageVector? = null
