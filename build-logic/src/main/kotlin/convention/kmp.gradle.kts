package convention

import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
	id("org.jetbrains.kotlin.multiplatform")
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
