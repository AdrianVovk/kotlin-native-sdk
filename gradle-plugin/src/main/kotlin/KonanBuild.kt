package sdk.plugin

import org.gradle.api.*
import org.gradle.api.tasks.*
import org.gradle.script.lang.kotlin.*

import org.jetbrains.kotlin.gradle.plugin.*

fun Project.configureKonan() {
	pluginManager.apply("konan") // Apply the Kotlin/Native build plugin

	val normAppName = meta.appName.split(" ", "_").map { it.capitalize()  }.joinToString("").capitalize()

	////////////////////
	// Konan compiler //
	////////////////////

	val konanInterop: NamedDomainObjectContainer<KonanInteropConfig> by project.extensions
	konanInterop {
		for (interop in meta.native.interops) {
			interop.name {
				defFile(interop.defFile)
				if (interop.pkg != "NONE") pkg(interop.pkg)
			}
		}
	}

	val konanArtifacts: NamedDomainObjectContainer<KonanCompilerConfig> by project.extensions
	konanArtifacts {
		normAppName {
			inputDir("$rootDir/src/native/")
			inputDir("$rootDir/src/shared")
			for (dir in meta.native.inputDirs) inputDir("$rootDir/$dir")
			inputFiles("$buildDir/sdk/gen/metadata.kt") // Include generated metadata

			outputDir("$rootDir/${meta.outputDir}")

			for (interop in meta.native.interops.map { it.name }) {
				useInterop(interop)
			}

			if (meta.native.linkerOpts != "NONE") linkerOpts(meta.native.linkerOpts)

			if (meta.native.optimize) enableOptimization()
		}
	}

	///////////
	// Tasks //
	///////////

	// Build

	val build = getTask(Constants.GENERIC_BUILD_TASK)
	build.group = "build"
	build.description = "Creates a native binary for this program (using Kotlin/Native)"
	parent.task(Constants.KONAN_COMPILE_TASK) {
		dependsOn(build)
		group = build.group
		description = build.description
	}

	// Metadata

	val metaTask = Constants.METADATA_TASK.fromParent(this)
	build.dependsOn(metaTask) // Add metadata task to the build process
	getTask("compileKonan$normAppName").mustRunAfter(metaTask) // Generate metadata before build

	// Def files

	task<GenDefsTask>(Constants.KONAN_DEF_TASK)
	build.dependsOn(Constants.KONAN_DEF_TASK) // Add def task to the build process
	for (interop in meta.native.interops) {
		getTask("gen${interop.name.capitalize()}InteropStubs").mustRunAfter(Constants.KONAN_DEF_TASK) // Generate def file before building
	}

	// Running

	val run = task<Exec>(Constants.GENERIC_RUN_TASK) {
		group = "run"
		description = "Creates a native binary and runs it (using kotlin/Native)"

		dependsOn(build)

		val fileExt = if (System.getProperty("os.name").startsWith("Windows")) "exe" else "kexe"
		val out = "$rootDir/${meta.outputDir}".removeSuffix("/")
		val args = (parent.properties[Constants.RUN_ARGUMENTS] as String?)?.split(" ")?.toTypedArray<String>() ?: arrayOf<String>()
		commandLine("$out/$normAppName.$fileExt", *args)
	}
	parent.task(Constants.KONAN_RUN_TASK) {
		dependsOn(run)
		group = run.group
		description = run.description
	}

	////////////////////////////////
	// Apply custom configuration //
	////////////////////////////////

	(meta.native.buildConfig)()
}

open class GenDefsTask : DefaultTask() {

	init {
		description = "Generates library defenition files for Kotlin/Native"
		group = "build setup"
	}

	@TaskAction fun generate() {
		for (def in meta.native.defFilesToGenerate) {
			val outputFile = with(project) {
				val file = file("$buildDir/sdk/nativeDefs/${def.name}.def")
				file.mkdirs() // Make sure that the folder exists
				file
			}
			var text = ""

			if (def.headers != "NONE") text += "headers = ${def.headers}\n"
			if (def.compilerOpts != "NONE") text += "compilerOpts = ${def.compilerOpts}\n"
			if (def.headerFilter != "NONE") text += "headerFilter = ${def.headerFilter}\n"
			if (def.excludeDependentModules != null) text += "excludeDependentModules = ${def.excludeDependentModules}\n"
			if (def.includeC != "NONE") {
				val c = """
					---
					${def.includeC}
				""".trimIndent().trim() // Fixes spacing
				text += c
			}

			outputFile.writeText(text)
		}
	}
}