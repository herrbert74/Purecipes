package app.purecipes.shared.ui.navigation

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class PostLoginNavigationTargetTest {

	private val sampleShareToken = "a1b2c3d4-e5f6-7890-abcd-ef1234567890"

	@Test
	fun `open search filters resolves to search with filters target`() {
		resolvePostLoginNavigationTarget(PostLoginAction.OpenSearchFilters) shouldBe
			PostLoginNavigationTarget.OpenSearchWithFilters
	}

	@Test
	fun `open create resolves to create target`() {
		resolvePostLoginNavigationTarget(PostLoginAction.OpenCreate) shouldBe
			PostLoginNavigationTarget.OpenCreate
	}

	@Test
	fun `open favorites my recipes resolves to my recipes target`() {
		resolvePostLoginNavigationTarget(PostLoginAction.OpenFavoritesMyRecipes) shouldBe
			PostLoginNavigationTarget.OpenFavoritesMyRecipes
	}

	@Test
	fun `import cookbook share resolves to favorites with token`() {
		resolvePostLoginNavigationTarget(PostLoginAction.ImportCookbookShare(sampleShareToken)) shouldBe
			PostLoginNavigationTarget.OpenFavoritesWithCookbookShare(sampleShareToken)
	}
}
