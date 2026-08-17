package app.purecipes.shared.domain.model

// Temporary kill switch for Firebase App Distribution releases until RevenueCat /
// Google Play premium sync updates app_users.is_premium reliably.
// When true, the backend accepts premium-only writes and search params even if the
// user is not premium in the database. The client still paywalls these features
// until Monetisation debug Force Premium (or a real subscription) sets isPremium.
// Flip to false (and delete this) once proper premium sync is in place.
const val TREAT_PREMIUM_SEARCH_AS_NON_PREMIUM = true

fun canUseKeyIngredients(isPremium: Boolean): Boolean =
	isPremium || TREAT_PREMIUM_SEARCH_AS_NON_PREMIUM

fun canUsePremiumFilters(isPremium: Boolean): Boolean =
	isPremium || TREAT_PREMIUM_SEARCH_AS_NON_PREMIUM

fun canPrivatizeRecipe(isPremium: Boolean): Boolean =
	isPremium || TREAT_PREMIUM_SEARCH_AS_NON_PREMIUM
