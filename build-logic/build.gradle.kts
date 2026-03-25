plugins {
	`kotlin-dsl`
}

kotlin {
	jvmToolchain(21)
}

dependencies {
	implementation("com.android.tools.build:gradle:${libs.versions.androidGradlePlugin.get()}")
	implementation(libs.plugins.detekt.get().run { "$pluginId:$pluginId.gradle.plugin:$version" })
	implementation(libs.plugins.jetBrainsCompose.get().run { "$pluginId:$pluginId.gradle.plugin:$version" })
	implementation(libs.plugins.kotlin.multiplatform.get().run { "$pluginId:$pluginId.gradle.plugin:$version" })
	implementation(libs.plugins.kotlin.composeCompiler.get().run { "$pluginId:$pluginId.gradle.plugin:$version" })
	implementation(libs.plugins.metro.get().run { "$pluginId:$pluginId.gradle.plugin:$version" })
}
