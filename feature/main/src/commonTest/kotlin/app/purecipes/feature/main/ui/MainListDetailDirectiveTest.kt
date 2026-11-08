package app.purecipes.feature.main.ui

import androidx.compose.material3.adaptive.Posture
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import app.purecipes.feature.home.ui.navigation.HomeDestination
import app.purecipes.feature.library.ui.navigation.CookbookDetailDestination
import app.purecipes.feature.library.ui.navigation.LibraryDestination
import app.purecipes.feature.recipedetails.ui.navigation.RecipeDetailsDestination
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class MainListDetailDirectiveTest {

	@Test
	fun phoneAndPortraitTabletStaySinglePane() {
		directive(widthDp = 0, heightDp = 0).maxHorizontalPartitions shouldBe 1
		directive(widthDp = 600, heightDp = 900).run {
			maxHorizontalPartitions shouldBe 1
			maxVerticalPartitions shouldBe 1
		}
		directive(widthDp = 840, heightDp = 900).maxHorizontalPartitions shouldBe 1
	}

	@Test
	fun largeWindowsSplitSideBySideWithoutAThirdPane() {
		val directive = directive(widthDp = 1200, heightDp = 900)
		directive.maxHorizontalPartitions shouldBe 2
		directive.maxVerticalPartitions shouldBe 1
		directive.horizontalPartitionSpacerSize shouldBe 0.dp
		directive.verticalPartitionSpacerSize shouldBe 0.dp
		directive.shouldAutoFocusCurrentDestination shouldBe false
	}

	@Test
	fun detailPaneFollowsRecipeAndCookbookDestinations() {
		showsDetailPane(listOf(HomeDestination)) shouldBe false
		showsDetailPane(
			listOf(HomeDestination, RecipeDetailsDestination(recipeId = 4)),
		) shouldBe true
		showsDetailPane(
			listOf(
				LibraryDestination(),
				CookbookDetailDestination(cookbookId = 2, name = "Weeknight"),
			),
		) shouldBe true
	}

	private fun directive(widthDp: Int, heightDp: Int) = mainListDetailDirective(
		WindowAdaptiveInfo(
			windowSizeClass = WindowSizeClass(minWidthDp = widthDp, minHeightDp = heightDp),
			windowPosture = Posture(),
		),
	)
}
