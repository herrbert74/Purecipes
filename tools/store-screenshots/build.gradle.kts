import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	alias(libs.plugins.kotlin.jvm)
	alias(libs.plugins.jetBrainsCompose)
	id("org.jetbrains.kotlin.plugin.compose")
	application
}

kotlin {
	jvmToolchain {
		languageVersion.set(JavaLanguageVersion.of(libs.versions.jdk.get()))
	}
}

dependencies {
	implementation(compose.desktop.currentOs)
	implementation(libs.google.androidPublisherApi)
	implementation(libs.google.apiClient)
	implementation(libs.google.authOauth2Http)
	implementation(libs.jetbrains.composeFoundation)
	implementation(libs.jetbrains.composeMaterial3)
	implementation(libs.jetbrains.composeRuntime)
	implementation(libs.jetbrains.composeUi)
}

application {
	mainClass.set("app.purecipes.store.screenshots.GenerateStoreScreenshotsKt")
}

tasks.withType<KotlinCompile>().configureEach {
	compilerOptions {
		jvmTarget.set(JvmTarget.fromTarget(libs.versions.jdk.get()))
		freeCompilerArgs.add("-opt-in=androidx.compose.ui.ExperimentalComposeUiApi")
	}
}

tasks.register<JavaExec>("generateStoreScreenshots") {
	group = "store listing"
	description = "Render framed Play Store marketing screenshots into store-listing/"
	classpath = sourceSets["main"].runtimeClasspath
	mainClass.set("app.purecipes.store.screenshots.GenerateStoreScreenshotsKt")
	workingDir = rootProject.projectDir
	val language = providers.gradleProperty("language")
		.orElse(providers.gradleProperty("purecipes.play.language"))
		.orElse("en-GB")
	args(
		buildList {
			add("--project-root")
			add(rootProject.projectDir.absolutePath)
			add("--output")
			add(rootProject.layout.projectDirectory.dir("store-listing").asFile.absolutePath)
			add("--language")
			add(language.get())
		},
	)
}

tasks.register<JavaExec>("uploadStoreScreenshots") {
	group = "store listing"
	description = "Upload store-listing/ screenshots to Google Play via the Android Publisher API"
	classpath = sourceSets["main"].runtimeClasspath
	mainClass.set("app.purecipes.store.screenshots.UploadStoreScreenshotsKt")
	workingDir = rootProject.projectDir
	mustRunAfter("generateStoreScreenshots")
	val credentials = providers.gradleProperty("purecipes.play.serviceAccountJson")
		.orElse(providers.environmentVariable("GOOGLE_APPLICATION_CREDENTIALS"))
	val language = providers.gradleProperty("language")
		.orElse(providers.gradleProperty("purecipes.play.language"))
		.orElse("en-GB")
	args(
		buildList {
			add("--project-root")
			add(rootProject.projectDir.absolutePath)
			add("--store-listing")
			add(rootProject.layout.projectDirectory.dir("store-listing").asFile.absolutePath)
			add("--language")
			add(language.get())
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
