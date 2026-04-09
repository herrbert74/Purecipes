plugins {
	alias(libs.plugins.androidApplication)
	alias(libs.plugins.googleServices)
	alias(libs.plugins.ksp)
	id("org.jetbrains.kotlin.plugin.compose")
	id("dev.zacsweers.metro")
}

private fun Project.googleWebClientId(): String {
	return providers.gradleProperty("purecipes.googleWebClientId")
		.orElse(providers.gradleProperty("PURECIPES_GOOGLE_WEB_CLIENT_ID"))
		.orElse(providers.environmentVariable("PURECIPES_GOOGLE_WEB_CLIENT_ID"))
		.orNull
		.orEmpty()
}

private fun Project.gaMeasurementId(): String {
	return providers.gradleProperty("purecipes.gaMeasurementId")
		.orElse(providers.gradleProperty("PURECIPES_GA_MEASUREMENT_ID"))
		.orElse(providers.environmentVariable("PURECIPES_GA_MEASUREMENT_ID"))
		.orNull
		.orEmpty()
}

private fun Project.mixpanelProjectToken(): String {
	return providers.gradleProperty("purecipes.mixpanelProjectToken")
		.orElse(providers.gradleProperty("PURECIPES_MIXPANEL_PROJECT_TOKEN"))
		.orElse(providers.environmentVariable("PURECIPES_MIXPANEL_PROJECT_TOKEN"))
		.orNull
		.orEmpty()
}

private fun Project.usercentricsSettingsId(): String {
	return providers.gradleProperty("purecipes.usercentricsSettingsId")
		.orElse(providers.gradleProperty("PURECIPES_USERCENTRICS_SETTINGS_ID"))
		.orElse(providers.environmentVariable("PURECIPES_USERCENTRICS_SETTINGS_ID"))
		.orNull
		.orEmpty()
}

private fun String.asBuildConfigString(): String {
	return buildString {
		append('"')
		for (character in this@asBuildConfigString) {
			if (character == '"' || character == '\\') {
				append('\\')
			}
			append(character)
		}
		append('"')
	}
}

android {
	namespace = "com.purecipes"
	compileSdk {
		version = release(36)
	}

	defaultConfig {
		applicationId = "com.purecipes"
		minSdk = 24
		targetSdk = 36
		versionCode = 1
		versionName = "1.0"
		buildConfigField("String", "PURECIPES_GOOGLE_WEB_CLIENT_ID", googleWebClientId().asBuildConfigString())
		buildConfigField("String", "PURECIPES_GA_MEASUREMENT_ID", gaMeasurementId().asBuildConfigString())
		buildConfigField("String", "PURECIPES_MIXPANEL_PROJECT_TOKEN", mixpanelProjectToken().asBuildConfigString())
		buildConfigField("String", "PURECIPES_USERCENTRICS_SETTINGS_ID", usercentricsSettingsId().asBuildConfigString())

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}

	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
		}
		create("staging") {
			initWith(getByName("release"))
			isDebuggable = true
			signingConfig = signingConfigs.getByName("debug")
			matchingFallbacks += listOf("release")
		}
	}
	kotlin {
		jvmToolchain(21)
	}
	buildFeatures {
		buildConfig = true
		compose = true
	}
}

dependencies {
	implementation(project(":feature:auth:data"))
	implementation(project(":feature:analytics:data"))
	implementation(project(":feature:main"))
	implementation(project(":feature:favorites:data"))
	implementation(project(":feature:newrecipe:data"))
	implementation(project(":feature:recipedetails:data"))
	implementation(project(":feature:search:data"))
	implementation(project(":feature:settings:data"))
	implementation(project(":shared:data"))
	implementation(project(":shared:ui"))
	implementation(libs.androidx.core)
	implementation(libs.androidx.lifecycleRuntime)
	implementation(libs.androidx.activityCompose)
	implementation(libs.kmpauth.facebook)
	implementation(libs.kmpnotifier)
	implementation(platform(libs.androidx.composeBom))
	implementation(libs.androidx.composeUi)
	implementation(libs.androidx.composeUiGraphics)
	implementation(libs.androidx.composeUiToolingPreview)
	implementation(libs.androidx.composeMaterial3)
	implementation(libs.androidx.composeMaterialIconsCore)
	implementation(libs.androidx.composeMaterialIconsExtended)
	implementation(libs.metro.runtime)
}
