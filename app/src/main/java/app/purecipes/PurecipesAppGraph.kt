package app.purecipes

import app.purecipes.feature.analytics.data.repository.AnalyticsDataModule
import app.purecipes.feature.analytics.domain.usecase.ObserveConsentStateUseCase
import app.purecipes.feature.analytics.domain.usecase.RefreshConsentUseCase
import app.purecipes.feature.analytics.domain.usecase.SetAnalyticsUserIdUseCase
import app.purecipes.feature.analytics.domain.usecase.ShowConsentFormUseCase
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.auth.data.repository.AuthenticationDataModule
import app.purecipes.feature.auth.domain.usecase.ObserveAuthenticationStateUseCase
import app.purecipes.feature.auth.domain.usecase.RegisterWithEmailUseCase
import app.purecipes.feature.auth.domain.usecase.ResendEmailVerificationUseCase
import app.purecipes.feature.auth.domain.usecase.SignInWithEmailUseCase
import app.purecipes.feature.auth.domain.usecase.SignInWithExternalProviderUseCase
import app.purecipes.feature.auth.domain.usecase.SignInWithGoogleUseCase
import app.purecipes.feature.auth.domain.usecase.SignOutUseCase
import app.purecipes.feature.favorites.data.repository.FavoritesDataModule
import app.purecipes.feature.favorites.domain.usecase.AddFavoriteRecipeUseCase
import app.purecipes.feature.favorites.domain.usecase.AddRecipeToCookbookUseCase
import app.purecipes.feature.favorites.domain.usecase.CreateCookbookUseCase
import app.purecipes.feature.favorites.domain.usecase.DeleteCookbookUseCase
import app.purecipes.feature.favorites.domain.usecase.GetCookbookCoverImageUrlUseCase
import app.purecipes.feature.favorites.domain.usecase.GetCookbookRecipesPageUseCase
import app.purecipes.feature.favorites.domain.usecase.GetCookbooksPageUseCase
import app.purecipes.feature.favorites.domain.usecase.GetFavoriteRecipesPageUseCase
import app.purecipes.feature.favorites.domain.usecase.GetRecipeCookbooksUseCase
import app.purecipes.feature.favorites.domain.usecase.RemoveFavoriteRecipeUseCase
import app.purecipes.feature.favorites.domain.usecase.RemoveRecipeFromCookbookUseCase
import app.purecipes.feature.measurement.domain.usecase.FilterRecipesForMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.GetMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.MarkMeasurementMismatchSeenUseCase
import app.purecipes.feature.measurement.domain.usecase.ObserveMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.ProcessRecipeDetailsForMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.ResetMeasurementPreferencesUseCase
import app.purecipes.feature.measurement.domain.usecase.SaveMeasurementPreferencesUseCase
import app.purecipes.feature.newrecipe.data.repository.NewRecipeDataModule
import app.purecipes.feature.newrecipe.domain.usecase.GetCreatedRecipesUseCase
import app.purecipes.feature.newrecipe.domain.usecase.SaveCreatedRecipeUseCase
import app.purecipes.feature.recipedetails.data.repository.RecipeDetailsDataModule
import app.purecipes.feature.recipedetails.domain.usecase.GetRecipeDetailsUseCase
import app.purecipes.feature.search.data.repository.SearchDataModule
import app.purecipes.feature.search.domain.usecase.GetSearchFiltersUseCase
import app.purecipes.feature.search.domain.usecase.GetUserPantryUseCase
import app.purecipes.feature.search.domain.usecase.SaveSearchFiltersUseCase
import app.purecipes.feature.search.domain.usecase.SearchRecipesUseCase
import app.purecipes.feature.search.domain.usecase.UpdateUserPantryUseCase
import app.purecipes.feature.settings.data.repository.SettingsDataModule
import app.purecipes.feature.settings.domain.usecase.InitializeNotificationsUseCase
import app.purecipes.feature.settings.domain.usecase.ObserveNotificationPreferencesUseCase
import app.purecipes.feature.settings.domain.usecase.SaveNotificationPreferencesUseCase
import app.purecipes.feature.settings.domain.usecase.SendTestNotificationUseCase
import app.purecipes.shared.data.config.PurecipesConfig
import app.purecipes.shared.data.network.DataNetworkModule
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph

