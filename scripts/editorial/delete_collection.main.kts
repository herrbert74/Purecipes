#!/usr/bin/env kotlin
@file:DependsOn("org.jetbrains.kotlinx:kotlinx-serialization-json:1.12.0")
@file:Import("EditorialCollections.kt")

require(EDITORIAL_SCRIPTS_REVISION == "4")
mainEditorial(::runDeleteCollection)
