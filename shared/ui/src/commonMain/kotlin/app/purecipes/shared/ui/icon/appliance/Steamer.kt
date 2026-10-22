package app.purecipes.shared.ui.icon.appliance

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val steamerIcon: ImageVector
	get() {
		val cached = cachedSteamer
		if (cached != null) {
			return cached
		}
		val icon =
			ImageVector.Builder(
				name = "Steamer",
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
						moveTo(5.1f, 13.5f)
						quadToRelative(0.2f, -0.25f, 0.3f, -0.61f)
						reflectiveQuadToRelative(0.1f, -0.89f)
						quadToRelative(0f, -0.75f, -0.5f, -1.9f)
						reflectiveQuadToRelative(-0.5f, -1.73f)
						quadToRelative(0f, -0.3f, 0.06f, -0.62f)
						reflectiveQuadToRelative(0.34f, -0.75f)
						horizontalLineToRelative(1.5f)
						quadToRelative(-0.28f, 0.42f, -0.34f, 0.75f)
						reflectiveQuadToRelative(-0.06f, 0.62f)
						quadToRelative(0f, 0.57f, 0.5f, 1.73f)
						reflectiveQuadToRelative(0.5f, 1.9f)
						quadToRelative(0f, 0.53f, -0.1f, 0.86f)
						reflectiveQuadTo(6.6f, 13.5f)
						horizontalLineToRelative(-1.5f)
						close()
						moveToRelative(6.5f, 0f)
						quadToRelative(0.2f, -0.25f, 0.3f, -0.61f)
						reflectiveQuadToRelative(0.1f, -0.89f)
						quadToRelative(0f, -0.75f, -0.5f, -1.9f)
						reflectiveQuadToRelative(-0.5f, -1.73f)
						quadToRelative(0f, -0.3f, 0.06f, -0.62f)
						reflectiveQuadToRelative(0.34f, -0.75f)
						horizontalLineToRelative(1.5f)
						quadToRelative(-0.28f, 0.42f, -0.34f, 0.75f)
						reflectiveQuadToRelative(-0.06f, 0.62f)
						quadToRelative(0f, 0.57f, 0.5f, 1.73f)
						reflectiveQuadToRelative(0.5f, 1.9f)
						quadToRelative(0f, 0.53f, -0.1f, 0.86f)
						reflectiveQuadTo(13.1f, 13.5f)
						horizontalLineToRelative(-1.5f)
						close()
						moveToRelative(-3.25f, 0f)
						quadToRelative(0.2f, -0.25f, 0.3f, -0.61f)
						reflectiveQuadToRelative(0.1f, -0.89f)
						quadToRelative(0f, -0.75f, -0.5f, -1.9f)
						reflectiveQuadToRelative(-0.5f, -1.73f)
						quadToRelative(0f, -0.3f, 0.06f, -0.62f)
						reflectiveQuadToRelative(0.34f, -0.75f)
						horizontalLineToRelative(1.5f)
						quadToRelative(-0.28f, 0.42f, -0.34f, 0.75f)
						reflectiveQuadToRelative(-0.06f, 0.62f)
						quadToRelative(0f, 0.57f, 0.5f, 1.73f)
						reflectiveQuadToRelative(0.5f, 1.9f)
						quadToRelative(0f, 0.53f, -0.1f, 0.86f)
						reflectiveQuadTo(9.85f, 13.5f)
						horizontalLineToRelative(-1.5f)
						close()
						moveToRelative(1.4f, 8.5f)
						quadToRelative(-2.52f, 0f, -4.45f, -1.69f)
						reflectiveQuadTo(3f, 16.12f)
						quadToRelative(-0.07f, -0.45f, 0.24f, -0.79f)
						reflectiveQuadTo(4f, 15f)
						horizontalLineToRelative(10.53f)
						lineToRelative(1.1f, -10.35f)
						quadToRelative(0.12f, -1.12f, 0.96f, -1.89f)
						reflectiveQuadTo(18.6f, 2f)
						quadToRelative(1.25f, 0f, 2.12f, 0.88f)
						reflectiveQuadToRelative(0.88f, 2.12f)
						quadToRelative(0f, 0.35f, -0.06f, 0.93f)
						lineToRelative(-0.06f, 0.57f)
						lineToRelative(-1.98f, -0.25f)
						lineToRelative(0.05f, -0.51f)
						quadToRelative(0.05f, -0.51f, 0.05f, -0.74f)
						quadToRelative(0f, -0.42f, -0.29f, -0.71f)
						reflectiveQuadTo(18.6f, 4f)
						quadToRelative(-0.4f, 0f, -0.68f, 0.26f)
						reflectiveQuadTo(17.6f, 4.9f)
						lineToRelative(-1.15f, 10.88f)
						quadToRelative(-0.28f, 2.65f, -2.17f, 4.44f)
						reflectiveQuadTo(9.75f, 22f)
						close()
						moveToRelative(0f, -2f)
						quadToRelative(1.48f, 0f, 2.65f, -0.82f)
						reflectiveQuadToRelative(1.7f, -2.17f)
						horizontalLineTo(5.33f)
						quadToRelative(0.57f, 1.35f, 1.76f, 2.17f)
						reflectiveQuadTo(9.75f, 20f)
						close()
						moveToRelative(0f, -3f)
						close()
					}
				}
				.build()
		cachedSteamer = icon
		return icon
	}

private var cachedSteamer: ImageVector? = null
