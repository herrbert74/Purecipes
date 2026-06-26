plugins {
	id("convention.data")
	id("convention.common-test")
}

kotlin {
	swiftPMDependencies {
		iosMinimumDeploymentTarget.set("26.0")
		swiftPackage(
			url = url("https://github.com/firebase/firebase-ios-sdk.git"),
			version = exact("12.14.0"),
			products = listOf(product("FirebaseAuth")),
		)
	}

	android {
		namespace = "app.purecipes.feature.auth.data"
	}

	sourceSets {
		androidMain {
			dependencies {
				implementation(project.dependencies.platform(libs.firebaseBom))
				implementation(libs.firebaseAuthKmp)
			}
		}
		iosMain {
			dependencies {
				implementation(libs.firebaseAuthKmp)
			}
		}
		commonMain {
			dependencies {
				api(project(":feature:auth:domain"))
				implementation(project(":shared:data"))
			}
		}
		commonTest {
			dependencies {
				implementation(project(":shared:dataTestFixtures"))
				implementation(project(":shared:testfixtures"))
			}
		}
	}

	compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")
}
