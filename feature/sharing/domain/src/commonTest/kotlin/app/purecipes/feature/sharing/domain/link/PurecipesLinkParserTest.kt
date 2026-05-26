package app.purecipes.feature.sharing.domain.link

import app.purecipes.feature.sharing.domain.model.PurecipesLink
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class PurecipesLinkParserTest {

	@Test
	fun `parses https recipe links`() {
		PurecipesLinkParser.parse("https://purecipes.app/r/42") shouldBe PurecipesLink.Recipe(42)
		PurecipesLinkParser.parse("https://www.purecipes.app/r/7?ref=share") shouldBe PurecipesLink.Recipe(7)
	}

	@Test
	fun `parses https cookbook links`() {
		PurecipesLinkParser.parse("https://purecipes.app/c/3") shouldBe PurecipesLink.Cookbook(3)
	}

	@Test
	fun `parses custom scheme links`() {
		PurecipesLinkParser.parse("purecipes://r/9") shouldBe PurecipesLink.Recipe(9)
		PurecipesLinkParser.parse("purecipes://c/11") shouldBe PurecipesLink.Cookbook(11)
	}

	@Test
	fun `parses relative web paths`() {
		PurecipesLinkParser.parse("/r/15") shouldBe PurecipesLink.Recipe(15)
		PurecipesLinkParser.parse("/c/2") shouldBe PurecipesLink.Cookbook(2)
	}

	@Test
	fun `rejects invalid links`() {
		PurecipesLinkParser.parse("https://example.com/r/1").shouldBeNull()
		PurecipesLinkParser.parse("purecipes://x/1").shouldBeNull()
		PurecipesLinkParser.parse("not-a-url").shouldBeNull()
	}
}
