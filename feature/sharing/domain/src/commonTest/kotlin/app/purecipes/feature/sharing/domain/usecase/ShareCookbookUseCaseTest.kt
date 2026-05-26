package app.purecipes.feature.sharing.domain.usecase

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ShareCookbookUseCaseTest {

	@Test
	fun `formatCookbookShareText uses singular recipe for one item`() {
		val text = formatCookbookShareText(
			recipeCount = 1,
			shareUrl = "https://purecipes.app/c/token",
		)
		text shouldBe
			"The below link will save 1 recipe to your favorites, unless you already saved them, " +
			"and save a new cookbook into your account.\n\nhttps://purecipes.app/c/token"
	}

	@Test
	fun `formatCookbookShareText uses plural recipes for multiple items`() {
		val text = formatCookbookShareText(
			recipeCount = 12,
			shareUrl = "https://purecipes.app/c/token",
		)
		text.contains("12 recipes") shouldBe true
		text.endsWith("https://purecipes.app/c/token") shouldBe true
	}
}
