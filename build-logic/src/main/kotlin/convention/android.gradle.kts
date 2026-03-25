package convention

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import libs
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

extensions.configure<KotlinMultiplatformExtension> {
	targets.withType<KotlinMultiplatformAndroidLibraryTarget>().configureEach {
		compileSdk = libs.versions.compileSdkVersion.get().toInt()
		minSdk = libs.versions.minSdkVersion.get().toInt()
	}
}
