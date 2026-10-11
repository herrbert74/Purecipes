package app.purecipes.backend.feature.search

import app.purecipes.backend.ErrorResponse
import app.purecipes.backend.auth.SessionService
import app.purecipes.backend.db.Db
import app.purecipes.backend.feature.auth.optionalAuthenticatedUserId
import app.purecipes.shared.domain.model.HOME_SHELF_PAGE_SIZE
import app.purecipes.shared.domain.model.HomeShelfId
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import java.time.LocalTime
import java.time.ZoneId

private const val MIN_LOCAL_HOUR = 0

private const val MAX_LOCAL_HOUR = 23

fun Route.homeRoutes(
	sessionService: SessionService,
	dbProvider: () -> Db,
) {
	get("/home") {
		call.respondHome(sessionService, dbProvider)
	}
	get("/home/shelves/{shelfId}") {
		call.respondShelfPage(sessionService, dbProvider)
	}
}

private suspend fun ApplicationCall.respondHome(
	sessionService: SessionService,
	dbProvider: () -> Db,
) {
	val localHour = localHourOrRespond() ?: return
	val repository = HomeFeedRepository(dbProvider().dataSource)
	respond(
		repository.loadHomeFeed(
			userId = optionalAuthenticatedUserId(sessionService),
			localHour = localHour,
		),
	)
}

private suspend fun ApplicationCall.localHourOrRespond(): Int? {
	val rawHour = request.queryParameters["localHour"]?.trim()?.takeIf { it.isNotEmpty() }
	val parsedHour = rawHour?.toIntOrNull()?.takeIf { hour -> hour in MIN_LOCAL_HOUR..MAX_LOCAL_HOUR }
	val localHour = parsedHour ?: clockHourWhenMissing(rawHour)
	if (localHour == null) {
		respond(
			HttpStatusCode.BadRequest,
			ErrorResponse(
				message = "Invalid request",
				detail = "localHour must be from $MIN_LOCAL_HOUR to $MAX_LOCAL_HOUR",
			),
		)
	}
	return localHour
}

private suspend fun ApplicationCall.respondShelfPage(
	sessionService: SessionService,
	dbProvider: () -> Db,
) {
	val shelfId = parameters["shelfId"]?.let { rawId ->
		HomeShelfId.entries.firstOrNull { shelf -> shelf.name == rawId }
	}
	if (shelfId == null) {
		respond(
			HttpStatusCode.BadRequest,
			ErrorResponse(
				message = "Invalid request",
				detail = "shelfId must be a home shelf id",
			),
		)
		return
	}
	val localHour = localHourOrRespond() ?: return
	val pageNumber = request.queryParameters["pageNumber"]?.toIntOrNull()?.coerceAtLeast(1) ?: 1
	val pageSize = request.queryParameters["pageSize"]?.toIntOrNull() ?: HOME_SHELF_PAGE_SIZE
	val repository = HomeFeedRepository(dbProvider().dataSource)
	respond(
		repository.loadShelfPage(
			userId = optionalAuthenticatedUserId(sessionService),
			localHour = localHour,
			shelfId = shelfId,
			pageNumber = pageNumber,
			pageSize = pageSize,
		),
	)
}

private fun clockHourWhenMissing(rawHour: String?): Int? = if (rawHour == null) {
	LocalTime.now(ZoneId.systemDefault()).hour
} else {
	null
}
