package convention

import libs
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
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

@OptIn(ExperimentalWasmDsl::class)
extensions.configure<KotlinMultiplatformExtension> {
	applyDefaultHierarchyTemplate()
	iosArm64()
	iosSimulatorArm64()
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
			implementation(kotlin("test"))
		}
	}
}
