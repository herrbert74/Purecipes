import app.purecipes.backend.tools.ScrapedIngredientGroup
import app.purecipes.backend.tools.ScrapedIngredientGroupProcessor
import app.purecipes.backend.tools.ScrapedIngredientItem

internal class ScrapedIngredientLinesBridge : ScrapedIngredientGroupProcessor {

	override fun processGroup(
		groupName: String?,
		storedItems: List<ScrapedIngredientItem>,
	): List<ScrapedIngredientGroup> {
		val processed = processStoredScrapedIngredientGroups(
			groupName = groupName,
			storedItems = storedItems.map { item ->
				ProcessedScrapedIngredient(
					text = item.text,
					requirement = item.requirement,
					alternativeGroupKey = item.alternativeGroupKey,
				)
			},
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
}
