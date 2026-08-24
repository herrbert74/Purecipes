package app.purecipes.feature.library.ui.cookbooks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import app.purecipes.shared.domain.model.CookbookSummary
import app.purecipes.shared.ui.component.EmptyStateContent
import app.purecipes.shared.ui.component.ErrorText
import app.purecipes.shared.ui.component.PurecipesButton
import app.purecipes.shared.ui.component.paging.PaginatedLazyVerticalGrid
import app.purecipes.shared.ui.component.paging.PaginationState
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
internal fun CookbooksTabContent(
	cookbooks: SnapshotStateList<CookbookSummary>,
	cookbooksErrorMessage: String?,
	paginationState: PaginationState<Int, CookbookSummary>,
	totalMatches: Int,
	getCookbookCoverUrl: (Int) -> String?,
	onRequestCookbookCover: (Int) -> Unit,
	onCreateClick: () -> Unit,
	deleteCookbookError: String?,
	isDeletingCookbook: Boolean,
	onDeleteCookbook: (CookbookSummary, (Boolean) -> Unit) -> Unit,
	onCookbookClick: (CookbookSummary) -> Unit,
	modifier: Modifier = Modifier,
) {
	when {
		cookbooksErrorMessage != null -> Box(
			modifier = modifier
				.fillMaxSize()
				.padding(PurecipesTheme.space.l),
			contentAlignment = Alignment.Center,
		) {
			ErrorText(text = cookbooksErrorMessage, textAlign = TextAlign.Center)
		}

		cookbooks.isEmpty() && totalMatches == 0 -> EmptyStateContent(
			icon = Icons.AutoMirrored.Filled.MenuBook,
			iconContentDescription = "Cookbooks",
			title = "No cookbooks yet",
			description = "Organize saved recipes into cookbooks.",
			modifier = modifier,
			action = {
				PurecipesButton(
					text = "Create new cookbook",
					onClick = onCreateClick,
				)
			},
		)

		else -> Column(modifier = modifier.fillMaxSize()) {
			deleteCookbookError?.let { message ->
				ErrorText(
					text = message,
					textAlign = TextAlign.Center,
					modifier = Modifier
						.fillMaxWidth()
						.padding(horizontal = PurecipesTheme.space.m, vertical = PurecipesTheme.space.s),
				)
			}
			TextButton(
				onClick = onCreateClick,
				modifier = Modifier
					.fillMaxWidth()
					.padding(horizontal = PurecipesTheme.space.m, vertical = PurecipesTheme.space.s),
			) {
				Text(text = "Create new cookbook")
			}
			PaginatedLazyVerticalGrid(
				paginationState = paginationState,
				modifier = Modifier.weight(1f),
				verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
				horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
				contentPadding = PaddingValues(
					start = PurecipesTheme.space.m,
					end = PurecipesTheme.space.m,
					bottom = PurecipesTheme.space.m,
				),
			) {
				items(cookbooks.size, key = { cookbooks[it].id }) { index ->
					val cookbook = cookbooks[index]
					CookbookRow(
						cookbook = cookbook,
						coverUrl = getCookbookCoverUrl(cookbook.id),
						onRequestCookbookCover = { onRequestCookbookCover(cookbook.id) },
						isDeletingCookbook = isDeletingCookbook,
						onDeleteCookbook = { onDeleteCookbook(cookbook) {} },
						onClick = { onCookbookClick(cookbook) },
					)
				}
			}
		}
	}
}
