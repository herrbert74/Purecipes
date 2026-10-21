package app.purecipes.shared.ui.icon.appliance

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val foodProcessorIcon: ImageVector
	get() {
		val cached = cachedFoodProcessor
		if (cached != null) {
			return cached
		}
		val icon =
			ImageVector.Builder(
				name = "FoodProcessor",
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
						moveTo(5f, 17f)
						horizontalLineTo(19f)
						verticalLineTo(20f)
						quadTo(19f, 22f, 17f, 22f)
						horizontalLineTo(7f)
						quadTo(5f, 22f, 5f, 20f)
						verticalLineTo(17f)
						close()
						moveTo(11f, 18.5f)
						horizontalLineTo(13f)
						verticalLineTo(20.5f)
						horizontalLineTo(11f)
						close()
						moveTo(6f, 8f)
						horizontalLineTo(18f)
						verticalLineTo(16f)
						horizontalLineTo(6f)
						verticalLineTo(8f)
						close()
						moveTo(8f, 10f)
						verticalLineTo(14f)
						horizontalLineTo(16f)
						verticalLineTo(10f)
						horizontalLineTo(8f)
						close()
						moveTo(11f, 11f)
						horizontalLineTo(13f)
						verticalLineTo(14f)
						horizontalLineTo(11f)
						close()
						moveTo(5f, 7f)
						horizontalLineTo(19f)
						verticalLineTo(8f)
						horizontalLineTo(5f)
						close()
						moveTo(13f, 3f)
						horizontalLineTo(17f)
						verticalLineTo(7f)
						horizontalLineTo(13f)
						close()
					}
				}
				.build()
		cachedFoodProcessor = icon
		return icon
	}

private var cachedFoodProcessor: ImageVector? = null
