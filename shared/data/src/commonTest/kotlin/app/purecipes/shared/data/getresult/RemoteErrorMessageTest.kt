package app.purecipes.shared.data.getresult

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class RemoteErrorMessageTest {

	@Test
	fun `extracts fortiguard html error into readable message`() {
		val html = """
			<!DOCTYPE html>
			<html>
			<head><title>Web Filter Block override</title></head>
			<body>
			<h1>FortiGuard Intrusion prevention</h1>
			<h3>Web page blocked</h3>
			<p>You have tried to access a web page which is in violation of your internet usage policy.</p>
			<p>Url: http://185.132.41.132/google/auth</p>
			</body>
			</html>
		""".trimIndent()

		html.toUserFacingRemoteErrorMessage() shouldBe
			"Web Filter Block override. FortiGuard Intrusion prevention. Web page blocked"
	}

	@Test
	fun `maps generic html without readable text to fallback`() {
		val html = "<html><body><img src=\"x\"/><br/></body></html>"

		html.toUserFacingRemoteErrorMessage() shouldBe DEFAULT_SERVER_ERROR_MESSAGE
	}

	@Test
	fun `maps blocked html comment to network blocked message`() {
		val html = "<html><body><!-- fortiguard web page blocked --></body></html>"

		html.toUserFacingRemoteErrorMessage() shouldBe NETWORK_BLOCKED_MESSAGE
	}

	@Test
	fun `returns plain api error unchanged`() {
		"Invalid request".toUserFacingRemoteErrorMessage() shouldBe "Invalid request"
	}

	@Test
	fun `maps long technical message to fallback`() {
		val message = "x".repeat(301)

		message.toUserFacingRemoteErrorMessage() shouldBe DEFAULT_SERVER_ERROR_MESSAGE
	}

	@Test
	fun `extracts html embedded in json conversion error`() {
		val message = """
			Illegal input: Unexpected JSON token at offset 0: Expected start of the object '{', but had '<' instead
			<!DOCTYPE html><html><head><title>Proxy error</title></head><body><h1>Access denied</h1></body></html>
		""".trimIndent()

		message.toUserFacingRemoteErrorMessage() shouldBe "Proxy error. Access denied"
	}
}
