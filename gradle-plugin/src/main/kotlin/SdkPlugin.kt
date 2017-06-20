package sdk.plugin

import org.gradle.api.*
import org.gradle.api.tasks.*
import org.gradle.script.lang.kotlin.*

object Constants {
	const val SDK_EXT = "sdk"

	// Shared/Generic
	const val GENERIC_BUILD_TASK = "build"
	const val GENERIC_RUN_TASK = "run"
	const val METADATA_TASK = "genMetadata"
	const val MOVE_TASK = "moveOutputs"
	const val CLEAN_TASK = "clean"
	const val RUN_ARGUMENTS = "args"

	// Native
	const val KONAN_COMPILE_TASK = "buildNative"
	const val KONAN_RUN_TASK = "runNative"
	const val KONAN_DEF_TASK = "genDefs"

	// JVM
	const val JVM_COMPILE_TASK = "buildJvm"
	const val JVM_RUN_TASK = "runJvm"

	// Android
	const val ANDROID_COMPILE_TASK = "compileAndroid"
	const val ANDROID_RUN_TASK = "runAndroid"
	const val ANDROID_MANIFEST_TASK = "genManifest"

	// Android SDK
	const val ANDROID_SDK_TASK = "installSdk"
	const val ANDROID_SDK_INSTALL_ARGUMENT = "sdk.dir"
	const val ANDROID_SDK_LICENSES_ARGUMENT = "autoaccept-licenses"
}

open class SdkPlugin() : Plugin<Project> {

	override fun apply(project: Project): Unit = with(project) {
		extensions.add(Constants.SDK_EXT, SdkConfig(this))
		task<GenMetadataTask>(Constants.METADATA_TASK)

		afterEvaluate {
			if (meta.appId == "NONE") throw GradleException("Please specify an appId")

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