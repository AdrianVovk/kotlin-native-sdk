package sdk.plugin

import org.gradle.api.*
import org.gradle.api.tasks.*
import org.gradle.script.lang.kotlin.*

object Constants {
	const val SDK_EXT = "sdk"
	const val SDK_DEFAULT_NAME = "SdkApplication"
	const val SDK_DEFAULT_ID = "com.group.SdkApplication"
	const val SDK_DEFAULT_OUTPUT_DIR = "out/"

	const val METADATA_TASK = ":genMetadata"
	const val NATIVE_DEF_TASK = "genDefs"

	const val RUN_ARGUMENTS = "args"
	const val KONAN_COMPILE_TASK = "buildNative"
	const val KONAN_RUN_TASK = "runNative"
	const val JVM_COMPILE_TASK = "buildJvm"
	const val JVM_RUN_TASK = "runJvm"
	const val ANDROID_COMPILE_TASK = "compileAndroid"
	const val ANDROID_RUN_TASK = "runAndroid"
}

open class SdkPlugin() : Plugin<Project> {

	override fun apply(project: Project): Unit = with(project) {
		extensions.add(Constants.SDK_EXT, SdkConfig(this))
		task<GenMetadataTask>(Constants.METADATA_TASK) {
			description = "Generates a metadata class that gives the program access to build flags"
			group = "build setup"
		}

		// Sandbox setup
		val sandboxDir = file("$buildDir/sdk")
		gradle.settingsEvaluated {
			//findProject(":native").projectDir = sandboxDir
			//findProject(":jvm").projectDir = sandboxDir
			//findProject(":android").projectDir = sandboxDir
		}

		sandbox("native")?.configureKonan()
		sandbox("jvm")?.configureJvm()
		sandbox("android")?.configureAndroid()
		//TODO: Clean tasks
	}

}