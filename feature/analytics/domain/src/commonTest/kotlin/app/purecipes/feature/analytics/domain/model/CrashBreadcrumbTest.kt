package app.purecipes.feature.analytics.domain.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class CrashBreadcrumbTest {

	@Test
	fun `formats breadcrumbs without user content`() {
		CrashBreadcrumb.screen("search") shouldBe "screen: search"
		CrashBreadcrumb.SEARCH_PERFORMED shouldBe "search_performed"
		CrashBreadcrumb.recipeOpened(42) shouldBe "recipe_opened: 42"
		CrashBreadcrumb.cookingStarted(7) shouldBe "cooking_started: 7"
		CrashBreadcrumb.cookingStepAdvanced(7, 2) shouldBe "cooking_step_advanced: 7:2"
		CrashBreadcrumb.RECIPE_SAVE_ATTEMPTED shouldBe "recipe_save_attempted"
		CrashBreadcrumb.signInAttempted(AnalyticsAuthMethod.EMAIL) shouldBe "sign_in_attempted: email"
	}
}
