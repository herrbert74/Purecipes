package app.purecipes.feature.onboarding.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.Dp
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.sin

internal const val LIQUID_SWIPE_SPLIT_POINT = 0.65f

private const val TRAVEL_EASE_END = 0.82f
private const val STRETCH_START = 0.5f
private const val HORIZONTAL_TRAVEL_FRACTION = 0.55f
private const val VERTICAL_TRAVEL_FRACTION = 0.015f
private const val ARC_HEIGHT_FRACTION = 0.02f
private const val WAVE_WIDTH_FRACTION = 0.65f
private const val WAVE_HEIGHT_FRACTION = 1.5f
private const val FULL_COVERAGE_FACTOR = 2.5f

/**
 * Ballistic geometry shared by the morphing next button and the reveal oval, so both follow the
 * exact same parabolic path and end up perfectly overlaid.
 */
@Immutable
internal data class LiquidSwipeGeometry(
	val width: Float,
	val height: Float,
	val buttonRadius: Float,
	val isIncoming: Boolean = false,
) {

	val isMeasured: Boolean get() = width > 0f && height > 0f && buttonRadius > 0f

	val waveRadiusX: Float
		get() = if (isIncoming) {
			width * (1f - WAVE_WIDTH_FRACTION)
		} else {
			width * WAVE_WIDTH_FRACTION
		}

	val waveRadiusY: Float get() = height * WAVE_HEIGHT_FRACTION

	private val horizontalTravel: Float get() = width * HORIZONTAL_TRAVEL_FRACTION

	private val verticalTravel: Float get() = -height * VERTICAL_TRAVEL_FRACTION

	private val arcHeight: Float get() = height * ARC_HEIGHT_FRACTION

	private val maxBaseScale: Float get() = waveRadiusX / buttonRadius

	private val maxVerticalBonus: Float get() = waveRadiusY / buttonRadius - maxBaseScale

	fun transformAt(progress: Float): LiquidSwipeTransform {
		val travelFraction = (progress / TRAVEL_EASE_END).coerceIn(0f, 1f)
		val stretchFraction = ((progress - STRETCH_START) / (1f - STRETCH_START)).coerceIn(0f, 1f)
		val easedTravel = FastOutSlowInEasing.transform(travelFraction)
		val easedStretch = FastOutSlowInEasing.transform(stretchFraction)
		val arc = sin(easedTravel * PI).toFloat()
		val baseScale = 1f + easedTravel * (maxBaseScale - 1f)
		return LiquidSwipeTransform(
			translationX = horizontalTravel * easedTravel,
			translationY = verticalTravel * easedTravel - arcHeight * arc,
			scaleX = baseScale,
			scaleY = baseScale + easedStretch * maxVerticalBonus,
		)
	}
}

@Immutable
internal data class LiquidSwipeTransform(
	val translationX: Float,
	val translationY: Float,
	val scaleX: Float,
	val scaleY: Float,
)

internal fun PagerState.offsetForPage(page: Int): Float =
	(currentPage - page) + currentPageOffsetFraction

/**
 * Progress of a page through the transition, normalised so that 0 means "at rest" for the page the
 * user is leaving and 1 means "fully revealed" for the page arriving.
 */
internal fun PagerState.liquidSwipeProgress(page: Int): Float {
	val absoluteOffset = offsetForPage(page).absoluteValue
	return if (page == settledPage) {
		absoluteOffset.coerceIn(0f, 1f)
	} else {
		(1f - absoluteOffset).coerceIn(0f, 1f)
	}
}

internal fun PagerState.isSwipingForward(page: Int): Boolean {
	val pageOffset = offsetForPage(page)
	return if (page == settledPage) pageOffset > 0f else pageOffset < 0f
}

internal fun horizontalDirection(isSwipingForward: Boolean, isOutgoing: Boolean): Float =
	if (isSwipingForward == isOutgoing) 1f else -1f

/**
 * Transform for the next button of [page], or `null` while the button should stay hidden.
 *
 * The button of the page being left travels along the arc for the first
 * [LIQUID_SWIPE_SPLIT_POINT] of the gesture and then hands over to the button of the arriving page,
 * which retraces the same arc backwards. Between the two hand-over points neither button is drawn.
 */
