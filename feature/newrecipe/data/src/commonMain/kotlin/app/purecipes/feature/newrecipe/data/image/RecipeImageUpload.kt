package app.purecipes.feature.newrecipe.data.image

import app.purecipes.base.kotlin.result.Failure
import app.purecipes.base.kotlin.result.Outcome
import app.purecipes.shared.data.config.PurecipesConfig
import app.purecipes.shared.data.network.backendBaseUrl
import app.purecipes.shared.data.util.runCatchingApi
import app.purecipes.shared.domain.model.RecipeImageUploadResponse
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

data class RecipeImageUpload(
	val bytes: ByteArray,
	val fileName: String,
	val contentType: String,
)

interface RecipeImagePathLoader {
	suspend fun load(path: String): Outcome<RecipeImageUpload>
}

@Inject
@ContributesBinding(AppScope::class)
class PlatformRecipeImagePathLoader : RecipeImagePathLoader {
	override suspend fun load(path: String): Outcome<RecipeImageUpload> = readRecipeImageUpload(path)
}

interface RecipeImageUploader {
	suspend fun upload(image: RecipeImageUpload): Outcome<String>
}

@Inject
@ContributesBinding(AppScope::class)
class RecipeImageRemoteUploader(
	private val httpClient: HttpClient,
	purecipesConfig: PurecipesConfig,
) : RecipeImageUploader {

	private val uploadEndpoint = backendBaseUrl(
		purecipesConfig.buildType(),
		purecipesConfig.debugBackendHostOverride(),
	).trimEnd('/') + "/recipe-images"

	override suspend fun upload(image: RecipeImageUpload): Outcome<String> {
		return runCatchingApi {
			httpClient.post(uploadEndpoint) {
				setBody(
					MultiPartFormDataContent(
						formData {
							append(
								"image",
								image.bytes,
								Headers.build {
									append(HttpHeaders.ContentDisposition, "filename=${image.fileName}")
									append(HttpHeaders.ContentType, image.contentType)
								},
							)
						},
					),
				)
			}.body<RecipeImageUploadResponse>().imageUrl
		}
	}
}

expect suspend fun readRecipeImageUpload(path: String): Outcome<RecipeImageUpload>

internal fun recipeImageFailure(message: String): Outcome<RecipeImageUpload> = Err(Failure.ServerError(message))

internal fun recipeImageSuccess(image: RecipeImageUpload): Outcome<RecipeImageUpload> = Ok(image)

internal fun normalizeRecipeImagePath(path: String): String = path.removePrefix("file://")

internal fun recipeImageFileName(path: String): String {
	return path
		.substringAfterLast('/')
		.substringAfterLast('\\')
		.substringBefore('?')
		.ifBlank { "recipe-image.jpg" }
}

internal fun recipeImageContentType(fileName: String): String {
	return when (fileName.substringAfterLast('.', missingDelimiterValue = "").lowercase()) {
		"jpg", "jpeg" -> "image/jpeg"
		"png" -> "image/png"
		"webp" -> "image/webp"
		"gif" -> "image/gif"
		else -> "image/jpeg"
	}
}
