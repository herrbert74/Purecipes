package app.purecipes.feature.home.ui.navigation

import androidx.navigation3.runtime.NavKey
import app.purecipes.shared.domain.model.HomeShelfId
import kotlinx.serialization.Serializable

@Serializable
data class HomeShelfDestination(
	val title: String,
	val shelfId: HomeShelfId,
	val collectionSlug: String? = null,
) : NavKey
