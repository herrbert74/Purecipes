package app.purecipes.shared.data.network

import app.purecipes.base.kotlin.async.ioDispatcher
import app.purecipes.shared.data.session.SessionTokenStore
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp

internal actual fun createPurecipesHttpClient(sessionTokenStore: SessionTokenStore): HttpClient {
	return HttpClient(OkHttp) {
		engine {
			dispatcher = ioDispatcher()
		}
		configurePurecipesHttpClient(sessionTokenStore)
	}
}
