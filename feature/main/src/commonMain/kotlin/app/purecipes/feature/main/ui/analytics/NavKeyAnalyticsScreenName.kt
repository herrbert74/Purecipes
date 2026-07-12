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

internal fun NavKey.toAnalyticsScreenName(): String? = when (this) {
	is SearchDestination -> AnalyticsScreenName.SEARCH
	is RecipeDetailsDestination -> AnalyticsScreenName.RECIPE_DETAILS
	is RecipeCookingDestination -> AnalyticsScreenName.COOKING
	is FavoritesDestination -> AnalyticsScreenName.FAVORITES
	CreateDestination -> AnalyticsScreenName.CREATE_RECIPE
	AccountDestination -> AnalyticsScreenName.ACCOUNT
	is EmailSignInDestination -> AnalyticsScreenName.EMAIL_SIGN_IN
	EmailRegistrationDestination -> AnalyticsScreenName.EMAIL_REGISTRATION
	AccountSettingsDestination -> AnalyticsScreenName.ACCOUNT_SETTINGS
	AboutDestination -> AnalyticsScreenName.ABOUT
	LicensesDestination -> AnalyticsScreenName.LICENSES
	else -> null
}