internal fun PagerState.nextButtonTransform(
	page: Int,
	geometry: LiquidSwipeGeometry,
): LiquidSwipeTransform? {
	val isOutgoing = page == settledPage
	val progress = liquidSwipeProgress(page)
	val isAtRest = if (isOutgoing) progress == 0f else progress == 1f
	if (isAtRest) {
		return LiquidSwipeTransform(translationX = 0f, translationY = 0f, scaleX = 1f, scaleY = 1f)
	}
	val forward = isSwipingForward(page)
	return when {
		!geometry.isMeasured || progress <= 0f || progress >= 1f -> null

		isOutgoing && progress <= LIQUID_SWIPE_SPLIT_POINT ->
			geometry.transformAt(progress / LIQUID_SWIPE_SPLIT_POINT)
				.directed(isSwipingForward = forward, isOutgoing = true)

		!isOutgoing && progress > LIQUID_SWIPE_SPLIT_POINT -> {
			val retrace =
				1f - (progress - LIQUID_SWIPE_SPLIT_POINT) / (1f - LIQUID_SWIPE_SPLIT_POINT)
			geometry.copy(isIncoming = true).transformAt(retrace)
				.directed(isSwipingForward = forward, isOutgoing = false)
		}

		else -> null
	}
}

private fun LiquidSwipeTransform.directed(
	isSwipingForward: Boolean,
	isOutgoing: Boolean,
): LiquidSwipeTransform = copy(
	translationX = translationX * horizontalDirection(isSwipingForward, isOutgoing),
)

/**
 * Clips the arriving page to an oval that grows out of the next button.
 *
 * Up to [LIQUID_SWIPE_SPLIT_POINT] the oval tracks the button along the same arc, so the button
 * appears to melt into the new page. After the split point the centre locks near the screen edge
 * and both radii burst outwards until the page fills the viewport.
 */
internal fun Modifier.liquidSwipeClip(
	pagerState: PagerState,
	page: Int,
	buttonSize: Dp,
	bottomPadding: Dp,
): Modifier = this.drawWithCache {
	val path = Path()
	val buttonRadiusPx = buttonSize.toPx() / 2f
	val bottomPaddingPx = bottomPadding.toPx()

	onDrawWithContent {
		val pageOffset = pagerState.offsetForPage(page)
		val revealProgress = (1f - pageOffset.absoluteValue).coerceIn(0f, 1f)
		val isArriving = page != pagerState.settledPage

		if (!isArriving || revealProgress <= 0f || revealProgress >= 1f) {
			drawContent()
			return@onDrawWithContent
		}

		val geometry = LiquidSwipeGeometry(
			width = size.width,
			height = size.height,
			buttonRadius = buttonRadiusPx,
		)
		val originX = size.width / 2f
		val originY = size.height - bottomPaddingPx - buttonRadiusPx
		val towardsRight = pageOffset <= 0f
		val bounds = geometry.revealBounds(
			revealProgress = revealProgress,
			originX = originX,
			originY = originY,
			towardsRight = towardsRight,
		)

		path.reset()
		path.addOval(bounds)
		clipPath(path) {
			this@onDrawWithContent.drawContent()
		}
	}
}

private fun LiquidSwipeGeometry.revealBounds(
	revealProgress: Float,
	originX: Float,
	originY: Float,
	towardsRight: Boolean,
): Rect {
	val centerX: Float
	val centerY: Float
	val radiusX: Float
	val radiusY: Float

	if (revealProgress <= LIQUID_SWIPE_SPLIT_POINT) {
		val transform = transformAt(revealProgress / LIQUID_SWIPE_SPLIT_POINT)
		centerX = originX + if (towardsRight) transform.translationX else -transform.translationX
		centerY = originY + transform.translationY
		radiusX = buttonRadius * transform.scaleX
		radiusY = buttonRadius * transform.scaleY
	} else {
		val burstProgress = (revealProgress - LIQUID_SWIPE_SPLIT_POINT) / (1f - LIQUID_SWIPE_SPLIT_POINT)
		val easedBurst = FastOutSlowInEasing.transform(burstProgress.coerceIn(0f, 1f))
		val restingTransform = transformAt(1f)
		centerX = originX + if (towardsRight) {
			restingTransform.translationX
		} else {
			-restingTransform.translationX
		}
		centerY = originY + restingTransform.translationY
		val fullCoverageRadius = maxOf(width, height) * FULL_COVERAGE_FACTOR
		radiusX = waveRadiusX + (fullCoverageRadius - waveRadiusX) * easedBurst
		radiusY = waveRadiusY + (fullCoverageRadius - waveRadiusY) * easedBurst
	}

	return Rect(
		left = centerX - radiusX,
		top = centerY - radiusY,
		right = centerX + radiusX,
		bottom = centerY + radiusY,
	)
}
