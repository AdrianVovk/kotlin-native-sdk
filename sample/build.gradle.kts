// KLib Workaround Gradle File
//
// This gradle file is temporary. This lets the user build the sample

import org.jetbrains.kotlin.gradle.plugin.*
import org.gradle.api.tasks.Exec

buildscript {
    repositories {
        mavenCentral()
        maven { setUrl("https://dl.bintray.com/jetbrains/kotlin-native-dependencies") }
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-native-gradle-plugin:+")
    }
}

apply {
	plugin<KonanPlugin>()
}

configure<KonanInteropContainer> {

	create("gtk", closureOf<KonanInteropConfig> {
		defFile("../sdk/native/libs/gtk.def")
		pkg("gtk")
	})

	create("time", closureOf<KonanInteropConfig> {
		defFile("../sdk/native/libs/time.def")
		pkg("c.time")
	})

	create("stdlib", closureOf<KonanInteropConfig> {
		defFile("../sdk/native/libs/stdlib.def")
		pkg("c.stdlib")
	})
}

configure<KonanArtifactsContainer> {
	create("sample", closureOf<KonanCompilerConfig> {
		inputDir("hack/")
		outputDir("../out/")

		// TODO: Get rid of GTK here - leave for Linux platform
		useInterop("gtk")
		//library("build/konan/interopStubs/genGtkInteropStubs/gtkInteropStubs.bc.klib")
		linkerOpts("-L/usr/lib/x86_64-linux-gnu -lglib-2.0 -lgdk-3 -lgtk-3 -lgio-2.0 -lgobject-2.0")

		useInterop("time")
		//library("build/konan/interopStubs/genTimeInteropStubs/timeInteropStubs.bc.klib")
		useInterop("stdlib")
		//library("build/konan/interopStubs/genStdlibInteropStubs/stdlibInteropStubs.bc.klib")

		enableOptimization() // Make smaller binaries at expense of compile time
	})
}

task<Exec>("run") {
	dependsOn("build")
	commandLine("../out/sample.kexe")
}

task<Exec>("update-sources") {
	commandLine("./update-sources")
}
tasks.getByName("compileKonanSample").dependsOn("update-sources")

// HACK
//tasks.getByName("compileKonanSample").dependsOn("genGtkInteropStubs")
//tasks.getByName("compileKonanSample").dependsOn("compileGtkInteropStubs")
//tasks.getByName("compileKonanSample").dependsOn("genStdlibInteropStubs")
//tasks.getByName("compileKonanSample").dependsOn("compileStdlibInteropStubs")
//tasks.getByName("compileKonanSample").dependsOn("genTimeInteropStubs")
//tasks.getByName("compileKonanSample").dependsOn("compileTimeInteropStubs")