package sdk.plugin

import org.gradle.api.*
import org.gradle.api.tasks.*
import org.gradle.script.lang.kotlin.*

import org.gradle.api.plugins.*
import org.gradle.api.internal.HasConvention

import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

val SourceSet.kotlin	get() = ((this as HasConvention).convention.plugins["kotlin"] as KotlinSourceSet).kotlin

fun Project.configureJvm() {
	println("TODO: Finish JVM support")

	////////////////////////
	// Application plugin //
	////////////////////////

	pluginManager.apply("application")

	configure<ApplicationPluginConvention> {
		mainClassName = meta.jvm.main
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

	val sourceSet = convention.getPlugin(JavaPluginConvention::class.java).sourceSets.getByName("main")
	sourceSet.kotlin.srcDirs("src/jvm", "src/shared")
	sourceSet.java.srcDir("src/jvm-ext/java")
	sourceSet.resources.srcDir("src/jvm-ext/resources")

	///////////
	// Tasks //
	///////////

	val oldBuild = getTask("build")
	tasks.remove(oldBuild)

	// TODO: configure new build

/*	val build = task(Constants.JVM_COMPILE_TASK) {
		group = "build"
		description = "Creates a binary to be run on the JVM"
	}

	build.dependsOn(Constants.METADATA_TASK)
	//TODO: getTask(???).mustRunAfter(Constants.METADATA_TASK)

	task<JavaExec>(Constants.JVM_RUN_TASK) {
		group = "run"
		description = "Creates a JVM binary and runs it"

		dependsOn(build)

		classpath = sourceSet.runtimeClasspath
		main = meta.jvm.main
		args((properties[Constants.RUN_ARGUMENTS] as String?)?.split(" ")?.toTypedArray<String>() ?: arrayOf<String>())
	}*/
}