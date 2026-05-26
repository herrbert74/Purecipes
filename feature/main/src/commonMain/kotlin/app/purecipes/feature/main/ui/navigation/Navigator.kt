package app.purecipes.feature.main.ui.navigation

import androidx.navigation3.runtime.NavKey

internal interface Navigator {
	fun push(destination: NavKey)
	fun replaceTabRoot(destination: NavKey)
	fun popTo(destination: NavKey)
	fun back(): Boolean
}
