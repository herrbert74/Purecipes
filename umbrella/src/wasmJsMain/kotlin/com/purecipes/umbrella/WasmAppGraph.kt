package com.purecipes.umbrella

import com.purecipes.feature.auth.data.repository.AuthenticationDataModule
import com.purecipes.feature.auth.domain.usecase.ObserveAuthenticationStateUseCase
import com.purecipes.feature.auth.domain.usecase.RegisterWithEmailUseCase
import com.purecipes.feature.auth.domain.usecase.SignInWithEmailUseCase
import com.purecipes.feature.auth.domain.usecase.SignInWithExternalProviderUseCase
import com.purecipes.feature.auth.domain.usecase.SignInWithGoogleUseCase
import com.purecipes.feature.auth.domain.usecase.SignOutUseCase
import com.purecipes.feature.favorites.data.repository.FavoritesDataModule
import com.purecipes.feature.favorites.domain.usecase.AddFavoriteRecipeUseCase
import com.purecipes.feature.favorites.domain.usecase.GetFavoriteRecipesUseCase
import com.purecipes.feature.favorites.domain.usecase.RemoveFavoriteRecipeUseCase
import com.purecipes.feature.recipedetails.data.repository.RecipeDetailsDataModule
import com.purecipes.feature.recipedetails.domain.usecase.GetRecipeDetailsUseCase
import com.purecipes.feature.search.data.repository.SearchDataModule
import com.purecipes.feature.search.domain.usecase.SearchRecipesUseCase
import com.purecipes.shared.data.config.PurecipesConfig
import com.purecipes.shared.data.network.DataNetworkModule
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph

@DependencyGraph(AppScope::class)
interface WasmAppGraph :
	AuthenticationDataModule, DataNetworkModule, FavoritesDataModule, RecipeDetailsDataModule, SearchDataModule {

	val purecipesConfig: PurecipesConfig

	val observeAuthenticationStateUseCase: ObserveAuthenticationStateUseCase

	val registerWithEmailUseCase: RegisterWithEmailUseCase

	val signInWithEmailUseCase: SignInWithEmailUseCase

	val signInWithExternalProviderUseCase: SignInWithExternalProviderUseCase

	val signInWithGoogleUseCase: SignInWithGoogleUseCase

	val signOutUseCase: SignOutUseCase

	interface WasmAppGraph : DataNetworkModule, FavoritesDataModule, RecipeDetailsDataModule, SearchDataModule {

		val addFavoriteRecipeUseCase: AddFavoriteRecipeUseCase

		val getFavoriteRecipesUseCase: GetFavoriteRecipesUseCase

		val getRecipeDetailsUseCase: GetRecipeDetailsUseCase

		val removeFavoriteRecipeUseCase: RemoveFavoriteRecipeUseCase

		val searchRecipesUseCase: SearchRecipesUseCase
	}
}
