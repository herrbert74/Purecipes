plugins {
	alias(libs.plugins.kotlin.jvm)
}

tasks.withType<Test>().configureEach {
	useJUnitPlatform()
}

tasks.register("jvmTest") {
	group = "verification"
	description = "Runs JVM unit tests."
	dependsOn(tasks.test)
}

dependencies {
	// Workaround for detekt-test requesting unpublished detekt-api-test-fixtures: https://github.com/detekt/detekt/issues/9409
	components {
		withModule("dev.detekt:detekt-test") {
			withVariant("runtimeElements") {
				withDependencies {
					removeAll { it.group == "dev.detekt" && it.name == "detekt-api" }
					add("dev.detekt:detekt-api:${id.version}")
				}
			}
		}
	}
	compileOnly(libs.detekt.api)
	implementation(libs.detekt.psi.utils)
	testImplementation(libs.detekt.test)
	testImplementation(libs.kotest.assertionsCore)
	testImplementation(libs.assertj)
	testImplementation(libs.jUnit5.jupiterApi)
	testRuntimeOnly(libs.jUnit5.jupiterEngine)
	testRuntimeOnly(libs.jUnit5.platformLauncher)
}
