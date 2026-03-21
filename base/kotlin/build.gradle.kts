plugins {
    id("convention.kmp")
    alias(libs.plugins.androidKotlinMultiPlatformLibrary)
    alias(libs.plugins.metro)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
kotlin {
    // targets used across the project
    android {
        namespace = "com.purecipes.base.kotlin"
        compileSdk = 36
        minSdk = 24
    }

    wasmJs {
        browser()
        binaries.executable()
    }

    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain {
            dependencies {
                api(libs.kotlinResult.result)
                api(libs.kotlinx.coroutinesCore)
                api(libs.metro.runtime)
            }
        }
    }
}
