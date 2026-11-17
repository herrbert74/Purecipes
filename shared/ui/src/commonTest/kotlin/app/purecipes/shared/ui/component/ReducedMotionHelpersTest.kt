package app.purecipes.shared.ui.component

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ReducedMotionHelpersTest {

	@Test
	fun staggeredAppearIsNoOpWhenReducedMotionIsEnabled() {
		shouldAnimateStaggeredAppear(index = 0, reducedMotion = true) shouldBe false
		shouldAnimateStaggeredAppear(index = 3, reducedMotion = true) shouldBe false
	}

	@Test
	fun staggeredAppearAnimatesInitialItemsWhenReducedMotionIsDisabled() {
		shouldAnimateStaggeredAppear(index = 0, reducedMotion = false) shouldBe true
		shouldAnimateStaggeredAppear(index = 7, reducedMotion = false) shouldBe true
		shouldAnimateStaggeredAppear(index = 8, reducedMotion = false) shouldBe false
	}

	@Test
	fun sharedElementIsDisabledWhenReducedMotionIsEnabled() {
		shouldUseSharedElement(
			key = "recipe-1",
			hasSharedTransitionScope = true,
			reducedMotion = true,
		) shouldBe false
	}

	@Test
	fun sharedElementRequiresNonBlankKeyAndScopeWhenReducedMotionIsDisabled() {
		shouldUseSharedElement(
			key = "recipe-1",
			hasSharedTransitionScope = true,
			reducedMotion = false,
		) shouldBe true
		shouldUseSharedElement(
			key = null,
			hasSharedTransitionScope = true,
			reducedMotion = false,
		) shouldBe false
		shouldUseSharedElement(
			key = "  ",
			hasSharedTransitionScope = true,
			reducedMotion = false,
		) shouldBe false
		shouldUseSharedElement(
			key = "recipe-1",
			hasSharedTransitionScope = false,
			reducedMotion = false,
		) shouldBe false
	}
}
