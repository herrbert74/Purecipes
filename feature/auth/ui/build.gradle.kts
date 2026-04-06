import org.jetbrains.kotlin.gradle.targets.native.tasks.PodBuildTask
import java.util.Properties

plugins {
	id("convention.ui")
	id("org.jetbrains.kotlin.native.cocoapods")
}

tasks.withType<PodBuildTask>().configureEach {
	notCompatibleWithConfigurationCache("Custom auth pod build action runs xcodebuild with -jobs 1")
	actions.clear()
	doLast {
		val syntheticIosDir = layout.buildDirectory.dir("cocoapods/synthetic/ios").get().asFile
		val podsDir = syntheticIosDir.resolve("Pods")
		val podsXcodeProjFile = podsDir.resolve("Pods.xcodeproj")
		val targetSupportFilesDir = syntheticIosDir.resolve("Pods/Target Support Files")
		val buildConfigurations = listOf(
			"Debug-iphoneos",
			"Debug-iphonesimulator",
			"Release-iphoneos",
			"Release-iphonesimulator",
		)
		targetSupportFilesDir.listFiles()
			?.filter { it.isDirectory }
			?.forEach { targetDirectory ->
				buildConfigurations.forEach { configuration ->
					syntheticIosDir
						.resolve("build/Pods.build/$configuration/${targetDirectory.name}.build")
						.mkdirs()
				}
			}

		val buildSettings = Properties().apply {
			buildSettingsFile.get().asFile.inputStream().use { input -> load(input) }
		}
		val schemeName = name.removePrefix("podBuild").removeSuffix("IosSimulator").removeSuffix("Ios")
		val destination = targetDeviceIdentifier.orNull?.let { "id=$it" }
			?: if (name.endsWith("IosSimulator")) "generic/platform=iOS Simulator" else "generic/platform=iOS"
		val command = mutableListOf(
			"xcodebuild",
			"-project", podsXcodeProjFile.name,
			"-scheme", schemeName,
			"-destination", destination,
			"-configuration", buildSettings.getProperty("CONFIGURATION"),
			"-jobs", "1",
		)
		xcodeBuildSettings.getOrElse(emptyMap()).forEach { (key, value) ->
			command += "$key=$value"
		}
		val process = ProcessBuilder(command)
			.directory(podsDir)
			.inheritIO()
			.start()
		val exitCode = process.waitFor()
		if (exitCode != 0) {
			throw GradleException("Command failed with exit code $exitCode: ${command.joinToString(" ")}")
		}
	}
}

kotlin {
	cocoapods {
		version = "1.0"
		summary = "Purecipes authentication UI"
		homepage = "https://github.com/zsoltbertalan/Purecipes"
		ios.deploymentTarget = "26.0"
		podfile = project.file("../../../iosApp/PurecipesIOSApp/Podfile")
		pod("AppAuth")
		pod("FBSDKCoreKit", "18.0.0")
		pod("FBSDKLoginKit", "18.0.0")
		pod("FirebaseAuth")
		pod("FirebaseCore")
		pod("GTMAppAuth")
		pod("GoogleSignIn")
	}

	android {
		namespace = "com.purecipes.feature.auth.ui"
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":feature:analytics:domain"))
				api(project(":feature:auth:domain"))
				implementation(libs.coil.compose)
				implementation(libs.coil.networkKtor3)
				implementation(libs.kmpauth.uihelper)
			}
		}
		commonTest {
			dependencies {
				implementation(project(":shared:testfixtures"))
			}
		}
		androidMain {
			dependencies {
				implementation(project.dependencies.platform(libs.firebaseBom))
				implementation(libs.firebaseAuth)
				implementation(libs.kmpauth.firebase)
				implementation(libs.kmpauth.firebaseFacebook)
				implementation(libs.kmpauth.google)
			}
		}
		iosMain {
			dependencies {
				implementation(libs.kmpauth.firebase)
				implementation(libs.kmpauth.firebaseFacebook)
				implementation(libs.kmpauth.google)
			}
		}
		wasmJsMain {
			dependencies {
				implementation(libs.kmpauth.google)
			}
		}
	}
}
