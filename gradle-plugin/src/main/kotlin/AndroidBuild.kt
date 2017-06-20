package sdk.plugin

import org.gradle.api.*
import org.gradle.api.tasks.*
import org.gradle.script.lang.kotlin.*

import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

fun Project.configureAndroid() {
	if (!configureAndroidSdk()) return // The SDK isn't configured

	////////////////////////////////
	// Android application plugin //
	////////////////////////////////

	pluginManager.apply("com.android.application")

	configure<AndroidExtension> {
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

	// Build

	val build = getTask(Constants.GENERIC_BUILD_TASK)
	build.group = "build"
	build.description = "Creates an APK to be run on Android"
	build.finalizedBy(task<Copy>(Constants.MOVE_TASK) {
		// TODO
	})
	parent.task(Constants.ANDROID_COMPILE_TASK) {
		dependsOn(build)
		group = build.group
		description = build.description
	}

	// Metadata

	val metadataTask = Constants.METADATA_TASK.fromParent(this)
	build.dependsOn(metadataTask)
	getTask("compileKotlin").mustRunAfter(metadataTask)

	// Running
	val run = getTask(Constants.GENERIC_RUN_TASK)
	run.group = "run"
	run.description = "Creates an APK file, installs it on a test device, and runs it"
	run.doFirst {
		if (project.parent.properties[Constants.RUN_ARGUMENTS] != null) println("Android does not support command-line arguments")
	}
	run.dependsOn(build)
	parent.task(Constants.JVM_RUN_TASK) {
		dependsOn(run)
		group = run.group
		description = run.description
	}

	////////////////////////////////
	// Apply custom configuration //
	////////////////////////////////

	(meta.android.buildConfig)()
}