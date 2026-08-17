package app.purecipes.feature.newrecipe.ui

import app.purecipes.feature.analytics.domain.model.AnalyticsEvent
import app.purecipes.feature.analytics.domain.model.AnalyticsOrigin
import app.purecipes.feature.analytics.domain.model.AnalyticsPremiumFeature
import app.purecipes.feature.analytics.domain.usecase.LogBreadcrumbUseCase
import app.purecipes.feature.analytics.domain.usecase.SendHandledExceptionUseCase
import app.purecipes.feature.analytics.domain.usecase.TrackEventUseCase
import app.purecipes.feature.newrecipe.domain.usecase.EstimateRecipeNutritionUseCase
import app.purecipes.feature.newrecipe.domain.usecase.GetCreatedRecipesUseCase
import app.purecipes.feature.newrecipe.domain.usecase.SaveCreatedRecipeUseCase
import app.purecipes.feature.subscription.domain.model.SubscriptionState
import app.purecipes.feature.subscription.domain.model.SubscriptionStatus
import app.purecipes.feature.subscription.domain.usecase.ObservePremiumStatusUseCase
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.IngredientGroup
import app.purecipes.shared.domain.model.NutritionSummary
import app.purecipes.shared.domain.model.RecipeDetails
import app.purecipes.shared.testfixtures.fake.FakeAnalyticsRepository
import app.purecipes.shared.testfixtures.fake.FakeCrashRepository
import app.purecipes.shared.testfixtures.fake.FakeCreatedRecipeRepository
import app.purecipes.shared.testfixtures.fake.FakeMonetisationDebugOverridesRepository
import app.purecipes.shared.testfixtures.fake.FakeRecipeNutritionEstimateRepository
import app.purecipes.shared.testfixtures.fake.FakeSubscriptionRepository
import app.purecipes.shared.testfixtures.fake.recipeIngredients
import app.purecipes.shared.testfixtures.runViewModelTest
import com.github.michaelbull.result.Ok
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CreateRecipeViewModelTest {

	@Test
	fun `save validates required fields`() = runViewModelTest {
		val repository = FakeCreatedRecipeRepository()
		val viewModel = createViewModel(repository = repository)

		viewModel.saveRecipe()

		viewModel.formErrorMessage shouldBe "Add a recipe title."
		repository.savedRequests.isEmpty() shouldBe true
	}

	@Test
	fun `save uploads a new recipe and signals completion`() = runViewModelTest {
		val analyticsRepository = FakeAnalyticsRepository()
		val repository = FakeCreatedRecipeRepository()
		val viewModel = createViewModel(
			repository = repository,
			analyticsRepository = analyticsRepository,
		)

		viewModel.onTitleChange("Tomato Pasta")
		viewModel.onDescriptionChange("Quick weeknight dinner.")
		viewModel.ingredientsEditor.pasteLines("200 g pasta\n2 tomatoes")
		viewModel.onStepChange(index = 0, value = "Boil the pasta")
		viewModel.addStep()
		viewModel.onStepChange(index = 1, value = "Finish with the tomatoes")
		viewModel.saveRecipe()

		advanceUntilIdle()

		repository.savedRequests.size shouldBe 1
		viewModel.successMessage shouldBe "Recipe uploaded."
		viewModel.saveCompletedEvent shouldBe 1
		viewModel.formErrorMessage shouldBe null
		viewModel.isEditing shouldBe false
		val trackedEvent = analyticsRepository.trackedEvents.single() as AnalyticsEvent.RecipeSaved
		trackedEvent.isEditing shouldBe false
		trackedEvent.hasPhoto shouldBe false
		trackedEvent.ingredientCount shouldBe 2
		trackedEvent.stepCount shouldBe 2
		trackedEvent.recipeName shouldBe "Tomato Pasta"
		trackedEvent.isPrivate shouldBe false
	}

	@Test
	fun `onRecipeIdChanged does not reset a new recipe draft`() = runViewModelTest {
		val viewModel = createViewModel()

		viewModel.onTitleChange("Roasted Carrots")
		viewModel.onDescriptionChange("Sweet and savory side dish.")
		viewModel.onStepChange(index = 0, value = "Trim the carrots")
		viewModel.onRecipeIdChanged(recipeId = null)

		viewModel.titleInput shouldBe "Roasted Carrots"
		viewModel.descriptionInput shouldBe "Sweet and savory side dish."
		viewModel.stepInputs.toList() shouldBe listOf("Trim the carrots")
	}

	@Test
	fun `onRecipeIdChanged resets the form when leaving edit mode`() = runViewModelTest {
		val recipe = sampleCreatedRecipe()
		val viewModel = createViewModel(
			repository = FakeCreatedRecipeRepository(initialRecipes = listOf(recipe)),
		)

		viewModel.loadRecipe(recipe.id)
		advanceUntilIdle()
		viewModel.onRecipeIdChanged(recipeId = null)

		viewModel.isEditing shouldBe false
		viewModel.titleInput shouldBe ""
		viewModel.stepInputs.toList() shouldBe listOf("")
	}

	@Test
	fun `loading a recipe populates the form for editing`() = runViewModelTest {
		val recipe = sampleCreatedRecipe()
		val repository = FakeCreatedRecipeRepository(initialRecipes = listOf(recipe))
		val viewModel = createViewModel(repository = repository)

		viewModel.loadRecipe(recipe.id)
		advanceUntilIdle()

		viewModel.isEditing shouldBe true
		viewModel.titleInput shouldBe "Tomato Pasta"
		viewModel.stepInputs.toList() shouldBe listOf("Boil the pasta", "Finish with the tomatoes")
		viewModel.ingredientsEditor.toLines() shouldBe listOf("200 g pasta", "2 tomatoes")
	}

	@Test
	fun `editing a recipe updates the existing item`() = runViewModelTest {
		val recipe = sampleCreatedRecipe()
		val analyticsRepository = FakeAnalyticsRepository()
		val repository = FakeCreatedRecipeRepository(initialRecipes = listOf(recipe))
		val viewModel = createViewModel(
			repository = repository,
			analyticsRepository = analyticsRepository,
		)

		viewModel.loadRecipe(recipe.id)
		advanceUntilIdle()
		viewModel.onTitleChange("Creamy Tomato Pasta")
		viewModel.saveRecipe()
		advanceUntilIdle()

		repository.savedRequests.single().title shouldBe "Creamy Tomato Pasta"
		viewModel.successMessage shouldBe "Recipe updated."
		viewModel.saveCompletedEvent shouldBe 1
		analyticsRepository.trackedEvents.filterIsInstance<AnalyticsEvent.RecipeSaved>()
			.single().isPrivate shouldBe false
		analyticsRepository.trackedEvents.filterIsInstance<AnalyticsEvent.RecipePrivacyChanged>() shouldBe emptyList()
	}

	@Test
	fun `reopening a recipe after save does not keep the completion event`() = runViewModelTest {
		val recipe = sampleCreatedRecipe()
		val viewModel = createViewModel(
			repository = FakeCreatedRecipeRepository(initialRecipes = listOf(recipe)),
		)

		viewModel.loadRecipe(recipe.id)
		advanceUntilIdle()
		viewModel.onTitleChange("Creamy Tomato Pasta")
		viewModel.saveRecipe()
		advanceUntilIdle()
		viewModel.onRecipeIdChanged(recipe.id)

		viewModel.saveCompletedEvent shouldBe 0
		viewModel.isEditing shouldBe true
		viewModel.titleInput shouldBe "Creamy Tomato Pasta"
	}

	@Test
	fun `add step appends a new editable step`() = runViewModelTest {
		val repository = FakeCreatedRecipeRepository()
		val viewModel = createViewModel(repository = repository)

		viewModel.onStepChange(index = 0, value = "Boil the pasta")
		viewModel.addStep()
		viewModel.onStepChange(index = 1, value = "Finish with the tomatoes")

		viewModel.stepInputs.toList() shouldBe listOf("Boil the pasta", "Finish with the tomatoes")
	}

	@Test
	fun `move step up reorders the list`() = runViewModelTest {
		val repository = FakeCreatedRecipeRepository()
		val viewModel = createViewModel(repository = repository)

		viewModel.onStepChange(index = 0, value = "Boil the pasta")
		viewModel.addStep()
		viewModel.onStepChange(index = 1, value = "Finish with the tomatoes")
		viewModel.moveStepUp(index = 1)

		viewModel.stepInputs.toList() shouldBe listOf("Finish with the tomatoes", "Boil the pasta")
	}

	@Test
	fun `move step down reorders the list`() = runViewModelTest {
		val repository = FakeCreatedRecipeRepository()
		val viewModel = createViewModel(repository = repository)

		viewModel.onStepChange(index = 0, value = "Boil the pasta")
		viewModel.addStep()
		viewModel.onStepChange(index = 1, value = "Finish with the tomatoes")
		viewModel.moveStepDown(index = 0)

		viewModel.stepInputs.toList() shouldBe listOf("Finish with the tomatoes", "Boil the pasta")
	}

	@Test
	fun `ingredient changes request nutrition estimate`() = runViewModelTest {
		val estimateRepository = FakeRecipeNutritionEstimateRepository(
			estimateResult = Ok(
				NutritionSummary(
					calories = 120.0,
					matchedIngredientCount = 1,
					totalIngredientCount = 1,
				),
			),
		)
		val viewModel = createViewModel(estimateRepository = estimateRepository)

		viewModel.ingredientsEditor.pasteLines("1 cup sugar")
		viewModel.onIngredientsEdited()
		advanceTimeBy(500)
		advanceUntilIdle()

		estimateRepository.lastIngredients shouldBe listOf("1 cup sugar")
		viewModel.nutritionEstimate?.calories shouldBe 120.0
	}

	@Test
	fun `optional toggle and alternative compose through the parser`() = runViewModelTest {
		val repository = FakeCreatedRecipeRepository()
		val viewModel = createViewModel(repository = repository)

		viewModel.ingredientsEditor.onRowChange(
			index = 0,
			row = IngredientRowInput(
				primary = IngredientPartInput(name = "parsley"),
				isOptional = true,
				alternatives = listOf(IngredientPartInput(name = "tarragon")),
			),
		)
		viewModel.onTitleChange("Herb Side")
		viewModel.onDescriptionChange("Fresh herbs.")
		viewModel.onStepChange(index = 0, value = "Chop the herbs")
		viewModel.saveRecipe()
		advanceUntilIdle()

		repository.savedRequests.single().ingredients.size shouldBe 2
	}

	@Test
	fun `new recipes are public by default`() = runViewModelTest {
		val viewModel = createViewModel()

		advanceUntilIdle()

		viewModel.isPrivate shouldBe false
		viewModel.canMakePrivate shouldBe false
	}

	@Test
	fun `premium users can mark a new recipe private`() = runViewModelTest {
		val repository = FakeCreatedRecipeRepository()
		val analyticsRepository = FakeAnalyticsRepository()
		val viewModel = createViewModel(
			repository = repository,
			analyticsRepository = analyticsRepository,
			subscriptionState = premiumSubscriptionState(),
		)

		advanceUntilIdle()
		viewModel.onTitleChange("Secret Pasta")
		viewModel.onDescriptionChange("Keep this one to myself.")
		viewModel.onStepChange(index = 0, value = "Boil the pasta")
		viewModel.onIsPrivateChange(true)
		viewModel.saveRecipe()
		advanceUntilIdle()

		repository.savedRequests.single().isPrivate shouldBe true
		viewModel.isPrivate shouldBe true
		analyticsRepository.trackedEvents.filterIsInstance<AnalyticsEvent.RecipeSaved>()
			.single().isPrivate shouldBe true
		analyticsRepository.trackedEvents.filterIsInstance<AnalyticsEvent.RecipePrivacyChanged>().single() shouldBe
			AnalyticsEvent.RecipePrivacyChanged(
				recipeId = -1,
				recipeName = "Secret Pasta",
				isPrivate = true,
				isEditing = false,
			)
	}

	@Test
	fun `free users cannot mark a new recipe private`() = runViewModelTest {
		val analyticsRepository = FakeAnalyticsRepository()
		val viewModel = createViewModel(analyticsRepository = analyticsRepository)

		advanceUntilIdle()
		viewModel.onIsPrivateChange(true)

		viewModel.isPrivate shouldBe false
		viewModel.canMakePrivate shouldBe false
		analyticsRepository.trackedEvents.single() shouldBe AnalyticsEvent.PremiumFeatureBlocked(
			feature = AnalyticsPremiumFeature.PRIVATE_RECIPES,
			origin = AnalyticsOrigin.CREATE_RECIPE,
		)
	}

	@Test
	fun `making a private recipe public tracks privacy changed`() = runViewModelTest {
		val recipe = sampleCreatedRecipe().copy(isPrivate = true)
		val analyticsRepository = FakeAnalyticsRepository()
		val viewModel = createViewModel(
			repository = FakeCreatedRecipeRepository(initialRecipes = listOf(recipe)),
			analyticsRepository = analyticsRepository,
		)

		viewModel.loadRecipe(recipe.id)
		advanceUntilIdle()
		viewModel.onIsPrivateChange(false)
		viewModel.saveRecipe()
		advanceUntilIdle()

		analyticsRepository.trackedEvents.filterIsInstance<AnalyticsEvent.RecipeSaved>()
			.single().isPrivate shouldBe false
		analyticsRepository.trackedEvents.filterIsInstance<AnalyticsEvent.RecipePrivacyChanged>().single() shouldBe
			AnalyticsEvent.RecipePrivacyChanged(
				recipeId = recipe.id,
				recipeName = recipe.title,
				isPrivate = false,
				isEditing = true,
			)
	}

	@Test
	fun `loading a private recipe keeps it private for a lapsed premium user`() = runViewModelTest {
		val recipe = sampleCreatedRecipe().copy(isPrivate = true)
		val viewModel = createViewModel(
			repository = FakeCreatedRecipeRepository(initialRecipes = listOf(recipe)),
		)

		viewModel.loadRecipe(recipe.id)
		advanceUntilIdle()

		viewModel.isPrivate shouldBe true
		viewModel.canMakePrivate shouldBe true
	}
}

