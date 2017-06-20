# Gradle Plugin
This project is a gradle plugin for using the Substance SDK. This will eventually allow for a codebase that works on multiple platforms and targets.

### Build file configuration:
##### `settings.gradle`
Configure the `settings.gradle` file like so:
```groovy
pluginManagement.repositories {
	maven { url 'https://dl.bintray.com/jetbrains/kotlin-native-dependencies/' } // Let the compiler find Kotlin-Native
	maven { url 'https://maven.google.com' } // Let the compiler find the Android build tools
	gradlePluginPortal()
}

// Include build types
include("native")
include("jvm")
include("android")
```
The first block gives Gradle the ability to find [Kotlin/Native](https://github.com/JetBrains/kotlin-native) and the Android SDK.

The `include` statements enable configurations for the plugin. In other words, `include("native")` enables compilation for native targets, `include("jvm")` enables compilation for the JVM, etc. (See note 1)

##### `build.gradle.kts`
Here is an example `build.gradle.kts` file:
```kotlin
plugins {
	id("substance.SdkPlugin") version "0.0.0"
}

sdk {
	appName = "My App" // Name of the application
	appId = "com.example.app" // ID of the application

	suppressPlatformWarning = true

	windows {
		"windows.Main" {
			main()
		}
		"windows.Details"()
		"windows.About"()
	}

	native {
		interop("myInterop", defFile = "path/to/another/file.def", pkg = "interop.another") // Can be run many times
		interop("generatedDef", pkg = "interop.generated") {
			headers("header1.h", "header2.h", "header3.h")
			compilerOpts("-I.", "-I/usr/include/something")
			excludeDependentModules = true
			includeC = """
				static inline int getErrno() {
				    return errno;
				}
			"""
		}
	}

	jvm.main = "MainKt"
	jvm.configure {
		dependencies {
			compile("foo.bar.bas:Asdf:1.0.0")
		}
	}

	android {
		compileSdkVersion = 26 // REQUIRED if the Android target is enabled. Acts the same as it does in the default Android gradle plugin
		buildToolsVersion = "26.0.0" // REQUIRED if the Android target is enabled. Acts the same as it does in the default Android gradle plugin

		androidConfigure {
			// This is the same as the `android` configuration block in the standard Android plugin
			defaultConfig {
				applicationId("com.example.app.android")
				minSdkVersion(15)
				targetSdkVersion(26)
				versionCode(1)
				versionName("1.0.0")
			}
		}
		configure {
			dependencies {
			    compile 'com.android.support:appcompat-v7:+'
			    compile 'com.android.support:design:+'
			    compile 'com.android.support.constraint:constraint-layout:+'
			    compile 'com.android.support:support-v4:+'
			    compile 'com.android.support:support-vector-drawable:+'
			}
		}
	}
}
```

Here is the compilete API that is acceccable from within the `sdk` block:
- `appName = String`: Name of the application. Default: "Application"
- `appId = String`: ID of the application. REQUIRED.
- `debug = Boolean`: Tells the program to print out debug information. ~~`release` tasks disable this~~ COMING SOON. DEFAULT: true
- `suppressPlatformWarning = Boolean`: Suppresses the warnings that occur when platforms aren't enabled. DEFAULT: false
- `modifyTasksReport = Boolean`: Modifies the output of `:tasks` to clean up the output. See note 2 for more details. DEFAULT: true
	- //TODO: Move details here
- `outputDir = String`: Specify a directory to put compiled binaries into. DEFAULT: out/
- `(native, jvm, or android).configure {}`: Provides a way to access the project directly, to be configured as if itwere inside a separate `build.gradle.kts` file.
- `(native, jvm, or android).inputDir(String)`: Adds an extra directory to the platform
- `native.optimize = Boolean`: Makes smaller binaries at the expense of compile time
- `native.linkerOpts = String`: Passes linker options to Kotlin/Native
- `native.interop(name: String, defFile = String, pkg = String /* optional */)`: Adds an interop configuration for a def file
- `native.interop(name: String, pkg = String /* optional */) {}`: Generates a def file (see `native:genDefs`) and adds an interop configuration for it. Functions from within the block:
	- `headers = String` or `headers(vararg String)`: Specifies which headers to use
	- `compilerOpts = String` or `compilerOpts(vararg String)`: Specifies extra compiler options to use
	- `headerFilter = String`: Specifies a header filter to use
	- `excludeDependentModules = Boolean`: Excludes modules that aren't directly specified by `headers`
	- `includeC = String`: Includes C code to be generated as interop.
- `jvm.main = String`: The main class to be used for the JVM. REQUIRED (if using the JVM without the SDK library). DEFUALT: Substance SDK library default loader
- `android.useKotlinExtensions = Boolean`: Applies the Kotlin Extensions gradle plugin
- `android.compileSdkVersion`: Acts the same as it does in the default Android plugin. REQUIRED (if using Android)
- `android.buildToolsVersion`: Acts the same as it does in the default Android plugin. REQUIRED (if using Android)
- `android.androidConfigure {}`: Configure the Android plugin directly
- `windows."name"()`: Tell the application that it supports the window at 'name'. (Only if using the SDK library)
- `windows."name" {}`: Tell the application that it supports the window at 'name'. (Only if using the SDK library). Functions from within the block:
	- `main()`: Tell the application that this window is the main window.

*__NOTE:__ Any `.` can also represent a block. So `a.b` can also represent `a { b }`*

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
			- `AndroidManifest.xml`: The Android manifest ~~(if it isn't being generated by the build script)~~
			- `java/`: Contains Java source files to be compiled
			- ~~`ndk/`: Contains Kotlin/Native (or C, depending on configuration) source files to be compiled for the NDK~~
			- `jni/`: Contains source for the JNI
			- `jni-libs/`: Contains libraries for the JNI
			- `res/`: Contains Android resource files to be compiled
			- `resources/`: Contains Java-syle resource files to be compiled
			- `assets/`: Contains assets to be included in the APK
			- `renderscript/`: Contains renderscript scripts to be built
			- `aidl/`: Contains AIDL files to be included in the build
	- `out/`: Where all compiled binaries are placed

### Tasks
##### Building
`buildNative` or `native:build`: Build the program for native targets (using Kotlin/Native)

`buildJvm` or `jvm:build`: Build the program for the JVM

`buildAndroid` or `android:build`: Build the program for Android

##### Running
`runNative` or `native:run`: Build, then run the program for native targets (using Kotlin/Native)

`runJvm` or `jvm:run`: Build, then run the program for the JVM

`runAndroid` or `android:run`: Run the program on Android

When using a run task (excluding Android), use `-Pargs="[ARGUMENTS]"` to pass arguments to the program

##### Extras
`genMetadata`: Generate a metadata file containing build information for the SDK library (located at `build/sdk/gen/metadata.kt`)

`native:genDefs`: Generate def files for native interop (located at `build/sdk/nativeDefs/`)

~~`android:genManifest`: Generate the Android manifest (located at `build/sdk/android/AndroidManifest.xml`)~~ COMING SOON

`android:installSdk`: Download and install the Android SDK to the default location for your system
	- Use `-Psdk.dir=[DIR]` to specify a custom installation location for the Android SDK. Put the directory in quotes if it contains spaces.
	- Use `-Pautoaccept-licenses=y` to automatically accept all Android SDK Licenses, which is useful for headless CI servers

### Extra notes
1: This project sandboxes all of its tasks to seperate subprojects. This fixes conflicts while still providing a way for the developer to access the tasks necessary.
This causes the side-effect of the 'platform syntax' (`platform:task`).

2: This plugin modifies the output of the `:tasks` task to *not* include subprojects. This fixes an issue where the sandboxing (See note 1) made the output messy.
By default, if you are only running one of the platforms, this acts as if there is no sandbox and allows the tasks to show up in the root,
but if you have multiple platforms enabled, this removes them (and only them) from the output. With this setup, any other subprojects will behave as expected.