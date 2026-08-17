package app.purecipes.backend.tools

import app.purecipes.backend.db.Db
import app.purecipes.backend.feature.auth.AccountDeletionRepository

fun main(args: Array<String>) {
	val request = AccountDeletionRequest(
		userId = readArgumentValue(args, ARG_USER_ID)?.toLongOrNull(),
		email = readArgumentValue(args, ARG_EMAIL),
		provider = readArgumentValue(args, ARG_PROVIDER),
		execute = args.contains(ARG_EXECUTE),
	)

	println("Purecipes account deletion${if (request.execute) "" else " (dry run)"}")
	val tool = AccountDeletionTool(AccountDeletionRepository(Db.create().dataSource))
	tool.run(request).lines.forEach(::println)
}

private fun readArgumentValue(args: Array<String>, key: String): String? {
	args.forEach { arg ->
		if (arg.startsWith("$key=")) {
			return arg.removePrefix("$key=").trim()
		}
	}
	return args.toList()
		.windowed(size = 2, step = 1)
		.firstOrNull { it.first() == key }
		?.last()
}
