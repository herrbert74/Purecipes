package app.purecipes.feature.auth.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import app.purecipes.feature.auth.domain.model.AuthUser
import app.purecipes.shared.ui.theme.PurecipesTheme

@Composable
internal fun ProfileHeader(user: AuthUser) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(PurecipesTheme.space.m),
	) {
		ProfileAvatar(user = user)
		Column(verticalArrangement = Arrangement.spacedBy(PurecipesTheme.space.xs)) {
			Text(
				text = user.displayName,
				style = PurecipesTheme.typography.headlineSmall,
				fontWeight = FontWeight.SemiBold,
			)
			Text(
				text = user.email,
				style = PurecipesTheme.typography.bodyMedium,
				color = PurecipesTheme.colorScheme.onSurfaceVariant,
			)
		}
	}
}