@DependencyGraph(AppScope::class)
interface PurecipesAppGraph :
	AnalyticsDataModule,
	AuthenticationDataModule,
	DataNetworkModule,
	FavoritesDataModule,
	NewRecipeDataModule,
	RecipeDetailsDataModule,
	SettingsDataModule,
	SearchDataModule {

	val purecipesConfig: PurecipesConfig

	val observeConsentStateUseCase: ObserveConsentStateUseCase

	val refreshConsentUseCase: RefreshConsentUseCase

	val setAnalyticsUserIdUseCase: SetAnalyticsUserIdUseCase

	val showConsentFormUseCase: ShowConsentFormUseCase

	val trackEventUseCase: TrackEventUseCase

	val observeAuthenticationStateUseCase: ObserveAuthenticationStateUseCase

	val registerWithEmailUseCase: RegisterWithEmailUseCase

	val resendEmailVerificationUseCase: ResendEmailVerificationUseCase

	val signInWithEmailUseCase: SignInWithEmailUseCase

	val signInWithExternalProviderUseCase: SignInWithExternalProviderUseCase

	val signInWithGoogleUseCase: SignInWithGoogleUseCase

	val signOutUseCase: SignOutUseCase

	val addFavoriteRecipeUseCase: AddFavoriteRecipeUseCase

	val getFavoriteRecipesPageUseCase: GetFavoriteRecipesPageUseCase

	val getCookbooksPageUseCase: GetCookbooksPageUseCase

	val createCookbookUseCase: CreateCookbookUseCase

	val deleteCookbookUseCase: DeleteCookbookUseCase

	val getCookbookRecipesPageUseCase: GetCookbookRecipesPageUseCase

	val getCookbookCoverImageUrlUseCase: GetCookbookCoverImageUrlUseCase

	val addRecipeToCookbookUseCase: AddRecipeToCookbookUseCase

	val removeRecipeFromCookbookUseCase: RemoveRecipeFromCookbookUseCase

	val getRecipeCookbooksUseCase: GetRecipeCookbooksUseCase

	val filterRecipesForMeasurementPreferencesUseCase: FilterRecipesForMeasurementPreferencesUseCase

	val getMeasurementPreferencesUseCase: GetMeasurementPreferencesUseCase

	val getCreatedRecipesUseCase: GetCreatedRecipesUseCase

	val getRecipeDetailsUseCase: GetRecipeDetailsUseCase

	val markMeasurementMismatchSeenUseCase: MarkMeasurementMismatchSeenUseCase

	val observeMeasurementPreferencesUseCase: ObserveMeasurementPreferencesUseCase

	val initializeNotificationsUseCase: InitializeNotificationsUseCase

	val observeNotificationPreferencesUseCase: ObserveNotificationPreferencesUseCase

	val processRecipeDetailsForMeasurementPreferencesUseCase: ProcessRecipeDetailsForMeasurementPreferencesUseCase

	val resetMeasurementPreferencesUseCase: ResetMeasurementPreferencesUseCase

	val removeFavoriteRecipeUseCase: RemoveFavoriteRecipeUseCase

	val saveMeasurementPreferencesUseCase: SaveMeasurementPreferencesUseCase

	val saveNotificationPreferencesUseCase: SaveNotificationPreferencesUseCase

	val sendTestNotificationUseCase: SendTestNotificationUseCase

	val saveCreatedRecipeUseCase: SaveCreatedRecipeUseCase

	val getSearchFiltersUseCase: GetSearchFiltersUseCase

	val saveSearchFiltersUseCase: SaveSearchFiltersUseCase

	val getUserPantryUseCase: GetUserPantryUseCase

	val updateUserPantryUseCase: UpdateUserPantryUseCase

	val searchRecipesUseCase: SearchRecipesUseCase
}
