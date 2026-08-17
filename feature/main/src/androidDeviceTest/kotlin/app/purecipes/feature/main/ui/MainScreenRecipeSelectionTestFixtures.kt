package app.purecipes.feature.main.ui

import androidx.lifecycle.ViewModel
import app.purecipes.feature.ads.domain.usecase.ObserveShouldShowAdsUseCase
import app.purecipes.feature.ads.ui.BannerAdViewModel
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.recipedetails.ui.RecipeDetailsViewModel
import app.purecipes.feature.search.ui.RecipeSearchViewModel
import app.purecipes.feature.subscription.domain.model.SubscriptionState
import app.purecipes.feature.subscription.domain.model.SubscriptionStatus
import app.purecipes.feature.subscription.domain.usecase.ObservePremiumStatusUseCase
import app.purecipes.shared.data.config.PurecipesBuildType
import app.purecipes.shared.data.config.PurecipesConfig
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import app.purecipes.shared.testfixtures.fake.FakeMonetisationDebugOverridesRepository
import app.purecipes.shared.testfixtures.fake.FakeRecipeDetailsRepository
import app.purecipes.shared.testfixtures.fake.FakeRecipeSearchRepository
import app.purecipes.shared.testfixtures.fake.FakeSubscriptionRepository
import app.purecipes.shared.testfixtures.fake.fakeRecipeDetails
import com.github.michaelbull.result.Ok
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory
import kotlin.reflect.KClass

internal const val RECIPE_SELECTION_TEST_FIRST_RECIPE_ID = 7

internal const val RECIPE_SELECTION_TEST_SECOND_RECIPE_ID = 12

internal const val RECIPE_SELECTION_TEST_FIRST_RECIPE_TITLE = "Roasted Carrots"

internal const val RECIPE_SELECTION_TEST_SECOND_RECIPE_TITLE = "Grilled Salmon"

internal const val RECIPE_SELECTION_TEST_FIRST_RECIPE_DESCRIPTION = "Sweet and savory side dish."

internal const val RECIPE_SELECTION_TEST_SECOND_RECIPE_DESCRIPTION = "Light and flaky main course."

internal data class RecipeSelectionTestEnvironment(
	val mainViewModel: MainViewModel,
	val searchViewModel: RecipeSearchViewModel,
	val recipeDetailsRepository: FakeRecipeDetailsRepository,
	val metroViewModelFactory: MetroViewModelFactory,
)

internal fun recipeSelectionTestEnvironment(): RecipeSelectionTestEnvironment {
	val recipeDetailsRepository = FakeRecipeDetailsRepository(
		recipesById = mapOf(
			RECIPE_SELECTION_TEST_FIRST_RECIPE_ID to Ok(
				fakeRecipeDetails(
					id = RECIPE_SELECTION_TEST_FIRST_RECIPE_ID,
					title = RECIPE_SELECTION_TEST_FIRST_RECIPE_TITLE,
					description = RECIPE_SELECTION_TEST_FIRST_RECIPE_DESCRIPTION,
				),
			),
			RECIPE_SELECTION_TEST_SECOND_RECIPE_ID to Ok(
				fakeRecipeDetails(
					id = RECIPE_SELECTION_TEST_SECOND_RECIPE_ID,
					title = RECIPE_SELECTION_TEST_SECOND_RECIPE_TITLE,
					description = RECIPE_SELECTION_TEST_SECOND_RECIPE_DESCRIPTION,
				),
			),
		),
	)
	val metroViewModelFactory = RecipeSelectionTestViewModelFactory(recipeDetailsRepository)
	return RecipeSelectionTestEnvironment(
		mainViewModel = mainViewModelForDeviceTest(),
		searchViewModel = recipeSearchViewModelForDeviceTest(
			searchRepository = FakeRecipeSearchRepository(
				result = Ok(
					listOf(
						RecipeSummary(
							id = RECIPE_SELECTION_TEST_FIRST_RECIPE_ID,
							title = RECIPE_SELECTION_TEST_FIRST_RECIPE_TITLE,
							cuisine = Cuisine.MEDITERRANEAN,
							imageUrl = null,
							totalTime = 35,
						),
						RecipeSummary(
							id = RECIPE_SELECTION_TEST_SECOND_RECIPE_ID,
							title = RECIPE_SELECTION_TEST_SECOND_RECIPE_TITLE,
							cuisine = Cuisine.MEDITERRANEAN,
							imageUrl = null,
							totalTime = 25,
						),
					),
				),
			),
		),
		recipeDetailsRepository = recipeDetailsRepository,
		metroViewModelFactory = metroViewModelFactory,
	)
}

private class RecipeSelectionTestViewModelFactory(
	private val recipeDetailsRepository: FakeRecipeDetailsRepository,
) : MetroViewModelFactory() {

	override val manualAssistedFactoryProviders:
		Map<KClass<out ManualViewModelAssistedFactory>, () -> ManualViewModelAssistedFactory> =
		mapOf(
			RecipeDetailsViewModel.Factory::class to {
				object : RecipeDetailsViewModel.Factory {
					override fun create(
						recipeId: Int,
						sessionKey: String?,
						origin: String,
					): RecipeDetailsViewModel =
						recipeDetailsViewModelForDeviceTest(
							recipeId = recipeId,
							recipeDetailsRepository = recipeDetailsRepository,
							sessionKey = sessionKey,
						)
				}
			},
		)

	override val viewModelProviders: Map<KClass<out ViewModel>, () -> ViewModel> = mapOf(
		BannerAdViewModel::class to {
			val overrides = FakeMonetisationDebugOverridesRepository()
			BannerAdViewModel(
				observeShouldShowAds = ObserveShouldShowAdsUseCase(
					observePremiumStatus = ObservePremiumStatusUseCase(
						repository = FakeSubscriptionRepository(
							SubscriptionState(
								status = SubscriptionStatus.PREMIUM,
								isActive = true,
								expirationInstant = null,
								trialActive = false,
							),
						),
						monetisationDebugOverrides = overrides,
					),
					monetisationDebugOverrides = overrides,
				),
				purecipesConfig = object : PurecipesConfig {
					override fun buildType(): PurecipesBuildType = PurecipesBuildType.DEBUG

					override fun versionName(): String = "0.0.0-test"

					override fun versionCode(): Long = 0L
				},
				trackEvent = TrackEventUseCase(FakeAnalyticsRepository()),
			)
		},
	)
}
