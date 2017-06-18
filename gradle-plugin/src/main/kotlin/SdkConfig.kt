package sdk.plugin

import org.gradle.api.Project

open class SdkConfig(val project: Project) {
	var debug = true // Debugging by default, "relea	se" task changes it
	var suppressPlatformWarning = false // Suppreses warning if platform isn't included in build
	var modifyTasksReport = true // Modify the output of the `tasks` task

	var appName = Constants.SDK_DEFAULT_NAME
	var appId = Constants.SDK_DEFAULT_ID

	var outputDir = Constants.SDK_DEFAULT_OUTPUT_DIR

	/////////////////////////////////////
	// Shared Platform class
	/////////////////////////////////////

	// Shared configuration for platforms
	open class Platform(var buildConfig: Project.() -> Unit = {}, val inputDirs: MutableList<String> = mutableListOf()) {
		fun configure(config: Project.() -> Unit) { buildConfig = config }

		fun inputDir(dir: String) = inputDirs.add(dir)
	}

	/////////////////////////////////////
	// Native
	/////////////////////////////////////

	data class NativeConf(val root: SdkConfig,
		var optimize: Boolean = true,
		val interops: MutableList<InteropConf> = mutableListOf(),
		val defFilesToGenerate: MutableList<DefFile> = mutableListOf(),
		var linkerOpts: String = "NONE") : Platform() {

		data class InteropConf(val name: String, val defFile: String, val pkg: String)
			data class DefFile(val name: String,
			var headers: String = "NONE",
			var compilerOpts: String = "NONE",
			var headerFilter: String = "NONE",
			var excludeDependentModules: Boolean? = null, /* TODO: Default value? */
			var includeC: String = "NONE") {

			fun headers(vararg files: String) {
				headers = files.joinToString(" ")
			}
			fun compilerOpts(vararg opts: String) {
				compilerOpts = opts.joinToString(" ")
			}
		}
		fun interop(name: String, defFile: String, pkg: String = "NONE") = interops.add(InteropConf(name, defFile, pkg))
		fun interop(name: String, pkg: String = "NONE", configure: DefFile.() -> Unit) {
			// Create the def file
			val def = DefFile(name)
			def.configure()
			defFilesToGenerate.add(def)

			// Create the interop object
			interops.add(InteropConf(name, defFile = "${root.project.buildDir}/sdk/nativeDefs/$name.def", pkg = pkg))
		}
	}

	val native = NativeConf(this)

	fun native(config: NativeConf.() -> Unit) = native.config()

	/////////////////////////////////////
	// JVM
	/////////////////////////////////////

	data class JvmConf(val root: SdkConfig, var main: String = "NONE") : Platform()

	val jvm = JvmConf(this)

	fun jvm(config: JvmConf.() -> Unit) = jvm.config()

	/////////////
	// Android //
	/////////////

	data class AndroidConf(val root: SdkConfig,
		var directConfig: AndroidExtension.() -> Unit = {},
		var useKotlinExtensions: Boolean = true,
		var compileSdkVersion: Int = -1,
		var buildToolsVersion: String = "NONE") : Platform() {

		fun androidConfigure(config: AndroidExtension.() -> Unit) { directConfig = config }
	}

	val android = AndroidConf(this)

	fun android(config: AndroidConf.() -> Unit) = android.config()

	/////////////////////////////////////
	// Windows
	/////////////////////////////////////

	data class WindowConf(val root: SdkConfig, val supported: MutableList<Window> = mutableListOf(),
		var main: String = "UNKNOWN") {

		operator fun String.invoke() = supported.add(Window(this))
		operator fun String.invoke(conf: Window.() -> Unit): Unit {
			val win = Window(this)
			win.conf()
			if (win.main) main = win.name.fullName(root)
			supported.add(win)
		}

		data class Window(val name: String, var main: Boolean = false) {
			fun main() { main = true }
		}

	}

	val windows = WindowConf(this)

	fun windows(config: WindowConf.() -> Unit) = windows.config()

}