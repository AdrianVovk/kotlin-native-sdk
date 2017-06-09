// KLib Workaround Gradle File
// This gradle file is temporary. This lets the user build the sample
import org.jetbrains.kotlin.gradle.plugin.*
import org.gradle.api.tasks.Exec

plugins {
	id("substance.SdkPlugin") version "0.0.0"
}

sdk {
	appName = "Sample"
	appId = "gtk.Sample"

	inputDir = "hack/"
	outputDir = "../out/"

	native {
		interop("gtk", defFile = "../sdk/native/libs/gtk.def", pkg = "gtk")
		interop("time", defFile = "../sdk/native/libs/time.def", pkg = "c.time")
		interop("stdlib", defFile = "../sdk/native/libs/stdlib.def", pkg = "c.stdlib")

		linkerOpts = "-L/usr/lib/x86_64-linux-gnu -lglib-2.0 -lgdk-3 -lgtk-3 -lgio-2.0 -lgobject-2.0"
	}
}

//task("run").dependsOn("runNative")
//task("build").dependsOn("buildNative")

task<Exec>("update-sources") {
	commandLine("./update-sources")
}
afterEvaluate {
	tasks.getByName("compileKonanApplication").dependsOn("update-sources")
}