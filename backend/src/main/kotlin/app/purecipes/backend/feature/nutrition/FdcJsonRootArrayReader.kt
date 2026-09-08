package app.purecipes.backend.feature.nutrition

import java.io.File
import java.io.Reader

internal object FdcJsonRootArrayReader {

	fun forEachObject(file: File, rootKey: String, action: (String) -> Unit): Int =
		file.reader(Charsets.UTF_8).buffered(BUFFER_SIZE).use { reader ->
			skipUntilRootArray(reader, rootKey)
			readArrayObjects(reader, action)
		}

	private fun skipUntilRootArray(reader: Reader, rootKey: String) {
		val needle = "\"$rootKey\""
		val window = StringBuilder()
		while (true) {
			val code = reader.read()
			if (code < 0) {
				error("FDC JSON missing $rootKey array")
			}
			window.append(code.toChar())
			if (window.length > needle.length) {
				window.deleteCharAt(0)
			}
			if (window.toString() == needle) {
				break
			}
		}
		expect(reader, ':')
		expect(reader, '[')
	}

	private fun readArrayObjects(reader: Reader, action: (String) -> Unit): Int {
		var count = 0
		while (true) {
			when (val char = nextNonWhitespace(reader)) {
				null -> error("FDC JSON array was truncated")
				']' -> return count
				',' -> continue
				'{' -> {
					action(readObject(reader))
					count++
				}

				else -> error("Unexpected character '$char' in FDC JSON array")
			}
		}
	}

	private fun readObject(reader: Reader): String {
		val text = StringBuilder("{")
		var depth = 1
		var inString = false
		var escape = false
		while (depth > 0) {
			val code = reader.read()
			if (code < 0) {
				error("FDC JSON object was truncated")
			}
			val char = code.toChar()
			text.append(char)
			when {
				inString && escape -> escape = false
				inString && char == '\\' -> escape = true
				inString && char == '"' -> inString = false
				inString -> Unit
				char == '"' -> inString = true
				char == '{' -> depth++
				char == '}' -> depth--
			}
		}
		return text.toString()
	}

	private fun nextNonWhitespace(reader: Reader): Char? {
		while (true) {
			val code = reader.read()
			if (code < 0) {
				return null
			}
			val char = code.toChar()
			if (!char.isWhitespace()) {
				return char
			}
		}
	}

	private fun expect(reader: Reader, expected: Char) {
		val actual = nextNonWhitespace(reader)
		if (actual != expected) {
			error("Expected '$expected' in FDC JSON, found '$actual'")
		}
	}

	private const val BUFFER_SIZE = 64 * 1024
}
