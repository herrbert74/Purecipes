package convention

import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project

val Project.libs: LibrariesForLibs
	get() = extensions.getByName("libs") as LibrariesForLibs
