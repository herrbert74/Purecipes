package app.purecipes.shared.ui.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val restaurantIcon: ImageVector
	get() {
		val cached = cachedRestaurant
		if (cached != null) {
			return cached
		}
		val icon =
			ImageVector.Builder(
				name = "Restaurant",
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
						moveTo(7f, 22f)
						verticalLineTo(12.85f)
						quadTo(5.73f, 12.5f, 4.86f, 11.45f)
						reflectiveQuadTo(4f, 9f)
						verticalLineTo(2f)
						horizontalLineTo(6f)
						verticalLineTo(9f)
						horizontalLineTo(7f)
						verticalLineTo(2f)
						horizontalLineTo(9f)
						verticalLineTo(9f)
						horizontalLineToRelative(1f)
						verticalLineTo(2f)
						horizontalLineToRelative(2f)
						verticalLineTo(9f)
						quadToRelative(0f, 1.4f, -0.86f, 2.45f)
						reflectiveQuadTo(9f, 12.85f)
						verticalLineTo(22f)
						horizontalLineTo(7f)
						close()
						moveToRelative(10f, 0f)
						verticalLineTo(14f)
						horizontalLineTo(14f)
						verticalLineTo(7f)
						quadTo(14f, 4.93f, 15.46f, 3.46f)
						reflectiveQuadTo(19f, 2f)
						verticalLineTo(22f)
						horizontalLineTo(17f)
						close()
					}
				}
				.build()
		cachedRestaurant = icon
		return icon
	}

private var cachedRestaurant: ImageVector? = null
