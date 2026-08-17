plugins {
	id("convention.kmp")
	id("convention.common-test")
	id("dev.zacsweers.metro")
	alias(libs.plugins.kotlin.serialization)
	alias(libs.plugins.ktorfit)
	alias(libs.plugins.ksp)
}

kotlin {
	android {
		namespace = "app.purecipes.shared.data"
	}

	sourceSets {
		commonMain {
			// mirror the source dir that the Ktorfit plugin normally wires for metadata generation.
			kotlin.srcDir(layout.buildDirectory.dir("generated/ksp/metadata/commonMain/kotlin"))
			dependencies {
				implementation(project(":base:kotlin"))
				api(project(":shared:domain"))
				implementation(libs.kmpnotifier.push.firebase)
				implementation(libs.kotlinResult.result)
				implementation(libs.kotlinRetry)
				implementation(libs.kotlinx.collectionsImmutable)
				implementation(libs.kotlinx.serializationJson)
				implementation(libs.ktor.clientCore)
				implementation(libs.ktor.clientContentNegotiation)
				implementation(libs.ktor.clientLogging)
				implementation(libs.ktor.serializationKotlinxJson)
				implementation(libs.ktorfit.annotations)
				implementation(libs.ktorfit.convertersResponse)
				implementation(libs.ktorfit.lib)
				implementation(libs.multiplatformSettings.noargs)
			}
		}
		commonTest {
			dependencies {
				implementation(libs.kotest.assertionsCore)
				implementation(libs.kotlinx.coroutinesTest)
				implementation(libs.ktor.clientMock)
			}
		}
		androidMain {
			dependencies {
				implementation(project(":base:kotlin"))
				implementation(libs.androidx.startupRuntime)
				implementation(libs.ktor.clientOkhttp)
			}
		}
		iosMain {
			dependencies {
				implementation(libs.ktor.clientDarwin)
			}
		}
		jvmMain {
			dependencies {
				implementation(libs.ktor.clientCio)
			}
		}
		wasmJsMain {
			dependencies {
				implementation(libs.ktor.clientJs)
			}
		}
	}
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>().configureEach {
	if (name != "kspCommonMainKotlinMetadata") {
		dependsOn("kspCommonMainKotlinMetadata")
	}
	compilerOptions.freeCompilerArgs.add("-opt-in=kotlin.contracts.ExperimentalContracts")
}

tasks.matching { it.name.startsWith("ksp") && it.name != "kspCommonMainKotlinMetadata" }.configureEach {
	dependsOn("kspCommonMainKotlinMetadata")
}

dependencies {
	add("kspCommonMainMetadata", libs.ktorfit.ksp)
	add("kspAndroidMain", libs.ktorfit.ksp)
	add("kspWasmJs", libs.ktorfit.ksp)
}
