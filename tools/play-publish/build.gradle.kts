import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	alias(libs.plugins.kotlin.jvm)
	application
}

kotlin {
	jvmToolchain {
		languageVersion.set(JavaLanguageVersion.of(libs.versions.jdk.get()))
	}
}

dependencies {
	implementation(libs.google.androidPublisherApi)
	implementation(libs.google.apiClient)
	implementation(libs.google.authOauth2Http)
}

application {
	mainClass.set("app.purecipes.play.publish.UploadPlayReleaseKt")
}

tasks.withType<KotlinCompile>().configureEach {
	compilerOptions {
		jvmTarget.set(JvmTarget.fromTarget(libs.versions.jdk.get()))
	}
}

tasks.register<JavaExec>("uploadPlayRelease") {
	group = "publishing"
	description = "Upload a signed AAB to a Google Play track via the Android Publisher API"
	classpath = sourceSets["main"].runtimeClasspath
	mainClass.set("app.purecipes.play.publish.UploadPlayReleaseKt")
	workingDir = rootProject.projectDir
	val credentials = providers.gradleProperty("purecipes.play.serviceAccountJson")
		.orElse(providers.environmentVariable("GOOGLE_APPLICATION_CREDENTIALS"))
	val defaultAab = rootProject.projectDir
		.resolve("app/build/outputs/bundle/release/app-release.aab")
		.absolutePath
	val aab = providers.gradleProperty("purecipes.play.aab").orElse(defaultAab)
	val defaultMapping = rootProject.projectDir
		.resolve("app/build/outputs/mapping/release/mapping.txt")
		.absolutePath
	val mapping = providers.gradleProperty("purecipes.play.mapping").orElse(defaultMapping)
	val track = providers.gradleProperty("purecipes.play.track").orElse("alpha")
	val status = providers.gradleProperty("purecipes.play.status").orElse("completed")
	val language = providers.gradleProperty("language")
		.orElse(providers.gradleProperty("purecipes.play.language"))
		.orElse("en-GB")
	val releaseNotes = providers.gradleProperty("purecipes.play.releaseNotesFile")
		.orElse(
			rootProject.layout.buildDirectory.file("release-notes.txt").map { it.asFile.absolutePath },
		)
	val releaseName = providers.gradleProperty("purecipes.play.releaseName")
	args(
		buildList {
			add("--project-root")
			add(rootProject.projectDir.absolutePath)
			add("--aab")
			add(aab.get())
			add("--mapping")
			add(mapping.get())
			add("--track")
			add(track.get())
			add("--status")
			add(status.get())
			add("--language")
			add(language.get())
			add("--release-notes-file")
			add(releaseNotes.get())
			releaseName.orNull?.takeIf { it.isNotBlank() }?.let { name ->
				add("--release-name")
				add(name)
			}
			if (project.hasProperty("dryRun")) {
				add("--dry-run")
			}
			credentials.orNull?.takeIf { it.isNotBlank() }?.let { path ->
				add("--credentials")
				add(path)
			}
		},
	)
}
