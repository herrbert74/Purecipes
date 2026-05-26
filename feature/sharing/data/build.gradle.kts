plugins {
	id("convention.data")
	id("convention.common-test")
}

kotlin {
	android {
		namespace = "app.purecipes.feature.sharing.data"
	}

	sourceSets {
		commonMain {
			dependencies {
				api(project(":feature:sharing:domain"))
				implementation(project(":shared:data"))
				implementation(libs.kotlinx.coroutinesCore)
			}
		}
	}

	compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")
	if (name.contains("WasmJs")) {
		compilerOptions.freeCompilerArgs.add("-opt-in=kotlin.js.ExperimentalWasmJsInterop")
	}
}
