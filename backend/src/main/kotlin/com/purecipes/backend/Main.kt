package com.purecipes.backend

import com.purecipes.backend.db.Db
import com.purecipes.backend.routes.recipeRoutes
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.serialization.kotlinx.json.json
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import kotlinx.serialization.json.Json

fun main() {
	val host = System.getenv("PURECIPES_BACKEND_HOST") ?: "0.0.0.0"
	val port = System.getenv("PURECIPES_BACKEND_PORT")?.toIntOrNull() ?: 8080
	println("Starting Purecipes backend on http://$host:$port")
	embeddedServer(Netty, host = host, port = port) {
		module()
	}.start(wait = true)
}

fun Application.module() {
	install(CallLogging)
	install(CORS) {
		anyHost()
		allowMethod(HttpMethod.Get)
		allowHeader(HttpHeaders.ContentType)
		allowHeader(HttpHeaders.Accept)
		allowNonSimpleContentTypes = true
	}
	install(ContentNegotiation) {
		json(
			Json {
				ignoreUnknownKeys = true
				explicitNulls = false
			}
		)
	}
	install(StatusPages) {
		exception<Throwable> { call, cause ->
			call.respond(mapOf("error" to (cause.message ?: "Unexpected error")))
		}
	}

	val db = Db.create()
	routing {
		get("/health") {
			call.respond(mapOf("status" to "ok"))
		}
		recipeRoutes(db)
	}
}
