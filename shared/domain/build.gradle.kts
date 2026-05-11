plugins {
	id("convention.kmp")
	id("convention.common-test")
	alias(libs.plugins.kotlin.serialization)
}

kotlin {
	compilerOptions {
		freeCompilerArgs.add("-opt-in=kotlinx.serialization.ExperimentalSerializationApi")
	}

	android {
		namespace = "app.purecipes.shared.domain"
	}

	sourceSets {
		commonMain {
			dependencies {
				api(libs.kotlinx.coroutinesCore)
				api(libs.kotlinx.serializationJson)
			}
		}
	}
}
