package app.purecipes.shared.ui.navigation

import androidx.navigation3.runtime.NavKey

interface Navigator {
	fun push(destination: NavKey)
	fun replaceTabRoot(destination: NavKey)
	fun popTo(destination: NavKey)
	fun back(): Boolean
}
