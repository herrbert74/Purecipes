package app.purecipes.backend.feature.recipe

import app.purecipes.backend.ErrorResponse
import app.purecipes.backend.RecipeImageStorage
import app.purecipes.backend.auth.SessionService
import app.purecipes.backend.feature.auth.requireAuthenticatedUserId
import app.purecipes.shared.domain.model.RecipeImageUploadResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.utils.io.readRemaining
import kotlinx.io.readByteArray

private const val MAX_RECIPE_IMAGE_BYTES = 5 * 1024 * 1024

fun Route.recipeImageRoutes(
	sessionService: SessionService,
	recipeImageStorage: RecipeImageStorage,
) {
	route("/recipe-images") {
		post {
			call.requireAuthenticatedUserId(sessionService) ?: return@post

			var imageBytes: ByteArray? = null
			var originalFileName: String? = null
			var contentType: ContentType? = null

			call.receiveMultipart().forEachPart { part ->
				if (part is PartData.FileItem && part.name == "image" && imageBytes == null) {
					imageBytes = part.provider().readRemaining().readByteArray()
					originalFileName = part.originalFileName
					contentType = part.contentType
				}
				part.release()
			}

			val bytes = imageBytes
			if (bytes == null) {
				call.respond(
					HttpStatusCode.BadRequest,
					ErrorResponse(
						message = "Invalid request",
						detail = "Multipart form must include an image file in the image field",
					),
				)
				return@post
			}

			if (bytes.isEmpty()) {
				call.respond(
					HttpStatusCode.BadRequest,
					ErrorResponse(
						message = "Invalid request",
						detail = "Uploaded image file is empty",
					),
				)
				return@post
			}

			if (bytes.size > MAX_RECIPE_IMAGE_BYTES) {
				call.respond(
					HttpStatusCode.BadRequest,
					ErrorResponse(
						message = "Invalid request",
						detail = "Uploaded image must be 5 MB or smaller",
					),
				)
				return@post
			}

			if (contentType?.match(ContentType.Image.Any) != true) {
				call.respond(
					HttpStatusCode.BadRequest,
					ErrorResponse(
						message = "Invalid request",
						detail = "Uploaded file must be an image",
					),
				)
				return@post
			}

			val storedFileName = recipeImageStorage.storeImage(
				bytes = bytes,
				originalFileName = originalFileName,
				contentType = contentType?.toString(),
			)
			call.respond(
				HttpStatusCode.Created,
				RecipeImageUploadResponse(
					imageUrl = recipeImageStorage.publicImageUrl(
						fileName = storedFileName,
						scheme = call.request.local.scheme,
						host = call.request.local.serverHost,
						port = call.request.local.serverPort,
					),
				),
			)
		}
	}
}
