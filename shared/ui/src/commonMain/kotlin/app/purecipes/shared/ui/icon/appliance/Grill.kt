package app.purecipes.shared.ui.icon.appliance

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val grillIcon: ImageVector
	get() {
		val cached = cachedGrill
		if (cached != null) {
			return cached
		}
		val icon =
			ImageVector.Builder(
				name = "Grill",
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
						moveTo(16f, 16f)
						quadToRelative(1.25f, 0f, 2.12f, 0.88f)
						reflectiveQuadToRelative(0.88f, 2.12f)
						quadToRelative(0f, 1.25f, -0.88f, 2.12f)
						reflectiveQuadToRelative(-2.12f, 0.88f)
						quadToRelative(-0.95f, 0f, -1.71f, -0.55f)
						reflectiveQuadTo(13.2f, 20f)
						horizontalLineTo(6.85f)
						lineToRelative(-1f, 1.55f)
						quadToRelative(-0.23f, 0.35f, -0.64f, 0.44f)
						reflectiveQuadTo(4.45f, 21.85f)
						quadToRelative(-0.35f, -0.23f, -0.44f, -0.64f)
						reflectiveQuadTo(4.15f, 20.45f)
						lineToRelative(3.95f, -6.05f)
						quadToRelative(-1.8f, -0.82f, -2.95f, -2.54f)
						reflectiveQuadTo(4f, 8f)
						horizontalLineToRelative(14f)
						quadToRelative(0f, 2.15f, -1.15f, 3.86f)
						reflectiveQuadTo(13.9f, 14.4f)
						lineToRelative(0.57f, 0.9f)
						quadToRelative(-0.53f, 0.25f, -0.86f, 0.5f)
						reflectiveQuadTo(12.88f, 16.5f)
						lineToRelative(-1f, -1.55f)
						quadToRelative(-0.2f, 0.05f, -0.42f, 0.05f)
						horizontalLineToRelative(-0.9f)
						quadToRelative(-0.23f, 0f, -0.42f, -0.05f)
						lineToRelative(-1.98f, 3.05f)
						horizontalLineToRelative(5.05f)
						quadToRelative(0.33f, -0.9f, 1.09f, -1.45f)
						reflectiveQuadToRelative(1.71f, -0.55f)
						close()
						moveToRelative(0f, 4f)
						quadToRelative(0.42f, 0f, 0.71f, -0.29f)
						reflectiveQuadTo(17f, 19f)
						quadToRelative(0f, -0.42f, -0.29f, -0.71f)
						reflectiveQuadTo(16f, 18f)
						quadToRelative(-0.42f, 0f, -0.71f, 0.29f)
						reflectiveQuadTo(15f, 19f)
						quadToRelative(0f, 0.42f, 0.29f, 0.71f)
						reflectiveQuadTo(16f, 20f)
						close()
						moveTo(11f, 13f)
						quadToRelative(1.5f, 0f, 2.74f, -0.82f)
						reflectiveQuadToRelative(1.86f, -2.17f)
						horizontalLineTo(6.4f)
						quadToRelative(0.65f, 1.35f, 1.88f, 2.17f)
						reflectiveQuadToRelative(2.73f, 0.82f)
						close()
						moveTo(8.4f, 7f)
						quadToRelative(0.12f, -0.72f, -0.04f, -1.23f)
						reflectiveQuadTo(7.67f, 4.62f)
						quadToRelative(-0.5f, -0.65f, -0.66f, -1.24f)
						reflectiveQuadTo(6.97f, 2f)
						horizontalLineToRelative(1f)
						quadToRelative(-0.12f, 0.72f, 0.04f, 1.21f)
						reflectiveQuadTo(8.7f, 4.35f)
						quadToRelative(0.53f, 0.65f, 0.68f, 1.24f)
						reflectiveQuadToRelative(0.03f, 1.41f)
						horizontalLineToRelative(-1f)
						close()
						moveToRelative(2.5f, 0f)
						quadToRelative(0.12f, -0.72f, -0.03f, -1.23f)
						reflectiveQuadToRelative(-0.68f, -1.15f)
						quadToRelative(-0.53f, -0.62f, -0.69f, -1.21f)
						reflectiveQuadTo(9.47f, 2f)
						horizontalLineToRelative(1f)
						quadToRelative(-0.12f, 0.72f, 0.04f, 1.21f)
						reflectiveQuadTo(11.2f, 4.35f)
						quadToRelative(0.5f, 0.62f, 0.66f, 1.21f)
						reflectiveQuadTo(11.9f, 7f)
						horizontalLineToRelative(-1f)
						close()
						moveToRelative(2.5f, 0f)
						quadToRelative(0.12f, -0.72f, -0.03f, -1.23f)
						reflectiveQuadToRelative(-0.68f, -1.15f)
						quadToRelative(-0.53f, -0.62f, -0.69f, -1.21f)
						reflectiveQuadTo(11.97f, 2f)
						horizontalLineToRelative(1f)
						quadToRelative(-0.12f, 0.72f, 0.04f, 1.21f)
						reflectiveQuadTo(13.7f, 4.35f)
						quadToRelative(0.5f, 0.62f, 0.66f, 1.21f)
						reflectiveQuadTo(14.4f, 7f)
						horizontalLineToRelative(-1f)
						close()
						moveToRelative(-2.4f, 6f)
						close()
					}
				}
				.build()
		cachedGrill = icon
		return icon
	}

private var cachedGrill: ImageVector? = null
