package app.purecipes.shared.ui.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal val dragHandleIcon: ImageVector
	get() {
		val cached = cachedDragHandle
		if (cached != null) {
			return cached
		}
		val icon =
			ImageVector.Builder(
				name = "DragHandle",
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
						moveTo(4f, 15f)
						verticalLineTo(13f)
						horizontalLineTo(20f)
						verticalLineToRelative(2f)
						horizontalLineTo(4f)
						close()
						moveTo(4f, 11f)
						verticalLineTo(9f)
						horizontalLineTo(20f)
						verticalLineToRelative(2f)
						horizontalLineTo(4f)
						close()
					}
				}
				.build()
		cachedDragHandle = icon
		return icon
	}

private var cachedDragHandle: ImageVector? = null
