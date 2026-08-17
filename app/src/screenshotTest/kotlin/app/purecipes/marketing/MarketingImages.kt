package app.purecipes.marketing

import app.purecipes.R

internal object MarketingImages {

	const val TUSCAN_CHICKEN = "marketing:tuscan_chicken"
	const val MISO_RAMEN = "marketing:miso_ramen"
	const val SHAKSHUKA = "marketing:shakshuka"
	const val TACOS = "marketing:tacos"
	const val THAI_CURRY = "marketing:thai_curry"
	const val RATATOUILLE = "marketing:ratatouille"

	fun drawableRes(key: String): Int {
		return when (key) {
			TUSCAN_CHICKEN -> R.drawable.marketing_tuscan_chicken
			MISO_RAMEN -> R.drawable.marketing_miso_ramen
			SHAKSHUKA -> R.drawable.marketing_shakshuka
			TACOS -> R.drawable.marketing_tacos
			THAI_CURRY -> R.drawable.marketing_thai_curry
			RATATOUILLE -> R.drawable.marketing_ratatouille
			else -> R.drawable.marketing_tuscan_chicken
		}
	}
}
