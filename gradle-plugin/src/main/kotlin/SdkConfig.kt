package sdk.plugin

import org.gradle.api.Project

open class SdkConfig(val project: Project) {
	var debug = true // Debugging by default, "release" task changes it

	var appName = Constants.SDK_DEFAULT_NAME
	var appId = Constants.SDK_DEFAULT_ID

	// TODO: Configure input files
	var outputDir = Constants.SDK_DEFAULT_OUTPUT_DIR
	var inputDir = "NONE"

	/////////////////////////////////////
	// JVM
	/////////////////////////////////////

	data class JvmConf(var main: String = "sdk.runtime.Zygote")

	val jvm = JvmConf()

	fun jvm(config: JvmConf.() -> Unit) = jvm.config()


	/////////////////////////////////////
	// Native
	/////////////////////////////////////

	data class NativeConf(val root: SdkConfig,
		var optimize: Boolean = true,
		val interops: MutableList<InteropConf> = mutableListOf(),
		val defFilesToGenerate: MutableList<DefFile> = mutableListOf(),
		var linkerOpts: String = "NONE") {

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
	// Windows
	/////////////////////////////////////

	data class WindowConf(val root: SdkConfig, val supported: MutableList<Window> = mutableListOf(),
		var main: String = "UNKNOWN") {

		operator fun String.invoke() = supported.add(Window(this))
		operator fun String.invoke(conf: Window.() -> Unit): Unit {
			val win = Window(this)
			win.conf()
			if (win.main) main = "${root.appId}.${win.name}"
			supported.add(win)
		}

		data class Window(val name: String, var main: Boolean = false) {
			fun main() { main = true }
		}

	}

	val windows = WindowConf(this)

	fun windows(config: WindowConf.() -> Unit) = windows.config()

}