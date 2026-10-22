package app.purecipes.shared.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.purecipes.shared.domain.model.Appliance
import app.purecipes.shared.ui.icon.appliance.icon
import app.purecipes.shared.ui.preview.PurecipesPreviewScaffold
import app.purecipes.shared.ui.theme.PurecipesTheme
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf

const val RECIPE_CARD_APPLIANCE_ICONS_TAG = "recipeCardApplianceIcons"
const val RECIPE_CARD_APPLIANCE_ICON_PREFIX = "recipeCardApplianceIcon:"

private const val MAX_DISPLAYED_CARD_APPLIANCES = 3
private val DEFAULT_APPLIANCE_ICON_SIZE = 16.dp

@Composable
fun RecipeCardApplianceIcons(
	appliances: ImmutableSet<Appliance>,
	tint: Color,
	modifier: Modifier = Modifier,
	iconSize: Dp = DEFAULT_APPLIANCE_ICON_SIZE,
) {
	if (appliances.isEmpty()) return
	Row(
		modifier = modifier.testTag(RECIPE_CARD_APPLIANCE_ICONS_TAG),
		horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs),
		verticalAlignment = Alignment.CenterVertically,
	) {
		appliances.take(MAX_DISPLAYED_CARD_APPLIANCES).forEach { appliance ->
			Icon(
				imageVector = appliance.icon,
				contentDescription = appliance.displayName,
				tint = tint,
				modifier = Modifier
					.size(iconSize)
					.testTag("$RECIPE_CARD_APPLIANCE_ICON_PREFIX${appliance.name}"),
			)
		}
	}
}

@Preview(showBackground = true)
@Composable
private fun RecipeCardApplianceIconsPreview() {
	PurecipesPreviewScaffold {
		RecipeCardApplianceIcons(
			appliances = persistentSetOf(Appliance.OVEN, Appliance.AIR_FRYER),
			tint = PurecipesTheme.colorScheme.onSurface,
		)
	}
}
