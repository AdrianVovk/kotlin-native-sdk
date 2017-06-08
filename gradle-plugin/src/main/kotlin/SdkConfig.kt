package sdk.plugin

open class SdkConfig() {
	var appName = Constants.SDK_DEFAULT_NAME
	var appId = Constants.SDK_DEFAULT_ID

	// TODO: Configure input files
	var outputDir = Constants.SDK_DEFAULT_OUTPUT_DIR

	// TODO: Better dsl. For example `meta.native.optimize`
	var nativeOptimize = true
	// TODO: Native interop
}