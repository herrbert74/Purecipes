plugins {
	id("convention.kmp")
	alias(libs.plugins.kotlin.serialization)
	// Applied before KSP to avoid Ktorfit issue #1030 auto-registering the deprecated root `ksp` configuration.
	alias(libs.plugins.ktorfit)
	alias(libs.plugins.ksp)
	alias(libs.plugins.metro)
}

/**
 * Temporary fix for Ktorfit KSP issue, which is still not resolved in Ktorfit 2.7.2
 * https://github.com/Foso/Ktorfit/issues/1010
 */
ktorfit {
	compilerPluginVersion.set("2.3.3")
}

/**
 * Part of the issue #1030 workaround: once Ktorfit stops adding Android MPP dependencies to `ksp`,
 * this manual KSP configuration can be removed together with the extra task wiring and dependency block below.
 * https://github.com/Foso/Ktorfit/issues/1030
 */
ksp {
	arg("Ktorfit_Errors", "1")
	arg("Ktorfit_QualifiedTypeName", false.toString())
}

kotlin {
	android {
		namespace = "com.purecipes.shared.data"
		compileSdk = 36
		minSdk = 24
	}

	sourceSets {
		commonMain {
			// Issue #1030 workaround: mirror the source dir that the Ktorfit plugin normally wires for metadata generation.
			kotlin.srcDir(layout.buildDirectory.dir("generated/ksp/metadata/commonMain/kotlin"))
			dependencies {
				implementation(project(":base:kotlin"))
				api(project(":shared:domain"))
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
				implementation(libs.ktor.clientOkhttp)
			}
		}
		iosMain {
			dependencies {
				implementation(libs.ktor.clientDarwin)
			}
		}
		wasmJsMain {
			dependencies {
				implementation(libs.ktor.clientJs)
			}
		}
	}
}

// Issue #1030 workaround: make metadata generation run before Kotlin compilation tasks that consume generated Ktorfit code.
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>().configureEach {
	if (name != "kspCommonMainKotlinMetadata") {
		dependsOn("kspCommonMainKotlinMetadata")
	}
}

// Issue #1030 workaround: Gradle 9 task validation also requires explicit ordering for KSP tasks themselves.
tasks.matching { it.name.startsWith("ksp") && it.name != "kspCommonMainKotlinMetadata" }.configureEach {
	dependsOn("kspCommonMainKotlinMetadata")
}

// Issue #1030 workaround: target-specific KSP dependencies replace Ktorfit's deprecated internal `dependencies.add("ksp", ...)`.
dependencies {
	add("kspCommonMainMetadata", libs.ktorfit.ksp)
	add("kspAndroidMain", libs.ktorfit.ksp)
	add("kspWasmJs", libs.ktorfit.ksp)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>().configureEach {
	compilerOptions.freeCompilerArgs.add("-opt-in=kotlin.contracts.ExperimentalContracts")
}
