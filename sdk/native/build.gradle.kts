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

configure<KonanInteropContainer> {

	create("gtk", closureOf<KonanInteropConfig> {
		defFile("libs/gtk.def")
		pkg("gtk")
	})

	create("time", closureOf<KonanInteropConfig> {
		defFile("libs/time.def")
		pkg("c.time")
	})

	create("stdlib", closureOf<KonanInteropConfig> {
		defFile("libs/stdlib.def")
		pkg("c.stdlib")
	})
}

configure<KonanArtifactsContainer> {
	create("sdk-library", closureOf<KonanCompilerConfig> {
		inputDir("src/")
		outputDir("../../out/")

		// TODO: Get rid of GTK here - leave for Linux platform
		useInterop("gtk")
		linkerOpts("-L/usr/lib/x86_64-linux-gnu -lglib-2.0 -lgdk-3 -lgtk-3 -lgio-2.0 -lgobject-2.0")

		useInterop("time")
		useInterop("stdlib")

		noMain() // Make sure this isn't executable
		noLink() // Make this non-runabble
		enableOptimization() // Make smaller binaries at expense of compile time
	})
}