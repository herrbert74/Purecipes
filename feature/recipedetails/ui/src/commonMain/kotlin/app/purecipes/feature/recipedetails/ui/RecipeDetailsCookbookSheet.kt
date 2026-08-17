package app.purecipes.feature.recipedetails.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import app.purecipes.feature.library.domain.CookbookNameSuggestions
import app.purecipes.shared.ui.component.ErrorText
import app.purecipes.shared.ui.theme.PurecipesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RecipeDetailsCookbookSheet(
	showSheet: Boolean,
	sheetCookbooks: RecipeSheetCookbooksList,
	cookbookActionError: String?,
	isCookbookActionInFlight: Boolean,
	newCookbookName: String,
	onNewCookbookNameChange: (String) -> Unit,
	onDismiss: () -> Unit,
	onAddToCookbook: (cookbookId: Int, onComplete: (String?) -> Unit) -> Unit,
	onCreateCookbookAndAdd: (name: String, onComplete: (String?) -> Unit) -> Unit,
) {
	if (!showSheet) {
		return
	}

	val existingCookbookNamesNormalized = remember(sheetCookbooks) {
		sheetCookbooks.items
			.map { it.name.trim().lowercase() }
			.toSet()
	}
	val suggestionNames = remember(existingCookbookNamesNormalized) {
		CookbookNameSuggestions.values.filter { suggestion ->
			suggestion.trim().lowercase() !in existingCookbookNamesNormalized
		}
	}
	ModalBottomSheet(onDismissRequest = onDismiss) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(PurecipesTheme.space.m),
			verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
		) {
			Text(
				text = "Add to cookbook",
				style = PurecipesTheme.typography.titleMedium,
			)
			LazyRow(horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s)) {
				items(suggestionNames, key = { it }) { suggestion ->
					FilterChip(
						selected = false,
						onClick = { onNewCookbookNameChange(suggestion) },
						label = { Text(text = suggestion) },
					)
				}
			}
			OutlinedTextField(
				value = newCookbookName,
				onValueChange = onNewCookbookNameChange,
				modifier = Modifier.fillMaxWidth(),
				label = { Text(text = "New cookbook name") },
				singleLine = true,
			)
			sheetCookbooks.items.forEach { cookbook ->
				TextButton(
					onClick = {
						onAddToCookbook(cookbook.id) { err ->
							if (err == null) {
								onDismiss()
							}
						}
					},
					enabled = !isCookbookActionInFlight,
				) {
					Text(text = cookbook.name)
				}
			}
			cookbookActionError?.let { ErrorText(text = it) }
			Button(
				onClick = {
					onCreateCookbookAndAdd(newCookbookName) { err ->
						if (err == null) {
							onDismiss()
						}
					}
				},
				enabled = !isCookbookActionInFlight && newCookbookName.trim().isNotEmpty(),
			) {
				Text(text = "Create and add")
			}
		}
	}
}
