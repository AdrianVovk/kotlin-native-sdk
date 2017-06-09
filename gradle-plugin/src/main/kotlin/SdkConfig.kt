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
		val interops: MutableList<String> = mutableListOf(),
		var linkerOpts: String = "NONE") {

		fun interop(name: String) = interops.add(name)
	}

	val native = NativeConf()

	fun native(config: NativeConf.() -> Unit) = native.config()

	/////////////////////////////////////
	// Windows
	/////////////////////////////////////

	val windows = mutableListOf<Window>()
	var mainWindow = "UNKNOWN"

	fun windows(config: WindowConf.() -> Unit) = WindowConf(this).config()


}

data class Window(val name: String, var main: Boolean = false) {
	fun main() { main = true }
}
class WindowConf(val root: SdkConfig) {
	operator fun String.invoke() = root.windows.add(Window(this))
	operator fun String.invoke(conf: Window.() -> Unit): Unit {
		val win = Window(this)
		win.conf()
		if (win.main) root.mainWindow = "${root.appId}.${win.name}"
		root.windows.add(win)
	}
}