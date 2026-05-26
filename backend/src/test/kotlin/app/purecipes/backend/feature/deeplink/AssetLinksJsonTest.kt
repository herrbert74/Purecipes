package app.purecipes.backend.feature.deeplink

import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlin.test.Test

class AssetLinksJsonTest {

	@Test
	fun `asset links json uses digital asset links snake_case field names`() {
		val payload = listOf(
			AssetLinksEntry(
				relation = listOf("delegate_permission/common.handle_all_urls"),
				target = AssetLinksTarget(
					namespace = "android_app",
					packageName = "app.purecipes.debug",
					sha256CertFingerprints = listOf(
						"18:0D:AE:62:87:2E:E9:0A:A0:18:FD:76:23:EB:3E:37:62:94:78:C3:3A:57:B2:4E:" +
							"DE:51:A0:0D:00:4D:4B:53",
					),
				),
			),
		)
		val json = Json.encodeToString(ListSerializer(AssetLinksEntry.serializer()), payload)
		json shouldContain """"package_name":"app.purecipes.debug""""
		json shouldContain """"sha256_cert_fingerprints""""
		json shouldContain "18:0D:AE:62"
		json shouldNotContain "18:0d:ae:62"
		json shouldNotContain "packageName"
		json shouldNotContain "sha256CertFingerprints"
	}
}
