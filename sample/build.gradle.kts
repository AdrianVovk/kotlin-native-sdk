// Import all of the Kotlin/Native gradle plugin configurations
import org.jetbrains.kotlin.gradle.plugin.*
import org.gradle.api.tasks.Exec

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
		linkerOpts("-L/usr/lib/x86_64-linux-gnu -lglib-2.0 -lgdk-3 -lgtk-3 -lgio-2.0 -lgobject-2.0")

		useInterop("time")
		useInterop("stdlib")

		enableOptimization() // Make smaller binaries at expense of compile time
	})
}

task<Exec>("run") {
	dependsOn("build")
	commandLine("../out/sample.kexe")
}