package app.purecipes.shared.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import app.purecipes.shared.domain.model.NutritionCalculationSource
import app.purecipes.shared.domain.model.NutritionSummary
import app.purecipes.shared.ui.theme.PurecipesTheme
import kotlin.math.roundToInt

const val NUTRITION_SUMMARY_CARD_TAG = "nutritionSummaryCard"

@Composable
fun NutritionSummaryCard(
	nutrition: NutritionSummary?,
	isLoading: Boolean,
	modifier: Modifier = Modifier,
) {
	Card(
		modifier = modifier
			.fillMaxWidth()
			.testTag(NUTRITION_SUMMARY_CARD_TAG),
		colors = CardDefaults.cardColors(containerColor = PurecipesTheme.colorScheme.surfaceContainerLow),
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(PurecipesTheme.space.m),
			verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
		) {
			Text(
				text = "Nutrition estimate",
				style = PurecipesTheme.typography.titleMedium,
			)

			when {
				isLoading ->
					CircularProgressIndicator(
						strokeWidth = PurecipesTheme.space.quark,
					)
				nutrition == null || !nutrition.hasDisplayableNutrients() ->
					Text(
						text = "Add measurable ingredients with amounts and units to see an estimate.",
						style = PurecipesTheme.typography.bodyMedium,
						color = PurecipesTheme.colorScheme.onSurfaceVariant,
					)
				else -> {
					nutrition.coverageText()?.let { coverage ->
						Text(
							text = coverage,
							style = PurecipesTheme.typography.bodyMedium,
							color = PurecipesTheme.colorScheme.onSurfaceVariant,
						)
					}
					NutritionSummaryNutrientRows(nutrition = nutrition)
				}
			}
		}
	}
}

@Composable
private fun NutritionSummaryNutrientRows(nutrition: NutritionSummary) {
	Column(verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs)) {
		nutrition.calories?.let { calories ->
			NutritionSummaryRow(label = "Calories", value = "${calories.roundToDisplayInt()} kcal")
		}
		nutrition.protein?.let { protein ->
			NutritionSummaryRow(label = "Protein", value = "${protein.roundToDisplay()} g")
		}
		nutrition.carbohydrates?.let { carbohydrates ->
			NutritionSummaryRow(label = "Carbohydrates", value = "${carbohydrates.roundToDisplay()} g")
		}
		nutrition.fat?.let { fat ->
			NutritionSummaryRow(label = "Fat", value = "${fat.roundToDisplay()} g")
		}
		nutrition.fiber?.let { fiber ->
			NutritionSummaryRow(label = "Fibre", value = "${fiber.roundToDisplay()} g")
		}
		nutrition.sugar?.let { sugar ->
			NutritionSummaryRow(label = "Sugar", value = "${sugar.roundToDisplay()} g")
		}
		nutrition.sodium?.let { sodium ->
			NutritionSummaryRow(label = "Sodium", value = "${sodium.roundToDisplay()} mg")
		}
	}
}

@Composable
private fun NutritionSummaryRow(label: String, value: String) {
	Text(
		text = "$label: $value",
		style = PurecipesTheme.typography.bodyLarge,
	)
}

private fun NutritionSummary.hasDisplayableNutrients(): Boolean {
	val totalCount = totalIngredientCount
	return listOf(
		calories,
		protein,
		carbohydrates,
		fat,
		fiber,
		sugar,
		sodium,
	).any { value -> value != null } ||
		(matchedIngredientCount != null && totalCount != null && totalCount > 0)
}

private fun NutritionSummary.coverageText(): String? {
	val matched = matchedIngredientCount
	val totalCount = totalIngredientCount
	if (matched == null || totalCount == null || totalCount <= 0) {
		return if (calculationSource == NutritionCalculationSource.SCRAPED) {
			"Imported nutrition values"
		} else {
			null
		}
	}

	return when {
		matched == 0 -> "No ingredients matched yet"
		isComplete -> "Estimated from all $totalCount ingredients"
		else -> "Estimated from $matched of $totalCount ingredients"
	}
}

private fun Double.roundToDisplay(): String {
	val rounded = (this * NUTRIENT_DISPLAY_SCALE).roundToInt() / NUTRIENT_DISPLAY_SCALE
	return if (rounded == rounded.toInt().toDouble()) {
		rounded.toInt().toString()
	} else {
		rounded.toString()
	}
}

private fun Double.roundToDisplayInt(): Int = roundToInt()

private const val NUTRIENT_DISPLAY_SCALE = 10.0
