package sdk.plugin

import org.gradle.api.*
import org.gradle.api.tasks.*

open class GenMetadataTask : DefaultTask() {

	val appName 	get() = meta.appName
	val appId 		get() = meta.appId

	val mainWindow	get() = meta.mainWindow

	@TaskAction fun generate() {
		description = "Test"
		println("Generating metadata.kt for $appName ($appId)")

		val outputFile = with(project) { file("$buildDir/sdk/metadata.kt") }
		outputFile.getParentFile().mkdirs() // Create directory

		outputFile.writeText("""
package sdk

object BuildMetadata {
	const val APP_NAME = "$appName"
	const val APP_ID = "$appId"

	const val MAIN_WINDOW = "$mainWindow"
}
		""")
	}

}