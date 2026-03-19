package com.purecipes.shared.data.network

import com.purecipes.base.kotlin.async.ioDispatcher
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin

internal actual fun createPurecipesHttpClient(): HttpClient {
	return HttpClient(Darwin) {
		engine {
			dispatcher = ioDispatcher()
		}
		configurePurecipesHttpClient()
	}
}
