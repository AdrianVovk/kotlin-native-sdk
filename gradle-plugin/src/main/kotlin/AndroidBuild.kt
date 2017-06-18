package sdk.plugin

import org.gradle.api.*
import org.gradle.api.tasks.*
import org.gradle.script.lang.kotlin.*

import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

import com.android.build.gradle.AppExtension

import java.io.File
import java.io.FileInputStream
import java.util.Properties
import groovy.util.XmlParser
import de.undercouch.gradle.tasks.download.Download
import de.undercouch.gradle.tasks.download.Verify

fun Project.configureAndroid() {

	////////////////////
	// SDK Management //
	////////////////////

	pluginManager.apply("de.undercouch.download")

	val os = System.getProperty("os.name")
	val sdkHome = when {
		os.startsWith("Windows") -> System.getenv("LOCALAPPDATA").removeSuffix("\\")
		os == "Mac OS X" -> System.getenv("HOME").removeSuffix("/") + "/Library"
		else -> System.getenv("HOME").removeSuffix("/")
	}
	val defaultSdkPath = file("$sdkHome/Android/${if (os == "Linux") "Sdk" else "sdk"}")

	if (!checkForSdk(defaultSdkPath)) {

		val customPath = (parent.properties[Constants.ANDROID_SDK_INSTALL_ARGUMENT] as String?)
		if (customPath != null) {
			println("Installing to custom location: $customPath")
			println("Please make sure you manually specify this location for other projects to avoid duplicate installs")
		}
		val path = file(customPath ?: defaultSdkPath.absolutePath)

		val repoTmpFile = file("$buildDir/sdk-repo.tmp")
		val sdkTmpFile = file("$buildDir/sdk-tools.tmp")

		var sdkFilename = ""
		var sdkChecksum = ""

		val downloadRepo = task<Download>("downloadAndroidToolsRepo") {
			src("https://dl.google.com/android/repository/repository2-1.xml")
			dest(repoTmpFile)
		}
		val processRepo = task("parseAndroidToolsRepo") { dependsOn(downloadRepo) }.doLast {
			val xml = XmlParser().parse(repoTmpFile)
			println(xml.get("remotePackage")::class.qualifiedName)

			sdkFilename = "sdk-tools-linux-3952940.zip" //TODO
			sdkChecksum = "a438247f7d752b8e90f9e432b16b187fcff40c85" // TODO
		}
		val downloadSdk = task<Download>("downloadAndroidSdk") {
			dependsOn(processRepo)
			src("https://dl.google.com/android/repository/$sdkFilename")
			dest(sdkTmpFile)
		}
		val verifySdk = task<Verify>("verifyAndroidSdk") {
			dependsOn(downloadSdk)
			src(sdkTmpFile)
			algorithm("SHA-256")
			checksum(sdkChecksum)
		}
		val extractSdk = task<Copy>("extractAndroidSdk") {
			dependsOn(verifySdk)
			from(zipTree(sdkTmpFile))
			into(path)
		}
		val acceptLicenses = task<Exec>("acceptAndroidSdkLicenses") {
			dependsOn(extractSdk)
			workingDir = path
			standardInput = "y\ny\ny\ny\ny\ny\ny\ny\ny\ny\ny\ny\ny\ny\ny\ny\ny\ny\ny\ny\n".byteInputStream() // Accept everything
			commandLine("tools/bin/sdkmanager", "--licenses")
		}

		// If the SDK doesn't exist at the default path, add a download task
		task(Constants.ANDROID_SDK_TASK) {
			description = "Download the Android SDK and accept all of its licenses."
			group = "build setup"

			dependsOn(acceptLicenses)
			doLast {
				foundSdkAt(path) // Save the SDK directory
				repoTmpFile.delete()
				sdkTmpFile.delete()
			}
		}

		return // Don't configure anything without an SDK
	}

	////////////////////////////////
	// Android application plugin //
	////////////////////////////////

	pluginManager.apply("com.android.application")

	configure<AppExtension> {
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

fun Project.checkForSdk(path: File) : Boolean {
	if (System.getenv("ANDROID_HOME") != null && System.getenv("ANDROID_HOME") != "") return true

	val props = loadLocalProperties()
	if (props.getProperty("sdk.dir") != null) return true

	val downloadMessage = "The Substance SDK Plugin can attempt to download the Android SDK by running `android:${Constants.ANDROID_SDK_INSTALL_ARGUMENT}`."
	if (!path.isDirectory()) {
		println("Couldn't find the Android SDK in the default location ($path). Please specify its location manually")
		println(downloadMessage)
		return false
	}

	if (!file("${path.absolutePath}/tools").isDirectory()) {
		println("The files found at $path do not seem to be the Android SDK. Please specify its location manually")
		println(downloadMessage)
		return false
	}

	foundSdkAt(path, props = props /* Why look for it again? */ )
	return true
}

fun Project.loadLocalProperties(path: File = file("$rootDir/local.properties")): Properties {
	val props = Properties()
	if (path.exists()) props.load(FileInputStream(path))
	return props
}

fun Project.foundSdkAt(path: File,
	propsFile: File = file("$rootDir/local.properties"),
	props: Properties = loadLocalProperties()) {

	props.setProperty("sdk.dir", path.absolutePath)
	props.store(file("$rootDir/local.properties").printWriter(), """
			This file was generated by the SDK Gradle plugin to locate the Android SDK
			If you edit this file, your changes WILL NOT be overwritten. Deleting the `sdk.dir` tag may lead to the SDK recreating it
			Exclude this file from version control, as it is system dependent
	""".trimIndent().trim())
}