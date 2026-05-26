package app.purecipes.shared.data.network

import app.purecipes.shared.data.config.PurecipesBuildType
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class PlatformNetworkTest {

	@Test
	fun `debug backend host override is used`() {
		backendBaseUrl(PurecipesBuildType.DEBUG, "192.168.1.42") shouldBe "http://192.168.1.42:9090/"
	}

	@Test
	fun `formatDebugBackendBaseUrl trims host`() {
		formatDebugBackendBaseUrl(" 10.0.2.2 ") shouldBe "http://10.0.2.2:9090/"
	}
}
