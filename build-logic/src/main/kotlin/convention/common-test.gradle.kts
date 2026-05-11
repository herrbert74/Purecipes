package convention

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

extensions.configure<KotlinMultiplatformExtension> {
	targets.withType<KotlinMultiplatformAndroidLibraryTarget>().configureEach {
		/*
		This is only needed to suppress the warning:
		WARNING: The 'commonTest' source directory exists, but android host tests are not enabled.
		To enable android host tests, add `withHostTest {}` to your android target configuration in
		the Gradle build file.
		This creates two tasks for each module, so it's not ideal. */
		withHostTestBuilder {
		}
	}
}
