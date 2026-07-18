@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
	id("convention.data")
	id("convention.common-test")
}

kotlin {
	android {
		namespace = "app.purecipes.feature.subscription.data"
	}

	sourceSets {
		val mobileMain by creating {
			dependsOn(commonMain.get())
			dependencies {
				implementation(libs.revenuecat.purchasesKmpCore)
			}
		}
		androidMain.get().dependsOn(mobileMain)
		iosMain.get().dependsOn(mobileMain)

		commonMain {
			dependencies {
				api(project(":feature:subscription:domain"))
				implementation(libs.kotlinx.coroutinesCore)
				implementation(libs.kotlinx.datetime)
				implementation(libs.multiplatformSettings.noargs)
			}
		}
		commonTest {
			dependencies {
				implementation(project(":shared:testfixtures"))
			}
		}
	}

	compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")
	compilerOptions.freeCompilerArgs.add("-opt-in=kotlin.time.ExperimentalTime")
	targets.withType<KotlinNativeTarget>().configureEach {
		compilerOptions.freeCompilerArgs.add("-opt-in=kotlinx.cinterop.ExperimentalForeignApi")
	}
}
