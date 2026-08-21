import com.google.firebase.appdistribution.gradle.firebaseAppDistribution
import java.util.Properties

plugins {
	alias(libs.plugins.androidApplication)
	alias(libs.plugins.appDistribution)
	alias(libs.plugins.googleServices)
	alias(libs.plugins.crashlytics)
	alias(libs.plugins.aboutLibraries)
	alias(libs.plugins.compose.screenshotTesting)
	alias(libs.plugins.ksp)
	id("org.jetbrains.kotlin.plugin.compose")
	id("dev.zacsweers.metro")
}

aboutLibraries {
	export {
		outputFile = file("src/main/res/raw/aboutlibraries.json")
		prettyPrint = true
	}
}

android {
	namespace = "app.purecipes"
	compileSdk {
		version = release(libs.versions.compileSdkVersion.get().toInt())
	}

	defaultConfig {
		applicationId = "app.purecipes"
		minSdk = 24
		targetSdk = libs.versions.targetSdkVersion.get().toInt()
		versionCode = libs.versions.versionCode.get().toInt()
		versionName = libs.versions.versionName.get()
		buildConfigField("String", "PURECIPES_GA_MEASUREMENT_ID", gaMeasurementId().asBuildConfigString())
		buildConfigField("String", "PURECIPES_USERCENTRICS_SETTINGS_ID", usercentricsSettingsId().asBuildConfigString())
		buildConfigField("String", "PURECIPES_ADMOB_APP_ID", admobAppId().asBuildConfigString())
		buildConfigField("String", "PURECIPES_ADMOB_BANNER_AD_UNIT_ID", admobBannerAdUnitId().asBuildConfigString())
		buildConfigField("Boolean", "PURECIPES_SHOW_MONETISATION_DEBUG_OVERRIDES", "false")
		buildConfigField(
			"String",
			"PURECIPES_ADMOB_INTERSTITIAL_AD_UNIT_ID",
			admobInterstitialAdUnitId().asBuildConfigString(),
		)
		buildConfigField("String", "PURECIPES_DEBUG_BACKEND_HOST", "\"\"")
		manifestPlaceholders["admobAppId"] = admobAppId().ifBlank {
			"ca-app-pub-3940256099942544~3347511713"
		}

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}
	signingConfigs {
		create("release") {
			releaseSigningStoreFile().takeIf { it.isNotBlank() }?.let {
				storeFile = file(it)
			}
			storePassword = releaseSigningStorePassword()
			keyAlias = releaseSigningKeyAlias()
			keyPassword = releaseSigningKeyPassword()
		}
	}

	buildTypes {
		debug {
			applicationIdSuffix = ".debug"
			buildConfigField(
				"String",
				"PURECIPES_GOOGLE_WEB_CLIENT_ID",
				googleWebClientId("debug").asBuildConfigString(),
			)
			buildConfigField(
				"String",
				"PURECIPES_DEBUG_BACKEND_HOST",
				purecipesDebugBackendHost().asBuildConfigString(),
			)
			buildConfigField(
				"String",
				"PURECIPES_MIXPANEL_PROJECT_TOKEN",
				mixpanelProjectToken("debug").asBuildConfigString(),
			)
			buildConfigField(
				"String",
				"PURECIPES_REVENUECAT_API_KEY",
				revenueCatApiKey("debug").asBuildConfigString(),
			)
			buildConfigField("Boolean", "PURECIPES_SHOW_MONETISATION_DEBUG_OVERRIDES", "true")
		}
		release {
			isMinifyEnabled = true
			isShrinkResources = true
			lint.checkReleaseBuilds = false
			signingConfig = signingConfigs.getByName("release")
			proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
			buildConfigField(
				"String",
				"PURECIPES_GOOGLE_WEB_CLIENT_ID",
				googleWebClientId("release").asBuildConfigString(),
			)
			buildConfigField(
				"String",
				"PURECIPES_MIXPANEL_PROJECT_TOKEN",
				mixpanelProjectToken("release").asBuildConfigString(),
			)
			buildConfigField(
				"String",
				"PURECIPES_REVENUECAT_API_KEY",
				revenueCatApiKey("release").asBuildConfigString(),
			)
			firebaseAppDistribution {
				artifactType = "APK"
				releaseNotesFile = rootProject.layout.buildDirectory.file("release-notes.txt").get().asFile.path
				groups = "alpha-testers"
				val credentialsFile = providers.gradleProperty("firebaseAppDistribution.serviceCredentialsFile")
					.orElse(providers.environmentVariable("GOOGLE_APPLICATION_CREDENTIALS"))
				credentialsFile.orNull?.let { serviceCredentialsFile = it }
			}
		}
		create("staging") {
			initWith(getByName("release"))
			applicationIdSuffix = ".staging"
			signingConfig = signingConfigs.getByName("debug")
			matchingFallbacks += listOf("release")
			buildConfigField(
				"String",
				"PURECIPES_GOOGLE_WEB_CLIENT_ID",
				googleWebClientId("staging").asBuildConfigString(),
			)
			buildConfigField(
				"String",
				"PURECIPES_MIXPANEL_PROJECT_TOKEN",
				mixpanelProjectToken("staging").asBuildConfigString(),
			)
			buildConfigField(
				"String",
				"PURECIPES_REVENUECAT_API_KEY",
				revenueCatApiKey("staging").asBuildConfigString(),
			)
			buildConfigField("Boolean", "PURECIPES_SHOW_MONETISATION_DEBUG_OVERRIDES", "true")
		}
	}
	kotlin {
		jvmToolchain {
			languageVersion.set(JavaLanguageVersion.of(libs.versions.jdk.get()))
		}
	}
	buildFeatures {
		buildConfig = true
		compose = true
	}
	experimentalProperties["android.experimental.enableScreenshotTest"] = true
}

