plugins {
	alias(libs.plugins.kotlin.multiplatform)
	alias(libs.plugins.androidLibrary)
	alias(libs.plugins.kotlin.serialization)
	alias(libs.plugins.jetBrainsCompose)
	alias(libs.plugins.kotlin.composeCompiler)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
kotlin {
	jvmToolchain {
		languageVersion.set(JavaLanguageVersion.of(libs.versions.jdk.get()))
	}
	androidTarget {
//		compilerOptions {
//			jvmTarget = JvmTarget.fromTarget(libs.versions.jdk.get())
//		}
	}

	wasmJs {
		browser {
			commonWebpackConfig {
				outputFileName = "umbrella.js"
			}
		}
		binaries.executable()
	}

	listOf(
		iosX64(),
		iosArm64(),
		iosSimulatorArm64()
	).forEach {
		it.binaries.framework {
			baseName = "umbrella"
			isStatic = true
		}
	}

	sourceSets {
		val commonMain by getting {
			dependencies {
				implementation(compose.runtime)
				implementation(compose.foundation)
				implementation(compose.material3)
				implementation(compose.materialIconsExtended)
				implementation(libs.kotlinx.coroutinesCore)
				implementation(libs.kotlinx.serializationJson)
			}
		}
		val commonTest by getting {
			dependencies {
				implementation(kotlin("test"))
			}
		}
		val androidMain by getting {
			dependencies {
				implementation(libs.androidx.core)
				implementation(libs.kotlinx.coroutinesAndroid)
			}
		}
		val wasmJsMain by getting
	}
}

android {
	namespace = "com.purecipes.umbrella"
	compileSdk = 36
	defaultConfig {
		minSdk = 24
	}
}
