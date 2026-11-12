package app.purecipes.backend.tools

import app.purecipes.backend.db.Db

private const val ARG_SOURCE_URL_PREFIX = "--source-url="
private const val ARG_SOURCE_USER_PREFIX = "--source-user="
private const val ARG_SOURCE_PASSWORD_PREFIX = "--source-password="
private const val ARG_DRY_RUN = "--dry-run"
private const val DEFAULT_LOCAL_DB_URL = "jdbc:postgresql://localhost:5432/purecipes"
private const val DEFAULT_LOCAL_DB_USER = "postgres"
private const val DEFAULT_LOCAL_DB_PASSWORD = "postgres"

fun main(args: Array<String>) {
	val sourceUrl = readPrefixArg(args, ARG_SOURCE_URL_PREFIX)
		?: System.getenv("PURECIPES_SOURCE_DB_URL")
		?: DEFAULT_LOCAL_DB_URL
	val sourceUser = readPrefixArg(args, ARG_SOURCE_USER_PREFIX)
		?: System.getenv("PURECIPES_SOURCE_DB_USER")
		?: DEFAULT_LOCAL_DB_USER
	val sourcePassword = readPrefixArg(args, ARG_SOURCE_PASSWORD_PREFIX)
		?: System.getenv("PURECIPES_SOURCE_DB_PASSWORD")
		?: DEFAULT_LOCAL_DB_PASSWORD
	val dryRun = args.contains(ARG_DRY_RUN)

	val targetUrl = System.getenv("PURECIPES_DB_URL") ?: DEFAULT_LOCAL_DB_URL
	if (sourceUrl == targetUrl && !dryRun) {
		error(
			"Source and target database URLs are identical ($targetUrl). " +
				"Set PURECIPES_DB_URL to the target (for example release tunnel) database.",
		)
	}

	val sourceDb = Db.create(url = sourceUrl, user = sourceUser, password = sourcePassword)
	val targetDb = Db.create()

	println("Syncing nutrition catalogue${if (dryRun) " (dry run)" else ""} from $sourceUrl to $targetUrl")
	val summary = NutritionCatalogueSyncer(
		sourceDataSource = sourceDb.dataSource,
		targetDataSource = targetDb.dataSource,
	).sync(dryRun = dryRun)

	println(
		"Nutrition catalogue sync complete: " +
			"foods=${summary.sourceFoodCount} " +
			"(+${summary.insertedFoodCount} inserted, ~${summary.updatedFoodCount} updated), " +
			"aliases=${summary.sourceAliasCount} " +
			"(+${summary.insertedAliasCount} inserted, ~${summary.updatedAliasCount} updated), " +
			"measures=${summary.sourceMeasureCount} " +
			"(+${summary.insertedMeasureCount} inserted, ~${summary.updatedMeasureCount} updated)",
	)
}

private fun readPrefixArg(args: Array<String>, prefix: String): String? =
	args.firstOrNull { it.startsWith(prefix) }
		?.removePrefix(prefix)
		?.takeIf(String::isNotBlank)
