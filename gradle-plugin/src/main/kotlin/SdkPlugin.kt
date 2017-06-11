package sdk.plugin

import org.gradle.api.*
import org.gradle.api.tasks.*
import org.gradle.script.lang.kotlin.*

object Constants {
	const val SDK_EXT = "sdk"
	const val SDK_DEFAULT_NAME = "SdkApplication"
	const val SDK_DEFAULT_ID = "com.group.SdkApplication"
	const val SDK_DEFAULT_OUTPUT_DIR = "out/"

	const val METADATA_TASK = "genMetadata"
	const val NATIVE_DEF_TASK = "genNativeDefs"

	const val KONAN_COMPILE_TASK = "buildNative"
	const val KONAN_RUN_TASK = "runNative"
	const val KONAN_RUN_ARGUMENTS = "args"
	const val JVM_COMPILE_TASK = "buildJvm"
	const val JVM_RUN_TASK = "runJvm"
	const val ANDROID_COMPILE_TASK = "compileAndroid"
	const val ANDROID_RUN_TASK = "runAndroid"
}

internal fun Project.getTask(name: String) : Task {
	val tasks = project.getTasksByName(name, false)
	return if (tasks.isEmpty()) {
		task<DefaultTask>(name)
	} else {
		tasks.single()
	}
}

internal fun <T> Project.ext(name: String): T = extensions.findByName(name) as T
internal val Project.meta	get() = ext<SdkConfig>(Constants.SDK_EXT)
internal val Task.meta	get() = project.meta

open class SdkPlugin() : Plugin<Project> {

	override fun apply(project: Project): Unit = with(project) {
		extensions.create<SdkConfig>(Constants.SDK_EXT, SdkConfig::class.java)

		task<GenMetadataTask>(Constants.METADATA_TASK) // Include metadata task in build

		afterEvaluate {
			configureKonan() // Configures the 'buildNative' task
			configureJvm() // Configures the 'buildJvm' task
			configureAndroid() // Configures the 'buildAndroid' task
		}
	}

}