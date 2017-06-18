package sdk.plugin

import org.gradle.api.*
import org.gradle.api.tasks.*
import org.gradle.script.lang.kotlin.*

import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

import com.android.build.gradle.AppExtension

fun Project.configureAndroid() {
	findSdk()

	////////////////////////////////
	// Android application plugin //
	////////////////////////////////

	pluginManager.apply("com.android.application")

	configure<AppExtension>() {
		if (meta.android.compileSdkVersion != -1) compileSdkVersion(meta.android .compileSdkVersion)
		if (meta.android.buildToolsVersion != "NONE") buildToolsVersion(meta.android.buildToolsVersion)

		// Configuring android
		(meta.android.directConfig)() // Run the user's direct configuration
	}

	////////////////////
	// Kotlin plugins //
	////////////////////

	pluginManager.apply("org.jetbrains.kotlin.android")
	if (meta.android.useKotlinExtensions) pluginManager.apply("org.jetbrains.kotlin.android.extensions")

	configure<KotlinProjectExtension> {
		experimental.coroutines = Coroutines.ENABLE // Enable Coroutines
	}

	/////////////////
	// Source sets //
	/////////////////

	val sourceSet = android.sourceSets.getByName("main")
	sourceSet.kotlin.srcDirs("$rootDir/src/android", "$rootDir/src/shared", "$buildDir/sdk/gen")
	for (dir in meta.android.inputDirs) sourceSet.kotlin.srcDir(dir)
	sourceSet.manifest.srcFile("$rootDir/src/android-ext/AndroidManifest.xml") //TODO: Gen
	sourceSet.java.srcDir("$rootDir/src/android-ext/java")
	sourceSet.jni.srcDir("$rootDir/src/android-ext/jni")
	sourceSet.jniLibs.srcDir("$rootDir/src/android-ext/jni-libs")
	sourceSet.resources.srcDir("$rootDir/src/android-ext/resources")
	sourceSet.res.srcDir("$rootDir/src/android-ext/java")
	sourceSet.renderscript.srcDir("$rootDir/src/android-ext/java")
	sourceSet.aidl.srcDir("$rootDir/src/android-ext/java")

	///////////////////////
	// Default Libraries //
	///////////////////////

	repositories {
		google()
	}

	dependencies {
		// TODO
	}

	///////////
	// Tasks //
	///////////

	////////////////////////////////
	// Apply custom configuration //
	////////////////////////////////

	(meta.android.buildConfig)()
}

fun Project.findSdk() {
	if (with(project) { file("$rootDir/local.properties").exists() || System.getenv("ANDROID_HOME") != null }) return
	val os = System.getProperty("os.name")
	val home = when {
		os.startsWith("Windows") -> System.getenv("LOCALAPPDATA").removeSuffix("\\")
		os == "Mac OS X" -> System.getenv("HOME").removeSuffix("/") + "/Library"
		else -> System.getenv("HOME").removeSuffix("/")
	}
	val defaultPath = file("$home/Android/${if (os == "Linux") "Sdk" else "sdk"}")

	if (!defaultPath.isDirectory())
		throw StopExecutionException("Couldn't find Android SDK in the default location ($defaultPath.absolutePath). Please specify it manually")

	if (!file("${defaultPath.absolutePath}/platform-tools").isDirectory())
		throw StopExecutionException("The files found at $defaultPath.absolutePath do not seem to be the Android SDK. Please specify its path manually")

	file("$rootDir/local.properties").writeText("sdk.dir=${defaultPath.absolutePath}")
}