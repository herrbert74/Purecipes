package app.purecipes.feature.featurerequests.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class FeatureRequestDetailDestination(
	val requestId: Int,
) : NavKey
