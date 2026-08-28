package app.purecipes.feature.onboarding.ui

import io.kotest.matchers.floats.shouldBeGreaterThan
import io.kotest.matchers.floats.shouldBeLessThan
import io.kotest.matchers.shouldBe
import kotlin.math.abs
import kotlin.test.Test

class LiquidSwipeGeometryTest {

	@Test
	fun `geometry without a measured viewport is not usable`() {
		LiquidSwipeGeometry(width = 0f, height = 0f, buttonRadius = 0f).isMeasured shouldBe false
		measuredGeometry().isMeasured shouldBe true
	}

	@Test
	fun `the button rests untransformed at the start of the gesture`() {
		val transform = measuredGeometry().transformAt(0f)

		transform.translationX shouldBe 0f
		transform.translationY shouldBe 0f
		transform.scaleX shouldBe 1f
		transform.scaleY shouldBe 1f
	}

	@Test
	fun `the button grows into the reveal wave by the end of the gesture`() {
		val geometry = measuredGeometry()
		val transform = geometry.transformAt(1f)

		transform.scaleX * BUTTON_RADIUS shouldBeCloseTo geometry.waveRadiusX
		transform.scaleY * BUTTON_RADIUS shouldBeCloseTo geometry.waveRadiusY
		transform.translationX shouldBeGreaterThan 0f
		transform.translationY shouldBeLessThan 0f
	}

	@Test
	fun `the arriving wave is narrower than the outgoing one`() {
		val geometry = measuredGeometry()

		geometry.copy(isIncoming = true).waveRadiusX shouldBeLessThan geometry.waveRadiusX
	}

	@Test
	fun `the button travels away from the finger when leaving a page`() {
		horizontalDirection(isSwipingForward = true, isOutgoing = true) shouldBe 1f
		horizontalDirection(isSwipingForward = true, isOutgoing = false) shouldBe -1f
		horizontalDirection(isSwipingForward = false, isOutgoing = true) shouldBe -1f
		horizontalDirection(isSwipingForward = false, isOutgoing = false) shouldBe 1f
	}

	private fun measuredGeometry() = LiquidSwipeGeometry(
		width = VIEWPORT_WIDTH,
		height = VIEWPORT_HEIGHT,
		buttonRadius = BUTTON_RADIUS,
	)

	private infix fun Float.shouldBeCloseTo(expected: Float) {
		(abs(this - expected) < TOLERANCE) shouldBe true
	}

	private companion object {

		const val VIEWPORT_WIDTH = 1000f
		const val VIEWPORT_HEIGHT = 2000f
		const val BUTTON_RADIUS = 40f
		const val TOLERANCE = 0.01f
	}
}
