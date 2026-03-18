plugins {
    id("convention.kmp")
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.metro)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
kotlin {
    // targets used across the project
    androidTarget()

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

android {
    namespace = "com.purecipes.base.kotlin"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }
}
