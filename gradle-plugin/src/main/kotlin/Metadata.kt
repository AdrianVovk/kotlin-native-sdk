package sdk.plugin

import org.gradle.api.*
import org.gradle.api.tasks.*

open class GenMetadataTask : DefaultTask() {

	val appName 	get() = meta.appName
	val appId 		get() = meta.appId

	val debug		get() = meta.debug

	val mainWindow	get() = meta.mainWindow
	val supportedWindowsString: String // This creates the parameters list for the SUPPORTED_WINDOWS array
		get() = meta.windows.map { "\"$appId.${it.name}\"" }.joinToString()

	@TaskAction fun generate() {
		setDescription("Test")

		val outputFile = with(project) { file("$buildDir/sdk/metadata.kt") }
		outputFile.getParentFile().mkdirs() // Create directory

		outputFile.writeText("""

			package sdk

			object BuildMetadata {
				const val APP_NAME = "$appName"
				const val APP_ID = "$appId"

				const val DEBUG = $debug

				const val MAIN_WINDOW = "$mainWindow"
				val SUPPORTED_WINDOWS = arrayOf($supportedWindowsString)
			}

		""".trimIndent().trim())
	}

}