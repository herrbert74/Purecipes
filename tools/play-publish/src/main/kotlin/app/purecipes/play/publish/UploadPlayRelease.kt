package app.purecipes.play.publish

import com.google.api.client.http.FileContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.androidpublisher.AndroidPublisher
import com.google.api.services.androidpublisher.AndroidPublisherScopes
import com.google.api.services.androidpublisher.model.LocalizedText
import com.google.api.services.androidpublisher.model.Track
import com.google.api.services.androidpublisher.model.TrackRelease
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import java.io.File
import java.io.FileInputStream

private const val DEFAULT_PACKAGE_NAME = "app.purecipes"
private const val DEFAULT_LANGUAGE = "en-GB"
private const val DEFAULT_TRACK = "alpha"
private const val DEFAULT_STATUS = "completed"
private const val APPLICATION_NAME = "Purecipes Play Publish"
private const val MAX_RELEASE_NOTES_LENGTH = 500

fun main(args: Array<String>) {
	val options = parseUploadArgs(args)
	require(options.aabFile.isFile) {
		"AAB not found: ${options.aabFile.absolutePath}. Run ./gradlew :app:bundleRelease first."
	}

	val releaseNotes = options.releaseNotesFile
		?.takeIf { it.isFile }
		?.readText()
		?.trim()
		?.takeIf { it.isNotEmpty() }
		?.let(::truncateReleaseNotes)

	println("Package: ${options.packageName}")
	println("AAB: ${options.aabFile.relativeTo(options.projectRoot)}")
	println("Track: ${options.track}")
	println("Status: ${options.status}")
	println("Language: ${options.language}")
	options.releaseName?.let { println("Release name: $it") }
	if (releaseNotes != null) {
		println("Release notes (${releaseNotes.length} chars):")
		println(releaseNotes)
	} else {
		println("Release notes: (none)")
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

	val content = FileContent("application/octet-stream", options.aabFile)
	val bundle = publisher.edits().bundles()
		.upload(options.packageName, editId, content)
		.execute()
	val versionCode = bundle.versionCode
		?: error("Play API returned a bundle without a versionCode")
	println("Uploaded bundle versionCode=$versionCode")

	val trackRelease = TrackRelease()
		.setVersionCodes(listOf(versionCode.toLong()))
		.setStatus(options.status)
	options.releaseName?.let { trackRelease.setName(it) }
	if (releaseNotes != null) {
		trackRelease.setReleaseNotes(
			listOf(
				LocalizedText()
					.setLanguage(options.language)
					.setText(releaseNotes),
			),
		)
	}

	val track = Track()
		.setTrack(options.track)
		.setReleases(listOf(trackRelease))
	publisher.edits().tracks()
		.update(options.packageName, editId, options.track, track)
		.execute()
	println("Updated track ${options.track}")

	publisher.edits().commit(options.packageName, editId).execute()
	println("Committed edit $editId")
}

private data class UploadOptions(
	val projectRoot: File,
	val aabFile: File,
	val credentialsFile: File?,
	val packageName: String,
	val track: String,
	val status: String,
	val language: String,
	val releaseName: String?,
	val releaseNotesFile: File?,
	val dryRun: Boolean,
)

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

private fun truncateReleaseNotes(notes: String): String {
	if (notes.length <= MAX_RELEASE_NOTES_LENGTH) {
		return notes
	}
	val trimmed = notes.take(MAX_RELEASE_NOTES_LENGTH - 1).trimEnd()
	return "$trimmed..."
}

private fun parseUploadArgs(args: Array<String>): UploadOptions {
	var projectRoot = File(".").canonicalFile
	var aabFile: File? = null
	var credentialsFile: File? = null
	var packageName = DEFAULT_PACKAGE_NAME
	var track = DEFAULT_TRACK
	var status = DEFAULT_STATUS
	var language = DEFAULT_LANGUAGE
	var releaseName: String? = null
	var releaseNotesFile: File? = null
	var dryRun = false
	var index = 0
	while (index < args.size) {
		when (val arg = args[index]) {
			"--project-root" -> {
				projectRoot = File(requireNext(args, index, arg)).canonicalFile
				index += 2
			}

			"--aab" -> {
				aabFile = File(requireNext(args, index, arg))
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

			"--track" -> {
				track = requireNext(args, index, arg)
				index += 2
			}

			"--status" -> {
				status = requireNext(args, index, arg)
				index += 2
			}

			"--language" -> {
				language = requireNext(args, index, arg)
				index += 2
			}

			"--release-name" -> {
				releaseName = requireNext(args, index, arg)
				index += 2
			}

			"--release-notes-file" -> {
				releaseNotesFile = File(requireNext(args, index, arg))
				index += 2
			}

			"--dry-run" -> {
				dryRun = true
				index += 1
			}

			else -> error("Unknown argument: $arg")
		}
	}
	val resolvedAab = resolveRelativeFile(
		projectRoot,
		aabFile ?: File(projectRoot, "app/build/outputs/bundle/release/app-release.aab"),
	)
	val resolvedNotes = releaseNotesFile?.let { resolveRelativeFile(projectRoot, it) }
	val resolvedCredentials = resolveCredentialsFile(projectRoot, credentialsFile)
	require(track in setOf("internal", "alpha", "beta", "production")) {
		"Unsupported track '$track'. Use internal, alpha, beta, or production."
	}
	require(status in setOf("completed", "draft", "inProgress", "halted")) {
		"Unsupported status '$status'. Use completed, draft, inProgress, or halted."
	}
	return UploadOptions(
		projectRoot = projectRoot,
		aabFile = resolvedAab,
		credentialsFile = resolvedCredentials,
		packageName = packageName,
		track = track,
		status = status,
		language = language,
		releaseName = releaseName,
		releaseNotesFile = resolvedNotes,
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
	return resolveRelativeFile(projectRoot, candidate)
}

private fun resolveRelativeFile(projectRoot: File, file: File): File {
	return if (file.isAbsolute) {
		file.canonicalFile
	} else {
		File(projectRoot, file.path).canonicalFile
	}
}

private fun requireNext(args: Array<String>, index: Int, flag: String): String {
	require(index + 1 < args.size) { "Missing value for $flag" }
	return args[index + 1]
}
