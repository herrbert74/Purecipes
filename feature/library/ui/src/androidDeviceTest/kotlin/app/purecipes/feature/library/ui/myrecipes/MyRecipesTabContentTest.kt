package app.purecipes.feature.library.ui.myrecipes

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.ui.component.RECIPE_CARD_DELETE_BUTTON_TAG_PREFIX
import app.purecipes.shared.ui.component.RECIPE_CARD_EDIT_BUTTON_TAG_PREFIX
import app.purecipes.shared.ui.theme.PurecipesTheme
import dejavu.runRecompositionTrackingUiTest
import dejavu.setTrackedContent
import kotlinx.collections.immutable.persistentListOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class MyRecipesTabContentTest {

	@Test
	fun recipeCardClickSelectsRecipeAndEditButtonOpensEditor() = runRecompositionTrackingUiTest {
		var selectedId: Int? = null
		var editedId: Int? = null
		setTrackedContent {
			PurecipesTheme {
				MyRecipesContent(
					isLoading = false,
					errorMessage = null,
					recipes = persistentListOf(sampleRecipe()),
					onCreateRecipe = {},
					onRecipeSelect = { selectedId = it },
					onEditRecipe = { editedId = it },
					onDeleteRecipe = {},
					onRetry = {},
				)
			}
		}

		onNodeWithText(RECIPE_TITLE).assertIsDisplayed()
		onNodeWithTag("$RECIPE_CARD_EDIT_BUTTON_TAG_PREFIX$RECIPE_ID").performClick()
		assertEquals(RECIPE_ID, editedId)
		assertNull(selectedId)

		onNodeWithText(RECIPE_TITLE).performClick()
		assertEquals(RECIPE_ID, selectedId)
	}

	@Test
	fun deleteRequiresConfirmationAndInvokesDeleteCallback() = runRecompositionTrackingUiTest {
		var deletedRecipe: RecipeSummary? = null
		setTrackedContent {
			PurecipesTheme {
				MyRecipesContent(
					isLoading = false,
					errorMessage = null,
					recipes = persistentListOf(sampleRecipe()),
					onCreateRecipe = {},
					onRecipeSelect = {},
					onEditRecipe = {},
					onDeleteRecipe = { deletedRecipe = it },
					onRetry = {},
				)
			}
		}

		onNodeWithTag("$RECIPE_CARD_DELETE_BUTTON_TAG_PREFIX$RECIPE_ID").performClick()
		onNodeWithText("Delete recipe?", useUnmergedTree = true).assertIsDisplayed()
		onNodeWithTag(DELETE_CREATED_RECIPE_DIALOG_CONFIRM_TAG, useUnmergedTree = true).performClick()

		assertEquals(RECIPE_ID, deletedRecipe?.id)
		assertEquals(RECIPE_TITLE, deletedRecipe?.title)
	}

	private fun sampleRecipe(): RecipeSummary = RecipeSummary(
		id = RECIPE_ID,
		title = RECIPE_TITLE,
		cuisine = Cuisine.ITALIAN,
		imageUrl = null,
		totalTime = 25,
	)

	private companion object {

		const val RECIPE_ID = 42
		const val RECIPE_TITLE = "Tomato Pasta"
	}
}
