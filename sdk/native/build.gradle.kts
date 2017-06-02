// Import all of the Kotlin/Native gradle plugin configurations
import org.jetbrains.kotlin.gradle.plugin.*

// Include Kotlin/Native
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

val konanInterop: NamedDomainObjectContainer<KonanInteropConfig> by extensions
konanInterop {
	"gtk" {
		defFile("../sdk/native/libs/gtk.def")
		pkg("gtk")
	}

	"time" {
		defFile("../sdk/native/libs/time.def")
		pkg("c.time")
	}

	"stdlib" {
		defFile("../sdk/native/libs/stdlib.def")
		pkg("c.stdlib")
	}
}

val konanArtifacts: NamedDomainObjectContainer<KonanCompilerConfig> by extensions
konanArtifacts {
	create("sdk", closureOf<KonanCompilerConfig> {
		inputDir("src/")
		outputDir("../../out/")

		// TODO: Get rid of GTK here - leave for Linux platform
		//useInterop("gtk")
		library("build/konan/interopCompiledStubs/gtkInteropStubs/gtkInteropStubs.klib.klib") // HACK
		linkerOpts("-L/usr/lib/x86_64-linux-gnu -lglib-2.0 -lgdk-3 -lgtk-3 -lgio-2.0 -lgobject-2.0")

		//useInterop("time")
		//useInterop("stdlib")
		library("build/konan/interopCompiledStubs/timeInteropStubs/timeInteropStubs.klib.klib") // HACK
		library("build/konan/interopCompiledStubs/stdlibInteropStubs/stdlibInteropStubs.klib.klib") // HACK

		//produce("library") // Make a klib
		enableOptimization() // Make smaller binaries at expense of compile time
	})
}