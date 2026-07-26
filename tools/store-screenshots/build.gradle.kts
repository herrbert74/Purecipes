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
	mainClass.set(application.mainClass)
	workingDir = rootProject.projectDir
	args(
		"--project-root",
		rootProject.projectDir.absolutePath,
		"--output",
		rootProject.layout.projectDirectory.dir("store-listing").asFile.absolutePath,
	)
}
