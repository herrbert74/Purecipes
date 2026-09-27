package app.purecipes.shared.ui.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val doneAllIcon: ImageVector
	get() {
		val cached = cachedDoneAll
		if (cached != null) {
			return cached
		}
		val icon =
			ImageVector.Builder(
				name = "DoneAll",
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
						moveTo(6.7f, 18f)
						lineTo(1.05f, 12.35f)
						lineToRelative(1.43f, -1.4f)
						lineTo(6.73f, 15.2f)
						lineToRelative(1.4f, 1.4f)
						lineTo(6.7f, 18f)
						close()
						moveToRelative(5.65f, 0f)
						lineTo(6.7f, 12.35f)
						lineTo(8.1f, 10.93f)
						lineToRelative(4.25f, 4.25f)
						lineToRelative(9.2f, -9.2f)
						lineToRelative(1.4f, 1.43f)
						lineTo(12.35f, 18f)
						close()
						moveToRelative(0f, -5.65f)
						lineToRelative(-1.43f, -1.4f)
						lineTo(15.88f, 6f)
						lineTo(17.3f, 7.4f)
						lineToRelative(-4.95f, 4.95f)
						close()
					}
				}
				.build()
		cachedDoneAll = icon
		return icon
	}

private var cachedDoneAll: ImageVector? = null
