package com.purecipes.shared.data.network

import com.purecipes.base.kotlin.async.ioDispatcher
import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js

internal actual fun createPurecipesHttpClient(): HttpClient {
	return HttpClient(Js) {
		engine {
			dispatcher = ioDispatcher()
		}
		configurePurecipesHttpClient()
	}
}
