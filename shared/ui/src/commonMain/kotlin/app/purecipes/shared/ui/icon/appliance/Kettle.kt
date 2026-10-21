package app.purecipes.shared.ui.icon.appliance

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val kettleIcon: ImageVector
	get() {
		val cached = cachedKettle
		if (cached != null) {
			return cached
		}
		val icon =
			ImageVector.Builder(
				name = "Kettle",
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
						moveTo(6f, 19f)
						verticalLineToRelative(-13f)
						lineTo(3f, 2f)
						horizontalLineToRelative(15f)
						verticalLineToRelative(3f)
						horizontalLineToRelative(2f)
						quadToRelative(0.82f, 0f, 1.41f, 0.59f)
						reflectiveQuadTo(22f, 7f)
						verticalLineToRelative(5f)
						quadToRelative(0f, 0.82f, -0.59f, 1.41f)
						reflectiveQuadTo(20f, 14f)
						horizontalLineToRelative(-2f)
						verticalLineToRelative(5f)
						horizontalLineTo(6f)
						close()
						moveToRelative(2f, -2f)
						horizontalLineToRelative(8f)
						verticalLineToRelative(-13f)
						horizontalLineTo(7f)
						lineToRelative(1f, 1.3f)
						verticalLineToRelative(11.7f)
						close()
						moveToRelative(10f, -5f)
						horizontalLineToRelative(2f)
						verticalLineToRelative(-5f)
						horizontalLineToRelative(-2f)
						verticalLineToRelative(5f)
						close()
						moveTo(12f, 16f)
						horizontalLineToRelative(3f)
						verticalLineToRelative(-11f)
						horizontalLineTo(12f)
						verticalLineToRelative(11f)
						close()
						moveTo(3f, 22f)
						verticalLineToRelative(-2f)
						horizontalLineToRelative(18f)
						verticalLineToRelative(2f)
						horizontalLineTo(3f)
						close()
						moveToRelative(8.5f, -11.5f)
						close()
					}
				}
				.build()
		cachedKettle = icon
		return icon
	}

private var cachedKettle: ImageVector? = null
