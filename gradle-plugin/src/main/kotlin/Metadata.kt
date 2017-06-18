package sdk.plugin

import org.gradle.api.*
import org.gradle.api.tasks.*

open class GenMetadataTask : DefaultTask() {

	@TaskAction fun generate() {
		val outputFile = with(project) { file("$buildDir/sdk/gen/metadata.kt") }
		outputFile.getParentFile().mkdirs() // Create directory

		outputFile.writeText("""

			package sdk.gen

			object BuildMetadata {
				const val APP_NAME = "${meta.appName}"
				const val APP_ID = "${meta.appId}"

				const val DEBUG = ${meta.debug}

				const val MAIN_WINDOW = "${meta.windows.main}"
				val SUPPORTED_WINDOWS = arrayOf<String>(${meta.windows.supported.map { "\"${meta.appId}.${it.name}\"" }.joinToString()})
			}

		""".trimIndent().trim())
	}

}