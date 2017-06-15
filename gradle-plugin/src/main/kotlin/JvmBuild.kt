package sdk.plugin

import org.gradle.api.*
import org.gradle.api.tasks.*
import org.gradle.script.lang.kotlin.*

import org.gradle.api.plugins.*
import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

fun Project.configureJvm() {

	if (meta.jvm.main == "NONE") throw GradleException("Please set jvm.main in your build file")

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
	sourceSet.kotlin.srcDirs("$rootDir/src/jvm", "$rootDir/src/shared", "$buildDir/sdk")
	sourceSet.java.srcDir("$rootDir/src/jvm-ext/java")
	sourceSet.resources.srcDir("$rootDir/src/jvm-ext/resources")

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

	// Build

	val build = getTask(Constants.GENERIC_BUILD_TASK)
	build.group = "build"
	build.description = "Creates a binary to be run on the JVM"
	build.finalizedBy(task<Copy>(Constants.MOVE_TASK) {
		// Move output file
		from("$buildDir/distributions") {
			include("*.zip")
		}
		// TODO: Fat jar
		into("$rootDir/${meta.outputDir}")
		rename("${meta.appName}.zip", "JVM Application.zip")
	})
	parent.task(Constants.JVM_COMPILE_TASK) {
		dependsOn(build)
		group = build.group
		description = build.description
	}

	// Metadata

	val metadataTask = Constants.METADATA_TASK.fromParent(this)
	build.dependsOn(metadataTask)
	getTask("compileKotlin").mustRunAfter(metadataTask)

	// Running

	val run = getTask(Constants.GENERIC_RUN_TASK) as JavaExec
	run.group = "run"
	run.description = "Creates a JVM binary and runs it"
	run.args((parent.properties[Constants.RUN_ARGUMENTS] as String?)?.split(" ")?.toTypedArray<String>() ?: arrayOf<String>())
	run.dependsOn(build)
	parent.task(Constants.JVM_RUN_TASK) {
		dependsOn(run)
		group = run.group
		description = run.description
	}
}