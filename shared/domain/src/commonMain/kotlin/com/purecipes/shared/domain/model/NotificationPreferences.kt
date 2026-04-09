package com.purecipes.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class NotificationPreferences(
	val enableAll: Boolean = true,
	val enableTimers: Boolean = true,
	val enableSocial: Boolean = true,
	val enableUpdates: Boolean = true,
)
