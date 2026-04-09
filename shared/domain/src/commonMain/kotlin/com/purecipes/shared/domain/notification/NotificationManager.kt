package com.purecipes.shared.domain.notification

import kotlinx.coroutines.flow.Flow

data class NotificationData(
	val title: String,
	val body: String,
	val data: Map<String, String> = emptyMap(),
	val imageUrl: String? = null,
	val actionUrl: String? = null,
)

interface NotificationManager {
	val token: Flow<String?>
	suspend fun initialize()
	suspend fun requestPermission(): Boolean
	suspend fun subscribeToTopic(topic: String)
	suspend fun unsubscribeFromTopic(topic: String)
	fun sendLocalNotification(notification: NotificationData)
}
