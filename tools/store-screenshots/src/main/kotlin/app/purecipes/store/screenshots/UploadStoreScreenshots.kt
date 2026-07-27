package app.purecipes.store.screenshots

import com.google.api.client.http.FileContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.androidpublisher.AndroidPublisher
import com.google.api.services.androidpublisher.AndroidPublisherScopes
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import java.io.File
import java.io.FileInputStream

private const val DEFAULT_PACKAGE_NAME = "app.purecipes"
private const val DEFAULT_LANGUAGE = "en-US"
private const val APPLICATION_NAME = "Purecipes Store Screenshots"

fun main(args: Array<String>) {
	val options = parseUploadArgs(args)
	val assets = collectUploadAssets(options.storeListingDir, options.language)
	require(assets.isNotEmpty()) {
		"No PNG assets found under ${options.storeListingDir}. Run ./gradlew generateStoreScreenshots first."
	}

	println("Package: ${options.packageName}")
	println("Language: ${options.language}")
	println("Assets to upload:")
	for (asset in assets) {
		println("  ${asset.imageType.apiValue}: ${asset.file.relativeTo(options.projectRoot)}")
	}

	if (options.dryRun) {
		println("Dry run only - no Play Console changes.")
		return
	}

	val credentialsFile = options.credentialsFile
		?: error(
			"Missing Play service account JSON. Pass --credentials, set " +
				"GOOGLE_APPLICATION_CREDENTIALS, or -Dpurecipes.play.serviceAccountJson=...",
		)
	val publisher = createPublisher(credentialsFile)
	val editId = publisher.edits().insert(options.packageName, null).execute().id
		?: error("Play API returned an edit without an id")
	println("Created edit $editId")

	val types = assets.map { it.imageType }.distinct()
	for (imageType in types) {
		publisher.edits().images()
			.deleteall(options.packageName, editId, options.language, imageType.apiValue)
			.execute()
		println("Cleared existing ${imageType.apiValue}")
	}
	for (asset in assets) {
		val content = FileContent("image/png", asset.file)
		publisher.edits().images()
			.upload(
				options.packageName,
				editId,
				options.language,
				asset.imageType.apiValue,
				content,
			)
			.execute()
		println("Uploaded ${asset.file.name} as ${asset.imageType.apiValue}")
	}
	publisher.edits().commit(options.packageName, editId).execute()
	println("Committed edit $editId")
}

private data class UploadAsset(
	val imageType: PlayImageType,
	val file: File,
)

private data class UploadOptions(
	val projectRoot: File,
	val storeListingDir: File,
	val credentialsFile: File?,
	val packageName: String,
	val language: String,
	val dryRun: Boolean,
)

private fun collectUploadAssets(storeListingDir: File, language: String): List<UploadAsset> {
	require(storeListingDir.isDirectory) {
		"Store listing directory missing: ${storeListingDir.absolutePath}"
	}
	return PlayImageType.entries.flatMap { imageType ->
		val localeDir = File(storeListingDir, "${imageType.directoryName}/$language")
		if (!localeDir.isDirectory) {
			emptyList()
		} else {
			localeDir.listFiles()
				.orEmpty()
				.filter { it.isFile && it.extension.equals("png", ignoreCase = true) }
				.sortedBy { it.name }
				.map { file -> UploadAsset(imageType = imageType, file = file) }
		}
	}
}

private fun createPublisher(credentialsFile: File): AndroidPublisher {
	require(credentialsFile.isFile) {
		"Service account JSON not found: ${credentialsFile.absolutePath}"
	}
	val credentials = GoogleCredentials.fromStream(FileInputStream(credentialsFile))
		.createScoped(listOf(AndroidPublisherScopes.ANDROIDPUBLISHER))
	return AndroidPublisher.Builder(
		NetHttpTransport(),
		GsonFactory.getDefaultInstance(),
		HttpCredentialsAdapter(credentials),
	).setApplicationName(APPLICATION_NAME).build()
}

private fun parseUploadArgs(args: Array<String>): UploadOptions {
	var projectRoot = File(".").canonicalFile
	var storeListingDir: File? = null
	var credentialsFile: File? = null
	var packageName = DEFAULT_PACKAGE_NAME
	var language = DEFAULT_LANGUAGE
	var dryRun = false
	var index = 0
	while (index < args.size) {
		when (val arg = args[index]) {
			"--project-root" -> {
				projectRoot = File(requireNext(args, index, arg)).canonicalFile
				index += 2
			}

			"--store-listing" -> {
				storeListingDir = File(requireNext(args, index, arg))
				index += 2
			}

			"--credentials" -> {
				credentialsFile = File(requireNext(args, index, arg))
				index += 2
			}

			"--package-name" -> {
				packageName = requireNext(args, index, arg)
				index += 2
			}

			"--language" -> {
				language = requireNext(args, index, arg)
				index += 2
			}

			"--dry-run" -> {
				dryRun = true
				index += 1
			}

			else -> error("Unknown argument: $arg")
		}
	}
	val resolvedStoreListing = (storeListingDir ?: File(projectRoot, "store-listing")).let { file ->
		if (file.isAbsolute) file else File(projectRoot, file.path)
	}.canonicalFile
	val resolvedCredentials = resolveCredentialsFile(projectRoot, credentialsFile)
	return UploadOptions(
		projectRoot = projectRoot,
		storeListingDir = resolvedStoreListing,
		credentialsFile = resolvedCredentials,
		packageName = packageName,
		language = language,
		dryRun = dryRun,
	)
}

private fun resolveCredentialsFile(projectRoot: File, explicit: File?): File? {
	val candidate = when {
		explicit != null -> explicit
		else -> {
			val fromEnv = System.getenv("GOOGLE_APPLICATION_CREDENTIALS")
				?.takeIf { it.isNotBlank() }
				?.let(::File)
			val fromProperty = System.getProperty("purecipes.play.serviceAccountJson")
				?.takeIf { it.isNotBlank() }
				?.let(::File)
			fromEnv ?: fromProperty
		}
	} ?: return null
	return if (candidate.isAbsolute) {
		candidate.canonicalFile
	} else {
		File(projectRoot, candidate.path).canonicalFile
	}
}

private fun requireNext(args: Array<String>, index: Int, flag: String): String {
	require(index + 1 < args.size) { "Missing value for $flag" }
	return args[index + 1]
}
