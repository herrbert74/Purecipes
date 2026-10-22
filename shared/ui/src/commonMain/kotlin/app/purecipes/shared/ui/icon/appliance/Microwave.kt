package app.purecipes.shared.ui.icon.appliance

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val microwaveIcon: ImageVector
	get() {
		val cached = cachedMicrowave
		if (cached != null) {
			return cached
		}
		val icon =
			ImageVector.Builder(
				name = "Microwave",
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
						moveTo(4f, 20f)
						quadToRelative(-0.82f, 0f, -1.41f, -0.59f)
						reflectiveQuadTo(2f, 18f)
						verticalLineToRelative(-12f)
						quadToRelative(0f, -0.82f, 0.59f, -1.41f)
						reflectiveQuadTo(4f, 4f)
						horizontalLineToRelative(16f)
						quadToRelative(0.82f, 0f, 1.41f, 0.59f)
						reflectiveQuadTo(22f, 6f)
						verticalLineToRelative(12f)
						quadToRelative(0f, 0.82f, -0.59f, 1.41f)
						reflectiveQuadTo(20f, 20f)
						horizontalLineTo(4f)
						close()
						moveToRelative(0f, -2f)
						horizontalLineToRelative(10f)
						verticalLineToRelative(-12f)
						horizontalLineTo(4f)
						verticalLineToRelative(12f)
						close()
						moveToRelative(12f, 0f)
						horizontalLineToRelative(4f)
						verticalLineToRelative(-12f)
						horizontalLineTo(16f)
						verticalLineToRelative(12f)
						close()
						moveToRelative(1f, -9f)
						horizontalLineToRelative(2f)
						verticalLineToRelative(-2f)
						horizontalLineToRelative(-2f)
						verticalLineToRelative(2f)
						close()
						moveToRelative(1f, 4f)
						quadToRelative(0.42f, 0f, 0.71f, -0.29f)
						reflectiveQuadTo(19f, 12f)
						quadToRelative(0f, -0.42f, -0.29f, -0.71f)
						reflectiveQuadTo(18f, 11f)
						quadToRelative(-0.42f, 0f, -0.71f, 0.29f)
						reflectiveQuadTo(17f, 12f)
						quadToRelative(0f, 0.42f, 0.29f, 0.71f)
						reflectiveQuadTo(18f, 13f)
						close()
						moveToRelative(0f, 4f)
						quadToRelative(0.42f, 0f, 0.71f, -0.29f)
						reflectiveQuadTo(19f, 16f)
						quadToRelative(0f, -0.42f, -0.29f, -0.71f)
						reflectiveQuadTo(18f, 15f)
						quadToRelative(-0.42f, 0f, -0.71f, 0.29f)
						reflectiveQuadTo(17f, 16f)
						quadToRelative(0f, 0.42f, 0.29f, 0.71f)
						reflectiveQuadTo(18f, 17f)
						close()
						moveToRelative(-7.75f, -1f)
						quadToRelative(-0.6f, 0f, -1.04f, -0.23f)
						reflectiveQuadTo(8.45f, 15.35f)
						quadToRelative(-0.25f, -0.17f, -0.4f, -0.26f)
						reflectiveQuadToRelative(-0.3f, -0.09f)
						quadToRelative(-0.28f, 0f, -0.55f, 0.23f)
						reflectiveQuadToRelative(-0.4f, 0.38f)
						lineToRelative(-1.43f, -1.4f)
						quadToRelative(0.28f, -0.3f, 0.89f, -0.75f)
						reflectiveQuadToRelative(1.49f, -0.45f)
						quadToRelative(0.6f, 0f, 1.04f, 0.24f)
						reflectiveQuadTo(9.55f, 13.68f)
						quadToRelative(0.23f, 0.15f, 0.4f, 0.24f)
						reflectiveQuadToRelative(0.3f, 0.09f)
						quadToRelative(0.28f, 0f, 0.55f, -0.23f)
						reflectiveQuadToRelative(0.4f, -0.38f)
						lineToRelative(1.43f, 1.4f)
						quadToRelative(-0.28f, 0.3f, -0.89f, 0.75f)
						reflectiveQuadTo(10.25f, 16f)
						close()
						moveToRelative(0f, -5f)
						quadToRelative(-0.6f, 0f, -1.04f, -0.23f)
						reflectiveQuadTo(8.45f, 10.35f)
						quadToRelative(-0.25f, -0.17f, -0.4f, -0.26f)
						reflectiveQuadToRelative(-0.3f, -0.09f)
						quadToRelative(-0.28f, 0f, -0.55f, 0.23f)
						reflectiveQuadToRelative(-0.4f, 0.38f)
						lineToRelative(-1.43f, -1.4f)
						quadToRelative(0.28f, -0.3f, 0.89f, -0.75f)
						reflectiveQuadToRelative(1.49f, -0.45f)
						quadToRelative(0.6f, 0f, 1.04f, 0.24f)
						reflectiveQuadTo(9.55f, 8.68f)
						quadToRelative(0.23f, 0.15f, 0.4f, 0.24f)
						reflectiveQuadToRelative(0.3f, 0.09f)
						quadToRelative(0.28f, 0f, 0.55f, -0.23f)
						reflectiveQuadToRelative(0.4f, -0.38f)
						lineToRelative(1.43f, 1.4f)
						quadToRelative(-0.28f, 0.3f, -0.89f, 0.75f)
						reflectiveQuadTo(10.25f, 11f)
						close()
						moveToRelative(5.75f, 7f)
						verticalLineToRelative(-12f)
						verticalLineToRelative(12f)
						close()
					}
				}
				.build()
		cachedMicrowave = icon
		return icon
	}

private var cachedMicrowave: ImageVector? = null
