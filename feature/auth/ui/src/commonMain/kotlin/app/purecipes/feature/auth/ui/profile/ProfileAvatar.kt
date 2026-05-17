package app.purecipes.feature.auth.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.purecipes.feature.auth.domain.model.AuthUser
import app.purecipes.shared.ui.theme.PurecipesTheme
import coil3.compose.AsyncImage

@Composable
internal fun ProfileAvatar(user: AuthUser) {
	val initials = remember(user.displayName) {
		user.displayName
			.split(' ')
			.filter { it.isNotBlank() }
			.take(2)
			.joinToString(separator = "") { it.first().uppercase() }
			.ifBlank { user.email.take(1).uppercase() }
	}
	if (user.profileImageUrl != null) {
		AsyncImage(
			model = user.profileImageUrl,
			contentDescription = user.displayName,
			modifier = Modifier
				.size(88.dp)
				.clip(CircleShape),
			contentScale = ContentScale.Crop,
		)
	} else {
		Box(
			modifier = Modifier
				.size(88.dp)
				.clip(CircleShape)
				.background(PurecipesTheme.colorScheme.primaryContainer),
			contentAlignment = Alignment.Center,
		) {
			Text(
				text = initials,
				style = PurecipesTheme.typography.headlineSmall,
				color = PurecipesTheme.colorScheme.onPrimaryContainer,
				textAlign = TextAlign.Center,
			)
		}
	}
}
