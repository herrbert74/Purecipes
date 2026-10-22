package app.purecipes.shared.ui.icon.appliance

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val sandwichMakerIcon: ImageVector
	get() {
		val cached = cachedSandwichMaker
		if (cached != null) {
			return cached
		}
		val icon =
			ImageVector.Builder(
				name = "SandwichMaker",
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
						moveTo(6f, 21f)
						quadToRelative(-0.82f, 0f, -1.41f, -0.59f)
						reflectiveQuadTo(4f, 19f)
						verticalLineToRelative(-8.55f)
						quadToRelative(-0.93f, -0.55f, -1.46f, -1.46f)
						reflectiveQuadTo(2f, 7f)
						quadToRelative(0f, -1.65f, 1.18f, -2.83f)
						reflectiveQuadToRelative(2.83f, -1.18f)
						horizontalLineToRelative(12f)
						quadToRelative(1.65f, 0f, 2.83f, 1.18f)
						reflectiveQuadToRelative(1.18f, 2.83f)
						quadToRelative(0f, 1.07f, -0.54f, 1.99f)
						reflectiveQuadTo(20f, 10.45f)
						verticalLineToRelative(8.55f)
						quadToRelative(0f, 0.82f, -0.59f, 1.41f)
						reflectiveQuadTo(18f, 21f)
						horizontalLineTo(6f)
						close()
						moveToRelative(0f, -2f)
						horizontalLineToRelative(12f)
						verticalLineToRelative(-9.7f)
						lineToRelative(1f, -0.6f)
						quadToRelative(0.45f, -0.28f, 0.72f, -0.72f)
						reflectiveQuadToRelative(0.28f, -0.97f)
						quadToRelative(0f, -0.82f, -0.59f, -1.41f)
						reflectiveQuadTo(18f, 5f)
						horizontalLineTo(6f)
						quadToRelative(-0.82f, 0f, -1.41f, 0.59f)
						reflectiveQuadTo(4f, 7f)
						quadToRelative(0f, 0.55f, 0.26f, 1.01f)
						reflectiveQuadTo(5f, 8.75f)
						lineToRelative(1f, 0.55f)
						verticalLineToRelative(9.7f)
						close()
						moveToRelative(5.3f, -2.3f)
						quadToRelative(0.3f, 0.28f, 0.71f, 0.28f)
						reflectiveQuadToRelative(0.69f, -0.28f)
						lineToRelative(3f, -3f)
						quadToRelative(0.3f, -0.3f, 0.3f, -0.71f)
						reflectiveQuadTo(15.7f, 12.3f)
						lineTo(12.7f, 9.3f)
						quadToRelative(-0.28f, -0.3f, -0.69f, -0.3f)
						reflectiveQuadTo(11.3f, 9.3f)
						lineTo(8.3f, 12.3f)
						quadToRelative(-0.28f, 0.28f, -0.28f, 0.69f)
						reflectiveQuadToRelative(0.28f, 0.71f)
						lineToRelative(3f, 3f)
						close()
						moveToRelative(0.7f, -2.1f)
						lineToRelative(-1.6f, -1.6f)
						lineToRelative(1.6f, -1.6f)
						lineToRelative(1.6f, 1.6f)
						lineToRelative(-1.6f, 1.6f)
						close()
						moveToRelative(0f, -2.6f)
						close()
					}
				}
				.build()
		cachedSandwichMaker = icon
		return icon
	}

private var cachedSandwichMaker: ImageVector? = null