private fun createViewModel(
	repository: FakeCreatedRecipeRepository = FakeCreatedRecipeRepository(),
	estimateRepository: FakeRecipeNutritionEstimateRepository = FakeRecipeNutritionEstimateRepository(),
	analyticsRepository: FakeAnalyticsRepository = FakeAnalyticsRepository(),
	crashRepository: FakeCrashRepository = FakeCrashRepository(),
	subscriptionState: SubscriptionState = SubscriptionState.FREE,
): CreateRecipeViewModel =
	CreateRecipeViewModel(
		getCreatedRecipes = GetCreatedRecipesUseCase(repository),
		saveCreatedRecipe = SaveCreatedRecipeUseCase(repository),
		estimateRecipeNutrition = EstimateRecipeNutritionUseCase(estimateRepository),
		trackEvent = TrackEventUseCase(analyticsRepository),
		logBreadcrumb = LogBreadcrumbUseCase(crashRepository),
		sendHandledException = SendHandledExceptionUseCase(crashRepository),
		observePremiumStatus = ObservePremiumStatusUseCase(
			FakeSubscriptionRepository(initialState = subscriptionState),
			FakeMonetisationDebugOverridesRepository(),
		),
	)

private fun sampleCreatedRecipe(): RecipeDetails = RecipeDetails(
	id = -1,
	title = "Tomato Pasta",
	description = "Quick weeknight dinner.",
	imageUrl = null,
	ingredientGroups = listOf(
		IngredientGroup(
			ingredients = recipeIngredients("200 g pasta", "2 tomatoes"),
		),
	),
	steps = listOf("Boil the pasta", "Finish with the tomatoes"),
	totalTime = 20,
	yields = "2 servings",
	cuisine = Cuisine.ITALIAN,
)

private fun premiumSubscriptionState(): SubscriptionState = SubscriptionState(
	status = SubscriptionStatus.PREMIUM,
	isActive = true,
	expirationInstant = null,
	trialActive = false,
)
