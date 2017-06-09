package sdk.plugin

open class SdkConfig() {
	var appName = Constants.SDK_DEFAULT_NAME
	var appId = Constants.SDK_DEFAULT_ID

	// TODO: Configure input files
	var outputDir = Constants.SDK_DEFAULT_OUTPUT_DIR

	// TODO: Better dsl. For example `meta.native.optimize`
	var nativeOptimize = true
	// TODO: Native interop

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