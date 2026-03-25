plugins {
	id("convention.kmp")
	alias(libs.plugins.kotlin.serialization)
}

kotlin {
	android {
		namespace = "com.purecipes.shared.domain"
		compileSdk = 36
		minSdk = 24
	}
	jvm()

	sourceSets {
		commonMain {
			dependencies {
				implementation(libs.kotlinx.serializationJson)
			}
		}
	}
}
