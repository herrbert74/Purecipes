plugins {
	id("convention.kmp")
	alias(libs.plugins.androidKotlinMultiPlatformLibrary)
	alias(libs.plugins.metro)
}

kotlin {
	android {
		namespace = "com.purecipes.feature.search.data"
		compileSdk = 36
		minSdk = 24
	}


	sourceSets {
		commonMain {
			dependencies {
				api(project(":feature:search:domain"))
				implementation(project(":shared:data"))
				implementation(libs.kotlinResult.result)
			}
		}
		commonTest {
			dependencies {
				implementation(libs.kotlinx.coroutinesTest)
			}
		}
	}
}
