package app.purecipes.feature.main.ui.analytics

import androidx.navigation3.runtime.NavKey
import app.purecipes.feature.analytics.domain.model.AnalyticsScreenName
import app.purecipes.feature.auth.ui.navigation.AccountDestination
import app.purecipes.feature.auth.ui.navigation.EmailRegistrationDestination
import app.purecipes.feature.auth.ui.navigation.EmailSignInDestination
import app.purecipes.feature.cooking.ui.navigation.RecipeCookingDestination
import app.purecipes.feature.favorites.ui.navigation.FavoritesDestination
import app.purecipes.feature.newrecipe.ui.navigation.CreateDestination
import app.purecipes.feature.recipedetails.ui.navigation.RecipeDetailsDestination
import app.purecipes.feature.search.ui.navigation.SearchDestination
import app.purecipes.feature.settings.ui.navigation.AboutDestination
import app.purecipes.feature.settings.ui.navigation.AccountSettingsDestination
import app.purecipes.feature.settings.ui.navigation.LicensesDestination
import app.purecipes.feature.subscription.ui.navigation.PaywallDestination
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class NavKeyAnalyticsScreenNameTest {

	@Test
	fun `maps every known destination and tab root to its screen name`() {
		val mappings = listOf(
			SearchDestination() to AnalyticsScreenName.SEARCH,
			RecipeDetailsDestination(42) to AnalyticsScreenName.RECIPE_DETAILS,
			RecipeCookingDestination(7) to AnalyticsScreenName.COOKING,
			FavoritesDestination() to AnalyticsScreenName.FAVORITES,
			CreateDestination to AnalyticsScreenName.CREATE_RECIPE,
			AccountDestination to AnalyticsScreenName.ACCOUNT,
			EmailSignInDestination() to AnalyticsScreenName.EMAIL_SIGN_IN,
			EmailRegistrationDestination to AnalyticsScreenName.EMAIL_REGISTRATION,
			AccountSettingsDestination to AnalyticsScreenName.ACCOUNT_SETTINGS,
			AboutDestination to AnalyticsScreenName.ABOUT,
			LicensesDestination to AnalyticsScreenName.LICENSES,
		)

		mappings.forEach { (destination, expected) ->
			destination.toAnalyticsScreenName() shouldBe expected
		}
	}

	@Test
	fun `returns null for destinations without a screen name mapping`() {
		(PaywallDestination as NavKey).toAnalyticsScreenName() shouldBe null
	}

	@Test
	fun `inventory includes account_settings and consent_preferences screen names`() {
		AnalyticsScreenName.ACCOUNT_SETTINGS shouldBe "account_settings"
		AnalyticsScreenName.CONSENT_PREFERENCES shouldBe "consent_preferences"
		AnalyticsScreenName.SETTINGS shouldBe "settings"
	}
}
