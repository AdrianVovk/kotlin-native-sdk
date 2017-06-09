package sdk.plugin

open class SdkConfig() {
	var debug = true // Debugging by default, "release" task changes it

	var appName = Constants.SDK_DEFAULT_NAME
	var appId = Constants.SDK_DEFAULT_ID

	// TODO: Configure input files
	var outputDir = Constants.SDK_DEFAULT_OUTPUT_DIR
	var inputDir = "NONE"

	/////////////////////////////////////
	// Native
	/////////////////////////////////////

	data class NativeConf(var optimize: Boolean = true,
		val interops: MutableList<InteropConf> = mutableListOf(),
		var linkerOpts: String = "NONE") {

		data class InteropConf(val name: String, val defFile: String, val pkg: String)
		fun interop(name: String, defFile: String, pkg: String = "NONE") = interops.add(InteropConf(name, defFile, pkg))
	}

	val native = NativeConf()

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