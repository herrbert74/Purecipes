package app.purecipes.shared.data.notification

import app.purecipes.shared.domain.notification.NotificationData
import app.purecipes.shared.domain.notification.NotificationManager
import com.diamondedge.logging.logging
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.PayloadData
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Inject
@ContributesBinding(AppScope::class)
class KmpNotificationManager : NotificationManager {

	private val _token = MutableStateFlow<String?>(null)
	override val token: Flow<String?> = _token.asStateFlow()
	val logging = logging()

	override suspend fun initialize() {
		NotifierManager.addListener(object : NotifierManager.Listener {

			override fun onNewToken(token: String) {
				logging.d { token }
				_token.value = token
			}

			override fun onPayloadData(data: PayloadData) {
				println("Push Notification payload Data: $data")
			}

		})
		val existingToken = NotifierManager.getPushNotifier().getToken()
		if (existingToken != null) {
			logging.d { "Existing FCM token: $existingToken" }
			_token.value = existingToken
		}
	}

	override suspend fun requestPermission(): Boolean = true
	override suspend fun subscribeToTopic(topic: String) {
		NotifierManager.getPushNotifier().subscribeToTopic(topic)
	}

	override suspend fun unsubscribeFromTopic(topic: String) {
		NotifierManager.getPushNotifier().unSubscribeFromTopic(topic)
	}

	override fun sendLocalNotification(notification: NotificationData) {
		NotifierManager.getLocalNotifier().notify(
			title = notification.title,
			body = notification.body,
			payloadData = notification.data
		)
	}
}
