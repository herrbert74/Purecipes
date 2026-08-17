plugins {
	`kotlin-dsl`
}

kotlin {
	jvmToolchain {
		languageVersion.set(JavaLanguageVersion.of(libs.versions.jdk.get()))
	}
}

dependencies {
	implementation(libs.agp)
	implementation(libs.plugins.detekt.get().run { "$pluginId:$pluginId.gradle.plugin:$version" })
	implementation(libs.plugins.jetBrainsCompose.get().run { "$pluginId:$pluginId.gradle.plugin:$version" })
	implementation(libs.plugins.kotlin.multiplatform.get().run { "$pluginId:$pluginId.gradle.plugin:$version" })
	implementation(libs.plugins.kotlin.composeCompiler.get().run { "$pluginId:$pluginId.gradle.plugin:$version" })
	implementation(libs.plugins.metro.get().run { "$pluginId:$pluginId.gradle.plugin:$version" })
}
