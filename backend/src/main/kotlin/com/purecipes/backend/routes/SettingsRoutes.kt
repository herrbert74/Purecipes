package com.purecipes.backend.routes

import com.purecipes.backend.ErrorResponse
import com.purecipes.backend.auth.SessionService
import com.purecipes.backend.db.Db
import com.purecipes.backend.repository.SettingsRepository
import com.purecipes.shared.domain.model.MeasurementPreferences
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.ContentConvertException
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.settingsRoutes(
	sessionService: SessionService,
	dbProvider: () -> Db,
) {
	route("/settings") {
		get("/measurement") {
			val userId = call.requireAuthenticatedUserId(sessionService) ?: return@get
			val repo = SettingsRepository(dbProvider().dataSource)
			val preferences = repo.getMeasurementPreferences(userId)
			if (preferences == null) {
				call.respond(
					HttpStatusCode.NotFound,
					ErrorResponse(
						message = "Settings not found",
						detail = "No measurement preferences found for user: $userId",
					),
				)
				return@get
			}

			call.respond(preferences)
		}

		put("/measurement") {
			val userId = call.requireAuthenticatedUserId(sessionService) ?: return@put
			val preferences = call.receiveMeasurementPreferencesOrRespond() ?: return@put
			val repo = SettingsRepository(dbProvider().dataSource)
			call.respond(repo.saveMeasurementPreferences(userId, preferences))
		}
	}
}

private suspend fun ApplicationCall.receiveMeasurementPreferencesOrRespond(): MeasurementPreferences? {
	return try {
		receive<MeasurementPreferences>()
	} catch (_: ContentConvertException) {
		respond(
			HttpStatusCode.BadRequest,
			ErrorResponse(
				message = "Invalid request",
				detail = "Request body must contain measurement preferences",
			),
		)
		null
	}
}
