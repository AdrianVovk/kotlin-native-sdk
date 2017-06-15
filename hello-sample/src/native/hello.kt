import sdk.gen.BuildMetadata

fun main(args: Array<String>) {
	println("Hello World from ${BuildMetadata.APP_NAME} (${BuildMetadata.APP_ID}) running on Native")

	for (arg in args) println("Arg: $arg")

	window("${BuildMetadata.APP_ID}.windows.Home")
	window("com.app.name.Window")
}

fun window(name: String) {
	if (!BuildMetadata.SUPPORTED_WINDOWS.contains(name)) println("Window $name is not included in the build metadata")
}