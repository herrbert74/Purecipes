plugins {
	alias(libs.plugins.kotlin.multiplatform)
	alias(libs.plugins.androidLibrary)
	alias(libs.plugins.kotlin.serialization)
	alias(libs.plugins.jetBrainsCompose)
	alias(libs.plugins.kotlin.composeCompiler)
}

kotlin {
	jvmToolchain {
		languageVersion.set(JavaLanguageVersion.of(libs.versions.jdk.get()))
	}
	androidTarget {
//		compilerOptions {
//			jvmTarget = JvmTarget.fromTarget(libs.versions.jdk.get())
//		}
	}

	// Temporarily commenting out wasm block
	// wasmJs {
	//     browser {
	//         commonWebpackConfig {
	//             outputFileName = "shared.js"
	//         }
	//     }
	// }

	listOf(
		iosX64(),
		iosArm64(),
		iosSimulatorArm64()
	).forEach {
		it.binaries.framework {
			baseName = "shared"
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
		// wasmJsMain is commented out as the wasmJs target is commented out
		// val wasmJsMain by getting {
		//     dependencies {
		//         implementation(compose.html.core)
		//     }
		// }
		// wasmJsTest is commented out as the wasmJs target is commented out
		// val wasmJsTest by getting {
		//     dependencies {
		//         implementation(kotlin("test-wasmjs"))
		//     }
		// }
		val iosMain by creating
	}
}

android {
	namespace = "com.purecipes.shared"
	compileSdk = 36
	defaultConfig {
		minSdk = 24
	}
}
