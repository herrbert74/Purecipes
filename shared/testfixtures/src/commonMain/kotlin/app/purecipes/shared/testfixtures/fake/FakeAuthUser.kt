package app.purecipes.shared.testfixtures.fake

import app.purecipes.feature.auth.domain.model.AuthProvider
import app.purecipes.feature.auth.domain.model.AuthUser

fun fakeAuthUser(
	id: String = "user-1",
	email: String = "taylor@example.com",
	displayName: String = "Taylor Baker",
	firstName: String? = null,
	familyName: String? = null,
	profileImageUrl: String? = null,
	provider: AuthProvider = AuthProvider.EMAIL,
): AuthUser = AuthUser(
	id = id,
	email = email,
	displayName = displayName,
	firstName = firstName,
	familyName = familyName,
	profileImageUrl = profileImageUrl,
	provider = provider,
)
