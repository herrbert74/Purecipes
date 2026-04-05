plugins {
	id("convention.kmp")
	alias(libs.plugins.kotlin.serialization)
}

kotlin {
	android {
		namespace = "com.purecipes.shared.domain"
	}

	sourceSets {
		commonMain {
			dependencies {
				implementation(libs.kotlinx.serializationJson)
			}
		}
	}
}
