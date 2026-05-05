package com.purecipes.backend

import com.purecipes.backend.auth.GoogleIdTokenVerifier
import com.purecipes.backend.auth.GoogleTokenInfoGoogleIdTokenVerifier
import com.purecipes.backend.auth.JdbcSessionService
import com.purecipes.backend.auth.SessionService
import com.purecipes.backend.db.Db
import com.purecipes.backend.feature.auth.authenticationRoutes
import com.purecipes.backend.feature.favorites.favoriteRoutes
import com.purecipes.backend.feature.favorites.cookbookRoutes
import com.purecipes.backend.feature.recipe.recipeImageRoutes
import com.purecipes.backend.feature.recipe.recipeRoutes
import com.purecipes.backend.feature.settings.settingsRoutes
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.staticFiles
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json

private const val BACKEND_PORT = 8080

fun main() {
	val host = System.getenv("PURECIPES_BACKEND_HOST") ?: "0.0.0.0"
	val port = System.getenv("PURECIPES_BACKEND_PORT")?.toIntOrNull() ?: BACKEND_PORT
	println("Starting Purecipes backend on http://$host:$port")
	embeddedServer(Netty, host = host, port = port) {
		module()
	}.start(wait = true)
}

fun Application.module(
	extraRoutes: Route.() -> Unit = {},
	db: Db = Db.create(),
	googleIdTokenVerifier: GoogleIdTokenVerifier = GoogleTokenInfoGoogleIdTokenVerifier(),
	sessionService: SessionService = JdbcSessionService(db.dataSource),
	recipeImageStorage: RecipeImageStorage = RecipeImageStorage(),
) {
	install(CallLogging)
	install(CORS) {
		anyHost()
		allowMethod(HttpMethod.Delete)
		allowMethod(HttpMethod.Get)
		allowMethod(HttpMethod.Patch)
		allowMethod(HttpMethod.Post)
		allowMethod(HttpMethod.Put)
		allowHeader(HttpHeaders.Authorization)
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
			call.respond(
				HttpStatusCode.InternalServerError,
				ErrorResponse(
					message = "Unexpected error",
					detail = cause.message
				)
			)
		}
	}
	sessionService.ensureSchema()

	routing {
		staticFiles("/uploads/recipes", recipeImageStorage.directory().toFile())
		get("/health") {
			call.respond(mapOf("status" to "ok"))
		}
		authenticationRoutes(googleIdTokenVerifier, sessionService)
		favoriteRoutes(sessionService) { db }
		cookbookRoutes(sessionService) { db }
		recipeImageRoutes(sessionService, recipeImageStorage)
		recipeRoutes(sessionService) { db }
		settingsRoutes(sessionService) { db }
		extraRoutes()
	}
}
