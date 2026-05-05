package com.purecipes.umbrella

import com.purecipes.feature.analytics.data.repository.AnalyticsDataModule
import com.purecipes.feature.analytics.domain.usecase.ObserveConsentStateUseCase
import com.purecipes.feature.analytics.domain.usecase.RefreshConsentUseCase
import com.purecipes.feature.analytics.domain.usecase.SetAnalyticsUserIdUseCase
import com.purecipes.feature.analytics.domain.usecase.ShowConsentFormUseCase
import com.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import com.purecipes.feature.auth.data.repository.AuthenticationDataModule
import com.purecipes.feature.auth.domain.usecase.ObserveAuthenticationStateUseCase
import com.purecipes.feature.auth.domain.usecase.RegisterWithEmailUseCase
import com.purecipes.feature.auth.domain.usecase.SignInWithEmailUseCase
import com.purecipes.feature.auth.domain.usecase.SignInWithExternalProviderUseCase
import com.purecipes.feature.auth.domain.usecase.SignInWithGoogleUseCase
import com.purecipes.feature.auth.domain.usecase.SignOutUseCase
import com.purecipes.feature.favorites.data.repository.FavoritesDataModule
import com.purecipes.feature.favorites.domain.usecase.AddFavoriteRecipeUseCase
import com.purecipes.feature.favorites.domain.usecase.AddRecipeToCookbookUseCase
import com.purecipes.feature.favorites.domain.usecase.CreateCookbookUseCase
import com.purecipes.feature.favorites.domain.usecase.DeleteCookbookUseCase
import com.purecipes.feature.favorites.domain.usecase.GetCookbookCoverImageUrlUseCase
import com.purecipes.feature.favorites.domain.usecase.GetCookbookRecipesPageUseCase
import com.purecipes.feature.favorites.domain.usecase.GetCookbooksPageUseCase
import com.purecipes.feature.favorites.domain.usecase.GetFavoriteRecipesPageUseCase
import com.purecipes.feature.favorites.domain.usecase.GetRecipeCookbooksUseCase
import com.purecipes.feature.favorites.domain.usecase.RemoveFavoriteRecipeUseCase
import com.purecipes.feature.favorites.domain.usecase.RemoveRecipeFromCookbookUseCase
import com.purecipes.feature.measurement.domain.usecase.FilterRecipesForMeasurementPreferencesUseCase
import com.purecipes.feature.measurement.domain.usecase.GetMeasurementPreferencesUseCase
import com.purecipes.feature.measurement.domain.usecase.MarkMeasurementMismatchSeenUseCase
import com.purecipes.feature.measurement.domain.usecase.ObserveMeasurementPreferencesUseCase
import com.purecipes.feature.measurement.domain.usecase.ProcessRecipeDetailsForMeasurementPreferencesUseCase
import com.purecipes.feature.measurement.domain.usecase.ResetMeasurementPreferencesUseCase
import com.purecipes.feature.measurement.domain.usecase.SaveMeasurementPreferencesUseCase
import com.purecipes.feature.newrecipe.data.repository.NewRecipeDataModule
import com.purecipes.feature.newrecipe.domain.usecase.GetCreatedRecipesUseCase
import com.purecipes.feature.newrecipe.domain.usecase.SaveCreatedRecipeUseCase
import com.purecipes.feature.recipedetails.data.repository.RecipeDetailsDataModule
import com.purecipes.feature.recipedetails.domain.usecase.GetRecipeDetailsUseCase
import com.purecipes.feature.search.data.repository.SearchDataModule
import com.purecipes.feature.search.domain.usecase.GetSearchFiltersUseCase
import com.purecipes.feature.search.domain.usecase.GetUserPantryUseCase
import com.purecipes.feature.search.domain.usecase.SaveSearchFiltersUseCase
import com.purecipes.feature.search.domain.usecase.SearchRecipesUseCase
import com.purecipes.feature.search.domain.usecase.UpdateUserPantryUseCase
import com.purecipes.feature.settings.data.repository.SettingsDataModule
import com.purecipes.feature.settings.domain.usecase.ObserveNotificationPreferencesUseCase
import com.purecipes.feature.settings.domain.usecase.SaveNotificationPreferencesUseCase
import com.purecipes.feature.settings.domain.usecase.SendTestNotificationUseCase
import com.purecipes.shared.data.config.PurecipesConfig
import com.purecipes.shared.data.network.DataNetworkModule
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph

@DependencyGraph(AppScope::class)
interface WasmAppGraph :
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
