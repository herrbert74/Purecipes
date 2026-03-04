package com.purecipes.feature.search.repository

sealed class SearchFailure(open val message: String) {
	data class ServerError(override val message: String) : SearchFailure(message)
	data object IoFailure : SearchFailure("IO failure")
}
