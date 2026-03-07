plugins {
	`kotlin-dsl`
}

repositories {
	mavenCentral()
	google()
}

kotlin {
	jvmToolchain(21)
}

dependencies {
	implementation(libs.plugins.detekt.get().run { "$pluginId:$pluginId.gradle.plugin:$version" })
	implementation(files(libs.javaClass.protectionDomain.codeSource.location))
}
