package app.purecipes.backend.tools

import ProcessedScrapedIngredient
import RecipeHeadingContext
import ScrapedLineOverride
import app.purecipes.backend.feature.nutrition.IngredientLineOverride
import processRecipeScrapedIngredientGroups

internal class ScrapedIngredientLinesBridge : ScrapedIngredientGroupProcessor {

	override fun processGroup(
		groupName: String?,
		storedItems: List<ScrapedIngredientItem>,
	): List<ScrapedIngredientGroup> =
		processRecipeGroups(
			groups = listOf(ScrapedIngredientGroup(name = groupName, ingredients = storedItems)),
			context = ScrapedRecipeProcessingContext(),
		)

	override fun processRecipeGroups(
		groups: List<ScrapedIngredientGroup>,
		context: ScrapedRecipeProcessingContext,
	): List<ScrapedIngredientGroup> {
		val processed = processRecipeScrapedIngredientGroups(
			groups = groups.map { group ->
				group.name to group.ingredients.map { item ->
					ProcessedScrapedIngredient(
						text = item.text,
						requirement = item.requirement,
						alternativeGroupKey = item.alternativeGroupKey,
					)
				}
			},
			context = RecipeHeadingContext(
				recipeTitle = context.recipeTitle,
				recipeInstructions = context.recipeInstructions,
				lineOverridesByRawText = context.lineOverridesByRawText.mapValues { (_, override) ->
					override.toScrapedOverride()
				},
			),
		)
		return processed.map { (resolvedGroupName, items) ->
			ScrapedIngredientGroup(
				name = resolvedGroupName,
				ingredients = items.map { item ->
					ScrapedIngredientItem(
						text = item.text,
						requirement = item.requirement,
						alternativeGroupKey = item.alternativeGroupKey,
					)
				},
			)
		}
	}

	private fun IngredientLineOverride.toScrapedOverride(): ScrapedLineOverride =
		ScrapedLineOverride(
			kind = kind,
			groupName = groupName,
		)
}
