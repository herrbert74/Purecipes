package app.purecipes.feature.settings.data.datasource

import java.util.Locale

internal actual fun detectRegionCode(): String? {
	return Locale.getDefault().country.takeIf { it.isNotBlank() }
}
