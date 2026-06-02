package app.purecipes.shared.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import app.purecipes.shared.ui.R

@Composable
actual fun StirringPotLayersPreview(modifier: Modifier) {
	Box(modifier = modifier) {
		Image(
			painter = painterResource(R.drawable.splash_pot_bg),
			contentDescription = null,
			modifier = Modifier.fillMaxSize(),
			contentScale = ContentScale.Fit,
		)
		Image(
			painter = painterResource(R.drawable.splash_pot_spoon),
			contentDescription = null,
			modifier = Modifier.fillMaxSize(),
			contentScale = ContentScale.Fit,
		)
		Image(
			painter = painterResource(R.drawable.splash_pot_fg),
			contentDescription = null,
			modifier = Modifier.fillMaxSize(),
			contentScale = ContentScale.Fit,
		)
	}
}