dependencies {
	implementation(project(":feature:ads:data"))
	implementation(project(":feature:auth:data"))
	implementation(project(":feature:analytics:data"))
	implementation(project(":feature:sharing:data"))
	implementation(project(":feature:main"))
	implementation(project(":feature:ads:ui"))
	implementation(project(":feature:analytics:ui"))
	implementation(project(":feature:auth:ui"))
	implementation(project(":feature:cooking:ui"))
	implementation(project(":feature:library:ui"))
	implementation(project(":feature:newrecipe:ui"))
	implementation(project(":feature:recipedetails:ui"))
	implementation(project(":feature:search:ui"))
	implementation(project(":feature:settings:ui"))
	implementation(project(":feature:subscription:ui"))
	implementation(project(":feature:library:data"))
	implementation(project(":feature:newrecipe:data"))
	implementation(project(":feature:recipedetails:data"))
	implementation(project(":feature:search:data"))
	implementation(project(":feature:measurement:data"))
	implementation(project(":feature:settings:data"))
	implementation(project(":feature:subscription:data"))
	implementation(project(":shared:data"))
	implementation(project(":shared:ui"))
	implementation(project.dependencies.platform(libs.firebaseBom))
	implementation(libs.firebaseCrashlytics)
	implementation(libs.androidx.core)
	implementation(libs.androidx.lifecycleRuntime)
	implementation(libs.androidx.activityCompose)
	implementation(libs.androidx.fragment)
	implementation(libs.androidx.splash)
	implementation(libs.androidx.vectordrawableAnimated)
	implementation(libs.kmpnotifier.push.firebase)
	implementation(libs.metrox.viewmodel)
	implementation(platform(libs.androidx.composeBom))
	implementation(libs.metro.runtime)
	screenshotTestImplementation(project(":feature:cooking:ui"))
	screenshotTestImplementation(project(":feature:library:ui"))
	screenshotTestImplementation(project(":feature:recipedetails:ui"))
	screenshotTestImplementation(project(":feature:search:ui"))
	screenshotTestImplementation(project(":shared:domain"))
	screenshotTestImplementation(project(":shared:ui"))
	screenshotTestImplementation(platform(libs.androidx.composeBom))
	screenshotTestImplementation(libs.androidx.composeFoundation)
	screenshotTestImplementation(libs.androidx.composeMaterial3)
	screenshotTestImplementation(libs.androidx.composeMaterialIconsExtended)
	screenshotTestImplementation(libs.androidx.composeUiTooling)
	screenshotTestImplementation(libs.androidx.core)
	screenshotTestImplementation(libs.coil.compose)
	screenshotTestImplementation(libs.jetbrains.composeMaterial3AdaptiveNavigationSuite)
	screenshotTestImplementation(libs.kotlinx.collectionsImmutable)
	screenshotTestImplementation(libs.screenshot.validationApi)
}

private fun Project.releaseSigningStoreFile(): String {
	return providers.gradleProperty("purecipes.storeFile")
		.orElse(providers.gradleProperty("purecipes.signing.storeFile"))
		.orElse(providers.gradleProperty("PURECIPES_SIGNING_STORE_FILE"))
		.orElse(providers.environmentVariable("PURECIPES_SIGNING_STORE_FILE"))
		.orNull
		.orEmpty()
}

private fun Project.releaseSigningStorePassword(): String {
	return providers.gradleProperty("purecipes.storePassword")
		.orElse(providers.gradleProperty("purecipes.signing.storePassword"))
		.orElse(providers.gradleProperty("PURECIPES_SIGNING_STORE_PASSWORD"))
		.orElse(providers.environmentVariable("PURECIPES_SIGNING_STORE_PASSWORD"))
		.orNull
		.orEmpty()
}

