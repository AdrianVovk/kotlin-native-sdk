package sdk.plugin

import org.gradle.api.*
import org.gradle.api.tasks.*
import org.gradle.script.lang.kotlin.*

import org.gradle.api.plugins.*
import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

fun Project.configureJvm() {

	////////////////////////
	// Application plugin //
	////////////////////////

	pluginManager.apply("application")

	configure<ApplicationPluginConvention> {
		mainClassName = meta.jvm.main.fullName(meta)
		applicationName = meta.appName
	}

	///////////////////
	// Kotlin Plugin //
	///////////////////


	pluginManager.apply("org.jetbrains.kotlin.jvm")

	configure<KotlinProjectExtension> {
		experimental.coroutines = Coroutines.ENABLE // Enable Coroutines
	}

	/////////////////
	// Source sets //
	/////////////////

	val sourceSet = java.sourceSets.getByName("main")
	sourceSet.kotlin.srcDirs("src/jvm", "src/shared", "$buildDir/sdk")
	sourceSet.java.srcDir("src/jvm-ext/java")
	sourceSet.resources.srcDir("src/jvm-ext/resources")

	//////////////////////////////
	// Include standard library //
	//////////////////////////////

	repositories {
		mavenCentral()
	}

	dependencies {
		compile("org.jetbrains.kotlin:kotlin-stdlib")
	}


	///////////
	// Tasks //
	///////////
/* WHY: Don't modify the task structure. Leaving this as-is allows for some debugging
	val oldBuild = getTask("build")
	tasks.remove(oldBuild)

	// TODO: configure new build

	val build = task(Constants.JVM_COMPILE_TASK) {
		group = "build"
		description = "Creates a binary to be run on the JVM"
	}

	build.dependsOn(Constants.METADATA_TASK)
	getTask("compileKotlin").mustRunAfter(Constants.METADATA_TASK)

	task<JavaExec>(Constants.JVM_RUN_TASK) {
		group = "run"
		description = "Creates a JVM binary and runs it"

		dependsOn(build)

		classpath = sourceSet.runtimeClasspath
		main = meta.jvm.main.fullName(meta)
		args((properties[Constants.RUN_ARGUMENTS] as String?)?.split(" ")?.toTypedArray<String>() ?: arrayOf<String>())
	}*/
}