package com.purecipes.backend

import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID

class RecipeImageStorage(
	private val uploadDirectory: Path = defaultUploadDirectory(),
	private val publicBaseUrl: String? = System.getenv("PURECIPES_PUBLIC_BASE_URL")
		?.trim()
		?.trimEnd('/')
		?.takeIf(String::isNotBlank),
) {

	init {
		Files.createDirectories(uploadDirectory)
	}

	fun directory(): Path = uploadDirectory

	fun storeImage(
		bytes: ByteArray,
		originalFileName: String?,
		contentType: String?,
	): String {
		val fileName = "${UUID.randomUUID()}.${fileExtension(originalFileName, contentType)}"
		Files.write(uploadDirectory.resolve(fileName), bytes)
		return fileName
	}

	fun publicImageUrl(
		fileName: String,
		scheme: String,
		host: String,
		port: Int,
	): String {
		val baseUrl = publicBaseUrl ?: URLBuilder(
			protocol = URLProtocol.createOrDefault(scheme),
			host = host,
			port = port,
		).buildString().trimEnd('/')
		return "$baseUrl/uploads/recipes/$fileName"
	}

	private fun fileExtension(originalFileName: String?, contentType: String?): String {
		val originalExtension = originalFileName
			?.substringAfterLast('.', missingDelimiterValue = "")
			?.lowercase()
			?.takeIf { it in supportedExtensions }
		if (originalExtension != null) {
			return originalExtension
		}

		return when (contentType?.substringBefore(';')?.lowercase()) {
			"image/jpeg" -> "jpg"
			"image/png" -> "png"
			"image/webp" -> "webp"
			"image/gif" -> "gif"
			else -> "jpg"
		}
	}

	companion object {
		private val supportedExtensions = setOf("jpg", "jpeg", "png", "webp", "gif")

		private fun defaultUploadDirectory(): Path {
			val configuredDirectory = System.getenv("PURECIPES_UPLOAD_DIR")
				?.trim()
				?.takeIf(String::isNotBlank)
			return configuredDirectory?.let(Path::of)
				?: Path.of(System.getProperty("user.dir"), "uploads", "recipes")
		}
	}
}
