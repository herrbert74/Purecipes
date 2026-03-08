plugins {
	`kotlin-dsl`
}

kotlin {
	jvmToolchain(21)
}

dependencies {
	implementation(libs.plugins.detekt.get().run { "$pluginId:$pluginId.gradle.plugin:$version" })
}
