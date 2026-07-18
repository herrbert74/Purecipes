package app.purecipes.shared.domain.model

// Temporary kill switch for Firebase App Distribution releases until RevenueCat /
// Google Play premium sync updates app_users.is_premium reliably.
// When true, the backend applies keyIngredients from the search request even if the
// user is not premium in the database. The client still paywalls key ingredients
// until Monetisation debug Force Premium (or a real subscription) sets isPremium.
// Flip to false (and delete this) once proper premium sync is in place.
const val TREAT_KEY_INGREDIENTS_AS_NON_PREMIUM = true

fun canUseKeyIngredients(isPremium: Boolean): Boolean =
	isPremium || TREAT_KEY_INGREDIENTS_AS_NON_PREMIUM
