package app.purecipes.shared.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import app.purecipes.shared.domain.model.IngredientNutritionLine
import app.purecipes.shared.domain.model.NutritionCalculationSource
import app.purecipes.shared.domain.model.NutritionSummary
import app.purecipes.shared.domain.model.RecipeNutrition
import app.purecipes.shared.domain.model.hasMacroNutrients
import app.purecipes.shared.ui.theme.PurecipesTheme
import kotlin.math.roundToInt

const val NUTRITION_FACTS_DIALOG_TAG = "nutritionFactsDialog"
const val NUTRITION_FACTS_BUTTON_TAG = "nutritionFactsButton"

@Immutable
internal data class IngredientNutritionLines(
	val items: List<IngredientNutritionLine>,
)

private enum class NutritionFactsBasis {
	RECIPE_TOTAL,
	PER_SERVING,
	PER_100_GRAMS,
}

@Composable
fun NutritionFactsDialog(
	nutrition: RecipeNutrition,
	onDismiss: () -> Unit,
	modifier: Modifier = Modifier,
) {
	var showIngredients by remember { mutableStateOf(false) }
	var basis by remember {
		mutableStateOf(
			when {
				nutrition.perServing?.hasMacroNutrients() == true -> NutritionFactsBasis.PER_SERVING
				else -> NutritionFactsBasis.RECIPE_TOTAL
			},
		)
	}

	Dialog(onDismissRequest = onDismiss) {
		Card(
			modifier = modifier
				.fillMaxWidth()
				.testTag(NUTRITION_FACTS_DIALOG_TAG),
			colors = CardDefaults.cardColors(containerColor = PurecipesTheme.colorScheme.surface),
		) {
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.padding(PurecipesTheme.space.m)
					.verticalScroll(rememberScrollState()),
				verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
			) {
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.SpaceBetween,
					verticalAlignment = Alignment.CenterVertically,
				) {
					Text(
						text = if (showIngredients) {
							"Nutrition by ingredient"
						} else {
							"Nutrition Facts"
						},
						style = PurecipesTheme.typography.headlineMedium,
						fontWeight = FontWeight.Bold,
					)
					IconButton(onClick = onDismiss) {
						Icon(
							imageVector = Icons.Filled.Close,
							contentDescription = "Close nutrition facts",
						)
					}
				}

				TextButton(
					onClick = { showIngredients = !showIngredients },
					modifier = Modifier.align(Alignment.Start),
				) {
					Text(
						text = if (showIngredients) {
							"Show recipe totals"
						} else {
							"Show by ingredient"
						},
					)
				}

				if (showIngredients) {
					IngredientNutritionList(ingredients = IngredientNutritionLines(nutrition.ingredients))
				} else {
					NutritionFactsBasisSelector(
						selectedBasis = basis,
						onBasisChange = { selected -> basis = selected },
						hasPerServing = nutrition.perServing?.hasMacroNutrients() == true,
						hasPer100Grams = nutrition.per100Grams?.hasMacroNutrients() == true,
					)

					val summary = nutrition.summaryForBasis(basis)
					NutritionFactsLabelContent(
						summary = summary,
						coverageText = nutrition.recipeTotals.coverageText(),
						servingDescription = when (basis) {
							NutritionFactsBasis.PER_SERVING -> nutrition.servingDescription
							else -> null
						},
						basisLabel = basis.displayLabel(),
					)
				}
			}
		}
	}
}

@Composable
private fun NutritionFactsBasisSelector(
	selectedBasis: NutritionFactsBasis,
	onBasisChange: (NutritionFactsBasis) -> Unit,
	hasPerServing: Boolean,
	hasPer100Grams: Boolean,
) {
	Row(
		horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
	) {
		FilterChip(
			selected = selectedBasis == NutritionFactsBasis.RECIPE_TOTAL,
			onClick = { onBasisChange(NutritionFactsBasis.RECIPE_TOTAL) },
			label = { Text(text = "Recipe") },
		)
		FilterChip(
			selected = selectedBasis == NutritionFactsBasis.PER_SERVING,
			onClick = { onBasisChange(NutritionFactsBasis.PER_SERVING) },
			enabled = hasPerServing,
			label = { Text(text = "Per serving") },
		)
		FilterChip(
			selected = selectedBasis == NutritionFactsBasis.PER_100_GRAMS,
			onClick = { onBasisChange(NutritionFactsBasis.PER_100_GRAMS) },
			enabled = hasPer100Grams,
			label = { Text(text = "Per 100 g") },
		)
	}
}

@Composable
private fun NutritionFactsLabelContent(
	summary: NutritionSummary,
	coverageText: String?,
	servingDescription: String?,
	basisLabel: String,
) {
	Column(
		modifier = Modifier.fillMaxWidth(),
		verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
	) {
		Text(
			text = basisLabel,
			style = PurecipesTheme.typography.labelLarge,
			color = PurecipesTheme.colorScheme.onSurfaceVariant,
		)
		servingDescription?.let { description ->
			Text(
				text = description,
				style = PurecipesTheme.typography.bodySmall,
				color = PurecipesTheme.colorScheme.onSurfaceVariant,
			)
		}
		coverageText?.let { coverage ->
			Text(
				text = coverage,
				style = PurecipesTheme.typography.bodySmall,
				color = PurecipesTheme.colorScheme.onSurfaceVariant,
			)
		}

		NutritionFactsThickDivider()

		summary.calories?.let { calories ->
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.Bottom,
			) {
				Text(
					text = "Calories",
					style = PurecipesTheme.typography.headlineSmall,
					fontWeight = FontWeight.Bold,
				)
				Text(
					text = "${calories.roundToDisplayInt()}",
					style = PurecipesTheme.typography.headlineSmall,
					fontWeight = FontWeight.Bold,
				)
			}
		}

		NutritionFactsThickDivider()

		NutritionFactsMacroRows(summary = summary)
	}
}

