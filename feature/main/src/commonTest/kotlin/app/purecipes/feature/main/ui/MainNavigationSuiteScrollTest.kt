package app.purecipes.feature.main.ui

import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class MainNavigationSuiteScrollTest {

	@Test
	fun bottomBarNavigationSuitesHideOnVerticalScrollWhenVisible() {
		shouldHideNavigationSuiteOnScroll(
			isNavBarVisible = true,
			navSuiteType = NavigationSuiteType.NavigationBar,
		) shouldBe true
		shouldHideNavigationSuiteOnScroll(
			isNavBarVisible = true,
			navSuiteType = NavigationSuiteType.ShortNavigationBarCompact,
		) shouldBe true
		shouldHideNavigationSuiteOnScroll(
			isNavBarVisible = true,
			navSuiteType = NavigationSuiteType.ShortNavigationBarMedium,
		) shouldBe true
		shouldHideNavigationSuiteOnScroll(
			isNavBarVisible = false,
			navSuiteType = NavigationSuiteType.NavigationBar,
		) shouldBe false
	}

	@Test
	fun navigationRailAndDrawerNeverHideOnVerticalScroll() {
		shouldHideNavigationSuiteOnScroll(
			isNavBarVisible = true,
			navSuiteType = NavigationSuiteType.NavigationRail,
		) shouldBe false
		shouldHideNavigationSuiteOnScroll(
			isNavBarVisible = true,
			navSuiteType = NavigationSuiteType.WideNavigationRailCollapsed,
		) shouldBe false
		shouldHideNavigationSuiteOnScroll(
			isNavBarVisible = true,
			navSuiteType = NavigationSuiteType.WideNavigationRailExpanded,
		) shouldBe false
		shouldHideNavigationSuiteOnScroll(
			isNavBarVisible = true,
			navSuiteType = NavigationSuiteType.NavigationDrawer,
		) shouldBe false
	}
}
