package convention

import libs
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.targets.wasm.nodejs.WasmNodeJsEnvSpec
import org.jetbrains.kotlin.gradle.targets.wasm.nodejs.WasmNodeJsPlugin
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile

plugins {
	alias(libs.plugins.kotlin.multiplatform)
	alias(libs.plugins.androidKotlinMultiPlatformLibrary)
	id("convention.android")
}

tasks.withType<Kotlin2JsCompile>().configureEach {
	if (name.contains("WasmJs")) {
		doFirst {
			incremental = false
			javaClass.methods.firstOrNull {
				it.name == $$"setIncrementalJsKlib$kotlin_gradle_plugin_common"
			}?.invoke(this, false)
		}
	}
}

plugins.withType<WasmNodeJsPlugin> {
	extensions.configure<WasmNodeJsEnvSpec>(WasmNodeJsEnvSpec.EXTENSION_NAME) {
		download.set(false)
		downloadBaseUrl.set(null as String?)
	}
}

@OptIn(ExperimentalWasmDsl::class)
extensions.configure<KotlinMultiplatformExtension> {
	applyDefaultHierarchyTemplate()
	iosArm64()
	iosSimulatorArm64()
	jvm()
	wasmJs {
		browser()
		binaries.executable()
	}
	jvmToolchain {
		languageVersion.set(JavaLanguageVersion.of(libs.versions.jdk.get()))
	}
	sourceSets {
		commonMain.dependencies {
			implementation(libs.diamondedge.logging)
		}
		commonTest.dependencies {
			implementation(libs.kotest.assertionsCore)
			implementation(kotlin("test"))
		}
	}
}
