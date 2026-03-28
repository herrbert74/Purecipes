package com.purecipes.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class GoogleSignInRequest(
	val idToken: String,
)

@Serializable
data class VerifiedGoogleUser(
	val id: String,
	val email: String,
	val displayName: String,
	val firstName: String? = null,
	val familyName: String? = null,
	val profileImageUrl: String? = null,
)
