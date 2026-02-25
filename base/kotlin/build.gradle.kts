plugins {
	kotlin("jvm")
	alias(libs.plugins.metro)
}

kotlin {
	jvmToolchain {
		languageVersion.set(JavaLanguageVersion.of(libs.versions.jdk.get()))
	}
}

dependencies {
	api(libs.kotlinResult.result)
	api(libs.kotlinx.coroutinesCore)
	api(libs.metro.runtime)

	testImplementation(kotlin("test"))
}