@Composable
private fun IngredientNutritionList(ingredients: IngredientNutritionLines) {
	if (ingredients.items.isEmpty()) {
		Text(
			text = "No ingredient breakdown is stored for this recipe yet.",
			style = PurecipesTheme.typography.bodyMedium,
			color = PurecipesTheme.colorScheme.onSurfaceVariant,
		)
		return
	}

	Column(verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m)) {
		ingredients.items.forEach { line ->
			IngredientNutritionCard(line = line)
		}
	}
}

@Composable
private fun IngredientNutritionCard(line: IngredientNutritionLine) {
	Column(
		modifier = Modifier.fillMaxWidth(),
		verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs),
	) {
		Text(
			text = line.displayName(),
			style = PurecipesTheme.typography.titleSmall,
			fontWeight = FontWeight.SemiBold,
		)
		line.foodDisplayName?.let { foodName ->
			Text(
				text = "Matched: $foodName",
				style = PurecipesTheme.typography.bodySmall,
				color = PurecipesTheme.colorScheme.onSurfaceVariant,
			)
		}
		line.grams?.let { grams ->
			Text(
				text = "${grams.roundToDisplay()} g estimated",
				style = PurecipesTheme.typography.bodySmall,
				color = PurecipesTheme.colorScheme.onSurfaceVariant,
			)
		}
		if (!line.isMatched) {
			Text(
				text = "Not matched to a food item yet",
				style = PurecipesTheme.typography.bodySmall,
				color = PurecipesTheme.colorScheme.onSurfaceVariant,
			)
		} else if (line.contribution.hasMacroNutrients()) {
			NutritionFactsMacroRows(summary = line.contribution)
		} else {
			line.per100Grams?.let { per100 ->
				Text(
					text = "Per 100 g reference",
					style = PurecipesTheme.typography.labelMedium,
				)
				NutritionFactsMacroRows(summary = per100)
			}
		}
		NutritionFactsThinDivider()
	}
}

@Composable
private fun NutritionFactsMacroRows(summary: NutritionSummary) {
	Column(verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs)) {
		summary.protein?.let { protein ->
			NutritionFactsMacroRow(label = "Protein", value = "${protein.roundToDisplay()} g")
		}
		summary.carbohydrates?.let { carbohydrates ->
			NutritionFactsMacroRow(label = "Carbohydrates", value = "${carbohydrates.roundToDisplay()} g")
		}
		summary.fat?.let { fat ->
			NutritionFactsMacroRow(label = "Fat", value = "${fat.roundToDisplay()} g")
		}
		summary.fiber?.let { fiber ->
			NutritionFactsMacroRow(label = "Fibre", value = "${fiber.roundToDisplay()} g")
		}
		summary.sugar?.let { sugar ->
			NutritionFactsMacroRow(label = "Sugar", value = "${sugar.roundToDisplay()} g")
		}
		summary.sodium?.let { sodium ->
			NutritionFactsMacroRow(label = "Sodium", value = "${sodium.roundToDisplay()} mg")
		}
	}
}

@Composable
private fun NutritionFactsMacroRow(label: String, value: String) {
	Column(modifier = Modifier.fillMaxWidth()) {
		NutritionFactsThinDivider()
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween,
		) {
			Text(
				text = label,
				style = PurecipesTheme.typography.bodySmall,
			)
			Text(
				text = value,
				style = PurecipesTheme.typography.bodySmall,
			)
		}
	}
}

@Composable
private fun NutritionFactsThickDivider() {
	HorizontalDivider(
		modifier = Modifier
			.fillMaxWidth()
			.height(NUTRITION_FACTS_THICK_DIVIDER_DP.dp),
		thickness = NUTRITION_FACTS_THICK_DIVIDER_DP.dp,
		color = PurecipesTheme.colorScheme.onSurface,
	)
}

@Composable
private fun NutritionFactsThinDivider() {
	HorizontalDivider(
		modifier = Modifier.fillMaxWidth(),
		thickness = NUTRITION_FACTS_THIN_DIVIDER_DP.dp,
		color = PurecipesTheme.colorScheme.outlineVariant,
	)
}

private fun RecipeNutrition.summaryForBasis(basis: NutritionFactsBasis): NutritionSummary =
	when (basis) {
		NutritionFactsBasis.RECIPE_TOTAL -> recipeTotals
		NutritionFactsBasis.PER_SERVING -> perServing ?: recipeTotals
		NutritionFactsBasis.PER_100_GRAMS -> per100Grams ?: recipeTotals
	}

private fun NutritionFactsBasis.displayLabel(): String =
	when (this) {
		NutritionFactsBasis.RECIPE_TOTAL -> "Amount per recipe"
		NutritionFactsBasis.PER_SERVING -> "Amount per serving"
		NutritionFactsBasis.PER_100_GRAMS -> "Amount per 100 g"
	}

private fun IngredientNutritionLine.displayName(): String =
	parsedName?.takeIf { name -> name.isNotBlank() } ?: rawText

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
private const val NUTRITION_FACTS_THICK_DIVIDER_DP = 4
private const val NUTRITION_FACTS_THIN_DIVIDER_DP = 1
