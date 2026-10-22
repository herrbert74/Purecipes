package app.purecipes.shared.ui.icon.appliance

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val deepFryerIcon: ImageVector
	get() {
		val cached = cachedDeepFryer
		if (cached != null) {
			return cached
		}
		val icon =
			ImageVector.Builder(
				name = "DeepFryer",
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
						moveTo(4f, 8f)
						horizontalLineTo(20f)
						verticalLineTo(19f)
						quadTo(20f, 21f, 18f, 21f)
						horizontalLineTo(6f)
						quadTo(4f, 21f, 4f, 19f)
						verticalLineTo(8f)
						close()
						moveTo(6f, 10f)
						verticalLineTo(19f)
						horizontalLineTo(18f)
						verticalLineTo(10f)
						horizontalLineTo(6f)
						close()
						moveTo(7f, 11f)
						horizontalLineTo(17f)
						verticalLineTo(17f)
						horizontalLineTo(7f)
						verticalLineTo(11f)
						close()
						moveTo(16f, 11f)
						lineTo(19f, 4f)
						horizontalLineTo(22f)
						lineTo(19.5f, 9f)
						close()
					}
				}
				.build()
		cachedDeepFryer = icon
		return icon
	}

private var cachedDeepFryer: ImageVector? = null
