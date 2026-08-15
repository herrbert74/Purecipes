package app.purecipes.feature.library.ui.cookbooks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.purecipes.feature.ads.ui.BannerAdViewModel
import app.purecipes.feature.ads.ui.InlineListBannerAdSlot
import app.purecipes.feature.sharing.ui.ShareIconButton
import app.purecipes.shared.domain.model.RecipeSummary
import app.purecipes.shared.ui.component.ErrorText
import app.purecipes.shared.ui.component.RecipeCard
import app.purecipes.shared.ui.component.paging.PaginatedLazyColumn
import app.purecipes.shared.ui.component.paging.PaginationState
import app.purecipes.shared.ui.theme.PurecipesTheme
import coil3.compose.AsyncImage

@Composable
internal fun CookbookDetailContent(
	title: String,
	errorMessage: String?,
	recipes: SnapshotStateList<RecipeSummary>,
	paginationState: PaginationState<Int, RecipeSummary>,
	totalMatches: Int,
	coverUrl: String?,
	onBack: () -> Unit,
	onShare: () -> Unit,
	onRecipeSelect: (Int) -> Unit,
	modifier: Modifier = Modifier,
	bannerAdViewModel: BannerAdViewModel? = null,
) {
	Column(modifier = modifier.fillMaxSize()) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
		) {
			IconButton(onClick = onBack) {
				Icon(
					imageVector = Icons.AutoMirrored.Filled.ArrowBack,
					contentDescription = "Back",
				)
			}
			AsyncImage(
				model = coverUrl?.trim()?.takeIf { it.isNotEmpty() },
				contentDescription = title,
				modifier = Modifier
					.size(48.dp)
					.clip(RoundedCornerShape(PurecipesTheme.space.s))
					.background(PurecipesTheme.colorScheme.secondaryContainer),
				contentScale = ContentScale.Crop,
			)
			Text(
				text = title,
				style = MaterialTheme.typography.titleLarge,
				modifier = Modifier.weight(1f),
			)
			ShareIconButton(
				onShare = onShare,
				contentDescription = "Share cookbook",
			)
		}
		when {
			errorMessage != null -> Box(
				modifier = Modifier
					.fillMaxSize()
					.padding(PurecipesTheme.space.l),
				contentAlignment = Alignment.Center,
			) {
				ErrorText(text = errorMessage, textAlign = TextAlign.Center)
			}

			else -> PaginatedLazyColumn(
				paginationState = paginationState,
				modifier = Modifier.fillMaxSize(),
				verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
				contentPadding = PaddingValues(PurecipesTheme.space.m),
			) {
				item {
					Text(
						text = "$totalMatches recipes",
						style = PurecipesTheme.typography.labelMedium,
						color = PurecipesTheme.colorScheme.onSurfaceVariant,
					)
				}
				items(recipes.size, key = { recipes[it].id }) { index ->
					val recipe = recipes[index]
					InlineListBannerAdSlot(
						contentIndex = index,
						contentCount = recipes.size,
						viewModel = bannerAdViewModel,
					) {
						RecipeCard(
							recipe = recipe,
							onClick = { onRecipeSelect(recipe.id) },
						)
					}
				}
			}
		}
	}
}
