import org.gradle.kotlin.dsl.the
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsEnvSpec
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootEnvSpec
import org.jetbrains.kotlin.gradle.targets.wasm.nodejs.WasmNodeJsEnvSpec
import org.jetbrains.kotlin.gradle.targets.wasm.nodejs.WasmNodeJsRootPlugin
import org.jetbrains.kotlin.gradle.targets.wasm.yarn.WasmYarnPlugin
import org.jetbrains.kotlin.gradle.targets.wasm.yarn.WasmYarnRootEnvSpec

plugins {
	alias(libs.plugins.androidApplication) apply false
	alias(libs.plugins.androidLibrary) apply false
	alias(libs.plugins.buildKonfig) apply false
	alias(libs.plugins.kotlin.android) apply false
	alias(libs.plugins.kotlin.multiplatform) apply false
	alias(libs.plugins.jetBrainsCompose) apply false
	alias(libs.plugins.ksp) apply false
	alias(libs.plugins.kotlin.jvm) apply false
	alias(libs.plugins.android.kotlin.multiplatform.library) apply false
	alias(libs.plugins.android.lint) apply false
	id("convention.detekt")
}

rootProject.plugins.withType<NodeJsRootPlugin> {
	rootProject.the<NodeJsEnvSpec>().download.set(false)
}

rootProject.plugins.withType<WasmNodeJsRootPlugin> {
	rootProject.the<WasmNodeJsEnvSpec>().download.set(false)
}

rootProject.plugins.withType<YarnPlugin> {
	rootProject.the<YarnRootEnvSpec>().download.set(false)
}

rootProject.plugins.withType<WasmYarnPlugin> {
	rootProject.the<WasmYarnRootEnvSpec>().download.set(false)
}
