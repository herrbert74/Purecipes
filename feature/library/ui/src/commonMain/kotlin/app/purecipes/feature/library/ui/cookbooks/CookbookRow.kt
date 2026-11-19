package app.purecipes.feature.library.ui.cookbooks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import app.purecipes.shared.domain.model.CookbookSummary
import app.purecipes.shared.ui.component.atom.BodyText
import app.purecipes.shared.ui.component.atom.PurecipesTextButton
import app.purecipes.shared.ui.component.atom.TitleText
import app.purecipes.shared.ui.theme.PurecipesTheme
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.stringResource
import purecipes.shared.ui.generated.resources.Res
import purecipes.shared.ui.generated.resources.cookbook_recipe_count
import purecipes.shared.ui.generated.resources.delete
import purecipes.shared.ui.generated.resources.delete_cookbook

internal const val DELETE_COOKBOOK_BUTTON_PREFIX = "deleteCookbookButton:"

@Composable
internal fun CookbookRow(
	cookbook: CookbookSummary,
	coverUrl: String?,
	onRequestCookbookCover: () -> Unit,
	isDeletingCookbook: Boolean,
	onDeleteCookbook: () -> Unit,
	onClick: () -> Unit,
) {
	val requestCover by rememberUpdatedState(onRequestCookbookCover)
	LaunchedEffect(cookbook.id) {
		requestCover()
	}
	val deleteDescription = stringResource(Res.string.delete_cookbook, cookbook.name)
	Card(
		modifier = Modifier
			.fillMaxWidth()
			.clickable(onClick = onClick),
		colors = CardDefaults.cardColors(
			containerColor = PurecipesTheme.colorScheme.surfaceContainerLow,
		),
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(PurecipesTheme.space.s),
			horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.s),
			verticalAlignment = Alignment.CenterVertically,
		) {
			AsyncImage(
				model = coverUrl?.trim()?.takeIf { it.isNotEmpty() },
				contentDescription = null,
				modifier = Modifier
					.size(56.dp)
					.clip(RoundedCornerShape(PurecipesTheme.space.s))
					.background(PurecipesTheme.colorScheme.secondaryContainer),
				contentScale = ContentScale.Crop,
			)
			Column(
				modifier = Modifier
					.weight(1f)
					.semantics(mergeDescendants = true) {},
			) {
				TitleText(text = cookbook.name)
				BodyText(
					text = stringResource(Res.string.cookbook_recipe_count, cookbook.recipeCount),
					color = PurecipesTheme.colorScheme.onSurfaceVariant,
				)
			}
			PurecipesTextButton(
				text = stringResource(Res.string.delete),
				onClick = onDeleteCookbook,
				enabled = !isDeletingCookbook && cookbook.recipeCount == 0,
				modifier = Modifier
					.testTag("$DELETE_COOKBOOK_BUTTON_PREFIX${cookbook.id}")
					.semantics { contentDescription = deleteDescription },
			)
		}
	}
}
