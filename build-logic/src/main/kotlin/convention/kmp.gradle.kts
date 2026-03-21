package convention

import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile

plugins {
	id("org.jetbrains.kotlin.multiplatform")
}

tasks.withType<Kotlin2JsCompile>().configureEach {
	if (name.contains("WasmJs")) {
		doFirst {
			incremental = false
			javaClass.methods.firstOrNull {
				it.name == "setIncrementalJsKlib\$kotlin_gradle_plugin_common"
			}?.invoke(this, false)
		}
	}
}

extensions.configure<KotlinMultiplatformExtension> {
	applyDefaultHierarchyTemplate()
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
