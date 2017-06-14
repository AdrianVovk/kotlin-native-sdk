# Gradle Plugin
This project is a gradle plugin for using the Substance SDK. This will eventually allow for a codebase that works on multiple platforms and targets.

### Build file configuration:
First, a custom repo needs to be added to your application by editing `settings.gradle`:
```groovy
pluginManagement.repositories {
	maven {
		url 'https://dl.bintray.com/jetbrains/kotlin-native-dependencies/'
	}
	gradlePluginPortal()
}
```
This gives the plugin the ability to use [Kotlin/Native](https://github.com/JetBrains/kotlin-native)

Here is an example `build.gradle.kts` file:
```kotlin
plugins {
	id("substance.SdkPlugin") version "0.0.0"
}

sdk {
	appName = "My App" // Name of the application
	appId = "com.example.app" // ID of the application

	debug = false // Tell the program to print out debug detail. DEFAULT: true

	inputDir = "sources/" // Currently adds one extra directory to compilation
	outputDir = "customOut/" // Where to place binaries. DEFAULT: "out/"

	windows {
		"windows.Main" {
			main()
		}
		"windows.Details"()
		"windows.About"()
	}

	native {
		interop("name", defFile = "path/to/def/file.def", pkg = "interop.name") // Include a def file as interop
		interop("another", defFile = "path/to/another/file.def", pkg = "interop.another") // Can be run many times
		interop("generatedDef", pkg = "interop.generated") {
			headers("header1.h", "header2.h", "header3.h")

			compilerOpts = "-I. -I/usr/include/something"
			// or use compilerOpts("-I.", "-I/usr/include/something")

			// this C code is included in the bindings
			includeC = """
				static inline int getErrno() {
				    return errno;
				}
			"""

			headerFilter = "SomeLibrary/**"
			excludeDependentModules = true
		}

		linkerOpts = "" // Pass linker opts to Konan compiler
		optimize = false // Make binaries smaller at the expense of compile time. DEFAULT: true
	}
}
```

### Project structure
The SDK plugin has a custom project structure for the multi-language build process.

This is the default directory structure:

- `Project root`
	- `build.gradle.kts`: The build script for configuring the build
	- `src/`: The main source directory
		- `shared/`: Contains files to be compiled by all builds.
		- `native/`: Contains files to be compiled by Kotlin/Native.
		- `jvm/`: Contains files to be compiled by Kotlin/JVM.
		- `jvm-ext/`: Contains extra files for the JVM
			- `java/`: Contains Java source files to be compiled
			- `resources/`: Contains resource files to be compiled
		- `android/`: Contains files to be compiled for Android
		- `android-ext/`: Contains extra files for Android
			- `java/`: Contains Java source files to be compiled
			- `ndk/`: Contains Kotlin/Native (or C, depending on configuration) source files to be compiled for the NDK
			- `resources/`: Contains resource files to be compiled
	- `out/`: Where all compiled binaries are placed

### Tasks
##### Building
`buildNative`: Build the program for native targets (using Kotlin/Native)

`buildJvm`: COMING SOON

`buildAndroid`: COMING SOON

##### Running
`runNative`: Build, then run the program for native targets (using Kotlin/Native)

`runJvm`: COMING SOON

`runAndroid`: COMING SOON

When using the run tasks, it is possible to pass arguments to the program.
To do this, use `-Pargs=""` with your build command and put your arguements in the quotes.

##### Extras
`genMetadata`: Generate a metadata file containing build information for the SDK library (located at `build/sdk/metadata.kt`)

`genNativeDefs`: Generate def files for native interop (located at `build/sdk/nativeDefs/`)