#!/usr/bin/env kotlin

@file:DependsOn("org.postgresql:postgresql:42.7.13")
@file:Import("../ScrapedIngredientLines.kt")

import java.sql.DriverManager

data class Args(
	val dbUrl: String,
	val dbUser: String,
	val dbPassword: String,
	val apply: Boolean,
)

fun parseArgs(argv: Array<String>): Args {
	var dbUrl = System.getenv("PURECIPES_DB_URL") ?: "jdbc:postgresql://localhost:5432/purecipes"
	var dbUser = System.getenv("PURECIPES_DB_USER") ?: "postgres"
	var dbPassword = System.getenv("PURECIPES_DB_PASSWORD") ?: "postgres"
	var apply = false
	argv.forEach { arg ->
		when {
			arg.startsWith("--db-url=") -> dbUrl = arg.removePrefix("--db-url=")
			arg.startsWith("--db-user=") -> dbUser = arg.removePrefix("--db-user=")
			arg.startsWith("--db-password=") -> dbPassword = arg.removePrefix("--db-password=")
			arg == "--apply" -> apply = true
		}
	}
	return Args(dbUrl = dbUrl, dbUser = dbUser, dbPassword = dbPassword, apply = apply)
}

val parsed = parseArgs(args)
Class.forName("org.postgresql.Driver")
DriverManager.getConnection(parsed.dbUrl, parsed.dbUser, parsed.dbPassword).use { connection ->
	val rows = mutableListOf<Pair<Int, String>>()
	connection.createStatement().use { statement ->
		statement.executeQuery("SELECT id, ingredient FROM ingredients WHERE ingredient IS NOT NULL").use { rs ->
			while (rs.next()) {
				rows += rs.getInt("id") to rs.getString("ingredient")
			}
		}
	}
	var updated = 0
	connection.prepareStatement("UPDATE ingredients SET ingredient = ? WHERE id = ?").use { update ->
		rows.forEach { (id, ingredient) ->
			val stripped = stripRetailerBrands(ingredient)
			if (stripped != ingredient && stripped.isNotBlank()) {
				updated++
				println("ingredient $id: '$ingredient' -> '$stripped'")
				if (parsed.apply) {
					update.setString(1, stripped)
					update.setInt(2, id)
					update.addBatch()
				}
			}
		}
		if (parsed.apply) {
			update.executeBatch()
		}
	}
	println(
		"Scanned ${rows.size} ingredients; ${if (parsed.apply) "updated" else "would update"} $updated " +
			"(checksum $SCRAPED_INGREDIENT_RULES_CHECKSUM)",
	)
}
