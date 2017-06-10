plugins {
	id("substance.SdkPlugin") version "0.0.0"
}

sdk {
	appName = "SdkDemo"
	appId = "subsance.sdk.Demo"

	debug = false

	inputDir = "hack/"
	outputDir = "../out/"

	native {
		interop("gtk", defFile = "../sdk/native/libs/gtk.def", pkg = "gtk")
		interop("time", defFile = "../sdk/native/libs/time.def", pkg = "c.time")
		interop("stdlib", defFile = "../sdk/native/libs/stdlib.def", pkg = "c.stdlib")

		linkerOpts = "-L/usr/lib/x86_64-linux-gnu -lglib-2.0 -lgdk-3 -lgtk-3 -lgio-2.0 -lgobject-2.0"
	}
}

// KLib Workaround
// TODO: Remove with Kotlin/Native 0.3
task<Exec>("update-sources") {
	commandLine("./update-sources")
}
afterEvaluate {
	tasks.getByName("compileKonanApplication").dependsOn("update-sources")
}