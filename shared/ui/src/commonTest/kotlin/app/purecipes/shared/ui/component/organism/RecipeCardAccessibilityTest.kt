package app.purecipes.shared.ui.component.organism

import app.purecipes.shared.domain.model.Appliance
import app.purecipes.shared.domain.model.Cuisine
import app.purecipes.shared.domain.model.MeasurementSystem
import app.purecipes.shared.domain.model.RecipeSummary
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotContain
import kotlin.test.Test

class RecipeCardAccessibilityTest {

	@Test
	fun gridDescriptionIncludesLabelsTimeFavoriteAndAppliances() {
		val description = recipeCardContentDescription(
			recipe = RecipeSummary(
				id = 1,
				title = "Tomato Pasta",
				cuisine = Cuisine.ITALIAN,
				imageUrl = null,
				totalTime = 20,
				measurementSystem = MeasurementSystem.METRIC,
				isFavorite = true,
				isPrivate = true,
				appliances = setOf(Appliance.OVEN, Appliance.AIR_FRYER),
			),
			favoritedLabel = "Favorited",
			includeLabels = true,
		)

		description shouldBe "Tomato Pasta, Quick, Italian, Metric, Private, 20 min, Favorited, Oven, Air Fryer"
	}

	@Test
	fun shelfDescriptionOmitsChipLabels() {
		val description = recipeCardContentDescription(
			recipe = RecipeSummary(
				id = 1,
				title = "Tomato Pasta",
				cuisine = Cuisine.ITALIAN,
				imageUrl = null,
				totalTime = 20,
				measurementSystem = MeasurementSystem.METRIC,
				isFavorite = true,
				appliances = setOf(Appliance.OVEN),
			),
			favoritedLabel = "Favorited",
			includeLabels = false,
		)

		description shouldBe "Tomato Pasta, 20 min, Favorited, Oven"
	}

	@Test
	fun unknownPrepTimeIsDescribedWithoutQuickLabel() {
		val description = recipeCardContentDescription(
			recipe = RecipeSummary(
				id = 3,
				title = "Green salad",
				cuisine = Cuisine.FRENCH,
				imageUrl = null,
				totalTime = null,
			),
			favoritedLabel = "Favorited",
			includeLabels = true,
		)

		description shouldBe "Green salad, French, Prep time unknown"
	}

	@Test
	fun longRecipesAreNotLabeledQuickAndApplianceListIsCapped() {
		val description = recipeCardContentDescription(
			recipe = RecipeSummary(
				id = 2,
				title = "Smoked brisket",
				cuisine = Cuisine.AMERICAN,
				imageUrl = null,
				totalTime = 240,
				appliances = setOf(
					Appliance.OVEN,
					Appliance.AIR_FRYER,
					Appliance.BLENDER,
					Appliance.GRILL,
				),
			),
			favoritedLabel = "Favorited",
			includeLabels = true,
		)

		description shouldBe "Smoked brisket, American, 240 min, Oven, Air Fryer, Blender"
		description.shouldNotContain("Quick")
		description.shouldNotContain("Grill")
	}
}
