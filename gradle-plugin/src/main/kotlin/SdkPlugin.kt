package sdk.plugin

import org.gradle.api.*
import org.gradle.api.tasks.*
import org.gradle.script.lang.kotlin.*

object Constants {
	const val SDK_EXT = "sdk"
	const val SDK_DEFAULT_NAME = "SdkApplication"
	const val SDK_DEFAULT_ID = "com.group.myapp"
	const val SDK_DEFAULT_OUTPUT_DIR = "out/"

	const val METADATA_TASK = "genMetadata"
	const val NATIVE_DEF_TASK = "genDefs"
	const val ANDROID_MANIFEST_TASK = "genManifest"
	const val ANDROID_SDK_TASK = "installSdk"
	const val MOVE_TASK = "moveOutputs"
	const val CLEAN_TASK = "clean"

	const val GENERIC_BUILD_TASK = "build"
	const val GENERIC_RUN_TASK = "run"

	const val RUN_ARGUMENTS = "args"
	const val ANDROID_SDK_INSTALL_ARGUMENT = "sdk.dir"
	const val ANDROID_SDK_LICENSES_AUTOACCEPT = "autoaccept"

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
		task<GenMetadataTask>(Constants.METADATA_TASK)

		afterEvaluate {
			sandbox("native")?.configureKonan()
			sandbox("jvm")?.configureJvm()
			sandbox("android")?.configureAndroid()

			if (meta.modifyTasksReport) modTasksReport()
		}

		task<Delete>(Constants.CLEAN_TASK) {
			delete(buildDir)
			delete(meta.outputDir)
		}
	}

}