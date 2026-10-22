package app.purecipes.shared.ui.icon.appliance

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val airFryerIcon: ImageVector
	get() {
		val cached = cachedAirFryer
		if (cached != null) {
			return cached
		}
		val icon =
			ImageVector.Builder(
				name = "AirFryer",
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
						moveTo(6f, 2f)
						horizontalLineTo(18f)
						quadTo(20f, 2f, 20f, 4f)
						verticalLineTo(20f)
						quadTo(20f, 22f, 18f, 22f)
						horizontalLineTo(6f)
						quadTo(4f, 22f, 4f, 20f)
						verticalLineTo(4f)
						quadTo(4f, 2f, 6f, 2f)
						close()
						moveTo(6f, 4f)
						verticalLineTo(8f)
						horizontalLineTo(18f)
						verticalLineTo(4f)
						horizontalLineTo(6f)
						close()
						moveTo(12f, 5f)
						quadTo(13f, 5f, 13f, 6f)
						quadTo(13f, 7f, 12f, 7f)
						quadTo(11f, 7f, 11f, 6f)
						quadTo(11f, 5f, 12f, 5f)
						close()
						moveTo(6f, 10f)
						horizontalLineTo(18f)
						verticalLineTo(20f)
						horizontalLineTo(6f)
						verticalLineTo(10f)
						close()
						moveTo(8f, 12f)
						horizontalLineTo(16f)
						verticalLineTo(18f)
						horizontalLineTo(8f)
						verticalLineTo(12f)
						close()
						moveTo(11f, 13.5f)
						horizontalLineTo(13f)
						verticalLineTo(16.5f)
						horizontalLineTo(11f)
						close()
					}
				}
				.build()
		cachedAirFryer = icon
		return icon
	}

private var cachedAirFryer: ImageVector? = null
