package app.purecipes.feature.ads.domain.usecase

import app.purecipes.feature.subscription.domain.usecase.ObservePremiumStatusUseCase
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Inject
class ObserveShouldShowAdsUseCase(
	private val observePremiumStatus: ObservePremiumStatusUseCase,
) {

	operator fun invoke(): Flow<Boolean> {
		return observePremiumStatus().map { isPremium -> !isPremium }
	}
}