private fun Project.releaseSigningKeyAlias(): String {
	return providers.gradleProperty("purecipes.keyAlias")
		.orElse(providers.gradleProperty("purecipes.signing.keyAlias"))
		.orElse(providers.gradleProperty("PURECIPES_SIGNING_KEY_ALIAS"))
		.orElse(providers.environmentVariable("PURECIPES_SIGNING_KEY_ALIAS"))
		.orNull
		.orEmpty()
}

private fun Project.releaseSigningKeyPassword(): String {
	return providers.gradleProperty("purecipes.keyPassword")
		.orElse(providers.gradleProperty("purecipes.signing.keyPassword"))
		.orElse(providers.gradleProperty("PURECIPES_SIGNING_KEY_PASSWORD"))
		.orElse(providers.environmentVariable("PURECIPES_SIGNING_KEY_PASSWORD"))
		.orNull
		.orEmpty()
}

private fun Project.googleWebClientId(buildType: String): String {
	val buildTypeSpecific = providers.gradleProperty("purecipes.googleWebClientId.$buildType")
		.orNull
		?.takeIf { it.isNotBlank() }
	val legacy = if (buildType == "debug") {
		null
	} else {
		providers.gradleProperty("purecipes.googleWebClientId")
			.orElse(providers.gradleProperty("PURECIPES_GOOGLE_WEB_CLIENT_ID"))
			.orElse(providers.environmentVariable("PURECIPES_GOOGLE_WEB_CLIENT_ID"))
			.orNull
			?.takeIf { it.isNotBlank() }
	}
	return buildTypeSpecific ?: legacy ?: defaultGoogleWebClientId(buildType)
}

private fun defaultGoogleWebClientId(buildType: String): String {
	return when (buildType) {
		"debug" -> "740437012648-ujd18e6l3pn7co7nslloofr9fvqq08mm.apps.googleusercontent.com"
		else -> "922845075790-aiom7ev08u8uamcrlt9714kfmfumked7.apps.googleusercontent.com"
	}
}

private fun Project.gaMeasurementId(): String {
	return providers.gradleProperty("purecipes.gaMeasurementId")
		.orElse(providers.gradleProperty("PURECIPES_GA_MEASUREMENT_ID"))
		.orElse(providers.environmentVariable("PURECIPES_GA_MEASUREMENT_ID"))
		.orNull
		.orEmpty()
}

private fun Project.mixpanelProjectToken(buildType: String): String {
	return providers.gradleProperty("purecipes.mixpanelProjectToken.$buildType")
		.orElse(providers.gradleProperty("purecipes.mixpanelProjectToken"))
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

private fun Project.revenueCatApiKey(buildType: String): String {
	return providers.gradleProperty("purecipes.revenueCatApiKey.$buildType")
		.orElse(providers.gradleProperty("purecipes.revenueCatApiKey"))
		.orElse(providers.gradleProperty("PURECIPES_REVENUECAT_API_KEY"))
		.orElse(providers.environmentVariable("PURECIPES_REVENUECAT_API_KEY"))
		.orNull
		.orEmpty()
}

private fun Project.admobAppId(): String {
	return providers.gradleProperty("purecipes.adMobAppId")
		.orElse(providers.gradleProperty("PURECIPES_ADMOB_APP_ID"))
		.orElse(providers.environmentVariable("PURECIPES_ADMOB_APP_ID"))
		.orNull
		.orEmpty()
}

private fun Project.admobBannerAdUnitId(): String {
	return providers.gradleProperty("purecipes.adMobBannerAdUnitId")
		.orElse(providers.gradleProperty("PURECIPES_ADMOB_BANNER_AD_UNIT_ID"))
		.orElse(providers.environmentVariable("PURECIPES_ADMOB_BANNER_AD_UNIT_ID"))
		.orNull
		.orEmpty()
}

private fun Project.admobInterstitialAdUnitId(): String {
	return providers.gradleProperty("purecipes.adMobInterstitialAdUnitId")
		.orElse(providers.gradleProperty("PURECIPES_ADMOB_INTERSTITIAL_AD_UNIT_ID"))
		.orElse(providers.environmentVariable("PURECIPES_ADMOB_INTERSTITIAL_AD_UNIT_ID"))
		.orNull
		.orEmpty()
}

private fun Project.purecipesDebugBackendHost(): String {
	providers.gradleProperty("purecipes.debugBackendHost").orNull
		?.takeIf { it.isNotBlank() }
		?.let { return it }

	val localPropertiesFile = rootProject.file("local.properties")
	return if (!localPropertiesFile.exists()) {
		""
	} else {
		val properties = Properties()
		localPropertiesFile.inputStream().use { properties.load(it) }
		properties.getProperty("purecipes.debugBackendHost").orEmpty()
	}
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
